package com.heroacademy.client;

import com.heroacademy.HeroAcademy;
import com.heroacademy.common.academic.AcademicCalendar;
import com.heroacademy.common.power.AbilitySlot;
import com.heroacademy.common.power.HeroSkill;
import com.heroacademy.common.power.HeroSkillRegistry;
import com.heroacademy.common.power.PowerType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = HeroAcademy.MODID, value = Dist.CLIENT)
public class HeroHudOverlay {

    @SubscribeEvent
    public static void registerGuiLayers(final RegisterGuiLayersEvent event) {
        event.registerAbove(
                VanillaGuiLayers.PLAYER_HEALTH,
                ResourceLocation.fromNamespaceAndPath(HeroAcademy.MODID, "hero_hud"),
                HeroHudOverlay::render
        );
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) {
            return;
        }

        if (ClientHeroData.powerType == PowerType.NONE) {
            return;
        }

        Font font = mc.font;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // ==============================================================
        // 1. TOPO ESQUERDO: EMBLEMA MINIMALISTA (Poder + Dia Atual)
        // ==============================================================
        int topX = 10;
        int topY = 10;
        int badgeWidth = 145;
        int badgeHeight = 24;

        // Fundo escuro fosco com borda fina elegante
        graphics.fill(topX, topY, topX + badgeWidth, topY + badgeHeight, 0x80101520);
        graphics.fill(topX, topY, topX + 2, topY + badgeHeight, ClientHeroData.rarity.getColorHex() | 0xFF000000);

        String powerText = "§f" + ClientHeroData.powerType.getDisplayName();
        String rarityTag = "§7[" + ClientHeroData.rarity.getDisplayName() + "]";
        graphics.drawString(font, powerText, topX + 6, topY + 4, 0xFFFFFF);
        graphics.drawString(font, rarityTag, topX + 6 + font.width(powerText) + 4, topY + 4, ClientHeroData.rarity.getColorHex());

        long gameTime = mc.level.getDayTime();
        int period = AcademicCalendar.getPeriod(gameTime);
        int dayOfPeriod = AcademicCalendar.getDayOfPeriod(gameTime);
        boolean isPortal = AcademicCalendar.isPortalExpeditionDay(gameTime);

        String calendarMini = isPortal ? "§6§l🌀 EXPEDIÇÃO ATIVA!" : "§8P." + period + " • Dia " + dayOfPeriod + "/180";
        graphics.drawString(font, calendarMini, topX + 6, topY + 14, isPortal ? 0xFFAA00 : 0xAAAAAA);

        // Notificação de pontos livres (apenas se houver pontos)
        int totalPoints = ClientHeroData.getTotalAvailablePoints();
        if (totalPoints > 0) {
            graphics.fill(topX + badgeWidth + 5, topY + 4, topX + badgeWidth + 60, topY + 20, 0xA0FF8800);
            graphics.drawString(font, "§f[K] +" + totalPoints + " pts", topX + badgeWidth + 9, topY + 8, 0xFFFFFF);
        }

        // ==============================================================
        // 2. CANTO INFERIOR ESQUERDO: BARRAS MODERNAS DE MANA E VIGOR
        // ==============================================================
        int botX = 10;
        int botY = screenHeight - 60;
        int barW = 110;
        int barH = 5;

        // --- BARRA DE MANA ---
        float manaPercent = Math.max(0.0f, Math.min(1.0f, ClientHeroData.currentEnergy / ClientHeroData.maxEnergy));
        int manaFill = (int) (barW * manaPercent);

        graphics.fill(botX - 2, botY - 2, botX + barW + 2, botY + barH + 2, 0x900A1020);
        graphics.fill(botX, botY, botX + barW, botY + barH, 0xFF152238);
        graphics.fill(botX, botY, botX + manaFill, botY + barH, 0xFF00AAFF);
        graphics.drawString(font, "§bMP §f" + (int) ClientHeroData.currentEnergy + "§7/" + (int) ClientHeroData.maxEnergy, botX + barW + 5, botY - 1, 0xFFFFFF);

        // --- BARRA DE VIGOR ---
        int staminaY = botY + 8;
        float staminaPercent = Math.max(0.0f, Math.min(1.0f, ClientHeroData.currentStamina / ClientHeroData.maxStamina));
        int staminaFill = (int) (barW * staminaPercent);

        graphics.fill(botX - 2, staminaY - 2, botX + barW + 2, staminaY + barH + 2, 0x900A1020);
        graphics.fill(botX, staminaY, botX + barW, staminaY + barH, 0xFF152818);
        graphics.fill(botX, staminaY, botX + staminaFill, staminaY + barH, 0xFF33DD55);
        graphics.drawString(font, "§aVP §f" + (int) ClientHeroData.currentStamina + "§7/" + (int) ClientHeroData.maxStamina, botX + barW + 5, staminaY - 1, 0xFFFFFF);

        // ==============================================================
        // 3. SLOTS DE SKILL (5 SLOTS ZXCVG + SELETOR DE PRESET/BARRA)
        // ==============================================================
        int slotsX = botX;
        int barHeaderY = staminaY + 9;
        int slotsY = barHeaderY + 11;

        int activeBar = ClientHeroData.activeBarIndex + 1;
        String barTitle = (activeBar == 1) ? "§6§lBARRA I §8• §7[R] Alternar" : "§b§lBARRA II §8• §7[R] Alternar";
        graphics.drawString(font, barTitle, slotsX, barHeaderY, 0xFFFFFF);

        AbilitySlot[] slots = AbilitySlot.values();
        int boxW = 23;
        int boxH = 24;
        int spacing = 3;

        for (int i = 0; i < slots.length; i++) {
            AbilitySlot slot = slots[i];
            int slotX = slotsX + (i * (boxW + spacing));
            int cdTicks = ClientHeroData.getActiveBarCooldown(i);
            String skillId = ClientHeroData.getActiveSkillInSlot(i);
            HeroSkill skill = HeroSkillRegistry.getSkill(skillId);

            int outlineColor = 0xFF334455;
            int boxBg = 0xBB0A101C;

            if (skill != null) {
                outlineColor = skill.getCategory().getColorHex() | 0xFF000000;
                boxBg = 0xDD121824;
            }

            if (cdTicks > 0) {
                boxBg = 0xDD2A0E10;
                outlineColor = 0xFFAA2222;
            }

            // Fundo e moldura
            graphics.fill(slotX, slotsY, slotX + boxW, slotsY + boxH, boxBg);
            graphics.fill(slotX, slotsY, slotX + boxW, slotsY + 1, outlineColor);
            graphics.fill(slotX, slotsY + boxH - 1, slotX + boxW, slotsY + boxH, outlineColor);
            graphics.fill(slotX, slotsY, slotX + 1, slotsY + boxH, outlineColor);
            graphics.fill(slotX + boxW - 1, slotsY, slotX + boxW, slotsY + boxH, outlineColor);

            // Tecla Z/X/C/V/G
            graphics.drawCenteredString(font, "§f" + slot.getKeyName(), slotX + (boxW / 2), slotsY + 2, 0xFFFFFF);

            // Indicador de skill ou status
            if (skill != null) {
                String shortName = skill.getName();
                if (shortName.length() > 3) shortName = shortName.substring(0, 3);
                graphics.drawCenteredString(font, "§7" + shortName, slotX + (boxW / 2), slotsY + 10, 0xCCCCCC);

                // Custo no rodapé
                if (skill.getManaCost() > 0) {
                    graphics.drawCenteredString(font, "§b" + (int) skill.getManaCost(), slotX + (boxW / 2), slotsY + 16, 0x55AAFF);
                } else if (skill.getStaminaCost() > 0) {
                    graphics.drawCenteredString(font, "§a" + (int) skill.getStaminaCost(), slotX + (boxW / 2), slotsY + 16, 0x55FF88);
                }
            } else {
                graphics.drawCenteredString(font, "§8-", slotX + (boxW / 2), slotsY + 12, 0x666666);
            }

            // Cooldown em overlay
            if (cdTicks > 0) {
                graphics.fill(slotX + 1, slotsY + 1, slotX + boxW - 1, slotsY + boxH - 1, 0xCC150505);
                float sec = cdTicks / 20.0f;
                String cdText = (sec >= 10.0f) ? String.valueOf((int) sec) : String.format("%.1f", sec);
                graphics.drawCenteredString(font, "§c" + cdText, slotX + (boxW / 2), slotsY + 8, 0xFF5555);
            }
        }
    }
}
