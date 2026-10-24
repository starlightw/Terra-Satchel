package com.starlight.terrautils.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Horse armor that turns hard landings into a shockwave. The behaviour lives in
 * {@link com.starlight.terrautils.event.StompHandler}; this class only handles looks.
 */
public class StompingHorseArmorItem extends AnimalArmorItem {
    /** Default tint applied to the (grayscale-ish) iron horse armor texture. */
    public static final int DEFAULT_COLOR = 0xB8452E;

    private static final ResourceLocation TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/horse/armor/horse_armor_leather.png");

    public StompingHorseArmorItem(Holder<ArmorMaterial> material, BodyType bodyType, boolean hasOverlay, Properties properties) {
        super(material, bodyType, hasOverlay, properties);
    }

    @Override
    public ResourceLocation getTexture() {
        // Reuse the vanilla leather horse armor texture; it is tinted with the dyed color (item is in #minecraft:dyeable).
        return TEXTURE;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.terrautils.stomping_horse_armor.desc").withStyle(ChatFormatting.GRAY));
    }
}
