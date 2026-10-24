package com.starlight.terrautils.client;

import com.starlight.terrautils.network.SyncPlayerDataPayload;
import com.starlight.terrautils.registry.ModAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public final class ClientPacketHandler {
    private ClientPacketHandler() {}

    public static void handleSync(SyncPlayerDataPayload payload) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.getData(ModAttachments.PLAYER_DATA).applySync(
                    payload.backpackRows(), payload.hotbarRows(), payload.stackLevel(), payload.hotbarBank());
        }
    }
}
