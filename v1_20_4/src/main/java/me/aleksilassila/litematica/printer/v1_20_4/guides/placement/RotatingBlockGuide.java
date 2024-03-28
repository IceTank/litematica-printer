package me.aleksilassila.litematica.printer.v1_20_4.guides.placement;

import me.aleksilassila.litematica.printer.v1_20_4.config.PrinterConfig;
import me.aleksilassila.litematica.printer.v1_20_4.SchematicBlockState;
import net.minecraft.block.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RotatingBlockGuide extends GeneralPlacementGuide {
    public RotatingBlockGuide(SchematicBlockState state) {
        super(state);
    }

    @Override
    protected List<Direction> getPossibleSides() {
        Block block = state.targetState.getBlock();
        if (block instanceof WallSkullBlock || block instanceof WallSignBlock || block instanceof WallBannerBlock) {
            Optional<Direction> side = getProperty(state.targetState, Properties.HORIZONTAL_FACING).map(Direction::getOpposite);
            return side.map(Collections::singletonList).orElseGet(Collections::emptyList);
        }

        return Collections.singletonList(Direction.DOWN);
    }

    @Override
    public boolean skipOtherGuides() {
        return true;
    }

    @Override
    public boolean canExecute(ClientPlayerEntity player) {
        if (!super.canExecute(player)) return false;

        if (PrinterConfig.PRINTER_IGNORE_ROTATION.getBooleanValue()) return true;

        int rotation = getProperty(state.targetState, Properties.ROTATION).orElse(0);
        if (targetState.getBlock() instanceof BannerBlock || targetState.getBlock() instanceof SignBlock) {
            rotation = (rotation + 8) % 16;
        }
        int distTo0 = rotation > 8 ? 16 - rotation : rotation;
        float yaw = Math.round(distTo0 / 8f * 180f * (rotation > 8 ? -1 : 1));

        Direction targetDirection = Direction.fromRotation(yaw);
        return player.getHorizontalFacing() == targetDirection;
    }
}
