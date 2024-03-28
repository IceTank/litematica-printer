package me.aleksilassila.litematica.printer.v1_20_4;

import me.aleksilassila.litematica.printer.v1_20_4.actions.Action;
import me.aleksilassila.litematica.printer.v1_20_4.actions.ActionChain;
import me.aleksilassila.litematica.printer.v1_20_4.actions.PrepareAction;
import me.aleksilassila.litematica.printer.v1_20_4.config.PrinterConfig;
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
                nextAction.send(client, player);
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
