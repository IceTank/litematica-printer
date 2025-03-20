package me.aleksilassila.litematica.printer.v1_21_4.actions;

import me.aleksilassila.litematica.printer.v1_21_4.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_21_4.config.PrinterConfig;
import me.aleksilassila.litematica.printer.v1_21_4.implementation.PrinterPlacementContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class PrepareLook extends Action {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public final PrinterPlacementContext context;
    public Optional<Float> yaw = Optional.empty();
    public Optional<Float> pitch = Optional.empty();

    public PrepareLook(PrinterPlacementContext context) {
        this.context = context;
    }

    static float[] getNeededRotations(ClientPlayerEntity player, Vec3d vec) {
        Vec3d eyesPos = player.getEyePos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double r = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
        double yaw = -Math.atan2(diffX, diffZ) / Math.PI * 180;

        double pitch = -Math.asin(diffY / r) / Math.PI * 180;

        return new float[]{(float) yaw, (float) pitch};
    }

    @Override
    public boolean send(MinecraftClient client, ClientPlayerEntity player) {
        if (context.canStealth) {
            float[] targetRot = getNeededRotations(player, context.getHitPos());

            if (PrinterConfig.ROTATE_PLAYER.getBooleanValue()) {
                LitematicaMixinMod.printer.rotate(targetRot[0], targetRot[1]);
            }

            this.yaw = Optional.of(targetRot[0]);
            this.pitch = Optional.of(targetRot[1]);
            if (PrinterConfig.PRINTER_GRIM_ROTATION.getBooleanValue()) {
                Vec3d playerPos = player.getPos();
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(playerPos.x, playerPos.y, playerPos.z, targetRot[0], targetRot[1], player.isOnGround(), player.horizontalCollision));
            }
        } else {
            float yaw = player.getYaw();
            float pitch = player.getPitch();

            if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue())
                System.out.println("Sending yaw for modified yaw: " + yaw + ", pitch: " + pitch);

            this.yaw = Optional.of(yaw);
            this.pitch = Optional.of(pitch);
            if (PrinterConfig.PRINTER_GRIM_ROTATION.getBooleanValue()) {
                Vec3d playerPos = player.getPos();
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(playerPos.x, playerPos.y, playerPos.z, yaw, pitch, player.isOnGround(), player.horizontalCollision));
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "PrepareAction{" +
                "context=" + context +
                '}';
    }
}
