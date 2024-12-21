package me.aleksilassila.litematica.printer.v1_20_4.implementation.actions;

import me.aleksilassila.litematica.printer.v1_20_4.actions.InteractAction;
import me.aleksilassila.litematica.printer.v1_20_4.config.PrinterConfig;
import me.aleksilassila.litematica.printer.v1_20_4.implementation.PrinterPlacementContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public class InteractActionImpl extends InteractAction {
    public InteractActionImpl(PrinterPlacementContext context) {
        super(context);
    }

    private final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    protected ActionResult interact(MinecraftClient client, ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        ActionResult result = client.interactionManager.interactBlock(player, hand, hitResult);
        if (!result.isAccepted()) {
            if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue())
                System.out.println("Failed to interact with block got " + result);
        }
        mc.player.swingHand(Hand.MAIN_HAND);
        return result;
    }
}
