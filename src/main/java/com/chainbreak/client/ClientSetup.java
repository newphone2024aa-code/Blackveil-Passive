package com.chainbreak.client;

import com.chainbreak.ChainBreakMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/** Mod-bus client registrations: keybind + HUD overlays. */
@Mod.EventBusSubscriber(modid = ChainBreakMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {

    /** Default key: Caps Lock. Can be rebound in Options > Controls > "Chain Break". */
    public static final KeyMapping ACTIVATE = new KeyMapping(
            "key.chainbreak.activate",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_CAPS_LOCK,
            "key.categories.chainbreak");

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(ACTIVATE);
    }

    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("gauge", new GaugeOverlay());
        event.registerAboveAll("impact", new ImpactOverlay());
    }

    private ClientSetup() {}
}
