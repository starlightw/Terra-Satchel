package com.starlight.terrautils.network;

import com.starlight.terrautils.TerraUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Client -> server: open the extended inventory. */
public record OpenBackpackPayload() implements CustomPacketPayload {
    public static final OpenBackpackPayload INSTANCE = new OpenBackpackPayload();
    public static final Type<OpenBackpackPayload> TYPE = new Type<>(TerraUtils.id("open_backpack"));
    public static final StreamCodec<ByteBuf, OpenBackpackPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
