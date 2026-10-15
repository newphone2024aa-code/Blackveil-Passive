package com.chainbreak;

import com.chainbreak.net.GaugePacket;
import com.chainbreak.net.Network;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

/** Server-side gauge, saved in the player's persistent data. */
public final class GaugeStore {
    private static final String KEY = "chainbreak_gauge";

    public static float get(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        return data.contains(KEY) ? data.getFloat(KEY) : 0.0F;
    }

    public static void set(ServerPlayer player, float value) {
        float max = max();
        player.getPersistentData().putFloat(KEY, Math.max(0.0F, Math.min(max, value)));
    }

    /** @return true if the stored value changed */
    public static boolean add(ServerPlayer player, float amount) {
        float before = get(player);
        set(player, before + amount);
        return get(player) != before;
    }

    public static float max() {
        return ChainConfig.MAX_GAUGE.get().floatValue();
    }

    public static void sync(ServerPlayer player) {
        Network.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new GaugePacket(get(player), max()));
    }

    private GaugeStore() {}
}
