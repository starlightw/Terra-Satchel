package com.starlight.terrautils.registry;

import com.starlight.terrautils.TerraUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TerraUtils.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.terrautils"))
                    .icon(() -> new ItemStack(ModItems.BACKPACK_UPGRADE.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.BACKPACK_UPGRADE.get());
                        output.accept(ModItems.HOTBAR_UPGRADE.get());
                        output.accept(ModItems.HOTBAR_SLOTS_UPGRADE.get());
                        output.accept(ModItems.STACK_UPGRADE.get());
                        output.accept(ModItems.STOMPING_HORSE_ARMOR.get());
                        for (Item container : List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION)) {
                            ModPotions.all().forEach(p -> output.accept(PotionContents.createItemStack(container, p)));
                        }
                    })
                    .build());

    private ModCreativeTabs() {}
}
