package me.aleksilassila.litematica.printer.v1_21_4.guides.placement;

import me.aleksilassila.litematica.printer.v1_21_4.SchematicBlockState;
import me.aleksilassila.litematica.printer.v1_21_4.config.PrinterConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Direction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TorchGuide extends GuesserGuide {
    public TorchGuide(SchematicBlockState state) {
        super(state);
    }

    @Override
    protected List<Direction> getPossibleSides() {
        // Prefer the wall-mounted FACING for wall torches; fallback to horizontal facing
        Optional<Direction> facing = getProperty(targetState, WallMountedBlock.FACING);
        if (facing.isEmpty()) facing = getProperty(targetState, HorizontalFacingBlock.FACING);

        return facing
                .map(direction -> Collections.singletonList(direction.getOpposite()))
                .orElseGet(() -> Collections.singletonList(Direction.DOWN));
    }

    @Override
    protected Optional<Block> getRequiredItemAsBlock(ClientPlayerEntity player) {
        return Optional.of(state.targetState.getBlock());
    }

    @Override
    protected boolean statesEqual(BlockState state1, BlockState state2) {
        // Redstone torches use LIT which can be false if powered; ignore it to allow placement
        if (PrinterConfig.PRINTER_IGNORE_ROTATION.getBooleanValue()) {
            var rotationProps = PropertySpecificGuesserGuide.rotationProperties;
            // Merge rotation props with LIT
            net.minecraft.state.property.Property<?>[] merged = new net.minecraft.state.property.Property<?>[rotationProps.length + 1];
            System.arraycopy(rotationProps, 0, merged, 0, rotationProps.length);
            merged[rotationProps.length] = Properties.LIT;
            return statesEqualIgnoreProperties(state1, state2, merged);
        } else {
            return statesEqualIgnoreProperties(state1, state2, Properties.LIT);
        }
    }
}
