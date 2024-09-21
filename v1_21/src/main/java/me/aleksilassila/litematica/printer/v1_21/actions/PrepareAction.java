package me.aleksilassila.litematica.printer.v1_21.actions;

import me.aleksilassila.litematica.printer.v1_21.Printer;
import me.aleksilassila.litematica.printer.v1_21.implementation.PrinterPlacementContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class PrepareAction extends Action {
//    public final Direction lookDirection;
//    public final boolean requireSneaking;
//    public final Item item;

//    public PrepareAction(Direction lookDirection, boolean requireSneaking, Item item) {
//        this.lookDirection = lookDirection;
//        this.requireSneaking = requireSneaking;
//        this.item = item;
//    }
//
//    public PrepareAction(Direction lookDirection, boolean requireSneaking, BlockState requiredState) {
//        this(lookDirection, requireSneaking, requiredState.getBlock().asItem());
//    }

    public final PrinterPlacementContext context;

    public boolean modifyYaw = true;
    public boolean modifyPitch = true;
    public float yaw = 0;
    public float pitch = 0;

    public PrepareAction(PrinterPlacementContext context) {
        this.context = context;

        @Nullable
        Direction lookDirection = context.lookDirection;

        if (lookDirection != null && lookDirection.getAxis().isHorizontal()) {
            this.yaw = lookDirection.asRotation();
        } else {
            this.modifyYaw = false;
        }

        if (lookDirection == Direction.UP) {
            this.pitch = -90;
        } else if (lookDirection == Direction.DOWN) {
            this.pitch = 90;
        } else if (lookDirection != null) {
            this.pitch = 0;
        } else {
            this.modifyPitch = false;
        }
    }

    public PrepareAction(PrinterPlacementContext context, float yaw, float pitch) {
        this.context = context;

        this.yaw = yaw;
        this.pitch = pitch;
    }

    static float[] getNeededRotations(ClientPlayerEntity player, Vec3d vec) {
        Vec3d eyesPos = player.getEyePos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double r = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
        double yaw = -Math.atan2(diffX, diffZ) / Math.PI * 180;

        double pitch = -Math.asin(diffY / r) / Math.PI * 180;

//        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
//
//        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
//        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
//        return new float[]{player.getYaw() + MathHelper.wrapDegrees(yaw - player.getYaw()), player.getPitch() + MathHelper.wrapDegrees(pitch - player.getPitch())
//        };
        return new float[]{(float) yaw, (float) pitch};
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    /**
     * Returns the yaw difference between two yaw values.
     * @param yaw1 The first yaw value.
     * @param yaw2 The second yaw value.
     * @return The yaw difference.
     */
    public static float deltaYaw (float yaw1, float yaw2) {
        final float PI_2 = (float) (Math.PI * 2);
        float dYaw = (yaw1 - yaw2) % PI_2;
        if (dYaw < -Math.PI) dYaw += PI_2;
        else if (dYaw > Math.PI) dYaw -= PI_2;

        return dYaw;
    }

    @Override
    public boolean send(MinecraftClient client, ClientPlayerEntity player) {
        ItemStack itemStack = context.getStack();

        if (!Printer.inventoryManager.select(itemStack)) {
            return false;
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
