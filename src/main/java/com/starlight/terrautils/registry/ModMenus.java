package com.starlight.terrautils.registry;

import com.starlight.terrautils.TerraUtils;
import com.starlight.terrautils.menu.ExtendedInventoryMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, TerraUtils.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ExtendedInventoryMenu>> EXTENDED_INVENTORY =
            MENUS.register("extended_inventory", () -> IMenuTypeExtension.create(ExtendedInventoryMenu::fromNetwork));

    private ModMenus() {}
}
