package me.aleksilassila.litematica.printer.v1_20_4.actions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class ReleaseShiftAction extends Action {
    @Override
    public boolean send(MinecraftClient client, ClientPlayerEntity player) {
        player.input.sneaking = false;
//        player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        return true;
    }
}
