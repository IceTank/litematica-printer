package me.aleksilassila.litematica.printer.v1_21.config;

import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;

public class PrinterInputHandler implements IKeybindProvider {
    static PrinterInputHandler INSTANCE;

    public static PrinterInputHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PrinterInputHandler();
        }
        return INSTANCE;
    }
    @Override
    public void addKeysToMap(IKeybindManager manager) {
        manager.addKeybindToMap(PrinterConfig.FREE_LOOK_TOGGLE.getKeybind());
        manager.addKeybindToMap(PrinterConfig.PRINTER_PICK_BLOCK.getKeybind());
    }

    @Override
    public void addHotkeys(IKeybindManager manager) {

    }
}
