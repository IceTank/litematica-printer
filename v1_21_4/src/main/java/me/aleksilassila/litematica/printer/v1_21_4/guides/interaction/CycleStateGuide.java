package me.aleksilassila.litematica.printer.v1_21_4.guides.interaction;

import me.aleksilassila.litematica.printer.v1_21_4.SchematicBlockState;
import net.minecraft.block.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class CycleStateGuide extends InteractionGuide {
    private static final Property<?>[] propertiesToIgnore = new Property[]{
            Properties.POWERED,
            Properties.LIT,
            Properties.BLOCK_FACE,
            Properties.FACING,
            Properties.LOCKED,
            Properties.BLOCK_HALF,
            Properties.DOOR_HINGE,
            Properties.IN_WALL
    };

    public CycleStateGuide(SchematicBlockState state) {
        super(state);
    }

    @Override
    public boolean canExecute(ClientPlayerEntity player) {
        if (!playerHasRightItem(player)) return false;

        if (currentState.getBlock() == Blocks.IRON_TRAPDOOR) {
            return false; // Iron trapdoors cannot be toggled by interaction
        }

        if (currentState.getBlock() != targetState.getBlock()) {
            return false; // Different blocks cannot be toggled
        }

        BlockState targetState = state.targetState;
        BlockState currentState = state.currentState;

        if (currentState.getBlock() == Blocks.LEVER) {
            if (currentState.get(LeverBlock.POWERED) == targetState.get(LeverBlock.POWERED)) {
                return false; // Lever blocks must be toggled if POWERED property is incorrect, regardless of other properties
            }
            return true;
        }
        return !statesEqualIgnoreProperties(targetState, currentState, propertiesToIgnore);
    }

    @Override
    protected @NotNull List<ItemStack> getRequiredItems() {
        return Collections.singletonList(ItemStack.EMPTY);
    }
}
