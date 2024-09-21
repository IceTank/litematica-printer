package me.aleksilassila.litematica.printer.v1_21.implementation.actions;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import me.aleksilassila.litematica.printer.v1_21.actions.InteractAction;
import me.aleksilassila.litematica.printer.v1_21.config.PrinterConfig;
import me.aleksilassila.litematica.printer.v1_21.implementation.PrinterPlacementContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class InteractActionImpl extends InteractAction {
    public InteractActionImpl(PrinterPlacementContext context) {
        super(context);
    }
    private final MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    protected ActionResult interact(MinecraftClient client, ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if(context.isAirPlace) {
            if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) System.out.println("InteractActionImpl.interact: attempting to air place block");
            airPlace(hitResult.getBlockPos().offset(hitResult.getSide()));
            return ActionResult.PASS;
        }

        ActionResult result = client.interactionManager.interactBlock(player, hand, hitResult);
        if (!result.isAccepted()) {
            if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) System.out.println("Failed to interact with block got " + result);
        }
        // client.interactionManager.interactItem(player, hand);
        // client.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        return result;
    }

    public boolean isInAir(BlockPos pos){
        if(mc.world == null ) return false;
        for(Direction dir : Direction.values()){
            if(!mc.world.getBlockState(pos.offset(dir)).isAir()){
                return false;
            }
        }
        return true;
    }
    private void airPlace(BlockPos pos){
        swap();
        place(pos);
        swapBack();
    }

    private void swap(){
        int ogSlot = mc.player.getInventory().getSlotWithStack(mc.player.getMainHandStack());
        mc.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(0,0,ogSlot + 36,0, SlotActionType.SWAP,mc.player.getMainHandStack(),new Int2ObjectArrayMap<>()));
        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(0));
    }

    private void swapBack(){
        int ogSlot = mc.player.getInventory().getSlotWithStack(mc.player.getMainHandStack());
        mc.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(0,0,ogSlot + 36,0,SlotActionType.SWAP,mc.player.getMainHandStack(),new Int2ObjectArrayMap<>()));
        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(ogSlot));
    }

    private void place(BlockPos pos){
        int slot = 36;
        ItemStack stack = mc.player.getMainHandStack();
        mc.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(0,0,slot,0,SlotActionType.PICKUP,stack,new Int2ObjectArrayMap<>()));
        mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(pos), Direction.DOWN, new BlockPos(pos), false), 0));
        mc.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(0,0,slot,0,SlotActionType.PICKUP,stack,new Int2ObjectArrayMap<>()));
        mc.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(0,0,slot,0,SlotActionType.PICKUP,stack,new Int2ObjectArrayMap<>()));
    }

}
