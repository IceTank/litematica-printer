package me.aleksilassila.litematica.printer.v1_21;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.materials.MaterialCache;
import fi.dy.masa.litematica.world.WorldSchematic;
import fi.dy.masa.malilib.config.options.ConfigString;
import me.aleksilassila.litematica.printer.v1_21.config.PrinterConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class InventoryManager {
    private static final List<Integer> USABLE_SLOTS = new ArrayList<>();
    int delay = 0;
    /**
     * The queue of items to pull from the inventory
     */
    private final ArrayList<SlotInfo> hotbarSlots = new ArrayList<>(9);

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final Deque<Integer> rollingSlots = new ArrayDeque<>();
    private final Deque<Integer> lastUsedSlots = new ArrayDeque<>();
    private static InventoryManager instance;
    private InventoryManager() {
        // Probably add a way to configure this
        for (int i = 0; i < 9; i++) {
            rollingSlots.add(i);
            lastUsedSlots.add(i);
            hotbarSlots.add(new SlotInfo());
        }
    }

    public static void setHotbarSlots(ConfigString config) {
        USABLE_SLOTS.clear();
        String configStr = config.getStringValue();
        String[] parts = configStr.split(",");

        for (String str : parts) {
            try {
                int slotNum = Integer.parseInt(str) - 1;

                if (PlayerInventory.isValidHotbarIndex(slotNum) &&
                        !USABLE_SLOTS.contains(slotNum)) {
                    USABLE_SLOTS.add(slotNum);
                }
            } catch (NumberFormatException ignore) {
            }
        }
    }

    public static InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }
        return instance;
    }

    public void reset() {
        hotbarSlots.forEach((info) -> info.ticksLocked = 0);
    }

    public boolean tick() {
        if (mc.player == null || mc.interactionManager == null) {
            return false;
        }

        delay = Math.max(0, delay - 1);

        return false;
    }

    /**
     * Swaps the item from the inventory to the hotbar
     * @param item The item to pull from the inventory
     * @return True if the item could the swapped into the hotbar
     */
    private boolean swapToHotbar(ClientPlayerEntity player, Item item) {
        if (getHotbarSlotWithItem(player, new ItemStack(item)) != -1) {
            return true;
        }
        int slot = getBestInventorySlotWithItem(player, new ItemStack(item));
        if (slot == -1) {
            return false;
        }
        int nextSlot = nextHotbarSlot();
        if (nextSlot == -1) {
            return false;
        }
        hotbarSlots.get(nextSlot).addTicksLocked(10);
        hotbarSlots.get(nextSlot).waitingForItem = item;
        player.getInventory().selectedSlot = nextSlot;
        if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) {
            System.out.println("Swapping item from inventory: " + slot + " into hotbar -> " + nextSlot);
        }
        swapAway(slot, nextSlot, player.playerScreenHandler);
        delay += PrinterConfig.INVENTORY_DELAY.getIntegerValue();
        return true;
    }

    public void pickSlot(WorldSchematic world, ClientPlayerEntity player, BlockPos pos) {
        if (mc.interactionManager == null) {
            return;
        }
        PlayerInventory inv = player.getInventory();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = MaterialCache.getInstance().getRequiredBuildItemForState(state, world, pos);
        int slot = inv.getSlotWithStack(stack);
        boolean shouldPick = slot > 8;
        if (slot != -1 && !shouldPick) {
            player.getInventory().selectedSlot = slot;
        } else if (slot != -1) {
            mc.interactionManager.pickFromInventory(slot);
        } else if (Configs.Generic.PICK_BLOCK_SHULKERS.getBooleanValue()) {
            slot = findSlotWithBoxWithItem(player.getInventory(), stack, true);
            if (slot > -1) {
                if (slot > 8) {
                    mc.interactionManager.pickFromInventory(slot);
                } else {
                    inv.selectedSlot = slot;
                }
            }
        }
    }

    public static int findSlotWithBoxWithItem(PlayerInventory inventory, ItemStack stackReference, boolean lestFirst) {
        int bestCount = lestFirst ? Integer.MAX_VALUE : 0;
        int bestSlot = -1;

        for(int slotNum = 0; slotNum < inventory.main.size(); slotNum += 1) {
            ItemStack itemStack = inventory.getStack(slotNum);
            int count = shulkerBoxItemCount(itemStack, stackReference);
            if (lestFirst && count < bestCount && count > 0) {
                bestCount = count;
                bestSlot = slotNum;
            } else if (!lestFirst && count > bestCount) {
                bestCount = count;
                bestSlot = slotNum;
            }
        }

        return bestSlot;
    }

    public static int shulkerBoxItemCount(ItemStack stack, ItemStack referenceItem) {
        DefaultedList<ItemStack> items = fi.dy.masa.malilib.util.InventoryUtils.getStoredItems(stack);
        int count = 0;
        if (!items.isEmpty()) {
            for (ItemStack item : items) {
                if (fi.dy.masa.malilib.util.InventoryUtils.areStacksEqual(item, referenceItem)) {
                    count += item.getCount();
                }
            }
        }

        return count;
    }

    public boolean select(ItemStack itemStack) {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.interactionManager == null) {
            return false;
        }
        if (itemStack != null) {
            PlayerInventory inventory = player.getInventory();

            // This thing is straight from MinecraftClient#doItemPick()
            if (player.getAbilities().creativeMode) {
                inventory.addPickBlock(itemStack);
                mc.interactionManager.clickCreativeStack(player.getStackInHand(Hand.MAIN_HAND), 36 + inventory.selectedSlot);
                updateLastUsedSlot(inventory.selectedSlot);
                return true;
            } else {
                int hotbarSlot = getHotbarSlotWithItem(player, itemStack);
                if (hotbarSlot == -1) {
                    if (delay > 0) {
                        return false;
                    }
                    if (swapToHotbar(mc.player, itemStack.getItem())) {
                        // If true the item should now be somewhere in the hotbar
                        hotbarSlot = getHotbarSlotWithItem(player, itemStack);
                        if (hotbarSlot == -1) {
                            return false;
                        }
                        player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(hotbarSlot));
                        player.getInventory().selectedSlot = hotbarSlot;
                        updateLastUsedSlot(hotbarSlot);
                        return true;
                    }
                    return false;
                } else {
                    player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(hotbarSlot));
                    player.getInventory().selectedSlot = hotbarSlot;
                    updateLastUsedSlot(hotbarSlot);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean swapAway(int inventorySlot, int hotbarSlot, PlayerScreenHandler screenHandler) {
        ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        if (mc.player == null || mc.interactionManager == null || networkHandler == null) return false;

        mc.interactionManager.clickSlot(screenHandler.syncId, inventorySlot, hotbarSlot, SlotActionType.SWAP, mc.player);
        return true;
    }

    /**
     * Returns the next available slot for a new item. Returns the slot to the end of the queue after returning.
     * Range from 0-8
     * @return The next available slot
     */
    private int nextHotbarSlot() {
        final String mode = PrinterConfig.PRINTER_INVENTORY_MANAGEMENT_MODE.getStringValue();

        if (PrinterConfig.InventoryManagementModeEnum.LEAST_USED.is(mode)) {
            if (!lastUsedSlots.isEmpty()) {
                List<Integer> usableSlots = lastUsedSlots.stream().filter(USABLE_SLOTS::contains).toList();
                if (usableSlots.isEmpty()) {
                    return -1;
                }
                int last = usableSlots.getLast();
                lastUsedSlots.remove(last);
                lastUsedSlots.addFirst(last);
                if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) {
                    System.out.println("Next new least used slot: " + last + "Last used slots: " + lastUsedSlots);
                }
                return last;
            }
        } else if (PrinterConfig.InventoryManagementModeEnum.ROLLING.is(mode)) {
            if (!rollingSlots.isEmpty()) {
                List<Integer> usableSlots = rollingSlots.stream().filter(USABLE_SLOTS::contains).toList();
                if (usableSlots.isEmpty()) {
                    return -1;
                }
                int slot = usableSlots.getLast();
                rollingSlots.remove(slot);
                rollingSlots.addLast(slot);
                if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) {
                    System.out.println("Next new rolling slot: " + slot + "Rolling slots: " + rollingSlots);
                }
                return slot;
            }
        }
        if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) {
            System.out.println("No slots available");
        }
        return -1;
    }

    /**
     * Returns the first slot with the given item. Returns -1 if no slot is found.
     * @param player The player to check
     * @param itemStack The item to check for
     * @return The first slot with the given item
     */
    private static int getBestInventorySlotWithItem(ClientPlayerEntity player, ItemStack itemStack) {
        PlayerInventory inventory = player.getInventory();

        if (itemStack.isEmpty()) return -1;

        int lowestCount = 0;
        int lowestSlot = -1;
        for(int i = 9; i < inventory.main.size(); ++i) {
            if (!(inventory.main.get(i)).isEmpty() && ItemStack.areItemsAndComponentsEqual(itemStack, inventory.main.get(i))) {
                if (inventory.main.get(i).getCount() < lowestCount || lowestSlot == -1) {
                    lowestCount = inventory.main.get(i).getCount();
                    lowestSlot = i;
                }
            }
        }

        return lowestSlot;
    }

    public int getHotbarSlotWithItem(ClientPlayerEntity player, ItemStack itemStack) {
        PlayerInventory inventory = player.getInventory();

        if (itemStack.isEmpty()) return -1;

        for (int i = 0; i < 9; ++i) {
            if (!inventory.main.get(i).isEmpty() && ItemStack.areItemsEqual(inventory.main.get(i), itemStack)) {
//                if (hotbarSlots.get(i).ticksLocked == 0) {
                    return i;
//                }
            }
        }

        return -1;
    }

    private void updateLastUsedSlot(int slot) {
        lastUsedSlots.remove(slot);
        lastUsedSlots.addFirst(slot);
        if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) {
            String list = lastUsedSlots.stream().map(String::valueOf).reduce((a, b) -> a + ", " + b).orElse("");
            System.out.println("Updating last used slot: " + slot + ". Now: " + list);
        }
    }

    static class SlotInfo {
        private int ticksLocked = 0;
        @Nullable
        public Item waitingForItem = null;

        public void addTicksLocked(int ticks) {
            ticksLocked += ticks;
        }

        public void decrementTicksLocked() {
            ticksLocked--;
        }
    }
}
