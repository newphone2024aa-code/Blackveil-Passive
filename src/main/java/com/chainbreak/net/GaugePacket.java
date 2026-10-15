package com.chainbreak.net;

import com.chainbreak.client.ClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Server -> client: current gauge value. */
public record GaugePacket(float current, float max) {

    public static void encode(GaugePacket msg, FriendlyByteBuf buf) {
        buf.writeFloat(msg.current);
        buf.writeFloat(msg.max);
    }

    public static GaugePacket decode(FriendlyByteBuf buf) {
        return new GaugePacket(buf.readFloat(), buf.readFloat());
    }

    public static void handle(GaugePacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        // ClientState is a plain data holder (no client-only imports), so this is safe on any side.
        context.enqueueWork(() -> ClientState.setGauge(msg.current, msg.max));
        context.setPacketHandled(true);
    }
}
