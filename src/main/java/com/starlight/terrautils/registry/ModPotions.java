package com.starlight.terrautils.registry;

import com.starlight.terrautils.TerraUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public final class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, TerraUtils.MOD_ID);

    // The potion "name" is the translation suffix: item.minecraft.potion.effect.<name>
    public static final DeferredHolder<Potion, Potion> SPELUNKER = POTIONS.register("spelunker",
            () -> new Potion("terrautils.spelunker", new MobEffectInstance(ModEffects.SPELUNKER, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_SPELUNKER = POTIONS.register("long_spelunker",
            () -> new Potion("terrautils.spelunker", new MobEffectInstance(ModEffects.SPELUNKER, 9600)));

    public static final DeferredHolder<Potion, Potion> TREASURE_SENSE = POTIONS.register("treasure_sense",
            () -> new Potion("terrautils.treasure_sense", new MobEffectInstance(ModEffects.TREASURE_SENSE, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_TREASURE_SENSE = POTIONS.register("long_treasure_sense",
            () -> new Potion("terrautils.treasure_sense", new MobEffectInstance(ModEffects.TREASURE_SENSE, 9600)));

    public static final DeferredHolder<Potion, Potion> INFERNO = POTIONS.register("inferno",
            () -> new Potion("terrautils.inferno", new MobEffectInstance(ModEffects.INFERNO_AURA, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_INFERNO = POTIONS.register("long_inferno",
            () -> new Potion("terrautils.inferno", new MobEffectInstance(ModEffects.INFERNO_AURA, 9600)));
    public static final DeferredHolder<Potion, Potion> STRONG_INFERNO = POTIONS.register("strong_inferno",
            () -> new Potion("terrautils.inferno", new MobEffectInstance(ModEffects.INFERNO_AURA, 1800, 1)));

    public static List<DeferredHolder<Potion, Potion>> all() {
        return List.of(SPELUNKER, LONG_SPELUNKER, TREASURE_SENSE, LONG_TREASURE_SENSE, INFERNO, LONG_INFERNO, STRONG_INFERNO);
    }

    private ModPotions() {}
}
