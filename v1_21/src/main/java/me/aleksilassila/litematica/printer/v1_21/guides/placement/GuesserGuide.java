package me.aleksilassila.litematica.printer.v1_21.guides.placement;

import me.aleksilassila.litematica.printer.v1_21.SchematicBlockState;
import me.aleksilassila.litematica.printer.v1_21.implementation.PrinterPlacementContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * This is the placement guide that most blocks will use.
 * It will try to predict the correct player state for producing the right blockState
 * by brute forcing the correct hit vector and look direction.
 */
public class GuesserGuide extends GeneralPlacementGuide {
    private PrinterPlacementContext contextCache = null;
    private final MinecraftClient mc = MinecraftClient.getInstance();

    protected static Direction[] directionsToTry = new Direction[]{
            Direction.NORTH,
            Direction.SOUTH,
            Direction.EAST,
            Direction.WEST,
            Direction.UP,
            Direction.DOWN
    };
    protected static Vec3d[] hitVecsToTry = new Vec3d[]{
            new Vec3d(-0.25, -0.25, -0.25),
            new Vec3d(+0.25, -0.25, -0.25),
            new Vec3d(-0.25, +0.25, -0.25),
            new Vec3d(-0.25, -0.25, +0.25),
            new Vec3d(+0.25, +0.25, -0.25),
            new Vec3d(-0.25, +0.25, +0.25),
            new Vec3d(+0.25, -0.25, +0.25),
            new Vec3d(+0.25, +0.25, +0.25)
    };

    protected static Vec3d[] carpetHitVecsToTry = new Vec3d[]{
            new Vec3d(-0.25, -0.25, -0.25),
            new Vec3d(+0.25, -0.25, -0.25),
            new Vec3d(-0.25, +0.25, -0.25),
            new Vec3d(-0.25, -0.25, +0.25),
            new Vec3d(+0.25, +0.25, -0.25),
            new Vec3d(-0.25, +0.25, +0.25),
            new Vec3d(+0.25, -0.25, +0.25),
            new Vec3d(+0.25, +0.25, +0.25),
            new Vec3d(-0.25, -0.49, -0.25), // 1/4 Just above the lower edge of a block. For carpets for instance as they are very thin
            new Vec3d(+0.25, -0.49, -0.25), // 2/4
            new Vec3d(-0.25, -0.49, +0.25), // 3/4
            new Vec3d(+0.25, -0.49, +0.25) // 4/4
    };

    public GuesserGuide(SchematicBlockState state) {
        super(state);
    }

    private boolean canSeeBlockFace(ClientPlayerEntity player, BlockHitResult hitResult) {
        // Draw a line between the player pos and the block pos and check if the block side is visible
        // BlockPos targetPos = state.blockPos;

        Direction side = hitResult.getSide();
        BlockPos blockPos = hitResult.getBlockPos();
        Vec3d playerEyePos = player.getEyePos();
        switch (side) {
            case UP:
                if (blockPos.getY() + 1 > playerEyePos.getY()) return false;
                break;
            case DOWN:
                if (blockPos.getY() < playerEyePos.getY()) return false;
                break;
            case NORTH:
                if (blockPos.getZ() < playerEyePos.getZ()) return false;
                break;
            case SOUTH:
                if (blockPos.getZ() + 1 > playerEyePos.getZ()) return false;
                break;
            case EAST:
                if (blockPos.getX() + 1 > playerEyePos.getX()) return false;
                break;
            case WEST:
                if (blockPos.getX() < playerEyePos.getX()) return false;
                break;
        }
        return true;

//        Direction side = hitResult.getSide(); // The side of the block we are placing on
//        BlockPos pos = hitResult.getBlockPos(); // Neighbor block pos
//
//        Vec3d hitLocation = Vec3d.ofCenter(pos).add(Vec3d.of(side.getVector()).multiply(0.5)); // Center of the block side face we are placing on
//
//        Vec3d lookVector = hitLocation.subtract(player.getEyePos());
//        return lookVector.dotProduct(Vec3d.of(side.getVector())) > PrinterConfig.MIN_BLOCK_HIT_ANGLE.getDoubleValue(); // If the dot product is negative, the block side is not visible
    }

    private boolean correctChestPlacement(BlockState targetState, BlockState result) {
        if (targetState.contains(ChestBlock.CHEST_TYPE) && result.contains(ChestBlock.CHEST_TYPE) && result.get(ChestBlock.FACING) == targetState.get(ChestBlock.FACING)) {
            ChestType targetChestType = targetState.get(ChestBlock.CHEST_TYPE);
            ChestType resultChestType = result.get(ChestBlock.CHEST_TYPE);

            return targetChestType != ChestType.SINGLE && resultChestType == ChestType.SINGLE;
        }

        return false;
    }
}
