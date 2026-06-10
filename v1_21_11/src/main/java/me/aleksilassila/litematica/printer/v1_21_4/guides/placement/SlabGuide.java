package me.aleksilassila.litematica.printer.v1_21_4.guides.placement;

import me.aleksilassila.litematica.printer.v1_21_4.SchematicBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.List;

public class SlabGuide extends GeneralPlacementGuide {
    public SlabGuide(SchematicBlockState state) {
        super(state);
    }

    @Override
    protected List<Direction> getPossibleSides() {
        return Arrays.stream(Direction.values())
                .filter(d -> d != (getRequiredHalf(state).getOpposite()) &&
                        getProperty(state.offset(d).currentState, SlabBlock.TYPE).orElse(SlabType.DOUBLE) == SlabType.DOUBLE)
                .toList();
    }

    private Direction getRequiredHalf(SchematicBlockState state) {
        BlockState targetState = state.targetState;
        BlockState currentState = state.currentState;

        if (!currentState.contains(SlabBlock.TYPE)) {
            return targetState.get(SlabBlock.TYPE) == SlabType.TOP ? Direction.UP : Direction.DOWN;
        } else if (currentState.get(SlabBlock.TYPE) != targetState.get(SlabBlock.TYPE)) {
            return currentState.get(SlabBlock.TYPE) == SlabType.TOP ? Direction.DOWN : Direction.UP;
        } else {
            return Direction.DOWN;
        }
    }
}
