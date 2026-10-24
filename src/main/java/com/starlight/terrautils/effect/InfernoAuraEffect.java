package com.starlight.terrautils.effect;

import com.starlight.terrautils.Config;
import com.starlight.terrautils.registry.ModDamageTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

/**
 * Terraria's Inferno potion: once per second, burns every hostile creature within the aura radius.
 */
public class InfernoAuraEffect extends MobEffect {
    private static final int INTERVAL = 20;

    public InfernoAuraEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity owner, int amplifier) {
        if (!(owner.level() instanceof ServerLevel level) || owner.isSpectator()) {
            return true;
        }
        double radius = Config.INFERNO_RADIUS.get() + amplifier;

        if (owner.tickCount % 4 == 0) {
            spawnRing(level, owner, radius);
        }
        if (owner.tickCount % INTERVAL != 0) {
            return true;
        }

        float damage = (float) (Config.INFERNO_DAMAGE.get() * (amplifier + 1));
        DamageSource source = ModDamageTypes.source(level, ModDamageTypes.INFERNO_AURA, owner, owner);
        AABB box = owner.getBoundingBox().inflate(radius);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, e -> isValidTarget(owner, e))) {
            if (target.distanceToSqr(owner) > radius * radius) {
                continue;
            }
            if (target.hurt(source, damage)) {
                target.igniteForSeconds(2);
                level.sendParticles(ParticleTypes.FLAME, target.getX(), target.getY(0.5), target.getZ(),
                        6, target.getBbWidth() * 0.4, target.getBbHeight() * 0.3, target.getBbWidth() * 0.4, 0.02);
            }
        }
        return true;
    }

    private static boolean isValidTarget(LivingEntity owner, LivingEntity target) {
        if (target == owner || !target.isAlive() || target instanceof ArmorStand || target.isAlliedTo(owner)) {
            return false;
        }
        if (target instanceof OwnableEntity ownable && ownable.getOwner() == owner) {
            return false;
        }
        if (owner instanceof Player) {
            // A player's aura only hurts monsters and creatures that are hunting that player.
            return target instanceof Enemy || (target instanceof Mob mob && mob.getTarget() == owner);
        }
        // A mob with the aura (e.g. hit by a splash potion) burns players instead.
        return target instanceof Player player && !player.isCreative() && !player.isSpectator();
    }

    private static void spawnRing(ServerLevel level, LivingEntity owner, double radius) {
        int points = Mth.ceil(radius * 4);
        double offset = (owner.tickCount * 0.05) % (Math.PI * 2);
        for (int i = 0; i < points; i++) {
            double angle = offset + Math.PI * 2 * i / points;
            double x = owner.getX() + Math.cos(angle) * radius;
            double z = owner.getZ() + Math.sin(angle) * radius;
            level.sendParticles(ParticleTypes.FLAME, x, owner.getY() + 0.1, z, 1, 0, 0.02, 0, 0.0);
        }
    }
}
