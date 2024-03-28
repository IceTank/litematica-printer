package me.aleksilassila.litematica.printer.v1_20_4.config;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBoolean;
import me.aleksilassila.litematica.printer.v1_20_4.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_20_4.Printer;

public class PrinterPickBlockKeyCallback implements IHotkeyCallback {
    @Override
    public boolean onKeyAction(KeyAction action, IKeybind key) {
        LitematicaMixinMod.printer.onMiddleClick();
        return true;
    }
}
