package com.starlight.terrautils.gametest;

import com.starlight.terrautils.TerraUtils;
import com.starlight.terrautils.data.PlayerExtData;
import com.starlight.terrautils.item.UpgradeType;
import com.starlight.terrautils.registry.ModAttachments;
import com.starlight.terrautils.registry.ModEffects;
import com.starlight.terrautils.registry.ModItems;
import com.starlight.terrautils.registry.ModPotions;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Run with `gradlew runGameTestServer`, or `/test runall` in a dev world. */
@GameTestHolder(TerraUtils.MOD_ID)
@PrefixGameTestTemplate(false)
public final class TerraUtilsGameTests {
    /** Resolved as terrautils:platform (the holder supplies the namespace). */
    private static final String TEMPLATE = "platform";

    private TerraUtilsGameTests() {}

    private static PlayerExtData data(Player player) {
        return player.getData(ModAttachments.PLAYER_DATA);
    }

    @GameTest(template = TEMPLATE)
    public static void backpackCatchesOverflow(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        for (int i = 0; i < 36; i++) {
            inv.setItem(i, new ItemStack(Items.DIRT, 64));
        }
        helper.assertFalse(inv.add(new ItemStack(Items.STONE, 5)), "full inventory without backpack should reject");

        data(player).setLevel(UpgradeType.BACKPACK, 1);
        ItemStack stone = new ItemStack(Items.STONE, 10);
        helper.assertTrue(inv.add(stone), "overflow should be accepted by the backpack");
        helper.assertTrue(stone.isEmpty(), "whole stack should be consumed");
        ItemStack stored = data(player).getBackpack().getStackInSlot(0);
        helper.assertTrue(stored.is(Items.STONE) && stored.getCount() == 10, "backpack slot 0 should hold 10 stone, got " + stored);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void stackUpgradeRaisesLimit(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        data(player).setLevel(UpgradeType.STACK, 1);

        inv.add(new ItemStack(Items.COBBLESTONE, 99));
        helper.assertTrue(inv.getItem(0).getCount() == 99, "cobblestone should stack to 99, got " + inv.getItem(0));
        inv.add(new ItemStack(Items.ENDER_PEARL, 32));
        helper.assertTrue(inv.getItem(1).getCount() == 32, "ender pearls should stack to 32, got " + inv.getItem(1));
        inv.add(new ItemStack(Items.DIAMOND_SWORD));
        inv.add(new ItemStack(Items.DIAMOND_SWORD));
        helper.assertTrue(inv.getItem(2).getCount() == 1 && inv.getItem(3).getCount() == 1, "unstackables stay unstackable");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void hotbarCycles(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        PlayerExtData data = data(player);
        data.setLevel(UpgradeType.HOTBAR, 2);
        inv.setItem(0, new ItemStack(Items.DIRT));
        data.getHotbarBank().setStackInSlot(0, new ItemStack(Items.STONE));
        data.getHotbarBank().setStackInSlot(9, new ItemStack(Items.SAND));

        data.cycleHotbar(inv, false);
        helper.assertTrue(inv.getItem(0).is(Items.STONE), "row 1 moves into the hotbar");
        helper.assertTrue(data.getHotbarBank().getStackInSlot(0).is(Items.SAND), "row 2 moves up to row 1");
        helper.assertTrue(data.getHotbarBank().getStackInSlot(9).is(Items.DIRT), "old hotbar goes to the last row");

        data.cycleHotbar(inv, true);
        helper.assertTrue(inv.getItem(0).is(Items.DIRT), "reverse cycle restores the hotbar");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void stompingArmorHurtsNearbyAndNegatesFall(GameTestHelper helper) {
        Horse horse = helper.spawnWithNoFreeWill(EntityType.HORSE, 3, 1, 3);
        horse.setBodyArmorItem(new ItemStack(ModItems.STOMPING_HORSE_ARMOR.get()));
        Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, 5, 1, 3);
        Player rider = helper.makeMockPlayer(GameType.SURVIVAL);
        rider.moveTo(horse.position());
        helper.assertTrue(rider.startRiding(horse, true), "rider should mount");

        float horseHealth = horse.getHealth();
        horse.causeFallDamage(12.0F, 1.0F, horse.damageSources().fall());
        helper.assertTrue(horse.getHealth() == horseHealth, "horse should take no fall damage");
        helper.assertTrue(zombie.getHealth() < zombie.getMaxHealth(), "zombie should be hit by the stomp");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void infernoBurnsEnemies(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.moveTo(helper.absoluteVec(new Vec3(3.5, 1, 3.5)));
        player.tickCount = 0;
        Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, 5, 1, 3);
        ModEffects.INFERNO_AURA.value().applyEffectTick(player, 0);
        helper.assertTrue(zombie.getHealth() < zombie.getMaxHealth(), "zombie inside the aura should be hurt");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void brewingRecipesRegistered(GameTestHelper helper) {
        PotionBrewing brewing = helper.getLevel().potionBrewing();
        ItemStack awkward = PotionContents.createItemStack(Items.POTION, Potions.AWKWARD);
        checkMix(helper, brewing, awkward, Items.RAW_GOLD, ModPotions.SPELUNKER.getKey().location().getPath());
        checkMix(helper, brewing, awkward, Items.COMPASS, ModPotions.TREASURE_SENSE.getKey().location().getPath());
        checkMix(helper, brewing, awkward, Items.FIRE_CHARGE, ModPotions.INFERNO.getKey().location().getPath());
        helper.succeed();
    }

    private static void checkMix(GameTestHelper helper, PotionBrewing brewing, ItemStack base,
                                 net.minecraft.world.item.Item ingredient, String expected) {
        ItemStack result = brewing.mix(new ItemStack(ingredient), base.copy());
        String got = result.getOrDefault(net.minecraft.core.component.DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
                .potion().flatMap(h -> h.unwrapKey()).map(k -> k.location().getPath()).orElse("none");
        helper.assertTrue(got.equals(expected), "awkward + " + ingredient + " should brew " + expected + ", got " + got);
    }
}
