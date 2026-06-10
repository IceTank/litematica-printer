package me.aleksilassila.litematica.printer.v1_21_4.actions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.util.PlayerInput;

public class ReleaseShiftAction extends Action {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public boolean send(MinecraftClient client, ClientPlayerEntity player) {
        player.input.playerInput = new PlayerInput(player.input.playerInput.forward(),
                player.input.playerInput.backward(), player.input.playerInput.left(), player.input.playerInput.right(),
                player.input.playerInput.jump(), mc.options.sneakKey.isPressed(), player.input.playerInput.sprint());
        player.networkHandler.sendPacket(new PlayerInputC2SPacket(player.input.playerInput));
        return true;
    }
}
