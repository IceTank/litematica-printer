package me.aleksilassila.litematica.printer.v1_21_4.actions;

import me.aleksilassila.litematica.printer.v1_21_4.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_21_4.Printer;
import me.aleksilassila.litematica.printer.v1_21_4.implementation.PrinterPlacementContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

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
        BlockPos pos = context.hitResult.isInsideBlock() ? context.hitResult.getBlockPos() : context.hitResult.getBlockPos().offset(context.hitResult.getSide());
        Printer.addTimeout(pos);
        return true;
    }

    @Override
    public String toString() {
        return "InteractAction{" +
                "context=" + context +
                '}';
    }
}
