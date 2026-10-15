package com.chainbreak.client;

import com.chainbreak.ClientConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/** Screen impact when the roll fires: white flash + the chain texture bursting from the centre. */
public final class ImpactOverlay implements IGuiOverlay {

    @Override
    public void render(ForgeGui gui, GuiGraphics g, float partialTick, int width, int height) {
        if (ClientState.flashTicks <= 0 || !ClientConfig.ENABLE_FLASH.get()) return;

        // t: 1.0 at the moment of impact -> 0.0 when finished
        float t = Math.max(0.0F, (ClientState.flashTicks - partialTick) / ClientState.FLASH_TICKS);
        float intensity = ClientConfig.FLASH_INTENSITY.get().floatValue();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // White flash (fades fast)
        int flashAlpha = (int) (255.0F * intensity * t * t);
        if (flashAlpha > 0) {
            g.fill(0, 0, width, height, (flashAlpha << 24) | 0xFFFFFF);
        }

        // Texture burst: grows from the centre while fading out
        float grow = 1.0F - t;
        float scale = 3.0F + grow * 9.0F;
        PoseStack pose = g.pose();
        pose.pushPose();
        pose.translate(width / 2.0F, height / 2.0F, 0.0F);
        pose.scale(scale, scale, 1.0F);
        g.setColor(1.0F, 1.0F, 1.0F, Math.min(1.0F, t * 1.3F));
        g.blit(GaugeOverlay.ICON, -16, -16, 0.0F, 0.0F, 32, 32, 32, 32);
        g.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        pose.popPose();

        RenderSystem.disableBlend();
    }
}
