package com.starlight.terrautils.network;

import com.starlight.terrautils.TerraUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** Server -> client: upgrade levels plus the contents of the extra hotbar rows (drawn on the HUD). */
public record SyncPlayerDataPayload(int backpackRows, int hotbarRows, int stackLevel, List<ItemStack> hotbarBank)
        implements CustomPacketPayload {
    public static final Type<SyncPlayerDataPayload> TYPE = new Type<>(TerraUtils.id("sync_player_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlayerDataPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncPlayerDataPayload::backpackRows,
            ByteBufCodecs.VAR_INT, SyncPlayerDataPayload::hotbarRows,
            ByteBufCodecs.VAR_INT, SyncPlayerDataPayload::stackLevel,
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(64)), SyncPlayerDataPayload::hotbarBank,
            SyncPlayerDataPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
