package me.aleksilassila.litematica.printer.v1_21;

import me.aleksilassila.litematica.printer.v1_21.actions.Action;
import me.aleksilassila.litematica.printer.v1_21.actions.ActionChain;
import me.aleksilassila.litematica.printer.v1_21.actions.InteractAction;
import me.aleksilassila.litematica.printer.v1_21.actions.PrepareAction;
import me.aleksilassila.litematica.printer.v1_21.config.PrinterConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ActionHandler {
    private final MinecraftClient client;
    private final ClientPlayerEntity player;

    private final Queue<Action> actionQueue = new LinkedList<>();
    public PrepareAction lookAction = null;

    public ActionHandler(MinecraftClient client, ClientPlayerEntity player) {
        this.client = client;
        this.player = player;
    }

    private int tick = 0;

    public void onGameTick() {
        int tickRate = PrinterConfig.TICK_DELAY.getIntegerValue();
        if (tickRate != 0 && tick % tickRate != 0) {
            tick++;
            actionQueue.clear();
            return;
        }

        boolean actionTaken = false;
        while (!actionTaken) {
            Action nextAction = actionQueue.poll();

            if (nextAction != null) {
                if (LitematicaMixinMod.DEBUG) System.out.println("Sending action " + nextAction);
                // System.out.println("Sending action " + nextAction);
                if (nextAction.send(client, player)) {
                    if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) {
                        System.out.println("Action " + nextAction + " was successful");
                    }
                    List<Action> runActions = nextAction instanceof ActionChain chain ? chain.getActions() : List.of(nextAction);
                    for (Action action : runActions) {
                        if (action instanceof InteractAction interactAction) {
                            if (interactAction.context.isAirPlace) {
                                Printer.addTimeout(interactAction.context.getBlockPos().offset(interactAction.context.getSide()));
                            } else {
                                Printer.addTimeout(interactAction.context.getBlockPos());
                            }
                            if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) {
                                if (interactAction.context.isAirPlace) {
                                    System.out.println("InteractActionImpl.interact: attempting to air place block");
                                } else {
                                    System.out.println("None airplace action finished");
                                }
                            }
                        }
                    }
                }
                Printer.inactivityCounter = 0;
            } else {
                lookAction = null;
                tick++;
                actionTaken = true;
            }
        }
        if (tickRate != 0) {
            tick %= tickRate;
        } else {
            tick = 0;
        }
        actionQueue.clear();
    }

    public boolean acceptsActions() {
        return actionQueue.isEmpty();
    }

    public void addActions(Action... actions) {
        if (!acceptsActions()) return;

        for (Action action : actions) {
            if (action instanceof PrepareAction)
                lookAction = (PrepareAction) action;
        }

        actionQueue.addAll(List.of(actions));
    }

    public void addActions(ActionChain... actionChains) {
        if (!acceptsActions()) return;

        for (ActionChain actionChain : actionChains) {
            actionQueue.addAll(actionChain.getActions());
        }
    }
}
