package me.aleksilassila.litematica.printer.v1_21;

import me.aleksilassila.litematica.printer.v1_21.config.PrinterConfig;
import me.aleksilassila.litematica.printer.v1_21.mixin.MixinAccessorKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Vec3d;

public class MovementHandler {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean disableNextTick = false;

    public void onGameTick() {
        if (mc.player == null) return;

        // Disabled check
        if (!PrinterConfig.FREE_LOOK.getBooleanValue() || !LitematicaMixinMod.PRINT_MODE.getBooleanValue()) {
            if (disableNextTick) {
                disableNextTick = false;
                InputDirections.apply(InputDirections.getCurrentInput());
            }
            return;
        }

        // Disable in inventories
        if (mc.currentScreen != null) {
            if (PrinterConfig.MOVE_WHILE_IN_INVENTORY.getBooleanValue()) {
                if (mc.currentScreen instanceof CraftingScreen || mc.currentScreen instanceof CreativeInventoryScreen) {
                    return;
                }
            } else {
                return;
            }
        }

        // Get current inputs
        InputDirections currentInputDirection = InputDirections.getCurrentInput();

        float cameraYaw = mc.gameRenderer.getCamera().getYaw() % 360;
        if (currentInputDirection != InputDirections.NONE) {
            // lastPlayerInputDirection = currentInputDirection;
            // Calculate resulting control direction and apply them
            float inputYaw = currentInputDirection.getYaw();
            float playerYaw = (mc.player.getYaw() + 360) % 360;

            // Calculate the result yaw
            float playerRelativeYaw = inputYaw + playerYaw;
            float resultPlayerYaw = playerRelativeYaw - cameraYaw;
            InputDirections resultDirection = InputDirections.getDirection(resultPlayerYaw);
            if (resultDirection == null || resultDirection == InputDirections.NONE) {
                // ChatUtil.sendClientMessage("Result direction is null");
                Printer.logger.warn("Result direction is null / None");
                return;
            }

            InputDirections.apply(resultDirection);
        } else /*if (overwriteKeys.getValue())*/ {
//            Printer.logger.info("No input direction");
            InputDirections.apply(InputDirections.NONE);
        }
    }

    public void onDisable(ClientPlayerEntity player) {
        disableNextTick = true;
    }

    enum InputDirections {
        FORWARD(0),
        FORWARD_LEFT(45),
        FORWARD_RIGHT(315),
        LEFT(90),
        RIGHT(270),
        BACK(180),
        BACK_LEFT(135),
        BACK_RIGHT(225),
        NONE(-1);
        private final float yaw;
        InputDirections(float i) {
            this.yaw = i;
        }

        static InputDirections getDirection(float yaw) {
            while (yaw < 0) yaw += 360;
            yaw = yaw % 360;
            if (yaw < 22.5) return FORWARD;
            if (yaw < 67.5) return FORWARD_LEFT;
            if (yaw < 112.5) return LEFT;
            if (yaw < 157.5) return BACK_LEFT;
            if (yaw < 202.5) return BACK;
            if (yaw < 247.5) return BACK_RIGHT;
            if (yaw < 292.5) return RIGHT;
            if (yaw < 337.5) return FORWARD_RIGHT;
            return FORWARD;
        }

        /**
         * Returns a unit vector in the direction of the input direction
         * @return a unit vector in the direction of the input direction
         */
        public Vec3d getVec3d() {
            return new Vec3d(Math.sin(Math.toRadians(yaw)), 0, Math.cos(Math.toRadians(yaw)));
        }

        public float getYaw() {
            return yaw;
        }

        static InputDirections getCurrentInput() {
            if (isKeyPressed(mc.options.forwardKey)) {
                if (isKeyPressed(mc.options.leftKey)) {
                    return FORWARD_LEFT;
                } else if (isKeyPressed(mc.options.rightKey)) {
                    return FORWARD_RIGHT;
                } else {
                    return FORWARD;
                }
            } else if (isKeyPressed(mc.options.backKey)) {
                if (isKeyPressed(mc.options.leftKey)) {
                    return BACK_LEFT;
                } else if (isKeyPressed(mc.options.rightKey)) {
                    return BACK_RIGHT;
                } else {
                    return BACK;
                }
            } else if (isKeyPressed(mc.options.leftKey)) {
                return LEFT;
            } else if (isKeyPressed(mc.options.rightKey)) {
                return RIGHT;
            } else {
                return NONE;
            }
        }

        public boolean isPressed() {
            switch (this) {
                case FORWARD -> isKeyPressed(mc.options.forwardKey);
                case FORWARD_LEFT -> {
                    return isKeyPressed(mc.options.forwardKey) && isKeyPressed(mc.options.leftKey);
                }
                case FORWARD_RIGHT -> {
                    return isKeyPressed(mc.options.forwardKey) && isKeyPressed(mc.options.rightKey);
                }
                case LEFT -> isKeyPressed(mc.options.leftKey);
                case RIGHT -> isKeyPressed(mc.options.rightKey);
                case BACK -> isKeyPressed(mc.options.backKey);
                case BACK_LEFT -> {
                    return isKeyPressed(mc.options.backKey) && isKeyPressed(mc.options.leftKey);
                }
                case BACK_RIGHT -> {
                    return isKeyPressed(mc.options.backKey) && isKeyPressed(mc.options.rightKey);
                }
                default -> {
                    return false;
                }
            }
            return false;
        }

        static void apply(InputDirections direction) {
            mc.options.forwardKey.setPressed(false);
            mc.options.leftKey.setPressed(false);
            mc.options.rightKey.setPressed(false);
            mc.options.backKey.setPressed(false);
            switch (direction) {
                case FORWARD -> mc.options.forwardKey.setPressed(true);
                case FORWARD_LEFT -> {
                    mc.options.leftKey.setPressed(true);
                    mc.options.forwardKey.setPressed(true);
                }
                case FORWARD_RIGHT -> {
                    mc.options.rightKey.setPressed(true);
                    mc.options.forwardKey.setPressed(true);
                }
                case LEFT -> mc.options.leftKey.setPressed(true);
                case RIGHT -> mc.options.rightKey.setPressed(true);
                case BACK -> mc.options.backKey.setPressed(true);
                case BACK_LEFT -> {
                    mc.options.backKey.setPressed(true);
                    mc.options.leftKey.setPressed(true);
                }
                case BACK_RIGHT -> {
                    mc.options.backKey.setPressed(true);
                    mc.options.rightKey.setPressed(true);
                }
                case NONE -> {

                }
            }
        }
    }

    static boolean isKeyPressed(KeyBinding keyBinding) {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), ((MixinAccessorKeyBinding) keyBinding).getBoundKey().getCode());
    }
}
