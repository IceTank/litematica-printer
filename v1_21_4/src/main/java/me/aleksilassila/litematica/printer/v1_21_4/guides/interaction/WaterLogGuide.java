package me.aleksilassila.litematica.printer.v1_21_4.guides.interaction;


import me.aleksilassila.litematica.printer.v1_21_4.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_21_4.SchematicBlockState;
import me.aleksilassila.litematica.printer.v1_21_4.actions.*;
import me.aleksilassila.litematica.printer.v1_21_4.guides.placement.GeneralPlacementGuide;
import me.aleksilassila.litematica.printer.v1_21_4.implementation.PrinterPlacementContext;
import me.aleksilassila.litematica.printer.v1_21_4.implementation.actions.UseItemActionImpl;
import net.minecraft.block.Waterloggable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * @author IceTank
 * @since 08.03.2026
 */
public class WaterLogGuide extends GeneralPlacementGuide {
    boolean canWork = false;

    public WaterLogGuide(SchematicBlockState state) {
        super(state);
        canWork = state.targetState.getBlock() instanceof Waterloggable;
    }

    @Override
    public boolean canExecute(ClientPlayerEntity player) {
        if (!canWork) return false;
        if (!(currentState.getBlock() instanceof Waterloggable)) return false;

        if (currentState.get(Properties.WATERLOGGED) == null
                || currentState.get(Properties.WATERLOGGED) == targetState.get(Properties.WATERLOGGED)) {
            return false;
        }

//        if (!super.canExecute(player)) return false;
        List<ItemStack> requiredItems = getRequiredItems();
        if (requiredItems.isEmpty() || requiredItems.stream().allMatch(i -> i.isOf(Items.AIR)))
            return false;
        for (Direction side : getPossibleSides()) {
            if (canSeeBlockFace(player, new BlockHitResult(Vec3d.ofCenter(state.blockPos), side.getOpposite(), state.blockPos.offset(side), false))) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected @NotNull List<ItemStack> getRequiredItems() {
        if (!canWork) return Collections.emptyList();
        return Collections.singletonList(new ItemStack(Items.WATER_BUCKET));
    }

    @Override
    public @Nullable PrinterPlacementContext getPlacementContext(ClientPlayerEntity player) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return null;

        Vec3d[] hitVecsToTryArray = getPossibleHitVecs();
        Vec3d playerEyePos = player.getEyePos();

        ItemStack requiredItem = getRequiredItem(player).stream().findFirst().orElse(ItemStack.EMPTY);
        int slot = getRequiredItemStackSlot(player);

        if (slot == -1) return null;

        for (Direction side : getPossibleSides()) {
            Vec3d hitVec = Vec3d.ofCenter(state.blockPos)
                    .add(Vec3d.of(side.getVector()).multiply(0.5)); // Center of the block side face we are placing on

            for (Vec3d hitVecToTry : hitVecsToTryArray) {
                Vec3d multiplier = Vec3d.of(side.getVector());
                multiplier = new Vec3d(
                        multiplier.x == 0 ? 1 : 0,
                        multiplier.y == 0 ? 1 : 0,
                        multiplier.z == 0 ? 1 : 0); // Offset from the Center of the block side face we are placing on by pre calculated values. This samples different points on that face.

                Vec3d blockHit = hitVec.add(hitVecToTry.multiply(multiplier));
                Vec3d lookDirection = blockHit.subtract(playerEyePos).normalize();
                Direction relativeDirection = Direction.getFacing(lookDirection.x, lookDirection.y, lookDirection.z);

                if (playerEyePos.distanceTo(blockHit) > LitematicaMixinMod.PRINTING_RANGE.getDoubleValue()) // Check if the hit vector is in range
                    continue;

                Vec3d lookVec = blockHit.subtract(playerEyePos).normalize(); // Look vector from the player's eye to the block hit vector
                Vec3d raycastEnd = playerEyePos.add(lookVec.multiply(5)); // 5 block max distance
                RaycastContext raycastContext = new RaycastContext(playerEyePos, raycastEnd, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player);
                BlockHitResult result = world.raycast(raycastContext);
                if (result.getType() != HitResult.Type.BLOCK || !result.getBlockPos().equals(state.blockPos)) { // If we didn't hit a block, skip
                    continue;
                }
                if (result.getPos().distanceTo(playerEyePos) > LitematicaMixinMod.PRINTING_RANGE.getDoubleValue()) { // Check if the hit result is in range
                    continue;
                }

                PrinterPlacementContext rayTraceContext = new PrinterPlacementContext(player, result, requiredItem, slot, relativeDirection, false);
                rayTraceContext.canStealth = true;
                rayTraceContext.isRaytrace = true;
                return rayTraceContext;
            }
        }
        return null;
    }

    @Override
    public @NotNull List<Action> execute(ClientPlayerEntity player) {
        List<Action> actions = new ArrayList<>();

        ItemStack requiredItem = getRequiredItem(player).stream().findFirst().orElse(ItemStack.EMPTY);
        if (requiredItem.isEmpty()) return actions;
        var ctx = getPlacementContext(player);
        if (ctx == null) return actions;

        ActionChain chain = new ActionChain();
        chain.addImmediateAction(new PrepareLook(ctx));
        chain.addNextTickAction(new PrepareAction(ctx));
        chain.addNextTickAction(new UseItemActionImpl(ctx));

        actions.add(chain);

        return actions;
    }
}
