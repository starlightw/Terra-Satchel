package com.starlight.terrautils.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.starlight.terrautils.Config;
import com.starlight.terrautils.TerraUtils;
import com.starlight.terrautils.client.render.HighlightScanner;
import com.starlight.terrautils.client.render.HighlightScanner.Highlight;
import com.starlight.terrautils.client.render.XrayRenderType;
import com.starlight.terrautils.data.PlayerExtData;
import com.starlight.terrautils.network.CycleHotbarPayload;
import com.starlight.terrautils.network.OpenBackpackPayload;
import com.starlight.terrautils.registry.ModAttachments;
import com.starlight.terrautils.registry.ModEffects;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@EventBusSubscriber(modid = TerraUtils.MOD_ID, value = Dist.CLIENT)
public final class ClientGameEvents {
    private static final int SCAN_INTERVAL = 20;

    private static List<Highlight> oreHighlights = List.of();
    private static List<Highlight> treasureHighlights = List.of();
    private static List<AbstractMinecartContainer> treasureCarts = List.of();
    private static int scanTimer;

    private ClientGameEvents() {}

    // ---------------------------------------------------------------- keys & scanning

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) {
            oreHighlights = List.of();
            treasureHighlights = List.of();
            treasureCarts = List.of();
            return;
        }

        while (ModKeys.OPEN_BACKPACK.consumeClick()) {
            requestOpenBackpack();
        }
        while (ModKeys.CYCLE_HOTBAR.consumeClick()) {
            if (player.getData(ModAttachments.PLAYER_DATA).getHotbarRows() > 0) {
                PacketDistributor.sendToServer(new CycleHotbarPayload(Screen.hasShiftDown()));
            }
        }

        boolean spelunker = player.hasEffect(ModEffects.SPELUNKER);
        boolean treasure = player.hasEffect(ModEffects.TREASURE_SENSE);
        if (!spelunker) oreHighlights = List.of();
        if (!treasure) {
            treasureHighlights = List.of();
            treasureCarts = List.of();
        }
        if (!spelunker && !treasure) {
            scanTimer = 0;
            return;
        }
        if (scanTimer-- > 0) {
            return;
        }
        scanTimer = SCAN_INTERVAL;
        if (spelunker) {
            oreHighlights = HighlightScanner.scan(mc.level, player.blockPosition(), Config.SPELUNKER_RADIUS.get(),
                    HighlightScanner::oreColor);
        }
        if (treasure) {
            int radius = Config.TREASURE_RADIUS.get();
            treasureHighlights = HighlightScanner.scan(mc.level, player.blockPosition(), radius,
                    HighlightScanner::treasureColor);
            treasureCarts = mc.level.getEntitiesOfClass(AbstractMinecartContainer.class,
                    player.getBoundingBox().inflate(radius), cart -> cart.distanceToSqr(player) <= (double) radius * radius);
        }
    }

    private static void requestOpenBackpack() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        if (mc.screen instanceof AbstractContainerScreen<?>) {
            // Close the current container properly first so a held cursor stack is returned to the inventory.
            mc.player.closeContainer();
        }
        PacketDistributor.sendToServer(OpenBackpackPayload.INSTANCE);
    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        HighlightScanner.clearCaches();
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        HighlightScanner.clearCaches();
    }

    // ---------------------------------------------------------------- world outlines

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        // AFTER_PARTICLES is the last stage that hands us a PoseStack in both Fast/Fancy and Fabulous modes.
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES
                || (oreHighlights.isEmpty() && treasureHighlights.isEmpty() && treasureCarts.isEmpty())) {
            return;
        }
        Vec3 cam = event.getCamera().getPosition();
        PoseStack pose = event.getPoseStack();
        pose.pushPose();
        pose.translate(-cam.x, -cam.y, -cam.z);

        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer lines = buffers.getBuffer(XrayRenderType.LINES);
        for (Highlight h : oreHighlights) {
            drawBox(pose, lines, h.box(), h.color());
        }
        for (Highlight h : treasureHighlights) {
            drawBox(pose, lines, h.box(), h.color());
        }
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        for (AbstractMinecartContainer cart : treasureCarts) {
            if (cart.isRemoved()) continue;
            Vec3 lerped = cart.getPosition(partialTick).subtract(cart.position());
            drawBox(pose, lines, cart.getBoundingBox().move(lerped), 0xFFC800);
        }
        buffers.endBatch(XrayRenderType.LINES);
        pose.popPose();
    }

    private static void drawBox(PoseStack pose, VertexConsumer lines, AABB box, int color) {
        float r = ((color >> 16) & 0xFF) / 255F;
        float g = ((color >> 8) & 0xFF) / 255F;
        float b = (color & 0xFF) / 255F;
        LevelRenderer.renderLineBox(pose, lines, box, r, g, b, 1.0F);
    }

    // ---------------------------------------------------------------- HUD: preview of extra hotbar rows

    @SubscribeEvent
    public static void onRenderHotbar(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.HOTBAR)) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || player.isSpectator() || mc.options.hideGui) {
            return;
        }
        PlayerExtData data = player.getData(ModAttachments.PLAYER_DATA);
        int rows = data.getHotbarRows();
        if (rows == 0) {
            return;
        }

        GuiGraphics g = event.getGuiGraphics();
        int x = g.guiWidth() / 2 + 91 + 6;
        if (player.getMainArm() == HumanoidArm.LEFT && !player.getOffhandItem().isEmpty()) {
            x += 29; // off-hand slot is drawn on the right
        }
        if (mc.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
            x += 22;
        }
        int rowHeight = 9;
        int bottom = g.guiHeight() - 3;
        for (int row = 0; row < rows; row++) {
            int y = bottom - (row + 1) * rowHeight - row; // row 1 (next) sits lowest, next to the hotbar
            g.fill(x - 1, y - 1, x + 9 * 9, y + rowHeight - 1, 0x80000000);
            g.pose().pushPose();
            g.pose().translate(x, y, 0);
            g.pose().scale(0.5F, 0.5F, 1.0F);
            for (int col = 0; col < 9; col++) {
                ItemStack stack = data.getHotbarBank().getStackInSlot(row * 9 + col);
                if (!stack.isEmpty()) {
                    g.renderItem(stack, col * 18, 0);
                }
            }
            g.pose().popPose();
        }
        g.drawString(mc.font, ModKeys.CYCLE_HOTBAR.getTranslatedKeyMessage(), x + 9 * 9 + 3, bottom - rowHeight, 0xA0FFFFFF, true);
    }

    // ---------------------------------------------------------------- inventory screen button / key

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen screen) {
            event.addListener(new FollowingButton(screen));
        }
    }

    @SubscribeEvent
    public static void onScreenKey(ScreenEvent.KeyPressed.Post event) {
        if (event.getScreen() instanceof InventoryScreen
                && ModKeys.OPEN_BACKPACK.isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode()))) {
            requestOpenBackpack();
            event.setCanceled(true);
        }
    }

    /** Small button in the inventory's top-right corner that tracks the panel when the recipe book shifts it. */
    private static final class FollowingButton extends Button {
        private final InventoryScreen screen;

        FollowingButton(InventoryScreen screen) {
            super(0, 0, 12, 12, Component.literal("+"), b -> requestOpenBackpack(), DEFAULT_NARRATION);
            this.screen = screen;
            setTooltip(net.minecraft.client.gui.components.Tooltip.create(
                    Component.translatable("button.terrautils.open_backpack", ModKeys.OPEN_BACKPACK.getTranslatedKeyMessage())));
            reposition();
        }

        private void reposition() {
            setPosition(screen.getGuiLeft() + 176 - 12 - 4, screen.getGuiTop() + 4);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            reposition();
            super.renderWidget(graphics, mouseX, mouseY, partialTick);
        }
    }
}
