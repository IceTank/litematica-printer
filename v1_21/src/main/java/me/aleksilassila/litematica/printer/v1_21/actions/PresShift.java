package me.aleksilassila.litematica.printer.v1_21.actions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class PresShift extends Action {
    @Override
    public boolean send(MinecraftClient client, ClientPlayerEntity player) {
        player.input.sneaking = true;
//        player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        return true;
    }
}
