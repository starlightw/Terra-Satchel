package com.starlight.terrautils.item;

import com.starlight.terrautils.data.PlayerExtData;
import com.starlight.terrautils.network.ModNetwork;
import com.starlight.terrautils.registry.ModAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/** Consumable item that permanently raises one of the player's {@link UpgradeType} levels. */
public class UpgradeItem extends Item {
    private final UpgradeType type;

    public UpgradeItem(UpgradeType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        PlayerExtData data = player.getData(ModAttachments.PLAYER_DATA);
        int current = data.getLevel(type);
        if (current >= type.maxLevel()) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.terrautils.upgrade.maxed")
                        .withStyle(ChatFormatting.RED), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            data.setLevel(type, current + 1);
            stack.consume(1, player);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8F, 1.2F);
            player.displayClientMessage(Component.translatable("message.terrautils.upgrade." + type.key(),
                    current + 1, type.maxLevel()).withStyle(ChatFormatting.GREEN), true);
            if (player instanceof ServerPlayer serverPlayer) {
                ModNetwork.syncToClient(serverPlayer);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.terrautils." + type.key() + "_upgrade.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.terrautils.max_level", type.maxLevel()).withStyle(ChatFormatting.DARK_GRAY));
    }
}
