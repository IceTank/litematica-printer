package me.aleksilassila.litematica.printer.v1_21_4.guides.placement;

import me.aleksilassila.litematica.printer.v1_21_4.SchematicBlockState;
import me.aleksilassila.litematica.printer.v1_21_4.config.PrinterConfig;
import net.minecraft.block.*;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import java.util.stream.Stream;

public class PropertySpecificGuesserGuide extends GuesserGuide {
    protected static Property<?>[] ignoredProperties = new Property[]{
            RepeaterBlock.DELAY,
            ComparatorBlock.MODE,
            RedstoneWireBlock.POWER,
            RedstoneWireBlock.WIRE_CONNECTION_EAST,
            RedstoneWireBlock.WIRE_CONNECTION_NORTH,
            RedstoneWireBlock.WIRE_CONNECTION_SOUTH,
            RedstoneWireBlock.WIRE_CONNECTION_WEST,
            Properties.POWERED,
            Properties.OPEN,
            PointedDripstoneBlock.THICKNESS,
            ScaffoldingBlock.DISTANCE,
            ScaffoldingBlock.BOTTOM,
            CactusBlock.AGE,
            BambooBlock.AGE,
            BambooBlock.LEAVES,
            BambooBlock.STAGE,
            SaplingBlock.STAGE,
            HorizontalConnectingBlock.EAST,
            HorizontalConnectingBlock.NORTH,
            HorizontalConnectingBlock.SOUTH,
            HorizontalConnectingBlock.WEST,
            SnowBlock.LAYERS,
            SeaPickleBlock.PICKLES,
            CandleBlock.CANDLES,
            EndPortalFrameBlock.EYE,
            Properties.LIT,
            LeavesBlock.DISTANCE,
            LeavesBlock.PERSISTENT,
            Properties.ATTACHED,
            Properties.NOTE,
            Properties.INSTRUMENT,
            Properties.EXTENDED,
            Properties.WEST_WALL_SHAPE,
            Properties.EAST_WALL_SHAPE,
            Properties.NORTH_WALL_SHAPE,
            Properties.SOUTH_WALL_SHAPE,
            Properties.ENABLED
    };

    public static Property<?>[] rotationProperties = new Property[]{
            Properties.ROTATION,
            Properties.HORIZONTAL_FACING,
            Properties.AXIS,
            Properties.HORIZONTAL_AXIS
    };

    public PropertySpecificGuesserGuide(SchematicBlockState state) {
        super(state);
    }

    @Override
    protected boolean statesEqual(BlockState resultState, BlockState targetState) {
        if (PrinterConfig.PRINTER_IGNORE_ROTATION.getBooleanValue()) {
            // Combine rotation properties with ignored properties
            return statesEqualIgnoreProperties(resultState, targetState, Stream.concat(
                    Stream.of(rotationProperties),
                    Stream.of(ignoredProperties)
            ).toArray(Property[]::new));
        }
        return statesEqualIgnoreProperties(resultState, targetState, ignoredProperties);
    }
}
