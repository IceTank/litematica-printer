package me.aleksilassila.litematica.printer.v1_21.guides.placement;

import me.aleksilassila.litematica.printer.v1_21.SchematicBlockState;
import me.aleksilassila.litematica.printer.v1_21.config.PrinterConfig;
import net.minecraft.block.*;

public class BlockIndifferentGuesserGuide extends GuesserGuide {
    public BlockIndifferentGuesserGuide(SchematicBlockState state) {
        super(state);
    }

    @Override
    protected boolean statesEqual(BlockState resultState, BlockState targetState) {
        Block targetBlock = targetState.getBlock();
        Block resultBlock = resultState.getBlock();

        if (targetBlock instanceof BambooBlock) {
            return resultBlock instanceof BambooBlock || resultBlock instanceof BambooShootBlock;
        }

        if (targetBlock instanceof BigDripleafStemBlock) {
            if (resultBlock instanceof BigDripleafBlock || resultBlock instanceof BigDripleafStemBlock) {
                return resultState.get(HorizontalFacingBlock.FACING) == targetState.get(HorizontalFacingBlock.FACING);
            }
        }

        if (targetBlock instanceof TwistingVinesPlantBlock) {
            if (resultBlock instanceof TwistingVinesBlock) {
                return true;
            } else if (resultBlock instanceof TwistingVinesPlantBlock) {
                return statesEqualIgnoreProperties(resultState, targetState, TwistingVinesBlock.AGE);
            }
        }

        if (targetBlock instanceof TripwireBlock && resultBlock instanceof TripwireBlock) {
            return statesEqualIgnoreProperties(resultState, targetState,
                    TripwireBlock.ATTACHED, TripwireBlock.DISARMED, TripwireBlock.POWERED, TripwireBlock.NORTH,
                    TripwireBlock.EAST, TripwireBlock.SOUTH, TripwireBlock.WEST);
        }

        if (targetBlock instanceof MushroomBlock) {
            return statesEqualIgnoreProperties(resultState, targetState, MushroomBlock.DOWN, MushroomBlock.UP, MushroomBlock.WEST, MushroomBlock.NORTH, MushroomBlock.EAST, MushroomBlock.SOUTH);
        }

        if (targetBlock instanceof GlowLichenBlock && PrinterConfig.PRINTER_ALLOW_NONE_EXACT_STATES.getBooleanValue()) {
            return resultState.getBlock() == targetState.getBlock();
        }

        return super.statesEqual(resultState, targetState);
    }
}
