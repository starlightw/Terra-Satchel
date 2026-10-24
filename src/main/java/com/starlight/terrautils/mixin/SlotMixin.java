package com.starlight.terrautils.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Slots that show the player's inventory use the inventory's (upgraded) per-item limit instead of the item's. */
@Mixin(Slot.class)
public abstract class SlotMixin {
    @Shadow
    @Final
    public Container container;

    @Shadow
    public abstract int getMaxStackSize();

    @Inject(method = "getMaxStackSize(Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private void terrautils$playerInventoryLimit(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (this.container instanceof Inventory inventory) {
            cir.setReturnValue(Math.min(this.getMaxStackSize(), inventory.getMaxStackSize(stack)));
        }
    }
}
