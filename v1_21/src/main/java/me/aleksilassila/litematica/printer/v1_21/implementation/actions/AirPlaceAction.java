package me.aleksilassila.litematica.printer.v1_21.implementation.actions;

import me.aleksilassila.litematica.printer.v1_21.actions.InteractAction;
import me.aleksilassila.litematica.printer.v1_21.config.PrinterConfig;
import me.aleksilassila.litematica.printer.v1_21.implementation.PrinterPlacementContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AirPlaceAction extends InteractAction {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public AirPlaceAction(PrinterPlacementContext context) {
        super(context);
    }

    @Override
    protected ActionResult interact(MinecraftClient client, ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (!mc.world.getBlockState(hitResult.getBlockPos().offset(hitResult.getSide())).isAir()) {
            if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) System.out.println("InteractActionImpl.interact: block is not air");
            return ActionResult.FAIL;
        }
        if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) System.out.println("InteractActionImpl.interact: attempting to air place block");
        airPlace(hitResult.getBlockPos().offset(hitResult.getSide()));
        return ActionResult.PASS;
    }

    private void airPlace(BlockPos pos) {
        ClientPlayNetworkHandler connection = mc.getNetworkHandler();
        ClientPlayerInteractionManager interactionManager = mc.interactionManager;
        if (mc.player == null || connection == null || interactionManager == null) return;
        connection.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.UP));

        Hand hand = Hand.OFF_HAND;

        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, true);
        interactionManager.interactBlock(mc.player, hand, hit);
        mc.player.swingHand(Hand.MAIN_HAND, false);
        connection.sendPacket(new HandSwingC2SPacket(hand));
        connection.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.UP));
    }
}
