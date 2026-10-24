package com.starlight.terrautils.menu;

/** Pixel layout of the extended inventory, shared by the menu (slot positions) and the screen (background). */
public record ExtendedInventoryLayout(int backpackRows, int hotbarRows,
                                      int backpackLabelY, int backpackY,
                                      int hotbarLabelY, int hotbarY,
                                      int inventoryLabelY, int inventoryY, int playerHotbarY,
                                      int height) {
    public static final int WIDTH = 176;
    public static final int SLOT_X = 8;

    public static ExtendedInventoryLayout of(int backpackRows, int hotbarRows) {
        int y = 6;
        int backpackLabelY = y;
        int backpackY = y + 11;
        if (backpackRows > 0) {
            y = backpackY + backpackRows * 18 + 3;
        }
        int hotbarLabelY = y;
        int hotbarY = y + 11;
        if (hotbarRows > 0) {
            y = hotbarY + hotbarRows * 18 + 3;
        }
        int inventoryLabelY = y;
        int inventoryY = y + 11;
        int playerHotbarY = inventoryY + 58;
        return new ExtendedInventoryLayout(backpackRows, hotbarRows, backpackLabelY, backpackY, hotbarLabelY, hotbarY,
                inventoryLabelY, inventoryY, playerHotbarY, playerHotbarY + 18 + 7);
    }
}
