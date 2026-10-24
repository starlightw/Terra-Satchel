package com.starlight.terrautils.registry;

import com.starlight.terrautils.TerraUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/** Damage types are data-driven: see data/terrautils/damage_type/*.json. */
public final class ModDamageTypes {
    public static final ResourceKey<DamageType> INFERNO_AURA = key("inferno_aura");
    public static final ResourceKey<DamageType> HORSE_STOMP = key("horse_stomp");

    private ModDamageTypes() {}

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, TerraUtils.id(name));
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> type, @Nullable Entity direct, @Nullable Entity causing) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), direct, causing);
    }
}
