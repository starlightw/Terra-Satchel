package com.starlight.terrautils.mixin;

import com.starlight.terrautils.data.PlayerExtData;
import com.starlight.terrautils.registry.ModAttachments;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** setItemInHand(MAIN_HAND, ...) writes into the selected extra hotbar slot when one is selected. */
@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "setItemSlot", at = @At("HEAD"), cancellable = true)
    private void terrautils$setExtraMainHand(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        if (slot != EquipmentSlot.MAINHAND) {
            return;
        }
        PlayerExtData data = this.getExistingData(ModAttachments.PLAYER_DATA).orElse(null);
        if (data == null || data.getSelectedExtra() < 0) {
            return;
        }
        int index = data.getSelectedExtra();
        ItemStack old = data.getExtraHotbar().getStackInSlot(index);
        data.getExtraHotbar().setStackInSlot(index, stack);
        this.onEquipItem(slot, old, stack);
        ci.cancel();
    }
}
