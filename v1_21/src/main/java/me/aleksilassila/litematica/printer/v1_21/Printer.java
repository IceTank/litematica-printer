package me.aleksilassila.litematica.printer.v1_21;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.util.RayTraceUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;
import me.aleksilassila.litematica.printer.v1_21.actions.Action;
import me.aleksilassila.litematica.printer.v1_21.config.PrinterConfig;
import me.aleksilassila.litematica.printer.v1_21.guides.Guide;
import me.aleksilassila.litematica.printer.v1_21.guides.Guides;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Printer {
    public static final Logger logger = LogManager.getLogger("litematica-printer");
    @NotNull
    public final ClientPlayerEntity player;
    MinecraftClient mc = MinecraftClient.getInstance();

    public final ActionHandler actionHandler;

    private final Guides interactionGuides = new Guides();
    public static final InventoryManager inventoryManager = InventoryManager.getInstance();
    public static int inactivityCounter = 0;
    static final LinkedList<BlockTimeout> blockPosTimeout = new LinkedList<>();
    int delayCounter = 0;
    @Nullable
    public static Vec2f lastRotation = null;

    public Printer(@NotNull MinecraftClient client, @NotNull ClientPlayerEntity player) {
        this.player = player;

        this.actionHandler = new ActionHandler(client, player);
    }

    public void onMiddleClick() {
        if (mc.world == null || mc.player == null) return;
        BlockPos pos = RayTraceUtils.getSchematicWorldTraceIfClosest(mc.world, mc.player, 6.0);

        if (pos != null) {
            WorldSchematic world = SchematicWorldHandler.getSchematicWorld();
            if (world == null) return;
            inventoryManager.pickSlot(world, player, pos);
        }
    }

    public boolean onGameTick() {
        WorldSchematic worldSchematic = SchematicWorldHandler.getSchematicWorld();
        blockPosTimeout.forEach((entry) -> entry.timer--);
        blockPosTimeout.removeIf((entry) -> entry.timer <= 0);

        // If the inactivityCounter is greater than the inactive snap back value, then set the lastRotation to the current rotation
        // This is used to snap back to the last rotation when the player is inactive
        inactivityCounter++;
        if (PrinterConfig.SNAP_BACK.getBooleanValue()) {
            if (inactivityCounter == PrinterConfig.INACTIVE_SNAP_BACK.getIntegerValue()) {
                if (lastRotation != null) {
                    player.setYaw(lastRotation.x);
                    player.setPitch(lastRotation.y);
                }
            } else if (inactivityCounter > PrinterConfig.INACTIVE_SNAP_BACK.getIntegerValue()) {
                lastRotation = new Vec2f(player.getYaw(), player.getPitch());
            }
        }

        actionHandler.onGameTick();
        inventoryManager.tick();

        // if (!actionHandler.acceptsActions()) return false;

        if (worldSchematic == null) return false;

        if (PrinterConfig.TICK_DELAY.getIntegerValue() != 0 && delayCounter < PrinterConfig.TICK_DELAY.getIntegerValue()) {
            delayCounter++;
            return false;
        } else {
            delayCounter = 0;
        }

        if (!LitematicaMixinMod.PRINT_MODE.getBooleanValue() && !LitematicaMixinMod.PRINT.getKeybind().isPressed())
            return false;

        PlayerAbilities abilities = player.getAbilities();
        if (!abilities.allowModifyWorld)
            return false;

        if (PrinterConfig.STOP_ON_MOVEMENT.getBooleanValue() && player.getVelocity().length() > 0.1)
            return false; // Stop if the player is moving
        if (PrinterConfig.PRINTER_DISABLE_IN_GUIS.getBooleanValue()) {
            if (mc.currentScreen != null) return false;
        }

        List<BlockPos> positions = getReachablePositions();

        if (PrinterConfig.BLOCK_TIMEOUT.getIntegerValue() != 0) {
            positions = positions.stream().filter((pos) -> blockPosTimeout.stream().noneMatch((entry) -> entry.pos.equals(pos))).toList(); // From block timeout. Don't place already placed blocks.
        }

        findBlock:
        for (BlockPos position : positions) {
            SchematicBlockState state = new SchematicBlockState(player.getWorld(), worldSchematic, position);
            if (state.targetState.equals(state.currentState) || state.targetState.isAir()) continue;

            Guide[] guides = interactionGuides.getInteractionGuides(state);

            for (Guide guide : guides) {
                if (guide.canExecute(player)) {
                    List<Action> actions = guide.execute(player);
                    actionHandler.addActions(actions.toArray(Action[]::new));
                    return true;
                }
                if (guide.skipOtherGuides()) continue findBlock;
            }
        }

        return false;
    }

    private List<BlockPos> getBlocksPlayerOccupied() {
        ArrayList<BlockPos> positions = new ArrayList<>();
        BlockPos playerPos = player.getBlockPos();
        int blocksHeightOccupied = (int) Math.ceil(player.getPos().y + player.getHeight() - playerPos.getY());

        positions.add(player.getBlockPos());
        positions.add(player.getBlockPos().up());
        if (blocksHeightOccupied > 2) {
            positions.add(playerPos.up(2));
        }
        if (Math.floor(player.getPos().x + player.getWidth() / 2) > playerPos.getX()) {
            for (int i = 0; i < blocksHeightOccupied; i++) {
                positions.add(playerPos.up(i).east());
            }
        }
        if ((player.getPos().x - player.getWidth() / 2) < playerPos.getX()) {
            for (int i = 0; i < blocksHeightOccupied; i++) {
                positions.add(playerPos.up(i).west());
            }
        }
        if (Math.floor(player.getPos().z + player.getWidth() / 2) > playerPos.getZ()) {
            for (int i = 0; i < blocksHeightOccupied; i++) {
                positions.add(playerPos.up(i).south());
            }
        }
        if ((player.getPos().z - player.getWidth() / 2) < playerPos.getZ()) {
            for (int i = 0; i < blocksHeightOccupied; i++) {
                positions.add(playerPos.up(i).north());
            }
        }
        return positions.stream().distinct().toList();
    }

    private List<BlockPos> getReachablePositions() {
        List<BlockPos> playerOccupied = getBlocksPlayerOccupied();
        int maxReach = (int) Math.ceil(LitematicaMixinMod.PRINTING_RANGE.getDoubleValue());
        double maxReachSquared = MathHelper.square(LitematicaMixinMod.PRINTING_RANGE.getDoubleValue());

        ArrayList<BlockPos> positions = new ArrayList<>();

        for (int y = -maxReach; y < maxReach + 1; y++) {
            for (int x = -maxReach; x < maxReach + 1; x++) {
                for (int z = -maxReach; z < maxReach + 1; z++) {
                    BlockPos blockPos = player.getBlockPos().north(x).west(z).up(y);

                    if (!DataManager.getRenderLayerRange().isPositionWithinRange(blockPos)) continue;
                    if (this.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(blockPos)) > maxReachSquared) {
                        continue;
                    }

                    positions.add(blockPos);
                }
            }
        }

        return positions.stream()
//                .filter(p -> playerOccupied.stream().noneMatch(p::equals))
                .sorted((a, b) -> {
                    double aDistance = this.player.getPos().squaredDistanceTo(Vec3d.ofCenter(a));
                    double bDistance = this.player.getPos().squaredDistanceTo(Vec3d.ofCenter(b));
                    return Double.compare(aDistance, bDistance);
                }).toList();
    }

    public static void addTimeout(BlockPos pos) {
        blockPosTimeout.add(new BlockTimeout(pos, PrinterConfig.BLOCK_TIMEOUT.getIntegerValue()));
    }

    public void rotate(float v, float v1) {
        LitematicaMixinMod.freeLook.ticksSinceLastRotation = 0;
        this.player.setYaw(v);
        this.player.setPitch(v1);
    }

    public static class BlockTimeout {
        int timer = 0;
        BlockPos pos;

        public BlockTimeout(BlockPos pos, int timer) {
            this.pos = pos;
            this.timer = timer;
        }
    }
}
