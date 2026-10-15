package com.chainbreak;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Gameplay settings (config/chainbreak-common.toml).
 * Gauge defaults: 100 max, 2 per hit -> 50 hits needed to unlock one roll.
 */
public final class ChainConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue MAX_GAUGE;
    public static final ForgeConfigSpec.DoubleValue GAIN_PER_HIT;
    public static final ForgeConfigSpec.BooleanValue TOTAL_INVULNERABLE;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.push("gauge");
        MAX_GAUGE = b.comment("Gauge value needed before the roll can be used.")
                .defineInRange("maxGauge", 100.0D, 1.0D, 100000.0D);
        GAIN_PER_HIT = b.comment("Gauge gained for every attack that damages an entity.",
                        "Hits needed = maxGauge / gainPerHit  (default 100 / 2 = 50 hits).")
                .defineInRange("gainPerHit", 2.0D, 0.01D, 100000.0D);
        b.pop();

        b.push("roll");
        TOTAL_INVULNERABLE = b.comment("true  = take no damage at all while rolling (except void / /kill).",
                        "false = only Epic Fight's normal dodge rules apply (explosions, magic etc. can still hit).")
                .define("totalInvulnerable", true);
        b.pop();

        SPEC = b.build();
    }

    private ChainConfig() {}
}
