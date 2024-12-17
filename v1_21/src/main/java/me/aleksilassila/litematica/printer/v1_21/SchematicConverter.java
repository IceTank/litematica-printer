package me.aleksilassila.litematica.printer.v1_21;

import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.util.FileType;
import me.aleksilassila.litematica.printer.v1_21.mixin.LitematicaSchematicAccessor;

import java.io.File;

/**
 * @author IceTank
 * @since 17.12.2024
 */
public class SchematicConverter {
    public static LitematicaSchematic convertAndReturn(File file, File out) {
        LitematicaSchematic schematic = LitematicaSchematicAccessor.invokeConstructor(file, FileType.VANILLA_STRUCTURE);
        schematic.readFromFile();
        String fileName = file.getName().replace(".nbt", "");
        schematic.writeToFile(out, fileName, true);
        LitematicaSchematic newSchem = LitematicaSchematicAccessor.invokeConstructor(new File(out, fileName + ".litematic"), FileType.LITEMATICA_SCHEMATIC);
        newSchem.readFromFile();
        return newSchem;
    }
}
