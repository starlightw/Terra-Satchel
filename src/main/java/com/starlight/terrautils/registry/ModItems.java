package com.starlight.terrautils.registry;

import com.starlight.terrautils.TerraUtils;
import com.starlight.terrautils.item.StompingHorseArmorItem;
import com.starlight.terrautils.item.UpgradeItem;
import com.starlight.terrautils.item.UpgradeType;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.core.component.DataComponents;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TerraUtils.MOD_ID);

    public static final DeferredItem<UpgradeItem> BACKPACK_UPGRADE = ITEMS.register("backpack_upgrade",
            () -> new UpgradeItem(UpgradeType.BACKPACK, new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<UpgradeItem> HOTBAR_UPGRADE = ITEMS.register("hotbar_upgrade",
            () -> new UpgradeItem(UpgradeType.HOTBAR, new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<UpgradeItem> HOTBAR_SLOTS_UPGRADE = ITEMS.register("hotbar_slots_upgrade",
            () -> new UpgradeItem(UpgradeType.HOTBAR_SLOTS, new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<UpgradeItem> STACK_UPGRADE = ITEMS.register("stack_upgrade",
            () -> new UpgradeItem(UpgradeType.STACK, new Item.Properties().stacksTo(16).rarity(Rarity.RARE)));

    public static final DeferredItem<StompingHorseArmorItem> STOMPING_HORSE_ARMOR = ITEMS.register("stomping_horse_armor",
            () -> new StompingHorseArmorItem(ModArmorMaterials.STOMPING, AnimalArmorItem.BodyType.EQUESTRIAN, false,
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                            .component(DataComponents.DYED_COLOR, new DyedItemColor(StompingHorseArmorItem.DEFAULT_COLOR, false))));

    private ModItems() {}
}
