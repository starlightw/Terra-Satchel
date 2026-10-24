package com.starlight.terrautils.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/** An effect with no server-side behaviour; the client reacts to its presence. */
public class MarkerEffect extends MobEffect {
    public MarkerEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
