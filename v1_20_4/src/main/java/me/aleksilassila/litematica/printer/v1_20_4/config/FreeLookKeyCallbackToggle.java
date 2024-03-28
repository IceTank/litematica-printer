package me.aleksilassila.litematica.printer.v1_20_4.config;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBooleanConfigWithMessage;
import me.aleksilassila.litematica.printer.v1_20_4.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_20_4.Printer;
import net.minecraft.client.MinecraftClient;

public class FreeLookKeyCallbackToggle extends KeyCallbackToggleBooleanConfigWithMessage {
    MinecraftClient mc = MinecraftClient.getInstance();
    public FreeLookKeyCallbackToggle(ConfigBoolean config) {
        super(config);
    }

    @Override
    public boolean onKeyAction(KeyAction action, IKeybind key) {
        super.onKeyAction(action, key);

        if (!config.getBooleanValue() && mc.player != null) {
            LitematicaMixinMod.movementHandler.onDisable(mc.player);
        }

        return true;
    }
}
