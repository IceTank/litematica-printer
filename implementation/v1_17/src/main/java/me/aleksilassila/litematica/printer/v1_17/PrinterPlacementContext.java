package me.aleksilassila.litematica.printer.v1_17;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class PrinterPlacementContext extends ItemPlacementContext {
    public final @Nullable Direction lookDirection;
    public final boolean requiresSneaking;
    public final BlockHitResult hitResult;
    public final int requiredItemSlot;

    public PrinterPlacementContext(PlayerEntity player, BlockHitResult hitResult, ItemStack requiredItem, int requiredItemSlot) {
        this(player, hitResult, requiredItem, requiredItemSlot, null, false);
    }

    public PrinterPlacementContext(PlayerEntity player, BlockHitResult hitResult, ItemStack requiredItem, int requiredItemSlot, @Nullable Direction lookDirection, boolean requiresSneaking) {
        super(player, Hand.MAIN_HAND, requiredItem, hitResult);

        this.lookDirection = lookDirection;
        this.requiresSneaking = requiresSneaking;
        this.hitResult = hitResult;
        this.requiredItemSlot = requiredItemSlot;
    }

    @Override
    public Direction getPlayerLookDirection() {
        return lookDirection == null ? super.getPlayerLookDirection() : lookDirection;
    }

    @Override
    public Direction getVerticalPlayerLookDirection() {
        if (lookDirection != null && lookDirection.getOpposite() == super.getVerticalPlayerLookDirection())
            return lookDirection;
        return super.getVerticalPlayerLookDirection();
    }

    @Override
    public Direction getPlayerFacing() {
        if (lookDirection == null || !lookDirection.getAxis().isHorizontal()) return super.getPlayerFacing();

        return lookDirection;
    }

    @Override
    public String toString() {
        return "PrinterPlacementContext{" +
                "lookDirection=" + lookDirection +
                ", requiresSneaking=" + requiresSneaking +
                ", blockPos=" + hitResult.getBlockPos() +
                ", side=" + hitResult.getSide() +
//                ", hitVec=" + hitResult +
                '}';
    }
}
