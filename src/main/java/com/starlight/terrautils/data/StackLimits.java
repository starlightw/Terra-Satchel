package com.starlight.terrautils.data;

import com.starlight.terrautils.registry.ModAttachments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Per-player stack size rules applied to the player's own inventory and mod storage. */
public final class StackLimits {
    /** Vanilla refuses to save stacks above 99 (ItemStack codec range), so this is a hard cap. */
    public static final int HARD_CAP = 99;

    private StackLimits() {}

    public static int limit(ItemStack stack, int level) {
        int base = stack.getMaxStackSize();
        if (base <= 1 || level <= 0) {
            return base;
        }
        return Math.min(HARD_CAP, base << level);
    }

    public static int forPlayer(Player player, ItemStack stack) {
        if (player == null || stack.getMaxStackSize() <= 1) {
            return stack.getMaxStackSize();
        }
        PlayerExtData data = player.getExistingData(ModAttachments.PLAYER_DATA).orElse(null);
        return data == null ? stack.getMaxStackSize() : limit(stack, data.getStackLevel());
    }
}
