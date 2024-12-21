package me.aleksilassila.litematica.printer.v1_21.actions;

import me.aleksilassila.litematica.printer.v1_21.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_21.Printer;
import me.aleksilassila.litematica.printer.v1_21.implementation.PrinterPlacementContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

abstract public class InteractAction extends Action {
    public final PrinterPlacementContext context;

    public InteractAction(PrinterPlacementContext context) {
        this.context = context;
    }

    protected abstract ActionResult interact(MinecraftClient client, ClientPlayerEntity player, Hand hand, BlockHitResult hitResult);

    @Override
    public boolean send(MinecraftClient client, ClientPlayerEntity player) {
        interact(client, player, Hand.MAIN_HAND, context.hitResult);

        if (LitematicaMixinMod.DEBUG)
            System.out.println("InteractAction.send: Blockpos: " + context.getBlockPos() + " Side: " + context.getSide() + " HitPos: " + context.getHitPos());
        if (context.isAirPlace) {
            Printer.addTimeout(context.getBlockPos().offset(context.getSide()));
        } else {
            Printer.addTimeout(context.getBlockPos());
        }
        return true;
    }

    @Override
    public String toString() {
        return "InteractAction{" +
                "context=" + context +
                '}';
    }
}
