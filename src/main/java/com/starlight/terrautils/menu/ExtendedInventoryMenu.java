package com.starlight.terrautils.menu;

import com.starlight.terrautils.data.PlayerExtData;
import com.starlight.terrautils.data.StackLimits;
import com.starlight.terrautils.network.ModNetwork;
import com.starlight.terrautils.registry.ModAttachments;
import com.starlight.terrautils.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * The extra backpack rows + extra hotbar rows, shown above the regular player inventory.
 * Both sides back the slots with the player's own {@link PlayerExtData}; vanilla menu sync keeps them in step.
 */
public class ExtendedInventoryMenu extends AbstractContainerMenu {
    private final Player player;
    private final ExtendedInventoryLayout layout;
    private final int extraSlots;

    public static ExtendedInventoryMenu fromNetwork(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        return new ExtendedInventoryMenu(containerId, inventory, buf.readVarInt(), buf.readVarInt());
    }

    public ExtendedInventoryMenu(int containerId, Inventory inventory, int backpackRows, int hotbarRows) {
        super(ModMenus.EXTENDED_INVENTORY.get(), containerId);
        this.player = inventory.player;
        this.layout = ExtendedInventoryLayout.of(backpackRows, hotbarRows);
        PlayerExtData data = player.getData(ModAttachments.PLAYER_DATA);

        for (int row = 0; row < backpackRows; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new ExtSlot(data.getBackpack(), row * 9 + col,
                        ExtendedInventoryLayout.SLOT_X + col * 18, layout.backpackY() + row * 18));
            }
        }
        for (int row = 0; row < hotbarRows; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new ExtSlot(data.getHotbarBank(), row * 9 + col,
                        ExtendedInventoryLayout.SLOT_X + col * 18, layout.hotbarY() + row * 18));
            }
        }
        this.extraSlots = (backpackRows + hotbarRows) * 9;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, 9 + row * 9 + col,
                        ExtendedInventoryLayout.SLOT_X + col * 18, layout.inventoryY() + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, ExtendedInventoryLayout.SLOT_X + col * 18, layout.playerHotbarY()));
        }
    }

    public ExtendedInventoryLayout getLayout() {
        return layout;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        int playerStart = extraSlots;
        int playerEnd = slots.size();

        if (index < extraSlots) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else if (extraSlots == 0 || !moveItemStackTo(stack, 0, extraSlots, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer serverPlayer) {
            // The hotbar rows are shown on the HUD, so push their new contents to the client.
            ModNetwork.syncToClient(serverPlayer);
        }
    }

    private class ExtSlot extends SlotItemHandler {
        ExtSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return StackLimits.forPlayer(player, stack);
        }
    }
}
