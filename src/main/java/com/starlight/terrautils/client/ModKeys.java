package com.starlight.terrautils.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public final class ModKeys {
    public static final String CATEGORY = "key.categories.terrautils";

    public static final KeyMapping OPEN_BACKPACK = new KeyMapping("key.terrautils.open_backpack",
            KeyConflictContext.UNIVERSAL, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY);

    /** Hold Shift to cycle the other way. */
    public static final KeyMapping CYCLE_HOTBAR = new KeyMapping("key.terrautils.cycle_hotbar",
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);

    private ModKeys() {}
}
