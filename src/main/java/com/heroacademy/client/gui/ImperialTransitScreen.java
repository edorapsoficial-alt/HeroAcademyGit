package com.heroacademy.client.gui;

import com.heroacademy.client.CampusNavigationClient;
import com.heroacademy.client.ClientHeroData;
import com.heroacademy.common.academic.CampusWaypoint;
import com.heroacademy.common.network.ImperialTransitPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class ImperialTransitScreen extends Screen {

    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 7;
    private final List<CampusWaypoint> waypoints = CampusWaypoint.getAll();
    public static final int TRANSIT_COST = 5;

    public ImperialTransitScreen() {
        super(Component.literal("Terminal de Trânsito Arcantécnico"));
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Sem blur vanilla
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Fundo arcantécnico azul-obsidiana escuro com linhas ciano neon
        graphics.fillGradient(0, 0, this.width, this.height, 0xD8060F1A, 0xF5091626);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Font font = this.font;
        int panelW = Math.min(500, this.width - 24);
        int panelH = Math.min(310, this.height - 30);
        int left = (this.width - panelW) / 2;
        int top = (this.height - panelH) / 2;

        // Moldura arcantécnica com bordas ciano neon e douradas
        graphics.fill(left, top, left + panelW, top + panelH, 0xEE0B192A);
        graphics.fill(left, top, left + panelW, top + 2, 0xFF00D4FF);
        graphics.fill(left, top + panelH - 2, left + panelW, top + panelH, 0xFF00D4FF);
        graphics.fill(left, top, left + 2, top + panelH, 0xFF00D4FF);
        graphics.fill(left + panelW - 2, top, left + panelW, top + panelH, 0xFF00D4FF);

        // Acentos dourados nos cantos
        graphics.fill(left, top, left + 8, top + 3, 0xFFFFCC00);
        graphics.fill(left + panelW - 8, top, left + panelW, top + 3, 0xFFFFCC00);
        graphics.fill(left, top + panelH - 3, left + 8, top + panelH, 0xFFFFCC00);
        graphics.fill(left + panelW - 8, top + panelH - 3, left + panelW, top + panelH, 0xFFFFCC00);

        // Cabeçalho
        graphics.drawCenteredString(font, "§3⚡ §b§lTERMINAL DE TRÂNSITO ARCANTÉCNICO §3⚡", left + (panelW / 2), top + 8, 0xFFFFFF);
        graphics.drawCenteredString(font, "§7Rede Imperial de Teletransporte • Campus da Academia", left + (panelW / 2), top + 20, 0x88B0D0);

        // Barra de Informações de Crédito
        int balanceY = top + 34;
        String balanceText = "§6Carteira: §e" + ClientHeroData.imperialCredits + " 🪙 Créditos Imperiais";
        graphics.drawString(font, balanceText, left + 14, balanceY, 0xFFFFFF);

        String costText = "§bTaxa da Rede: §f" + TRANSIT_COST + " 🪙 §7(Simbólica)";
        graphics.drawString(font, costText, left + 220, balanceY, 0xCCCCCC);

        int maxPages = (int) Math.ceil((double) waypoints.size() / ITEMS_PER_PAGE);
        String pageStr = "§7Pág " + (currentPage + 1) + "/" + maxPages;
        graphics.drawString(font, pageStr, left + panelW - font.width(pageStr) - 14, balanceY, 0xAAAAAA);

        // Lista de Terminais
        int startIdx = currentPage * ITEMS_PER_PAGE;
        int endIdx = Math.min(startIdx + ITEMS_PER_PAGE, waypoints.size());
        int itemY = top + 48;
        int itemH = 28;

        boolean hasEnoughCredits = (mc.player.isCreative() || ClientHeroData.imperialCredits >= TRANSIT_COST);

        for (int i = startIdx; i < endIdx; i++) {
            CampusWaypoint wp = waypoints.get(i);
            boolean hover = mouseX >= left + 12 && mouseX <= left + panelW - 12 && mouseY >= itemY && mouseY <= itemY + itemH;

            int cardBg = hover ? 0xEE162E48 : 0xAA0F2136;
            graphics.fill(left + 12, itemY, left + panelW - 12, itemY + itemH, cardBg);
            graphics.fill(left + 12, itemY, left + 15, itemY + itemH, hover ? 0xFF00FFCC : 0xFF0088AA);

            // Ícone e Nome
            graphics.drawString(font, wp.getIcon() + " §f§l" + wp.getName(), left + 20, itemY + 5, 0xFFFFFF);
            graphics.drawString(font, "§7" + wp.getDescription(), left + 20, itemY + 16, 0x7799BB);

            // Distância
            double dist = wp.getDistanceTo(mc.player.position());
            String distStr = "§e" + (int) dist + "m";
            graphics.drawString(font, distStr, left + panelW - 170, itemY + 10, 0xFFFFFF);

            // Botão Traçar Rota com Rastro de Luz
            int routeBtnW = 60;
            int routeBtnH = 18;
            int routeBtnX = left + panelW - 14 - 90 - routeBtnW - 4;
            int btnY = itemY + 5;
            boolean routeHover = mouseX >= routeBtnX && mouseX <= routeBtnX + routeBtnW && mouseY >= btnY && mouseY <= btnY + routeBtnH;
            graphics.fill(routeBtnX, btnY, routeBtnX + routeBtnW, btnY + routeBtnH, routeHover ? 0xFF35587A : 0xFF203A54);
            graphics.drawCenteredString(font, "§e🧭 Rota", routeBtnX + (routeBtnW / 2), btnY + 5, 0xFFFFFF);

            // Botão Translocar (gastar 5 créditos)
            int btnW = 90;
            int btnH = 18;
            int btnX = left + panelW - 14 - btnW;
            boolean btnHover = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;

            int btnBg;
            String btnLabel;
            if (!hasEnoughCredits) {
                btnBg = 0xFF552222;
                btnLabel = "§cSem Créditos";
            } else {
                btnBg = btnHover ? 0xFF00C882 : 0xFF008B5B;
                btnLabel = "§f⚡ Ir [" + TRANSIT_COST + " 🪙]";
            }

            graphics.fill(btnX, btnY, btnX + btnW, btnY + btnH, btnBg);
            graphics.drawCenteredString(font, btnLabel, btnX + (btnW / 2), btnY + 5, 0xFFFFFF);

            itemY += itemH + 4;
        }

        // Rodapé
        int footerY = top + panelH - 24;
        if (currentPage > 0) {
            boolean prevHover = mouseX >= left + 14 && mouseX <= left + 84 && mouseY >= footerY && mouseY <= footerY + 16;
            graphics.fill(left + 14, footerY, left + 84, footerY + 16, prevHover ? 0xCC1A3858 : 0xAA10243A);
            graphics.drawCenteredString(font, "§b◀ Anterior", left + 49, footerY + 4, 0xFFFFFF);
        }

        if (currentPage < maxPages - 1) {
            int nextX = left + 92;
            boolean nextHover = mouseX >= nextX && mouseX <= nextX + 70 && mouseY >= footerY && mouseY <= footerY + 16;
            graphics.fill(nextX, footerY, nextX + 70, footerY + 16, nextHover ? 0xCC1A3858 : 0xAA10243A);
            graphics.drawCenteredString(font, "§bPróximo ▶", nextX + 35, footerY + 4, 0xFFFFFF);
        }

        // Botão Fechar
        int closeW = 70;
        int closeX = left + panelW - 14 - closeW;
        boolean closeHover = mouseX >= closeX && mouseX <= closeX + closeW && mouseY >= footerY && mouseY <= footerY + 16;
        graphics.fill(closeX, footerY, closeX + closeW, footerY + 16, closeHover ? 0xCC442020 : 0x88301515);
        graphics.drawCenteredString(font, "§cFechar ✕", closeX + (closeW / 2), footerY + 4, 0xFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int panelW = Math.min(500, this.width - 24);
        int panelH = Math.min(310, this.height - 30);
        int left = (this.width - panelW) / 2;
        int top = (this.height - panelH) / 2;

        int maxPages = (int) Math.ceil((double) waypoints.size() / ITEMS_PER_PAGE);

        // Paginação
        int footerY = top + panelH - 24;
        if (currentPage > 0 && mouseX >= left + 14 && mouseX <= left + 84 && mouseY >= footerY && mouseY <= footerY + 16) {
            currentPage--;
            playClickSound();
            return true;
        }

        if (currentPage < maxPages - 1 && mouseX >= left + 92 && mouseX <= left + 162 && mouseY >= footerY && mouseY <= footerY + 16) {
            currentPage++;
            playClickSound();
            return true;
        }

        // Fechar
        int closeW = 70;
        int closeX = left + panelW - 14 - closeW;
        if (mouseX >= closeX && mouseX <= closeX + closeW && mouseY >= footerY && mouseY <= footerY + 16) {
            this.onClose();
            return true;
        }

        // Clique nas linhas
        int startIdx = currentPage * ITEMS_PER_PAGE;
        int endIdx = Math.min(startIdx + ITEMS_PER_PAGE, waypoints.size());
        int itemY = top + 48;
        int itemH = 28;

        Minecraft mc = Minecraft.getInstance();
        boolean hasEnoughCredits = (mc.player != null && (mc.player.isCreative() || ClientHeroData.imperialCredits >= TRANSIT_COST));

        for (int i = startIdx; i < endIdx; i++) {
            CampusWaypoint wp = waypoints.get(i);
            int btnY = itemY + 5;

            // Botão Rota
            int routeBtnW = 60;
            int routeBtnH = 18;
            int routeBtnX = left + panelW - 14 - 90 - routeBtnW - 4;
            if (mouseX >= routeBtnX && mouseX <= routeBtnX + routeBtnW && mouseY >= btnY && mouseY <= btnY + routeBtnH) {
                CampusNavigationClient.setWaypoint(wp);
                this.onClose();
                return true;
            }

            // Botão Translocar
            int btnW = 90;
            int btnH = 18;
            int btnX = left + panelW - 14 - btnW;
            if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                if (hasEnoughCredits) {
                    PacketDistributor.sendToServer(new ImperialTransitPayload(wp.getId()));
                    this.onClose();
                } else {
                    playErrorSound();
                }
                return true;
            }

            itemY += itemH + 4;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void playClickSound() {
        Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f)
        );
    }

    private void playErrorSound() {
        Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.VILLAGER_NO, 1.0f)
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
