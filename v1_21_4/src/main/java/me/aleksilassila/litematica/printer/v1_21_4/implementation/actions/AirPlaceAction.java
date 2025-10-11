package me.aleksilassila.litematica.printer.v1_21_4.implementation.actions;

import me.aleksilassila.litematica.printer.v1_21_4.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_21_4.actions.InteractAction;
import me.aleksilassila.litematica.printer.v1_21_4.config.PrinterConfig;
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
import net.minecraft.util.math.Vec3d;

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
            if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) 
                System.out.println("InteractActionImpl.interact: block is not air and fluid replacement is disabled");
            return ActionResult.FAIL;
        }
        if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) System.out.println("InteractActionImpl.interact: attempting to air place block");
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
        // Build a synthetic hit result anchored on the target block position (inside-block) but preserving
        // the sampled side & hit vector for orientation. This avoids accidentally clicking a real neighbor
        // and placing a block one block ahead (issue seen with hoppers & slabs).
        BlockHitResult original = this.context.hitResult;
        BlockHitResult placeHit;
        if (original != null) {
            placeHit = new BlockHitResult(original.getPos(), original.getSide(), pos, true);
        } else {
            placeHit = new BlockHitResult(Vec3d.ofCenter(pos), Direction.DOWN, pos, true);
        }
        interactionManager.interactBlock(mc.player, hand, placeHit);
        mc.player.swingHand(Hand.MAIN_HAND, false);
        connection.sendPacket(new HandSwingC2SPacket(hand));
        connection.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
    }
}
