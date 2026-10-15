package com.chainbreak.net;

import com.chainbreak.ChainBreakMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class Network {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ChainBreakMod.MODID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, ActivatePacket.class, ActivatePacket::encode, ActivatePacket::decode, ActivatePacket::handle);
        CHANNEL.registerMessage(id++, GaugePacket.class, GaugePacket::encode, GaugePacket::decode, GaugePacket::handle);
        CHANNEL.registerMessage(id++, ImpactPacket.class, ImpactPacket::encode, ImpactPacket::decode, ImpactPacket::handle);
    }

    private Network() {}
}
