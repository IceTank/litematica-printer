package me.aleksilassila.litematica.printer.v1_21.actions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public abstract class Action {
    abstract public boolean send(MinecraftClient client, ClientPlayerEntity player);
}
