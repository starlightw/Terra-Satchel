package com.starlight.terrautils;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue SPELUNKER_RADIUS;
    public static final ModConfigSpec.IntValue TREASURE_RADIUS;

    public static final ModConfigSpec.DoubleValue INFERNO_DAMAGE;
    public static final ModConfigSpec.DoubleValue INFERNO_RADIUS;

    public static final ModConfigSpec.DoubleValue STOMP_MIN_FALL;
    public static final ModConfigSpec.DoubleValue STOMP_DAMAGE_PER_BLOCK;
    public static final ModConfigSpec.DoubleValue STOMP_MAX_DAMAGE;
    public static final ModConfigSpec.BooleanValue STOMP_HURTS_PLAYERS;

    static {
        BUILDER.push("potions");
        SPELUNKER_RADIUS = BUILDER.comment("Block radius scanned by the Spelunker effect (client side).")
                .defineInRange("spelunkerRadius", 24, 4, 64);
        TREASURE_RADIUS = BUILDER.comment("Block radius scanned by the Treasure Sense effect (client side).")
                .defineInRange("treasureRadius", 48, 4, 96);
        INFERNO_DAMAGE = BUILDER.comment("Inferno aura damage per second at level I (+100% per extra level).")
                .defineInRange("infernoDamage", 3.0, 0.0, 100.0);
        INFERNO_RADIUS = BUILDER.comment("Inferno aura radius at level I (+1 block per extra level).")
                .defineInRange("infernoRadius", 4.0, 1.0, 16.0);
        BUILDER.pop();

        BUILDER.push("stomping_horse_armor");
        STOMP_MIN_FALL = BUILDER.comment("Minimum fall distance (blocks) for the ridden horse to trigger a stomp.")
                .defineInRange("minFallDistance", 3.0, 0.0, 64.0);
        STOMP_DAMAGE_PER_BLOCK = BUILDER.comment("Stomp damage per block fallen.")
                .defineInRange("damagePerBlock", 2.0, 0.0, 20.0);
        STOMP_MAX_DAMAGE = BUILDER.comment("Maximum stomp damage.")
                .defineInRange("maxDamage", 40.0, 1.0, 1000.0);
        STOMP_HURTS_PLAYERS = BUILDER.comment("Whether the stomp also damages other players.")
                .define("hurtsPlayers", false);
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {}
}
