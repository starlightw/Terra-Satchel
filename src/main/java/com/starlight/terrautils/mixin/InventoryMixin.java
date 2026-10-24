package com.starlight.terrautils.mixin;

import com.starlight.terrautils.data.PlayerExtData;
import com.starlight.terrautils.data.StackLimits;
import com.starlight.terrautils.registry.ModAttachments;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class InventoryMixin implements Container {
    @Shadow
    @Final
    public Player player;

    /** Stack upgrade: the player's own inventory accepts bigger stacks. Overrides Container#getMaxStackSize(ItemStack). */
    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Math.min(StackLimits.HARD_CAP, StackLimits.forPlayer(this.player, stack));
    }

    /** Backpack upgrade: whatever doesn't fit in the main inventory overflows into the backpack. */
    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"), cancellable = true)
    private void terrautils$overflowIntoBackpack(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (slot != -1 || stack.isEmpty() || this.player.level().isClientSide) {
            return;
        }
        this.player.getExistingData(ModAttachments.PLAYER_DATA).ifPresent(data -> {
            if (data.getBackpackRows() == 0) {
                return;
            }
            int before = stack.getCount();
            ItemStack remainder = data.insertIntoBackpack(stack.copy());
            if (remainder.getCount() != before) {
                stack.setCount(remainder.getCount());
                cir.setReturnValue(true);
            }
        });
    }

    // ------------------------------------------------------------ extra hotbar slots

    private PlayerExtData terrautils$data() {
        return this.player.getExistingData(ModAttachments.PLAYER_DATA).orElse(null);
    }

    /** While an extra hotbar slot is selected, it is the main-hand item. */
    @Inject(method = "getSelected", at = @At("HEAD"), cancellable = true)
    private void terrautils$extraSelected(CallbackInfoReturnable<ItemStack> cir) {
        PlayerExtData data = terrautils$data();
        ItemStack extra = data == null ? null : data.getSelectedExtraStack();
        if (extra != null) {
            cir.setReturnValue(extra);
        }
    }

    /** Dropping (Q) takes from the selected extra slot. */
    @Inject(method = "removeFromSelected", at = @At("HEAD"), cancellable = true)
    private void terrautils$dropFromExtra(boolean removeStack, CallbackInfoReturnable<ItemStack> cir) {
        PlayerExtData data = terrautils$data();
        ItemStack extra = data == null ? null : data.getSelectedExtraStack();
        if (extra != null) {
            ItemStack taken = extra.isEmpty() ? ItemStack.EMPTY : extra.split(removeStack ? extra.getCount() : 1);
            if (extra.isEmpty()) {
                data.getExtraHotbar().setStackInSlot(data.getSelectedExtra(), ItemStack.EMPTY);
            }
            cir.setReturnValue(taken);
        }
    }

    /** Mining speed uses the selected extra slot's tool. */
    @Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
    private void terrautils$extraDestroySpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        PlayerExtData data = terrautils$data();
        ItemStack extra = data == null ? null : data.getSelectedExtraStack();
        if (extra != null) {
            cir.setReturnValue(extra.getDestroySpeed(state));
        }
    }

    /** Pick-block selects a vanilla hotbar slot, so leave the extra slot. */
    @Inject(method = {"setPickedItem", "pickSlot"}, at = @At("HEAD"))
    private void terrautils$leaveExtraOnPick(CallbackInfo ci) {
        PlayerExtData data = terrautils$data();
        if (data != null) {
            data.setSelectedExtra(-1);
        }
    }
}
