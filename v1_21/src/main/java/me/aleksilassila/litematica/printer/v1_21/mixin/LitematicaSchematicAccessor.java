package me.aleksilassila.litematica.printer.v1_21.mixin;

import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.util.FileType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.File;

/**
 * @author IceTank
 * @since 17.12.2024
 */
@Mixin(LitematicaSchematic.class)
public interface LitematicaSchematicAccessor {
    @Invoker("<init>")
    static LitematicaSchematic invokeConstructor(File file, FileType fileType) {
        throw new AssertionError();
    }
}
