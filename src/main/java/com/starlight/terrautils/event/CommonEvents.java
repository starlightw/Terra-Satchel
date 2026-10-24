package com.starlight.terrautils.event;

import com.starlight.terrautils.TerraUtils;
import com.starlight.terrautils.data.PlayerExtData;
import com.starlight.terrautils.network.ModNetwork;
import com.starlight.terrautils.registry.ModAttachments;
import com.starlight.terrautils.registry.ModPotions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = TerraUtils.MOD_ID)
public final class CommonEvents {
    private CommonEvents() {}

    @SubscribeEvent
    public static void onBrewingRecipes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();
        builder.addMix(Potions.AWKWARD, Items.RAW_GOLD, ModPotions.SPELUNKER);
        builder.addMix(ModPotions.SPELUNKER, Items.REDSTONE, ModPotions.LONG_SPELUNKER);

        builder.addMix(Potions.AWKWARD, Items.COMPASS, ModPotions.TREASURE_SENSE);
        builder.addMix(ModPotions.TREASURE_SENSE, Items.REDSTONE, ModPotions.LONG_TREASURE_SENSE);

        builder.addMix(Potions.AWKWARD, Items.FIRE_CHARGE, ModPotions.INFERNO);
        builder.addMix(ModPotions.INFERNO, Items.REDSTONE, ModPotions.LONG_INFERNO);
        builder.addMix(ModPotions.INFERNO, Items.GLOWSTONE_DUST, ModPotions.STRONG_INFERNO);
    }

    /** Drop the extra storage like the normal inventory (respects keepInventory and Curse of Vanishing). */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onPlayerDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            return;
        }
        PlayerExtData data = player.getData(ModAttachments.PLAYER_DATA);
        data.drainAll(stack -> {
            if (EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
                return;
            }
            ItemEntity item = new ItemEntity(player.level(), player.getX(), player.getEyeY() - 0.3, player.getZ(), stack);
            item.setDefaultPickUpDelay();
            float speed = player.getRandom().nextFloat() * 0.5F;
            float angle = player.getRandom().nextFloat() * (float) (Math.PI * 2);
            item.setDeltaMovement(-Math.sin(angle) * speed, 0.2, Math.cos(angle) * speed);
            event.getDrops().add(item);
        });
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ModNetwork.syncToClient(player);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ModNetwork.syncToClient(player);
        }
    }

    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ModNetwork.syncToClient(player);
        }
    }
}
