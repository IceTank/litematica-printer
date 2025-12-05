package me.aleksilassila.litematica.printer.v1_21_4.implementation;

import me.aleksilassila.litematica.printer.v1_21_4.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_21_4.config.PrinterConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class PrinterPlacementContext extends ItemPlacementContext {
    public final @Nullable Direction lookDirection;
    public final boolean shouldSneak;
    public final BlockHitResult hitResult;
    public final int requiredItemSlot;
    public boolean canStealth = false;
    public boolean isRaytrace = false;
    public boolean isAirPlace = false;

    public PrinterPlacementContext(PlayerEntity player, BlockHitResult hitResult, ItemStack requiredItem, int requiredItemSlot) {
        this(player, hitResult, requiredItem, requiredItemSlot, null, false);
    }

    public PrinterPlacementContext(PlayerEntity player, BlockHitResult hitResult, ItemStack requiredItem, int requiredItemSlot, @Nullable Direction lookDirection, boolean requiresSneaking) {
        super(player, Hand.MAIN_HAND, requiredItem, hitResult);

        this.lookDirection = lookDirection;
        this.shouldSneak = requiresSneaking;
        this.hitResult = hitResult;
        this.requiredItemSlot = requiredItemSlot;
    }

    @Override
    public Direction getPlayerLookDirection() {
        return lookDirection == null ? super.getPlayerLookDirection() : lookDirection;
    }

    @Override
    public Direction getVerticalPlayerLookDirection() {
        if (lookDirection != null && lookDirection.getOpposite() == super.getVerticalPlayerLookDirection())
            return lookDirection;
        return super.getVerticalPlayerLookDirection();
    }

    @Override
    public Direction getHorizontalPlayerFacing() {
        if (lookDirection == null || !lookDirection.getAxis().isHorizontal()) return super.getHorizontalPlayerFacing();

        return lookDirection;
    }

    @Override
    public boolean canPlace() {
        if (!isAirPlace) {
            return super.canPlace();
        }
        if (!super.canPlace()) {
            return false;
        }
        if (this.getPlayer().getEyePos().distanceTo(hitResult.getBlockPos().toCenterPos()) > PrinterConfig.PRINTER_AIRPLACE_RANGE.getDoubleValue()) {
            return false;
        }
        BlockState currentState = this.getWorld().getBlockState(hitResult.getBlockPos());
        if (!currentState.isReplaceable() || currentState.getBlock() instanceof FireBlock) {
            return LitematicaMixinMod.REPLACE_FLUIDS_SOURCE_BLOCKS.getBooleanValue() ||
                    !(currentState.getBlock() instanceof FluidBlock);
        }
        return true;
    }

    @Override
    public String toString() {
        return "PrinterPlacementContext{" +
                "lookDirection=" + lookDirection +
                ", requiresSneaking=" + shouldSneak +
                ", blockPos=" + hitResult.getBlockPos() +
                ", side=" + hitResult.getSide() +
//                ", hitVec=" + hitResult +
                '}';
    }
}
