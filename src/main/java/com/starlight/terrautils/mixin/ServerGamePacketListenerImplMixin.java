package com.starlight.terrautils.mixin;

import com.starlight.terrautils.data.StackLimits;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Creative mode: accept upgraded stack sizes and the extra hotbar slots appended to the inventory menu. */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Redirect(method = "handleSetCreativeModeSlot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I"))
    private int terrautils$creativeStackLimit(ItemStack stack) {
        return StackLimits.forPlayer(this.player, stack);
    }

    @ModifyConstant(method = "handleSetCreativeModeSlot", constant = @Constant(intValue = 45))
    private int terrautils$creativeSlotRange(int original) {
        return this.player.inventoryMenu.slots.size() - 1;
    }
}
