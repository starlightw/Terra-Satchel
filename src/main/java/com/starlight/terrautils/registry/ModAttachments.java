package com.starlight.terrautils.registry;

import com.starlight.terrautils.TerraUtils;
import com.starlight.terrautils.data.PlayerExtData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, TerraUtils.MOD_ID);

    /**
     * Copied on death so upgrade levels are permanent; stored items are dropped (or kept with keepInventory)
     * in {@link com.starlight.terrautils.event.CommonEvents} before the copy happens.
     */
    public static final Supplier<AttachmentType<PlayerExtData>> PLAYER_DATA = ATTACHMENTS.register("player_data",
            () -> AttachmentType.serializable(PlayerExtData::new).copyOnDeath().build());

    private ModAttachments() {}
}
