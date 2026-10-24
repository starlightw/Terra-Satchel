package com.starlight.terrautils;

import com.starlight.terrautils.network.ModNetwork;
import com.starlight.terrautils.registry.ModArmorMaterials;
import com.starlight.terrautils.registry.ModAttachments;
import com.starlight.terrautils.registry.ModCreativeTabs;
import com.starlight.terrautils.registry.ModEffects;
import com.starlight.terrautils.registry.ModItems;
import com.starlight.terrautils.registry.ModMenus;
import com.starlight.terrautils.registry.ModPotions;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(TerraUtils.MOD_ID)
public class TerraUtils {
    public static final String MOD_ID = "terrautils";

    public TerraUtils(IEventBus modBus, ModContainer container) {
        ModArmorMaterials.ARMOR_MATERIALS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModEffects.EFFECTS.register(modBus);
        ModPotions.POTIONS.register(modBus);
        ModAttachments.ATTACHMENTS.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        modBus.addListener(ModNetwork::register);

        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
