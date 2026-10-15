package com.chainbreak.client;

import com.chainbreak.ChainBreakMod;
import com.chainbreak.ClientConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/** Epic Fight-style gauge bar. Fills with every hit, glows when ready. */
public final class GaugeOverlay implements IGuiOverlay {
    static final ResourceLocation ICON = new ResourceLocation(ChainBreakMod.MODID, "textures/gui/chain_break.png");

    private static final int BAR_W = 110;
    private static final int BAR_H = 7;

    @Override
    public void render(ForgeGui gui, GuiGraphics g, float partialTick, int width, int height) {
        if (!ClientConfig.SHOW_HUD.get()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        float frac = ClientState.fraction();
        boolean full = ClientState.isFull();
        float pulse = (float) (0.5D + 0.5D * Math.sin(System.currentTimeMillis() / 140.0D));

        int shake = ClientState.denyTicks > 0 ? (int) Math.round(Math.sin(ClientState.denyTicks * 2.2D) * 2.0D) : 0;
        int cx = width / 2 + ClientConfig.HUD_OFFSET_X.get() + shake;
        int y = height - ClientConfig.HUD_OFFSET_Y.get();
        int x0 = cx - BAR_W / 2 + 10;

        // Frame + background
        g.fill(x0 - 1, y - 1, x0 + BAR_W + 1, y + BAR_H + 1, 0xFF000000);
        g.fill(x0, y, x0 + BAR_W, y + BAR_H, 0xFF22242B);

        // Fill
        int fw = Math.round(BAR_W * frac);
        if (fw > 0) {
            if (full) {
                int a = lerpColor(0xFFFFC94D, 0xFFFFFFFF, pulse);
                int b = lerpColor(0xFFE08A00, 0xFFFFE9A0, pulse);
                g.fillGradient(x0, y, x0 + fw, y + BAR_H, a, b);
            } else {
                g.fillGradient(x0, y, x0 + fw, y + BAR_H, 0xFF8FD3FF, 0xFF3F6FB5);
            }
        }

        // Segment marks (10 segments)
        for (int i = 1; i < 10; i++) {
            g.fill(x0 + BAR_W * i / 10, y, x0 + BAR_W * i / 10 + 1, y + BAR_H, 0x55000000);
        }

        // Icon
        RenderSystem.enableBlend();
        float iconAlpha = full ? 0.75F + 0.25F * pulse : 0.5F;
        g.setColor(1.0F, 1.0F, 1.0F, iconAlpha);
        g.blit(ICON, x0 - 25, y - 7, 20, 20, 0.0F, 0.0F, 32, 32, 32, 32);
        g.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();

        // Label
        if (full) {
            MutableComponent label = Component.translatable("hud.chainbreak.ready")
                    .append(" [")
                    .append(ClientSetup.ACTIVATE.getTranslatedKeyMessage())
                    .append("]");
            g.drawCenteredString(mc.font, label, cx, y - 12, lerpColor(0xFFFFD35A, 0xFFFFFFFF, pulse) & 0xFFFFFF);
        } else {
            g.drawString(mc.font, Math.round(frac * 100.0F) + "%", x0 + BAR_W + 5, y - 1, 0xAAB4C8, true);
        }
    }

    static int lerpColor(int from, int to, float t) {
        int a = (int) (((from >>> 24) & 0xFF) + (((to >>> 24) & 0xFF) - ((from >>> 24) & 0xFF)) * t);
        int r = (int) (((from >>> 16) & 0xFF) + (((to >>> 16) & 0xFF) - ((from >>> 16) & 0xFF)) * t);
        int gr = (int) (((from >>> 8) & 0xFF) + (((to >>> 8) & 0xFF) - ((from >>> 8) & 0xFF)) * t);
        int b = (int) ((from & 0xFF) + ((to & 0xFF) - (from & 0xFF)) * t);
        return (a << 24) | (r << 16) | (gr << 8) | b;
    }
}
