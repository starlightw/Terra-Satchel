package com.starlight.terrautils.network;

import com.starlight.terrautils.client.ClientPacketHandler;
import com.starlight.terrautils.data.PlayerExtData;
import com.starlight.terrautils.menu.ExtendedInventoryMenu;
import com.starlight.terrautils.registry.ModAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.List;

public final class ModNetwork {
    private ModNetwork() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(SyncPlayerDataPayload.TYPE, SyncPlayerDataPayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> ClientPacketHandler.handleSync(payload)));
        registrar.playToServer(OpenBackpackPayload.TYPE, OpenBackpackPayload.STREAM_CODEC, ModNetwork::handleOpen);
        registrar.playToServer(CycleHotbarPayload.TYPE, CycleHotbarPayload.STREAM_CODEC, ModNetwork::handleCycle);
    }

    public static void syncToClient(ServerPlayer player) {
        PlayerExtData data = player.getData(ModAttachments.PLAYER_DATA);
        List<ItemStack> bank = new ArrayList<>(data.getHotbarRows() * 9);
        for (int i = 0; i < data.getHotbarRows() * 9; i++) {
            bank.add(data.getHotbarBank().getStackInSlot(i).copy());
        }
        PacketDistributor.sendToPlayer(player, new SyncPlayerDataPayload(
                data.getBackpackRows(), data.getHotbarRows(), data.getStackLevel(), bank));
    }

    private static void handleOpen(OpenBackpackPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player) || player.isSpectator()) {
                return;
            }
            PlayerExtData data = player.getData(ModAttachments.PLAYER_DATA);
            int backpackRows = data.getBackpackRows();
            int hotbarRows = data.getHotbarRows();
            if (backpackRows == 0 && hotbarRows == 0) {
                player.displayClientMessage(Component.translatable("message.terrautils.no_upgrades")
                        .withStyle(ChatFormatting.YELLOW), true);
                return;
            }
            syncToClient(player);
            player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new ExtendedInventoryMenu(id, inv, backpackRows, hotbarRows),
                    Component.translatable("container.terrautils.extended_inventory")), buf -> {
                buf.writeVarInt(backpackRows);
                buf.writeVarInt(hotbarRows);
            });
        });
    }

    private static void handleCycle(CycleHotbarPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player) || player.isSpectator()
                    || player.containerMenu != player.inventoryMenu) {
                return;
            }
            PlayerExtData data = player.getData(ModAttachments.PLAYER_DATA);
            if (data.getHotbarRows() == 0) {
                return;
            }
            player.stopUsingItem();
            data.cycleHotbar(player.getInventory(), payload.reverse());
            syncToClient(player);
        });
    }
}
