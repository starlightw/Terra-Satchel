package com.starlight.terrautils.client.screen;

import com.starlight.terrautils.client.ModKeys;
import com.starlight.terrautils.menu.ExtendedInventoryLayout;
import com.starlight.terrautils.menu.ExtendedInventoryMenu;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ExtendedInventoryScreen extends AbstractContainerScreen<ExtendedInventoryMenu> {
    private static final int LABEL_COLOR = 0x404040;
    private static final Component BACKPACK_LABEL = Component.translatable("container.terrautils.backpack");
    private static final Component HOTBAR_LABEL = Component.translatable("container.terrautils.hotbar_rows");

    private final ExtendedInventoryLayout layout;

    public ExtendedInventoryScreen(ExtendedInventoryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.layout = menu.getLayout();
        this.imageWidth = ExtendedInventoryLayout.WIDTH;
        this.imageHeight = layout.height();
        this.inventoryLabelY = layout.inventoryLabelY();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        if (layout.backpackRows() > 0) {
            graphics.drawString(font, BACKPACK_LABEL, 8, layout.backpackLabelY(), LABEL_COLOR, false);
        }
        if (layout.hotbarRows() > 0) {
            graphics.drawString(font, HOTBAR_LABEL, 8, layout.hotbarLabelY(), LABEL_COLOR, false);
            Component hint = Component.translatable("container.terrautils.hotbar_hint", ModKeys.CYCLE_HOTBAR.getTranslatedKeyMessage());
            graphics.drawString(font, hint, imageWidth - 8 - font.width(hint), layout.hotbarLabelY(), 0x707070, false);
        }
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL_COLOR, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        drawPanel(graphics, x, y, imageWidth, imageHeight);

        for (int row = 0; row < layout.backpackRows(); row++) {
            drawSlotRow(graphics, x + 7, y + layout.backpackY() - 1 + row * 18);
        }
        for (int row = 0; row < layout.hotbarRows(); row++) {
            int rowY = y + layout.hotbarY() - 1 + row * 18;
            drawSlotRow(graphics, x + 7, rowY);
            graphics.drawString(font, String.valueOf(row + 1), x + 2, rowY + 5, 0x808080, false);
        }
        for (int row = 0; row < 3; row++) {
            drawSlotRow(graphics, x + 7, y + layout.inventoryY() - 1 + row * 18);
        }
        drawSlotRow(graphics, x + 7, y + layout.playerHotbarY() - 1);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ModKeys.OPEN_BACKPACK.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /** Vanilla-style raised gray panel. */
    private static void drawPanel(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x + 1, y, x + w - 1, y + h, 0xFF000000);
        g.fill(x, y + 1, x + w, y + h - 1, 0xFF000000);
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, 0xFFFFFFFF);
        g.fill(x + 2, y + 2, x + w - 1, y + h - 1, 0xFF555555);
        g.fill(x + 2, y + 2, x + w - 2, y + h - 2, 0xFFC6C6C6);
    }

    private static void drawSlotRow(GuiGraphics g, int x, int y) {
        for (int col = 0; col < 9; col++) {
            int sx = x + col * 18;
            g.fill(sx, y, sx + 18, y + 18, 0xFFFFFFFF);
            g.fill(sx, y, sx + 17, y + 17, 0xFF373737);
            g.fill(sx + 1, y + 1, sx + 17, y + 17, 0xFF8B8B8B);
        }
    }
}
