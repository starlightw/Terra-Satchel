package com.starlight.terrautils.event;

import com.starlight.terrautils.Config;
import com.starlight.terrautils.registry.ModDamageTypes;
import com.starlight.terrautils.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Stomping horse armor: when a player-ridden horse wearing it lands from a height, it deals area damage
 * around the landing point and neither horse nor rider takes fall damage.
 * Called from {@link com.starlight.terrautils.mixin.AbstractHorseMixin}.
 */
public final class StompHandler {
    private StompHandler() {}

    /** @return true if vanilla fall damage (for the horse and its riders) should be skipped. */
    public static boolean onHorseLanding(AbstractHorse horse, float fallDistance) {
        if (!horse.getBodyArmorItem().is(ModItems.STOMPING_HORSE_ARMOR.get())
                || !(horse.getFirstPassenger() instanceof Player rider)) {
            return false;
        }
        if (horse.level() instanceof ServerLevel level && fallDistance >= Config.STOMP_MIN_FALL.get()) {
            stomp(level, horse, rider, fallDistance);
        }
        rider.resetFallDistance();
        return true;
    }

    private static void stomp(ServerLevel level, AbstractHorse horse, Player rider, float fallDistance) {
        float damage = (float) Math.min(Config.STOMP_MAX_DAMAGE.get(), fallDistance * Config.STOMP_DAMAGE_PER_BLOCK.get());
        double radius = Mth.clamp(2.5 + fallDistance * 0.3, 2.5, 8.0);
        boolean hurtPlayers = Config.STOMP_HURTS_PLAYERS.get();
        Vec3 center = horse.position();

        DamageSource source = ModDamageTypes.source(level, ModDamageTypes.HORSE_STOMP, horse, rider);
        AABB box = new AABB(center, center).inflate(radius, 2.0, radius);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box)) {
            if (!canHit(horse, rider, target, hurtPlayers)) {
                continue;
            }
            double dist = Math.sqrt(target.distanceToSqr(center));
            if (dist > radius) {
                continue;
            }
            // Full damage at the center, half at the edge.
            float scaled = damage * (float) (1.0 - 0.5 * dist / radius);
            if (target.hurt(source, scaled)) {
                double dx = target.getX() - center.x;
                double dz = target.getZ() - center.z;
                double len = Math.max(0.1, Math.sqrt(dx * dx + dz * dz));
                double strength = 0.6 + fallDistance * 0.05;
                target.push(dx / len * strength, 0.35 + fallDistance * 0.02, dz / len * strength);
                target.hurtMarked = true;
            }
        }

        spawnEffects(level, horse, center, radius);
    }

    private static boolean canHit(AbstractHorse horse, Player rider, LivingEntity target, boolean hurtPlayers) {
        if (target == horse || target == rider || target.getVehicle() == horse || !target.isAlive()
                || target instanceof ArmorStand || target.isAlliedTo(rider)) {
            return false;
        }
        if (target instanceof OwnableEntity ownable && ownable.getOwner() == rider) {
            return false;
        }
        if (target instanceof Player player) {
            return hurtPlayers && !player.isCreative() && !player.isSpectator();
        }
        return true;
    }

    private static void spawnEffects(ServerLevel level, AbstractHorse horse, Vec3 center, double radius) {
        BlockPos ground = horse.blockPosition().below();
        BlockState groundState = level.getBlockState(ground);
        if (groundState.isAir()) {
            groundState = level.getBlockState(ground.below());
        }
        if (!groundState.isAir()) {
            BlockParticleOption dust = new BlockParticleOption(ParticleTypes.BLOCK, groundState);
            int points = Mth.ceil(radius * 8);
            for (int i = 0; i < points; i++) {
                double angle = Math.PI * 2 * i / points;
                for (double r = 1.0; r <= radius; r += 1.5) {
                    level.sendParticles(dust, center.x + Math.cos(angle) * r, center.y + 0.1, center.z + Math.sin(angle) * r,
                            2, 0.1, 0.05, 0.1, 0.15);
                }
            }
        }
        level.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y + 0.3, center.z, 1, 0, 0, 0, 0);
        level.playSound(null, center.x, center.y, center.z, SoundEvents.MACE_SMASH_GROUND_HEAVY, SoundSource.PLAYERS, 1.0F, 0.8F);
        level.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.4F, 1.4F);
    }
}
