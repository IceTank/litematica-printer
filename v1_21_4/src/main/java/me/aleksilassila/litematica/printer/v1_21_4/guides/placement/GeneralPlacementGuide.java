package me.aleksilassila.litematica.printer.v1_21_4.guides.placement;

import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;
import me.aleksilassila.litematica.printer.v1_21_4.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_21_4.Printer;
import me.aleksilassila.litematica.printer.v1_21_4.SchematicBlockState;
import me.aleksilassila.litematica.printer.v1_21_4.config.PrinterConfig;
import me.aleksilassila.litematica.printer.v1_21_4.implementation.PrinterPlacementContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * An old school guide where there are defined specific conditions
 * for player state depending on the block being placed.
 */
public class GeneralPlacementGuide extends PlacementGuide {
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

    protected static Vec3d[] extendedHitVecs = new Vec3d[]{
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
    private PrinterPlacementContext contextCache = null;

    public GeneralPlacementGuide(SchematicBlockState state) {
        super(state);
    }

    protected List<Direction> getPossibleSides() {
        return Arrays.asList(Direction.values());
    }

    protected Optional<Direction> getLookDirection() {
        return Optional.empty();
    }

    protected boolean getRequiresSupport() {
        return false;
    }

    protected boolean getRequiresExplicitShift() {
        return false;
    }

    protected Vec3d getHitModifier(Direction validSide) {
        return new Vec3d(0, 0, 0);
    }

    private Optional<Direction> getValidSide(SchematicBlockState state) {
        boolean printInAir = false; // LitematicaMixinMod.PRINT_IN_AIR.getBooleanValue();

        List<Direction> sides = getPossibleSides();

        if (sides.isEmpty()) {
            return Optional.empty();
        }

        List<Direction> validSides = new ArrayList<>();
        for (Direction side : sides) {
            if (printInAir && !getRequiresSupport()) {
                return Optional.of(side);
            } else {
                SchematicBlockState neighborState = state.offset(side);

                if (getProperty(neighborState.currentState, SlabBlock.TYPE).orElse(null) == SlabType.DOUBLE) {
                    validSides.add(side);
                    continue;
                }

                if (canBeClicked(neighborState.world, neighborState.blockPos) && // Handle unclickable grass for example
                        !neighborState.currentState.isReplaceable())
                    validSides.add(side);
            }
        }

        for (Direction validSide : validSides) {
            if (!isInteractive(state.offset(validSide).currentState.getBlock())) {
                return Optional.of(validSide);
            }
        }

        return validSides.isEmpty() ? Optional.empty() : Optional.of(validSides.get(0));
    }

    protected Vec3d[] getPossibleHitVecs() {
        return PrinterConfig.CARPET_MODE.getBooleanValue() ? extendedHitVecs : hitVecsToTry;
    }

    private List<Direction> getValidSides(SchematicBlockState state) {
        List<Direction> sides = getPossibleSides();

        List<Direction> validSides = new ArrayList<>();
        for (Direction side : sides) {
            SchematicBlockState neighborState = state.offset(side);

            if (getProperty(neighborState.currentState, SlabBlock.TYPE).orElse(null) == SlabType.DOUBLE) {
                validSides.add(side);
                continue;
            }

            if (canBeClicked(neighborState.world, neighborState.blockPos) && // Handle unclickable grass for example
                    !neighborState.currentState.isReplaceable())
                validSides.add(side);
        }

        return validSides;
    }

    protected boolean getUseShift(SchematicBlockState state) {
        if (getRequiresExplicitShift()) return true;

        Direction clickSide = getValidSide(state).orElse(null);
        if (clickSide == null) return false;
        return isInteractive(state.offset(clickSide).currentState.getBlock());
    }

    @Override
    public boolean canExecute(ClientPlayerEntity player) {
        if (!super.canExecute(player)) return false;
        if (!PrinterConfig.STRICT_BLOCK_FACE_CHECK.getBooleanValue()) {
            return true;
        }
        for (Direction side : getPossibleSides()) {
            if (canSeeBlockFace(player, new BlockHitResult(Vec3d.ofCenter(state.blockPos), side.getOpposite(), state.blockPos.offset(side), false))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable PrinterPlacementContext getPlacementContext(ClientPlayerEntity player) {
        if (PrinterConfig.PRINTER_AIRPLACE.getBooleanValue()) {
            return getContextByRotation(player);
        } else {
            return getContextByStrictLook(player);
        }
    }

    @Nullable
    public PrinterPlacementContext getContextByStrictLook(ClientPlayerEntity player) {
        if (contextCache != null && !LitematicaMixinMod.DEBUG && !PrinterConfig.NO_PLACEMENT_CACHE.getBooleanValue() && contextCache.isRaytrace == PrinterConfig.RAYCAST.getBooleanValue())
            return contextCache;

        ItemStack requiredItem = getRequiredItem(player).stream().findFirst().orElse(ItemStack.EMPTY);
        int slot = getRequiredItemStackSlot(player);

        if (slot == -1) return null;

        Vec3d[] hitVecsToTryArray = getPossibleHitVecs();

        Vec3d playerEyePos = player.getEyePos();
        // Direction relativeRotation = Direction.getEntityFacingOrder(player)[0];

        // for (Direction lookDirection : directionsToTry) {
        for (Direction side : getPossibleSides()) {
            BlockPos neighborPos = state.blockPos.offset(side);

            // Check if the block face is visible. Prevents the printer from trying to place blocks on the backside of other blocks
            if (PrinterConfig.STRICT_BLOCK_FACE_CHECK.getBooleanValue()) {
                if (!canSeeBlockFace(player, new BlockHitResult(Vec3d.ofCenter(state.blockPos), side.getOpposite(), neighborPos, false))) {
                    continue;
                }
            }

            BlockState neighborState = state.world.getBlockState(neighborPos);
            boolean requiresShift = getRequiresExplicitShift() || isInteractive(neighborState.getBlock());

            if (!canBeClicked(state.world, neighborPos) || // Handle unclickable grass for example
                    neighborState.isReplaceable())
                continue;

            Vec3d hitVec = Vec3d.ofCenter(state.blockPos)
                    .add(Vec3d.of(side.getVector()).multiply(0.5)); // Center of the block side face we are placing on

            // Now we bring on the big guns, brute force the hit vector until we find a solution that directly hits the neighbor block without obstruction
            for (Vec3d hitVecToTry : hitVecsToTryArray) {
                Vec3d multiplier = Vec3d.of(side.getVector());
                multiplier = new Vec3d(
                        multiplier.x == 0 ? 1 : 0,
                        multiplier.y == 0 ? 1 : 0,
                        multiplier.z == 0 ? 1 : 0); // Offset from the Center of the block side face we are placing on by pre calculated values. This samples different points on that face.

                Vec3d blockHit = hitVec.add(hitVecToTry.multiply(multiplier));
                Vec3d lookDirection = blockHit.subtract(playerEyePos).normalize();
                Direction relativeDirection = Direction.getFacing(lookDirection.x, lookDirection.y, lookDirection.z);

                if (playerEyePos.distanceTo(blockHit) > LitematicaMixinMod.PRINTING_RANGE.getDoubleValue()) // Check if the hit vector is in range
                    continue;

                if (PrinterConfig.RAYCAST.getBooleanValue() && mc.world != null && mc.player != null) {
                    Vec3d lookVec = blockHit.subtract(playerEyePos).normalize(); // Look vector from the player's eye to the block hit vector
                    Vec3d raycastEnd = playerEyePos.add(lookVec.multiply(5)); // 5 block max distance
                    RaycastContext raycastContext = new RaycastContext(playerEyePos, raycastEnd, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player);
                    BlockHitResult result = mc.world.raycast(raycastContext);
                    if (result.getType() != HitResult.Type.BLOCK) { // If we didn't hit a block, skip
                        continue;
                    }

                    if (result.getBlockPos().equals(neighborPos)) {
                        if (PrinterConfig.RAYCAST_STRICT_BLOCK_HIT.getBooleanValue()) { // Check if the right side was hit
                            Direction hitSide = result.getSide();
                            if (hitSide.getOpposite() != side) {
                                continue;
                            }
                        }
                        if (result.getPos().distanceTo(playerEyePos) > LitematicaMixinMod.PRINTING_RANGE.getDoubleValue()) { // Check if the hit result is in range
                            continue;
                        }
                        BlockHitResult hitResult = new BlockHitResult(blockHit, side.getOpposite(), neighborPos, false);
                        PrinterPlacementContext rayTraceContext = new PrinterPlacementContext(player, hitResult, requiredItem, slot, relativeDirection, requiresShift);
                        rayTraceContext.canStealth = true;
                        rayTraceContext.isRaytrace = true;
                        BlockState resultState = getRequiredItemAsBlock(player)
                                .orElse(targetState.getBlock())
                                .getPlacementState(rayTraceContext);

                        if (resultState != null && correctObserverPlacement(targetState, resultState) && (statesEqual(resultState, targetState) || correctChestPlacement(targetState, resultState))) {
                            contextCache = rayTraceContext;
                            return rayTraceContext;
                        }
                    }
                    continue;
                } else /* No Raycast */ {
                    BlockHitResult hitResult = new BlockHitResult(blockHit, side.getOpposite(), neighborPos, false);
                    PrinterPlacementContext context = new PrinterPlacementContext(player, hitResult, requiredItem, slot, relativeDirection, requiresShift);
                    context.canStealth = true;
                    BlockState result = getRequiredItemAsBlock(player)
                            .orElse(targetState.getBlock())
                            .getPlacementState(context); // FIXME torch shift clicks another torch and getPlacementState is the clicked block, which is true

                    if (result != null && correctObserverPlacement(targetState, result) && (statesEqual(result, targetState) || correctChestPlacement(targetState, result))) {
                        contextCache = context;
                        return context;
                    }
                }
            }
        }
        // }

        return null;
    }

    protected static Direction[] directionsToTry = new Direction[]{
            Direction.NORTH,
            Direction.SOUTH,
            Direction.EAST,
            Direction.WEST,
            Direction.UP,
            Direction.DOWN
    };

    @Nullable
    public PrinterPlacementContext getContextByRotation(ClientPlayerEntity player) {
        if (contextCache != null && !LitematicaMixinMod.DEBUG) return contextCache;

        ItemStack requiredItem = getRequiredItem(player).stream().findFirst().orElse(ItemStack.EMPTY);
        int slot = getRequiredItemStackSlot(player);

        if (slot == -1) return null;

        if (Printer.inactivityCounter <= PrinterConfig.PRINTER_MIN_INACTIVE_TIME_AIR_PLACE.getIntegerValue()) {
            return null;
        }

        // Check if we can place without rotating
        // Direction in BlockHitResult should not matter as placement result should come from internal player rotation
        // Maybe does matter for slabs? But then again airplace always places DOWN so idk
        BlockHitResult noRotateHitResult = new BlockHitResult(Vec3d.ofCenter(state.blockPos), Direction.UP, state.blockPos, true);
        PrinterPlacementContext noRotateContext = new PrinterPlacementContext(player, noRotateHitResult, requiredItem, slot, null, false);
        BlockState noRotateResult = getRequiredItemAsBlock(player)
                .orElse(targetState.getBlock())
                .getPlacementState(noRotateContext); // FIXME torch shift clicks another torch and getPlacementState is the clicked block, which is true

        if (noRotateResult != null && correctObserverPlacement(targetState, noRotateResult) && (statesEqual(noRotateResult, targetState))) {
            contextCache = noRotateContext;
            noRotateContext.isAirPlace = true;
            return noRotateContext;
        }

        for (Direction lookDirection : directionsToTry) {
            final Direction side = Direction.UP; // Airplace only works for UP for now
            BlockPos neighborPos = state.blockPos.offset(side);

            Vec3d hitVec = Vec3d.ofCenter(state.blockPos);

            BlockHitResult hitResult = new BlockHitResult(hitVec, side.getOpposite(), neighborPos, false);
            PrinterPlacementContext context = new PrinterPlacementContext(player, hitResult, requiredItem, slot, lookDirection, false);
            BlockState result = getRequiredItemAsBlock(player)
                    .orElse(targetState.getBlock())
                    .getPlacementState(context); // FIXME torch shift clicks another torch and getPlacementState is the clicked block, which is true

            if (result != null && correctObserverPlacement(targetState, result) && (statesEqual(result, targetState))) {
                contextCache = context;
                context.isAirPlace = true;
                return context;
            }
        }

//        if (PrinterConfig.PRINTER_AIRPLACE_FLOATING_ONLY.getBooleanValue() && !isInAir(state.blockPos)) {
//            return null;
//        }
//        if (Printer.inactivityCounter < PrinterConfig.PRINTER_MIN_INACTIVE_TIME_AIR_PLACE.getIntegerValue()) {
//            return null;
//        }
//        if (PrinterConfig.PRINTER_AIRPLACE_RANGE.getDoubleValue() < 0 || player.getEyePos().distanceTo(Vec3d.ofCenter(state.blockPos)) > PrinterConfig.PRINTER_AIRPLACE_RANGE.getDoubleValue()) {
//            return null;
//        }

        return null;
    }

    private boolean correctChestPlacement(BlockState targetState, BlockState result) {
        if (targetState.contains(ChestBlock.CHEST_TYPE) && result.contains(ChestBlock.CHEST_TYPE) && result.get(ChestBlock.FACING) == targetState.get(ChestBlock.FACING)) {
            ChestType targetChestType = targetState.get(ChestBlock.CHEST_TYPE);
            ChestType resultChestType = result.get(ChestBlock.CHEST_TYPE);

            return targetChestType != ChestType.SINGLE && resultChestType == ChestType.SINGLE;
        }

        return false;
    }

    /**
     * Returns true if the observer is placed correctly or if the config printerPlaceObserversLast is set to false
     * @param targetState the target state of the block being placed
     * @param result the result state of the block being placed
     * @return true if the observer is placed correctly, false otherwise
     */
    private boolean correctObserverPlacement(BlockState targetState, BlockState result) {
        if (!PrinterConfig.PRINTER_PLACE_OBSERVERS_LAST.getBooleanValue()) return true; // If the config is set to place observers last, we don't need to check this

        if (result.getBlock() != Blocks.OBSERVER) return true;
        if (targetState.getBlock() != Blocks.OBSERVER) return true;

        Direction facing = result.get(Properties.FACING);
        if (facing == null) return false;

        WorldSchematic schematicWorld = SchematicWorldHandler.getSchematicWorld();
        if (schematicWorld == null) return false;
        if (mc.world == null) return false;

        if (schematicWorld.getBlockState(state.blockPos.offset(facing)).isAir()) {
            return true;
        } else {
            return !mc.world.getBlockState(state.blockPos.offset(facing)).isAir();
        }
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
    }
}
