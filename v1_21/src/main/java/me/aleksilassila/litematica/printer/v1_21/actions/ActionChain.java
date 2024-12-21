package me.aleksilassila.litematica.printer.v1_21.actions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class ActionChain extends Action {
    List<Action> actionsCurrentTick = new ArrayList<>();
    List<Action> actionsNextTick = new ArrayList<>();

    @Override
    public boolean send(MinecraftClient client, ClientPlayerEntity player) {
        throw new RuntimeException("ActionChain should not be sent. Manually send each preTick and postTick action.");
    }

    public void addImmediateAction(Action action) {
        actionsCurrentTick.add(action);
    }
    public void addNextTickAction(Action action) {
        actionsNextTick.add(action);
    }

    public void clear() {
        actionsCurrentTick.clear();
    }

    public List<Action> getActionsCurrentTick() {
        return actionsCurrentTick;
    }
    public List<Action> getActionsNextTick() {
        return actionsNextTick;
    }
}
