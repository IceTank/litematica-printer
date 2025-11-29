package me.aleksilassila.litematica.printer.v1_21_4.implementation.actions;

import me.aleksilassila.litematica.printer.v1_21_4.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_21_4.actions.InteractAction;
import me.aleksilassila.litematica.printer.v1_21_4.implementation.PrinterPlacementContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AirPlaceAction extends InteractAction {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public AirPlaceAction(PrinterPlacementContext context) {
        super(context);
    }

    @Override
    protected ActionResult interact(MinecraftClient client, ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.isInsideBlock() ? hitResult.getBlockPos() : hitResult.getBlockPos().offset(hitResult.getSide());
        BlockState currentState = mc.world.getBlockState(pos);

        // Allow placement in air or in fluids when REPLACE_FLUIDS_SOURCE_BLOCKS is enabled
        boolean canPlace = currentState.isAir() ||
                          (LitematicaMixinMod.REPLACE_FLUIDS_SOURCE_BLOCKS.getBooleanValue() &&
                           currentState.getBlock() instanceof FluidBlock);

        if (!canPlace) {
            return ActionResult.FAIL;
        }
        airPlace(pos);
        return ActionResult.PASS;
    }

    private void airPlace(BlockPos pos) {
        ClientPlayNetworkHandler connection = mc.getNetworkHandler();
        ClientPlayerInteractionManager interactionManager = mc.interactionManager;
        if (mc.player == null || connection == null || interactionManager == null) return;
        if (mc.player.getInventory().getStack(PlayerInventory.OFF_HAND_SLOT).getItem() instanceof BlockItem) {
            mc.interactionManager.clickSlot(0, 45, 0, SlotActionType.PICKUP, mc.player);
            mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.playerScreenHandler.syncId));
        }
        connection.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));

        Hand hand = Hand.OFF_HAND;
        BlockHitResult blockHitResult = new BlockHitResult(this.context.hitResult.getPos(), this.context.hitResult.getSide(), pos, true);
        interactionManager.interactBlock(mc.player, hand, blockHitResult);
        mc.player.swingHand(Hand.MAIN_HAND, false);
        connection.sendPacket(new HandSwingC2SPacket(hand));
        connection.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
    }
}
