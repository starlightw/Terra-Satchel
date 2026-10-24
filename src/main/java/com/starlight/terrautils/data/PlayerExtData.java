package com.starlight.terrautils.data;

import com.starlight.terrautils.item.UpgradeType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;
import java.util.function.Consumer;

/**
 * Permanent per-player data: upgrade levels, the extra backpack storage, the extra hotbar rows
 * and the extra hotbar slots (keys ` 0 - =).
 */
public class PlayerExtData implements INBTSerializable<CompoundTag> {
    public static final int MAX_BACKPACK_ROWS = 4;
    public static final int MAX_HOTBAR_ROWS = 3;
    public static final int MAX_STACK_LEVEL = 3;
    public static final int MAX_EXTRA_SLOTS = ExtraHotbar.SLOTS;

    private int backpackRows;
    private int hotbarRows;
    private int stackLevel;
    private int extraSlots;

    /** Selected extra hotbar slot, or -1 when a vanilla hotbar slot is selected. Not saved. */
    private int selectedExtra = -1;
    /** Last selection value exchanged with the other side; used to detect what still needs syncing. */
    private int syncedSelection = -1;

    private final ItemStackHandler backpack = new Storage(MAX_BACKPACK_ROWS * 9);
    private final ItemStackHandler hotbarBank = new Storage(MAX_HOTBAR_ROWS * 9);
    private final ItemStackHandler extraHotbar = new Storage(MAX_EXTRA_SLOTS);

    public int getBackpackRows() {
        return backpackRows;
    }

    public int getHotbarRows() {
        return hotbarRows;
    }

    public int getStackLevel() {
        return stackLevel;
    }

    public int getExtraSlots() {
        return extraSlots;
    }

    public int getLevel(UpgradeType type) {
        return switch (type) {
            case BACKPACK -> backpackRows;
            case HOTBAR -> hotbarRows;
            case STACK -> stackLevel;
            case HOTBAR_SLOTS -> extraSlots;
        };
    }

    public void setLevel(UpgradeType type, int level) {
        int clamped = Math.max(0, Math.min(type.maxLevel(), level));
        switch (type) {
            case BACKPACK -> backpackRows = clamped;
            case HOTBAR -> hotbarRows = clamped;
            case STACK -> stackLevel = clamped;
            case HOTBAR_SLOTS -> extraSlots = clamped;
        }
    }

    public ItemStackHandler getBackpack() {
        return backpack;
    }

    public ItemStackHandler getHotbarBank() {
        return hotbarBank;
    }

    public ItemStackHandler getExtraHotbar() {
        return extraHotbar;
    }

    public boolean isExtraSlotUnlocked(int index) {
        return index >= 0 && index < extraSlots;
    }

    public int getSelectedExtra() {
        return isExtraSlotUnlocked(selectedExtra) ? selectedExtra : -1;
    }

    public void setSelectedExtra(int index) {
        this.selectedExtra = isExtraSlotUnlocked(index) ? index : -1;
    }

    /** The extra hotbar stack currently held in the main hand, or null when a vanilla hotbar slot is selected. */
    public ItemStack getSelectedExtraStack() {
        int index = getSelectedExtra();
        return index < 0 ? null : extraHotbar.getStackInSlot(index);
    }

    public int getSyncedSelection() {
        return syncedSelection;
    }

    public void setSyncedSelection(int syncedSelection) {
        this.syncedSelection = syncedSelection;
    }

    /** Puts as much of {@code stack} as possible into the unlocked backpack slots; returns the remainder. */
    public ItemStack insertIntoBackpack(ItemStack stack) {
        int slots = backpackRows * 9;
        ItemStack remaining = stack;
        // First merge into existing stacks, then fill empty slots.
        for (int pass = 0; pass < 2 && !remaining.isEmpty(); pass++) {
            for (int i = 0; i < slots && !remaining.isEmpty(); i++) {
                boolean empty = backpack.getStackInSlot(i).isEmpty();
                if ((pass == 0) != empty) {
                    remaining = backpack.insertItem(i, remaining, false);
                }
            }
        }
        return remaining;
    }

    /**
     * Rotates the vanilla hotbar through the unlocked bank rows.
     * Forward: hotbar <- row 1 <- row 2 ... <- row N <- old hotbar.
     */
    public void cycleHotbar(Inventory inventory, boolean reverse) {
        int rows = hotbarRows + 1;
        if (rows <= 1) {
            return;
        }
        ItemStack[][] snapshot = new ItemStack[rows][9];
        for (int col = 0; col < 9; col++) {
            snapshot[0][col] = inventory.getItem(col);
            for (int r = 1; r < rows; r++) {
                snapshot[r][col] = hotbarBank.getStackInSlot((r - 1) * 9 + col);
            }
        }
        for (int r = 0; r < rows; r++) {
            int from = reverse ? (r - 1 + rows) % rows : (r + 1) % rows;
            for (int col = 0; col < 9; col++) {
                ItemStack moved = snapshot[from][col];
                if (r == 0) {
                    inventory.setItem(col, moved);
                } else {
                    hotbarBank.setStackInSlot((r - 1) * 9 + col, moved);
                }
            }
        }
    }

    /** Removes every stored item, handing each non-empty stack to {@code sink}. */
    public void drainAll(Consumer<ItemStack> sink) {
        for (ItemStackHandler handler : List.of(backpack, hotbarBank, extraHotbar)) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    handler.setStackInSlot(i, ItemStack.EMPTY);
                    sink.accept(stack);
                }
            }
        }
    }

    /** Applies data received from the server (client side only). */
    public void applySync(int backpackRows, int hotbarRows, int stackLevel, int extraSlots,
                          List<ItemStack> bank, List<ItemStack> extra) {
        this.backpackRows = backpackRows;
        this.hotbarRows = hotbarRows;
        this.stackLevel = stackLevel;
        this.extraSlots = extraSlots;
        for (int i = 0; i < hotbarBank.getSlots(); i++) {
            hotbarBank.setStackInSlot(i, i < bank.size() ? bank.get(i) : ItemStack.EMPTY);
        }
        for (int i = 0; i < extraHotbar.getSlots(); i++) {
            extraHotbar.setStackInSlot(i, i < extra.size() ? extra.get(i) : ItemStack.EMPTY);
        }
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("backpack_rows", backpackRows);
        tag.putInt("hotbar_rows", hotbarRows);
        tag.putInt("stack_level", stackLevel);
        tag.putInt("extra_slots", extraSlots);
        tag.put("backpack", backpack.serializeNBT(provider));
        tag.put("hotbar_bank", hotbarBank.serializeNBT(provider));
        tag.put("extra_hotbar", extraHotbar.serializeNBT(provider));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        backpackRows = Math.min(MAX_BACKPACK_ROWS, tag.getInt("backpack_rows"));
        hotbarRows = Math.min(MAX_HOTBAR_ROWS, tag.getInt("hotbar_rows"));
        stackLevel = Math.min(MAX_STACK_LEVEL, tag.getInt("stack_level"));
        extraSlots = Math.min(MAX_EXTRA_SLOTS, tag.getInt("extra_slots"));
        loadHandler(provider, backpack, tag.getCompound("backpack"));
        loadHandler(provider, hotbarBank, tag.getCompound("hotbar_bank"));
        loadHandler(provider, extraHotbar, tag.getCompound("extra_hotbar"));
    }

    private static void loadHandler(HolderLookup.Provider provider, ItemStackHandler handler, CompoundTag tag) {
        int size = handler.getSlots();
        handler.deserializeNBT(provider, tag);
        // deserializeNBT resizes to the saved size; keep our fixed capacity.
        if (handler.getSlots() != size) {
            ItemStack[] kept = new ItemStack[size];
            for (int i = 0; i < size; i++) {
                kept[i] = i < handler.getSlots() ? handler.getStackInSlot(i) : ItemStack.EMPTY;
            }
            handler.setSize(size);
            for (int i = 0; i < size; i++) {
                handler.setStackInSlot(i, kept[i]);
            }
        }
    }

    /** Item storage whose stack limit follows the owner's stack upgrade level. */
    private class Storage extends ItemStackHandler {
        Storage(int size) {
            super(size);
        }

        @Override
        public int getSlotLimit(int slot) {
            return StackLimits.HARD_CAP;
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return StackLimits.limit(stack, stackLevel);
        }
    }
}
