package com.starlight.terrautils.item;

import com.starlight.terrautils.data.PlayerExtData;

public enum UpgradeType {
    BACKPACK("backpack", PlayerExtData.MAX_BACKPACK_ROWS),
    HOTBAR("hotbar", PlayerExtData.MAX_HOTBAR_ROWS),
    STACK("stack", PlayerExtData.MAX_STACK_LEVEL),
    HOTBAR_SLOTS("hotbar_slots", PlayerExtData.MAX_EXTRA_SLOTS);

    private final String key;
    private final int maxLevel;

    UpgradeType(String key, int maxLevel) {
        this.key = key;
        this.maxLevel = maxLevel;
    }

    public String key() {
        return key;
    }

    public int maxLevel() {
        return maxLevel;
    }
}
