package com.heroacademy.client;

import com.heroacademy.HeroAcademy;
import com.heroacademy.common.academic.CampusZone;
import com.heroacademy.common.academic.CampusZoneManager;
import com.heroacademy.common.world.ModDimensions;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = HeroAcademy.MODID, value = Dist.CLIENT)
public class CampusZoneOverlay {

    private static CampusZone currentZone = null;
    private static CampusZone displayedZone = null;
    private static long displayStartTime = 0;
    private static final long DURATION_MS = 4200;
    private static final long FADE_IN_MS = 500;
    private static final long FADE_OUT_MS = 700;

    @SubscribeEvent
    public static void registerGuiLayers(final RegisterGuiLayersEvent event) {
        event.registerAbove(
                VanillaGuiLayers.PLAYER_HEALTH,
                ResourceLocation.fromNamespaceAndPath(HeroAcademy.MODID, "campus_zone_overlay"),
                CampusZoneOverlay::render
        );
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) {
            return;
        }

        // Verifica se está na dimensão do Campus ou no Overworld do mapa da Academia
        boolean isCampus = mc.level.dimension().equals(ModDimensions.CAMPUS_LEVEL_KEY);
        boolean isOverworld = mc.level.dimension().equals(net.minecraft.world.level.Level.OVERWORLD);

        if (!isCampus && !isOverworld) {
            currentZone = null;
            displayedZone = null;
            return;
        }

        // Checagem de zona atual
        CampusZone detected = CampusZoneManager.getZoneAt(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        if (isOverworld && detected == null && currentZone == null) {
            return;
        }
        if (detected != currentZone) {
            currentZone = detected;
            if (currentZone != null) {
                displayedZone = currentZone;
                displayStartTime = System.currentTimeMillis();
                // Toca som sutil e nobre de descoberta de área
                mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.4f, 0.8f));
            }
        }

        if (displayedZone == null) {
            return;
        }

        long elapsed = System.currentTimeMillis() - displayStartTime;
        if (elapsed >= DURATION_MS) {
            return;
        }

        // Cálculo de Alpha suave (Fade In / Hold / Fade Out)
        float alpha;
        if (elapsed < FADE_IN_MS) {
            alpha = (float) elapsed / FADE_IN_MS;
        } else if (elapsed > DURATION_MS - FADE_OUT_MS) {
            alpha = (float) (DURATION_MS - elapsed) / FADE_OUT_MS;
        } else {
            alpha = 1.0f;
        }
        alpha = Math.max(0.0f, Math.min(1.0f, alpha));

        int intAlpha = (int) (alpha * 255.0f);
        if (intAlpha <= 5) return;

        Font font = mc.font;
        int screenWidth = mc.getWindow().getGuiScaledWidth();

        String title = displayedZone.getIcon() + " " + displayedZone.getTitle();
        String subtitle = displayedZone.getSubtitle();

        int titleW = font.width(title);
        int subW = font.width(subtitle);
        int boxW = Math.max(titleW, subW) + 40;
        boxW = Math.max(boxW, 200);
        int boxH = 34;

        int boxX = (screenWidth - boxW) / 2;
        int boxY = 24;

        // Fundo estilo Dark Fantasy translúcido
        int bgAlpha = (int) (alpha * 190.0f);
        int bgColor = (bgAlpha << 24) | 0x090D18;
        graphics.fill(boxX, boxY, boxX + boxW, boxY + boxH, bgColor);

        // Borda dourada/ciano suave com degradê
        int borderAlpha = (int) (alpha * 220.0f);
        int goldBorder = (borderAlpha << 24) | 0xD4AF37;
        int cyanAccent = (borderAlpha << 24) | 0x5BC0BE;

        // Linhas de topo e base com acabamento refinado
        graphics.fill(boxX + 2, boxY, boxX + boxW - 2, boxY + 1, goldBorder);
        graphics.fill(boxX + 10, boxY + boxH - 1, boxX + boxW - 10, boxY + boxH, cyanAccent);
        graphics.fill(boxX, boxY + 2, boxX + 1, boxY + boxH - 2, goldBorder);
        graphics.fill(boxX + boxW - 1, boxY + 2, boxX + boxW, boxY + boxH - 2, goldBorder);

        // Detalhes pontuais nos cantos
        graphics.fill(boxX + 1, boxY + 1, boxX + 2, boxY + 2, goldBorder);
        graphics.fill(boxX + boxW - 2, boxY + 1, boxX + boxW - 1, boxY + 2, goldBorder);

        // Desenhar Textos
        int textAlpha = (int) (alpha * 255.0f);
        int titleColor = (textAlpha << 24) | 0xFFFFFF;
        int subColor = (textAlpha << 24) | 0xDDDDDD;

        int textCenterX = screenWidth / 2;
        graphics.drawCenteredString(font, title, textCenterX, boxY + 6, titleColor);
        graphics.drawCenteredString(font, subtitle, textCenterX, boxY + 19, subColor);
    }
}
