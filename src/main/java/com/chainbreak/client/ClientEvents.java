package com.chainbreak.client;

import com.chainbreak.ChainBreakMod;
import com.chainbreak.ClientConfig;
import com.chainbreak.net.ActivatePacket;
import com.chainbreak.net.Network;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Forge-bus client events: key handling, ticking visuals, camera shake. */
@Mod.EventBusSubscriber(modid = ChainBreakMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        ClientState.tick();

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        while (ClientSetup.ACTIVATE.consumeClick()) {
            if (player == null || mc.screen != null) continue;

            if (!ClientState.isFull()) {
                // Gauge not full yet: shake the bar + small "denied" sound.
                ClientState.denyTicks = ClientState.DENY_TICKS;
                player.playSound(SoundEvents.DISPENSER_FAIL, 0.4F, 1.3F);
                continue;
            }

            sendActivate(player);
        }
    }

    /** Direction from WASD: forward roll along the pressed direction, backward roll when pressing S. */
    private static void sendActivate(LocalPlayer player) {
        Input input = player.input;
        float forward = input.forwardImpulse;
        float left = input.leftImpulse;
        float yRot = player.getYRot();

        boolean backward;
        float yaw;
        if (Math.abs(forward) < 0.01F && Math.abs(left) < 0.01F) {
            backward = false;
            yaw = yRot;
        } else {
            float offset = (float) -Math.toDegrees(Math.atan2(left, forward));
            if (forward >= 0.0F) {
                backward = false;
                yaw = Mth.wrapDegrees(yRot + offset);
            } else {
                backward = true;
                yaw = Mth.wrapDegrees(yRot + offset + 180.0F);
            }
        }

        Network.CHANNEL.sendToServer(new ActivatePacket(backward, yaw));
    }

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (ClientState.shakeTicks <= 0 || !ClientConfig.ENABLE_SHAKE.get()) return;

        float t = Math.max(0.0F, (ClientState.shakeTicks - (float) event.getPartialTick()) / ClientState.SHAKE_TICKS);
        float amp = t * t * ClientConfig.SHAKE_INTENSITY.get().floatValue() * 2.5F;
        double time = (System.currentTimeMillis() % 100000L) / 50.0D;

        event.setPitch(event.getPitch() + (float) Math.sin(time * 2.3D) * amp);
        event.setYaw(event.getYaw() + (float) Math.cos(time * 1.7D) * amp);
        event.setRoll(event.getRoll() + (float) Math.sin(time * 3.1D) * amp * 0.6F);
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientState.setGauge(0.0F, ClientState.max);
        ClientState.flashTicks = 0;
        ClientState.shakeTicks = 0;
    }

    private ClientEvents() {}
}
