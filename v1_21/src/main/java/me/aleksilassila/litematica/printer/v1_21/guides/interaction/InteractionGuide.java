package me.aleksilassila.litematica.printer.v1_21.guides.interaction;

import me.aleksilassila.litematica.printer.v1_21.actions.ActionChain;
import me.aleksilassila.litematica.printer.v1_21.implementation.PrinterPlacementContext;
import me.aleksilassila.litematica.printer.v1_21.SchematicBlockState;
import me.aleksilassila.litematica.printer.v1_21.actions.Action;
import me.aleksilassila.litematica.printer.v1_21.actions.PrepareAction;
import me.aleksilassila.litematica.printer.v1_21.actions.ReleaseShiftAction;
import me.aleksilassila.litematica.printer.v1_21.guides.Guide;
import me.aleksilassila.litematica.printer.v1_21.implementation.actions.InteractActionImpl;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A guide that clicks the current block to change its state.
 */
public abstract class InteractionGuide extends Guide {
    public InteractionGuide(SchematicBlockState state) {
        super(state);
    }

    @Override
    public @NotNull List<Action> execute(ClientPlayerEntity player) {
        List<Action> actions = new ArrayList<>();

        BlockHitResult hitResult = new BlockHitResult(Vec3d.ofCenter(state.blockPos), Direction.UP, state.blockPos, false);
        ItemStack requiredItem = getRequiredItem(player).stream().findFirst().orElse(ItemStack.EMPTY);
        int requiredSlot = getRequiredItemStackSlot(player);

        if (requiredSlot == -1) return actions;

        PrinterPlacementContext ctx = new PrinterPlacementContext(player, hitResult, requiredItem, requiredSlot);

        ActionChain chain = new ActionChain();

        chain.addImmediateAction(new ReleaseShiftAction());
        chain.addImmediateAction(new PrepareAction(ctx));
        chain.addImmediateAction(new InteractActionImpl(ctx));

        actions.add(chain);

        return actions;
    }

    @Override
    abstract protected @NotNull List<ItemStack> getRequiredItems();
}
