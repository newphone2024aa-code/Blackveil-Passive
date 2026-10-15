package com.chainbreak.net;

import com.chainbreak.client.ClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Server -> client: the roll was used, play the on-screen impact. */
public record ImpactPacket() {

    public static void encode(ImpactPacket msg, FriendlyByteBuf buf) {}

    public static ImpactPacket decode(FriendlyByteBuf buf) {
        return new ImpactPacket();
    }

    public static void handle(ImpactPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(ClientState::startImpact);
        context.setPacketHandled(true);
    }
}
