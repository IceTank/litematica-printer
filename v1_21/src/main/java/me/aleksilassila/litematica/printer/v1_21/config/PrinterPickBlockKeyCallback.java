package me.aleksilassila.litematica.printer.v1_21.config;

import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import me.aleksilassila.litematica.printer.v1_21.LitematicaMixinMod;

public class PrinterPickBlockKeyCallback implements IHotkeyCallback {
    @Override
    public boolean onKeyAction(KeyAction action, IKeybind key) {
        LitematicaMixinMod.printer.onMiddleClick();
        return true;
    }
}
