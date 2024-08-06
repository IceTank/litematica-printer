package me.aleksilassila.litematica.printer.v1_20_4.actions;

import me.aleksilassila.litematica.printer.v1_20_4.config.PrinterConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class ActionChain extends Action {
    List<Action> actions = new ArrayList<>();

    @Override
    public boolean send(MinecraftClient client, ClientPlayerEntity player) {
        for (Action action : actions) {
            if (!action.send(client, player)) {
                if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) {
                    System.out.println("ActionChain.send: Chain failed at " + action);
                }
                return false;
            }
        }
        return true;
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
