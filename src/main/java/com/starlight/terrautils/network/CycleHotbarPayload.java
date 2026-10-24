package com.starlight.terrautils.network;

import com.starlight.terrautils.TerraUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Client -> server: rotate the hotbar with the extra hotbar rows. */
public record CycleHotbarPayload(boolean reverse) implements CustomPacketPayload {
    public static final Type<CycleHotbarPayload> TYPE = new Type<>(TerraUtils.id("cycle_hotbar"));
    public static final StreamCodec<ByteBuf, CycleHotbarPayload> STREAM_CODEC =
            ByteBufCodecs.BOOL.map(CycleHotbarPayload::new, CycleHotbarPayload::reverse);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
