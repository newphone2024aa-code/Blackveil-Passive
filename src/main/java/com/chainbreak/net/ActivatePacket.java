package com.chainbreak.net;

import com.chainbreak.RollLogic;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Client -> server: "the keybind was pressed", with the roll direction. */
public record ActivatePacket(boolean backward, float yaw) {

    public static void encode(ActivatePacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.backward);
        buf.writeFloat(msg.yaw);
    }

    public static ActivatePacket decode(FriendlyByteBuf buf) {
        return new ActivatePacket(buf.readBoolean(), buf.readFloat());
    }

    public static void handle(ActivatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && Float.isFinite(msg.yaw)) {
                RollLogic.tryActivate(player, msg.backward, msg.yaw);
            }
        });
        context.setPacketHandled(true);
    }
}
