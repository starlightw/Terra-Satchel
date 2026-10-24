package com.starlight.terrautils.mixin.client;

import com.starlight.terrautils.data.ExtraHotbar;
import com.starlight.terrautils.data.StackLimits;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin {
    /** Creative panel width; the extra hotbar slots are placed just outside it. */
    private static final int TERRAUTILS$PANEL_WIDTH = 195;

    /**
     * "A full stack" in the creative screen (shift-click, middle-click, Ctrl+Q, number-key swap...) means the
     * player's upgraded stack size instead of the item's vanilla one.
     */
    @Redirect(method = "slotClicked",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I"))
    private int terrautils$upgradedFullStack(ItemStack stack) {
        return StackLimits.forPlayer(Minecraft.getInstance().player, stack);
    }

    /** The inventory tab copies every inventory-menu slot; move our extra hotbar slots next to the hotbar row. */
    @Inject(method = "selectTab", at = @At("TAIL"))
    private void terrautils$placeExtraSlots(CreativeModeTab tab, CallbackInfo ci) {
        if (tab.getType() != CreativeModeTab.Type.INVENTORY) {
            return;
        }
        List<Slot> slots = ((CreativeModeInventoryScreen) (Object) this).getMenu().slots;
        for (int i = 0; i < slots.size(); i++) {
            Slot wrapper = slots.get(i);
            if (wrapper instanceof CreativeSlotWrapperAccessor accessor
                    && accessor.terrautils$getTarget() instanceof ExtraHotbar.ExtraSlot extra) {
                int x = ExtraHotbar.slotX(extra.extraIndex(), TERRAUTILS$PANEL_WIDTH);
                slots.set(i, CreativeSlotWrapperAccessor.terrautils$create(extra, wrapper.index, x, 112));
            }
        }
    }
}
