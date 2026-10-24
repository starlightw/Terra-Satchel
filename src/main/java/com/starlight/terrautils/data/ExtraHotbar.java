package com.starlight.terrautils.data;

import com.starlight.terrautils.registry.ModAttachments;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * The extra hotbar slots unlocked by the Hotbar Slot Extender. They are unlocked in index order:
 * <ol start="0">
 *     <li>right of slot 9, key {@code 0}</li>
 *     <li>right of that, key {@code -}</li>
 *     <li>right of that, key {@code =}</li>
 *     <li>left of slot 1, key {@code `}</li>
 * </ol>
 * The items live in {@link PlayerExtData}; while one is selected, mixins make it the main-hand item.
 */
public final class ExtraHotbar {
    public static final int SLOTS = 4;
    public static final int LEFT_INDEX = 3;

    private ExtraHotbar() {}

    public static boolean isLeft(int index) {
        return index == LEFT_INDEX;
    }

    /** Position counted from the right edge of the vanilla hotbar (0 = first slot after slot 9). */
    public static int rightPosition(int index) {
        return index;
    }

    public static int leftCount(int unlocked) {
        return unlocked > LEFT_INDEX ? 1 : 0;
    }

    public static int rightCount(int unlocked) {
        return Math.min(unlocked, LEFT_INDEX);
    }

    /**
     * Every selectable hotbar position from left to right. Values 0..8 are vanilla slots,
     * values {@code -1 - i} stand for extra slot {@code i}.
     */
    public static List<Integer> selectionOrder(int unlocked) {
        List<Integer> order = new ArrayList<>(9 + SLOTS);
        if (leftCount(unlocked) > 0) {
            order.add(encodeExtra(LEFT_INDEX));
        }
        for (int i = 0; i < 9; i++) {
            order.add(i);
        }
        for (int i = 0; i < rightCount(unlocked); i++) {
            order.add(encodeExtra(i));
        }
        return order;
    }

    public static int encodeExtra(int index) {
        return -1 - index;
    }

    public static int decodeExtra(int encoded) {
        return -1 - encoded;
    }

    public static PlayerExtData data(Player player) {
        return player.getData(ModAttachments.PLAYER_DATA);
    }

    /** A {@link Container} view that always reads the player's current attachment (it can be replaced on load). */
    public static final class PlayerContainer implements Container {
        private final Player player;

        public PlayerContainer(Player player) {
            this.player = player;
        }

        private ItemStackHandler handler() {
            return data(player).getExtraHotbar();
        }

        @Override
        public int getContainerSize() {
            return SLOTS;
        }

        @Override
        public boolean isEmpty() {
            for (int i = 0; i < SLOTS; i++) {
                if (!getItem(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return handler().getStackInSlot(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack stack = getItem(slot);
            if (stack.isEmpty() || amount <= 0) {
                return ItemStack.EMPTY;
            }
            ItemStack taken = stack.split(amount);
            if (stack.isEmpty()) {
                setItem(slot, ItemStack.EMPTY);
            }
            return taken;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            ItemStack stack = getItem(slot);
            setItem(slot, ItemStack.EMPTY);
            return stack;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            handler().setStackInSlot(slot, stack);
        }

        @Override
        public int getMaxStackSize() {
            return StackLimits.HARD_CAP;
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {
            for (int i = 0; i < SLOTS; i++) {
                setItem(i, ItemStack.EMPTY);
            }
        }
    }

    /** A menu slot for one extra hotbar slot; hidden and unusable until unlocked. */
    public static final class ExtraSlot extends Slot {
        private final Player player;

        public ExtraSlot(Player player, int index, int x, int y) {
            super(new PlayerContainer(player), index, x, y);
            this.player = player;
        }

        public int extraIndex() {
            return getContainerSlot();
        }

        @Override
        public boolean isActive() {
            return data(player).isExtraSlotUnlocked(getContainerSlot());
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return isActive();
        }

        @Override
        public boolean mayPickup(Player player) {
            return isActive();
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return Math.min(getMaxStackSize(), StackLimits.forPlayer(player, stack));
        }
    }
}
