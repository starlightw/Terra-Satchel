package com.starlight.terrautils.mixin;

import com.starlight.terrautils.event.StompHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** AbstractHorse overrides causeFallDamage without firing NeoForge's LivingFallEvent, so hook it directly. */
@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin {
    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void terrautils$stompOnLanding(float fallDistance, float multiplier, DamageSource source,
                                           CallbackInfoReturnable<Boolean> cir) {
        if (StompHandler.onHorseLanding((AbstractHorse) (Object) this, fallDistance)) {
            cir.setReturnValue(false);
        }
    }
}
