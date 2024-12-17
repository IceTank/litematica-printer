package me.aleksilassila.litematica.printer.v1_21.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase;
import fi.dy.masa.malilib.util.InfoUtils;
import me.aleksilassila.litematica.printer.v1_21.SchematicConverter;
import me.aleksilassila.litematica.printer.v1_21.config.PrinterConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author IceTank
 * @since 17.12.2024
 */
@Mixin(targets = "fi.dy.masa.litematica.gui.GuiSchematicLoad$ButtonListener", remap = false)
public class ButtonListenerMixin {

    @Inject(method = "actionPerformedWithButton",
            at = @At(
                    target = "Lfi/dy/masa/litematica/data/SchematicHolder;addSchematic(Lfi/dy/masa/litematica/schematic/LitematicaSchematic;Z)V",
                    value = "INVOKE", shift = At.Shift.BEFORE)
    )
    private void onActionPerformedWithButton(ButtonBase button, int mouseButton, CallbackInfo ci,
                                             @Local LocalBooleanRef warnType, @Local LocalRef<LitematicaSchematic> schematic,
                                             @Local WidgetFileBrowserBase.DirectoryEntry entry) {
        if (!warnType.get() || !PrinterConfig.AUTO_CONVERT_SCHEMATIC_TO_LITEMATIC_ON_LOAD.getBooleanValue()) {
            return;
        }
        LitematicaSchematic newSchem = SchematicConverter.convertAndReturn(schematic.get().getFile(), entry.getDirectory());
        warnType.set(false);
        schematic.set(newSchem);
        InfoUtils.showGuiOrInGameMessage(Message.MessageType.INFO, 15000, "Auto converted schematic to litematic format");
    }
}
