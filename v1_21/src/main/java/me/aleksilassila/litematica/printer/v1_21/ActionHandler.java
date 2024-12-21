package me.aleksilassila.litematica.printer.v1_21;

import me.aleksilassila.litematica.printer.v1_21.actions.Action;
import me.aleksilassila.litematica.printer.v1_21.actions.ActionChain;
import me.aleksilassila.litematica.printer.v1_21.actions.PrepareAction;
import me.aleksilassila.litematica.printer.v1_21.config.PrinterConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.LinkedList;
import java.util.Queue;

public class ActionHandler {
    private final MinecraftClient client;
    private final ClientPlayerEntity player;

    private final Queue<Action> currentTickActions = new LinkedList<>();
    private final Queue<Action> previousTickActions = new LinkedList<>();
    public PrepareAction lookAction = null;

    public ActionHandler(MinecraftClient client, ClientPlayerEntity player) {
        this.client = client;
        this.player = player;
    }

    public void processCurrentTickActions() {
        Action nextAction = currentTickActions.poll();
        while (nextAction != null) {
            if (LitematicaMixinMod.DEBUG) {
                System.out.println("Sending action " + nextAction);
            }
            boolean success = nextAction.send(client, player);
            if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) {
                if (success) {
                    System.out.println("Action (Pre) success " + nextAction);
                } else {
                    System.out.println("Action (Pre) failed " + nextAction);
                }
            }
            nextAction = currentTickActions.poll();
        }
        Printer.inactivityCounter = 0;
    }

    public void processPreviousTickActions() {
        // Hours spent figuring this out: 6
        Action nextAction = previousTickActions.poll();
        while (nextAction != null) {
            if (LitematicaMixinMod.DEBUG) {
                System.out.println("Sending action " + nextAction);
            }
            boolean success = nextAction.send(client, player);
            if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) {
                if (success) {
                    System.out.println("Action (Post Tick) success " + nextAction);
                } else {
                    System.out.println("Action (Post Tick) failed " + nextAction);
                }
            }
            nextAction = previousTickActions.poll();
        }
    }

    public boolean acceptsActions() {
        return currentTickActions.isEmpty() && previousTickActions.isEmpty();
    }

    public void addActions(Action... actions) {
        if (!acceptsActions()) return;

        for (Action action : actions) {
            if (action instanceof PrepareAction)
                lookAction = (PrepareAction) action;
        }

        for (Action action : actions) {
            if (action instanceof ActionChain chain) {
                currentTickActions.addAll(chain.getActionsCurrentTick());
                previousTickActions.addAll(chain.getActionsNextTick());
            } else {
                currentTickActions.add(action);
            }
        }
    }
}
