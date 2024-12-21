package me.aleksilassila.litematica.printer.v1_20_4.actions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class PresShift extends Action {
    @Override
    public boolean send(MinecraftClient client, ClientPlayerEntity player) {
        player.input.sneaking = true;
//        player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        return true;
    }
}
