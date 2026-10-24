package com.starlight.terrautils.client;

import com.starlight.terrautils.TerraUtils;
import com.starlight.terrautils.client.screen.ExtendedInventoryScreen;
import com.starlight.terrautils.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = TerraUtils.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {}

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(ModKeys.OPEN_BACKPACK);
        event.register(ModKeys.CYCLE_HOTBAR);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.EXTENDED_INVENTORY.get(), ExtendedInventoryScreen::new);
    }
}
