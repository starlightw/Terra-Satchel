package com.starlight.terrautils.registry;

import com.starlight.terrautils.TerraUtils;
import com.starlight.terrautils.effect.InfernoAuraEffect;
import com.starlight.terrautils.effect.MarkerEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, TerraUtils.MOD_ID);

    /** Highlights ores through walls (rendered client side). */
    public static final DeferredHolder<MobEffect, MobEffect> SPELUNKER = EFFECTS.register("spelunker",
            () -> new MarkerEffect(MobEffectCategory.BENEFICIAL, 0xE8C547));

    /** Highlights chests, barrels and other loot containers through walls (rendered client side). */
    public static final DeferredHolder<MobEffect, MobEffect> TREASURE_SENSE = EFFECTS.register("treasure_sense",
            () -> new MarkerEffect(MobEffectCategory.BENEFICIAL, 0xB07A3E));

    /** Burns nearby enemies every second. */
    public static final DeferredHolder<MobEffect, MobEffect> INFERNO_AURA = EFFECTS.register("inferno_aura",
            () -> new InfernoAuraEffect(MobEffectCategory.BENEFICIAL, 0xFF5A00));

    private ModEffects() {}
}
