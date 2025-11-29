package me.aleksilassila.litematica.printer.v1_21_4.guides.placement;

import me.aleksilassila.litematica.printer.v1_21_4.SchematicBlockState;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FacingBlockGuide extends SlabGuide {
    public FacingBlockGuide(SchematicBlockState state) {
        super(state);
    }

    @Override
    protected List<Direction> getPossibleSides() {
        Block block = state.targetState.getBlock();
        if (block instanceof WallSkullBlock || block instanceof WallSignBlock || block instanceof WallBannerBlock) {
            Optional<Direction> side = getProperty(state.targetState, Properties.HORIZONTAL_FACING).map(Direction::getOpposite);
            return side.map(Collections::singletonList).orElseGet(Collections::emptyList);
        }
        if (block instanceof StairsBlock) {
            Direction half = getRequiredHalf(state).getOpposite();
            return Arrays.stream(Direction.values()).filter(d -> d != half).toList();
        }

        return Arrays.stream(Direction.values()).toList();
    }

    @Override
    public boolean skipOtherGuides() {
        return true;
    }

    private Direction getRequiredHalf(SchematicBlockState state) {
        BlockState targetState = state.targetState;
        BlockState currentState = state.currentState;

        if (!currentState.contains(StairsBlock.HALF)) {
            return targetState.get(StairsBlock.HALF) == BlockHalf.TOP ? Direction.UP : Direction.DOWN;
        } else if (currentState.get(StairsBlock.HALF) != targetState.get(StairsBlock.HALF)) {
            return currentState.get(StairsBlock.HALF) == BlockHalf.TOP ? Direction.DOWN : Direction.UP;
        } else {
            return Direction.DOWN;
        }
    }
}
