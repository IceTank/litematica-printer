package me.aleksilassila.litematica.printer.v1_20_4.implementation.actions;

import me.aleksilassila.litematica.printer.v1_20_4.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_20_4.implementation.PrinterPlacementContext;
import me.aleksilassila.litematica.printer.v1_20_4.actions.InteractAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import org.apache.commons.lang3.mutable.MutableObject;

public class InteractActionImpl extends InteractAction {
    public InteractActionImpl(PrinterPlacementContext context) {
        super(context);
    }

    @Override
    protected ActionResult interact(MinecraftClient client, ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        ActionResult result = client.interactionManager.interactBlock(player, hand, hitResult);
        if (!result.isAccepted()) {
            if (LitematicaMixinMod.DEBUG) MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("Failed to interact with block got " + result));
        }
        // client.interactionManager.interactItem(player, hand);
        // client.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        return result;
    }
}
