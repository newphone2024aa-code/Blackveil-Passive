package com.chainbreak.client;

/**
 * Plain data holder for client visuals. Intentionally has no Minecraft client imports
 * so network handlers can touch it without loading client-only classes.
 */
public final class ClientState {
    public static final int FLASH_TICKS = 10;
    public static final int SHAKE_TICKS = 12;
    public static final int DENY_TICKS = 6;

    public static volatile float gauge = 0.0F;
    public static volatile float max = 100.0F;

    public static int flashTicks = 0;
    public static int shakeTicks = 0;
    public static int denyTicks = 0;

    public static void setGauge(float current, float maxValue) {
        gauge = current;
        max = Math.max(1.0F, maxValue);
    }

    public static boolean isFull() {
        return gauge >= max - 0.001F;
    }

    public static float fraction() {
        return Math.max(0.0F, Math.min(1.0F, gauge / max));
    }

    public static void startImpact() {
        flashTicks = FLASH_TICKS;
        shakeTicks = SHAKE_TICKS;
    }

    public static void tick() {
        if (flashTicks > 0) flashTicks--;
        if (shakeTicks > 0) shakeTicks--;
        if (denyTicks > 0) denyTicks--;
    }

    private ClientState() {}
}
