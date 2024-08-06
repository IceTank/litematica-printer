package me.aleksilassila.litematica.printer.v1_20_4.actions;

import me.aleksilassila.litematica.printer.v1_20_4.config.PrinterConfig;
import me.aleksilassila.litematica.printer.v1_20_4.implementation.PrinterPlacementContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class PostAction extends PrepareAction {
    public PostAction(PrinterPlacementContext context, ClientPlayerEntity player) {
        super(context);
        this.pitch = player.getPitch();
        this.yaw = player.getYaw();
    }

    @Override
    public boolean send(MinecraftClient client, ClientPlayerEntity player) {
        if (context.canStealth) {
            PlayerMoveC2SPacket.LookAndOnGround packet = new PlayerMoveC2SPacket.LookAndOnGround(this.yaw, this.pitch, player.isOnGround());

            if (PrinterConfig.ROTATE_PLAYER.getBooleanValue()) {
                player.setYaw(this.yaw);
                player.setPitch(this.pitch);
            }

            player.networkHandler.sendPacket(packet);
        }
        return true;
    }
}
