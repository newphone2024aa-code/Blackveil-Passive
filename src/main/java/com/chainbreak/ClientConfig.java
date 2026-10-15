package com.chainbreak;

import net.minecraftforge.common.ForgeConfigSpec;

/** Visual settings (config/chainbreak-client.toml). */
public final class ClientConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue SHOW_HUD;
    public static final ForgeConfigSpec.IntValue HUD_OFFSET_X;
    public static final ForgeConfigSpec.IntValue HUD_OFFSET_Y;
    public static final ForgeConfigSpec.BooleanValue ENABLE_FLASH;
    public static final ForgeConfigSpec.DoubleValue FLASH_INTENSITY;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SHAKE;
    public static final ForgeConfigSpec.DoubleValue SHAKE_INTENSITY;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.push("hud");
        SHOW_HUD = b.comment("Show the gauge bar.").define("showHud", true);
        HUD_OFFSET_X = b.comment("Horizontal offset of the bar from screen centre.")
                .defineInRange("offsetX", 0, -2000, 2000);
        HUD_OFFSET_Y = b.comment("Vertical offset of the bar (distance from the bottom of the screen).")
                .defineInRange("offsetY", 66, 0, 2000);
        b.pop();

        b.push("impact");
        ENABLE_FLASH = b.comment("White flash + big texture burst on screen when the roll is used.")
                .define("enableFlash", true);
        FLASH_INTENSITY = b.defineInRange("flashIntensity", 0.5D, 0.0D, 1.0D);
        ENABLE_SHAKE = b.comment("Camera shake when the roll is used.").define("enableShake", true);
        SHAKE_INTENSITY = b.defineInRange("shakeIntensity", 1.0D, 0.0D, 5.0D);
        b.pop();

        SPEC = b.build();
    }

    private ClientConfig() {}
}
