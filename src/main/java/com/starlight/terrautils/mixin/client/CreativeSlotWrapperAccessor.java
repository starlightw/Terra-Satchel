package com.starlight.terrautils.mixin.client;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen$SlotWrapper")
public interface CreativeSlotWrapperAccessor {
    @Accessor("target")
    Slot terrautils$getTarget();

    @Invoker("<init>")
    static Slot terrautils$create(Slot target, int index, int x, int y) {
        throw new AssertionError();
    }
}
