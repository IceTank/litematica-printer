package me.aleksilassila.litematica.printer.v1_21.guides.placement;

import me.aleksilassila.litematica.printer.v1_21.SchematicBlockState;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

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

    protected Vec3d[] getPossibleHitVecs() {
        Vec3d[] parentVecs = super.getPossibleHitVecs();
        Block block = state.targetState.getBlock();
        if (!(block instanceof StairsBlock)) {
            return parentVecs;
        }

        Direction half = getRequiredHalf(state);

        return Arrays.stream(parentVecs).filter(vec -> half == Direction.DOWN ? vec.y <= 0 : vec.y > 0).toArray(Vec3d[]::new);
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
