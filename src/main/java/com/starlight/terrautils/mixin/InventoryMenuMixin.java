package com.starlight.terrautils.mixin;

import com.starlight.terrautils.data.ExtraHotbar;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Appends the extra hotbar slots to the player's inventory menu (menu indices 46..49), so vanilla handles
 * clicking, syncing and creative-mode edits for them. They sit outside the panel next to the hotbar row.
 */
@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends AbstractContainerMenu {
    protected InventoryMenuMixin(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void terrautils$addExtraHotbarSlots(Inventory playerInventory, boolean active, Player owner, CallbackInfo ci) {
        for (int i = 0; i < ExtraHotbar.SLOTS; i++) {
            this.addSlot(new ExtraHotbar.ExtraSlot(owner, i, ExtraHotbar.slotX(i, 176), 142));
        }
    }
}
