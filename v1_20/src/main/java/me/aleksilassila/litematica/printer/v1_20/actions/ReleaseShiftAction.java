package me.aleksilassila.litematica.printer.v1_20.actions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class ReleaseShiftAction extends Action {
    public ReleaseShiftAction() {
        isSync = true;
    }
    @Override
    public void send(MinecraftClient client, ClientPlayerEntity player) {
        player.input.sneaking = false;
        player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
    }
}
