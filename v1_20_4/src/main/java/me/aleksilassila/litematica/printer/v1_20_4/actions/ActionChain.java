package me.aleksilassila.litematica.printer.v1_20_4.actions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class ActionChain extends Action {
    List<Action> actions = new ArrayList<>();

    @Override
    public void send(MinecraftClient client, ClientPlayerEntity player) {
        for (Action action : actions) {
            action.send(client, player);
        }
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public void clear() {
        actions.clear();
    }

    public List<Action> getActions() {
        return actions;
    }
}
