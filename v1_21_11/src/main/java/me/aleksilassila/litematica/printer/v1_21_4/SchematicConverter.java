package me.aleksilassila.litematica.printer.v1_21_4;

import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.util.FileType;
import me.aleksilassila.litematica.printer.v1_21_4.mixin.LitematicaSchematicAccessor;

import java.io.File;
import java.nio.file.Path;

/**
 * @author IceTank
 * @since 17.12.2024
 */
public class SchematicConverter {
    public static LitematicaSchematic convertAndReturn(File file, File out) {
        LitematicaSchematic schematic = LitematicaSchematicAccessor.invokeConstructor(file.toPath(), FileType.VANILLA_STRUCTURE);
        schematic.readFromFile();
        String fileName = file.getName().replace(".nbt", "");
        schematic.writeToFile(out.toPath(), fileName, true);
        LitematicaSchematic newSchem = LitematicaSchematicAccessor.invokeConstructor(out.toPath().resolve(fileName + ".litematic"), FileType.LITEMATICA_SCHEMATIC);
        newSchem.readFromFile();
        return newSchem;
    }
}
