package me.aleksilassila.litematica.printer.v1_20_4;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import me.aleksilassila.litematica.printer.v1_20_4.config.PrinterConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;

public class FreeLook {
    static FreeLook INSTANCE = null;
    float cameraYaw = 0;
    float cameraPitch = 0;
    Perspective prevPerspective = Perspective.FIRST_PERSON;
    MinecraftClient mc = MinecraftClient.getInstance();
    boolean enabled = false;
    int ticksSinceLastRotation = 0;

//    BooleanSetting changePers = addBooleanSetting("Change Perspective",true);
//    EnumSetting<Mode> cameraMode = addEnumSetting("Camera Mode",Mode.CAMERA);
//    FloatSetting sensitivity = addFloatSetting("Sensitivity",8,0,10);
    private FreeLook() {
        enabled = PrinterConfig.FREE_LOOK.getBooleanValue();
        PrinterConfig.FREE_LOOK.setValueChangeCallback(this::setEnabled);
    }

    public static FreeLook getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FreeLook();
        }
        return INSTANCE;
    }

    public void onGameTick() {
        if (shouldRotate() && ((ticksSinceLastRotation -1) == PrinterConfig.FREE_LOOK_LOOK_BACK.getIntegerValue() || PrinterConfig.FREE_LOOK_LOOK_BACK_ALWAYS_ROTATE_PLAYER.getBooleanValue())) {
            if (mc.player != null) {
                // Reset player rotation. The mouse mixin only rotates the player by a delta. So without this the player
                // rotation would be offset from the camera rotation.
                mc.player.setYaw(cameraYaw);
                mc.player.setPitch(cameraPitch);
            }
        }
        ticksSinceLastRotation++;
    }

    private void setEnabled(ConfigBoolean configBoolean) {
        this.enabled = configBoolean.getBooleanValue();
        if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) System.out.println("FreeLook: " + enabled);
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    void onEnable() {
        if(mc.player == null) {
            return;
        }
        this.enabled = true;
        if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) System.out.println("FreeLook: onEnable");

        cameraPitch = mc.player.getPitch();
        cameraYaw = mc.player.getYaw();
        prevPerspective = mc.options.getPerspective();

        if (PrinterConfig.FREE_LOOK_THIRD_PERSON.getBooleanValue()) {
            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        } else {
            mc.options.setPerspective(Perspective.FIRST_PERSON);
        }
    }

    void onDisable() {
        this.enabled = false;
        if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) System.out.println("FreeLook: onDisable");
        if (prevPerspective != null && mc.options.getPerspective() != prevPerspective /*&& changePers.getValue()*/) mc.options.setPerspective(prevPerspective);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean shouldRotate() {
        return enabled && PrinterConfig.FREE_LOOK_LOOK_BACK.getIntegerValue() != 0 && ticksSinceLastRotation > PrinterConfig.FREE_LOOK_LOOK_BACK.getIntegerValue();
    }

    public float getCameraYaw() {
        return cameraYaw;
    }

    public float getCameraPitch() {
        return cameraPitch;
    }

    public void setCameraYaw(float v) {
        cameraYaw = v;
    }

    public void setCameraPitch(float v) {
        cameraPitch = v;
    }

    public void setPrevPerspective(Perspective perspective) {
        prevPerspective = perspective;
    }

    public Perspective getPrevPerspective() {
        return prevPerspective;
    }
}
