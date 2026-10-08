package com.heroacademy.client.gui;

import com.heroacademy.client.CampusNavigationClient;
import com.heroacademy.common.academic.CampusWaypoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class CampusNavigationScreen extends Screen {

    private String selectedCategory = "Todas";
    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 7;
    private static final String[] CATEGORIES = new String[]{"Todas", "Aulas", "Convivência", "Alojamento", "Treino", "Serviços"};

    public CampusNavigationScreen() {
        super(Component.literal("Bússola & Guia do Campus • Imperial Destiny"));
    }

    private List<CampusWaypoint> getFilteredWaypoints() {
        List<CampusWaypoint> list = new ArrayList<>();
        for (CampusWaypoint wp : CampusWaypoint.getAll()) {
            if ("Todas".equals(selectedCategory) || wp.getCategory().equalsIgnoreCase(selectedCategory)) {
                list.add(wp);
            }
        }
        return list;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Sem blur vanilla
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Fundo estilo códice arcano dourado e azul celeste escuro
        graphics.fillGradient(0, 0, this.width, this.height, 0xD808111D, 0xF50B1626);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Font font = this.font;
        int panelW = Math.min(520, this.width - 24);
        int panelH = Math.min(310, this.height - 30);
        int left = (this.width - panelW) / 2;
        int top = (this.height - panelH) / 2;

        // Moldura com bordas douradas elegantes
        graphics.fill(left, top, left + panelW, top + panelH, 0xEE101B2B);
        graphics.fill(left, top, left + panelW, top + 2, 0xFFE5B342);
        graphics.fill(left, top + panelH - 2, left + panelW, top + panelH, 0xFFE5B342);
        graphics.fill(left, top, left + 2, top + panelH, 0xFFE5B342);
        graphics.fill(left + panelW - 2, top, left + panelW, top + panelH, 0xFFE5B342);

        // Cabeçalho
        graphics.drawCenteredString(font, "§e§l🧭 GUIA & MAPA DO CAMPUS IMPERIAL DESTINY 🧭", left + (panelW / 2), top + 8, 0xFFFFFF);
        graphics.drawCenteredString(font, "§7Selecione um local para traçar uma trilha de luz mágica no chão guiando seus passos", left + (panelW / 2), top + 20, 0xAAAAAA);

        // Barra de Categorias
        int catX = left + 12;
        int catY = top + 34;
        for (String cat : CATEGORIES) {
            int catW = font.width(cat) + 12;
            boolean isSelected = cat.equalsIgnoreCase(selectedCategory);
            boolean hover = mouseX >= catX && mouseX <= catX + catW && mouseY >= catY && mouseY <= catY + 14;

            int bg = isSelected ? 0xFFE5B342 : (hover ? 0xAA2B4058 : 0x88172538);
            int textColor = isSelected ? 0x000000 : 0xFFFFFF;

            graphics.fill(catX, catY, catX + catW, catY + 14, bg);
            graphics.drawString(font, cat, catX + 6, catY + 3, textColor, false);
            catX += catW + 4;
        }

        // Status da Rota Atual
        CampusWaypoint current = CampusNavigationClient.getActiveWaypoint();
        if (current != null) {
            String routeStatus = "§aRota ativa: " + current.getIcon() + " §f" + current.getName() + " §7(" + (int) current.getDistanceTo(mc.player.position()) + "m)";
            graphics.drawString(font, routeStatus, left + 12, top + 52, 0xFFFFFF);
        } else {
            graphics.drawString(font, "§7Nenhuma rota ativa no momento.", left + 12, top + 52, 0x888888);
        }

        List<CampusWaypoint> filtered = getFilteredWaypoints();
        int maxPages = Math.max(1, (int) Math.ceil((double) filtered.size() / ITEMS_PER_PAGE));
        if (currentPage >= maxPages) currentPage = maxPages - 1;

        String pageStr = "§7Pág " + (currentPage + 1) + "/" + maxPages;
        graphics.drawString(font, pageStr, left + panelW - font.width(pageStr) - 12, top + 52, 0xCCCCCC);

        // Lista de Locais
        int startIdx = currentPage * ITEMS_PER_PAGE;
        int endIdx = Math.min(startIdx + ITEMS_PER_PAGE, filtered.size());
        int itemY = top + 66;
        int itemH = 26;

        for (int i = startIdx; i < endIdx; i++) {
            CampusWaypoint wp = filtered.get(i);
            boolean isTracking = (current == wp);
            boolean hover = mouseX >= left + 12 && mouseX <= left + panelW - 12 && mouseY >= itemY && mouseY <= itemY + itemH;

            int cardBg = isTracking ? 0xEE1E3C2B : (hover ? 0xEE1C2B3E : 0xAA121D2C);
            graphics.fill(left + 12, itemY, left + panelW - 12, itemY + itemH, cardBg);
            graphics.fill(left + 12, itemY, left + 15, itemY + itemH, isTracking ? 0xFF00FF88 : (hover ? 0xFF55AAFF : 0xFF2A4365));

            // Ícone, Nome e Categoria
            graphics.drawString(font, wp.getIcon() + " §f§l" + wp.getName() + " §8[" + wp.getCategory() + "]", left + 20, itemY + 4, 0xFFFFFF);
            graphics.drawString(font, "§7" + wp.getDescription(), left + 20, itemY + 15, 0x88A0B8);

            // Distância
            double dist = wp.getDistanceTo(mc.player.position());
            String distStr = "§e" + (int) dist + "m";
            graphics.drawString(font, distStr, left + panelW - 170, itemY + 9, 0xFFFFFF);

            // Botão Traçar Rota / Cancelar
            int btnW = 82;
            int btnH = 16;
            int btnX = left + panelW - 14 - btnW;
            int btnY = itemY + 5;
            boolean btnHover = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;

            int btnBg = isTracking ? (btnHover ? 0xFFCC3333 : 0xFFAA2222) : (btnHover ? 0xFFE5B342 : 0xFF9E7B28);
            graphics.fill(btnX, btnY, btnX + btnW, btnY + btnH, btnBg);
            String btnText = isTracking ? "§f❌ Cancelar" : "§f🧭 Traçar Rota";
            graphics.drawCenteredString(font, btnText, btnX + (btnW / 2), btnY + 4, 0xFFFFFF);

            // Atalho de Teleporte Arcantécnico
            int transitW = 54;
            int transitX = btnX - transitW - 4;
            boolean transitHover = mouseX >= transitX && mouseX <= transitX + transitW && mouseY >= btnY && mouseY <= btnY + btnH;
            graphics.fill(transitX, btnY, transitX + transitW, btnY + btnH, transitHover ? 0xFF00C882 : 0xFF007548);
            graphics.drawCenteredString(font, "§f⚡ 5 🪙", transitX + (transitW / 2), btnY + 4, 0xFFFFFF);

            itemY += itemH + 4;
        }

        // Rodapé
        int footerY = top + panelH - 22;
        if (currentPage > 0) {
            boolean prevHover = mouseX >= left + 14 && mouseX <= left + 84 && mouseY >= footerY && mouseY <= footerY + 14;
            graphics.fill(left + 14, footerY, left + 84, footerY + 14, prevHover ? 0xCC2A405A : 0xAA182535);
            graphics.drawCenteredString(font, "§b◀ Anterior", left + 49, footerY + 3, 0xFFFFFF);
        }

        if (currentPage < maxPages - 1) {
            int nextX = left + 90;
            boolean nextHover = mouseX >= nextX && mouseX <= nextX + 70 && mouseY >= footerY && mouseY <= footerY + 14;
            graphics.fill(nextX, footerY, nextX + 70, footerY + 14, nextHover ? 0xCC2A405A : 0xAA182535);
            graphics.drawCenteredString(font, "§bPróximo ▶", nextX + 35, footerY + 3, 0xFFFFFF);
        }

        // Botão Desativar Todas as Rotas
        if (current != null) {
            int cancelAllX = left + 170;
            int cancelAllW = 120;
            boolean cHover = mouseX >= cancelAllX && mouseX <= cancelAllX + cancelAllW && mouseY >= footerY && mouseY <= footerY + 14;
            graphics.fill(cancelAllX, footerY, cancelAllX + cancelAllW, footerY + 14, cHover ? 0xCCAA3333 : 0x88772222);
            graphics.drawCenteredString(font, "§cDesativar Rota", cancelAllX + (cancelAllW / 2), footerY + 3, 0xFFFFFF);
        }

        // Botão Fechar
        int closeW = 60;
        int closeX = left + panelW - 14 - closeW;
        boolean closeHover = mouseX >= closeX && mouseX <= closeX + closeW && mouseY >= footerY && mouseY <= footerY + 14;
        graphics.fill(closeX, footerY, closeX + closeW, footerY + 14, closeHover ? 0xCC442020 : 0x88301515);
        graphics.drawCenteredString(font, "§cFechar ✕", closeX + (closeW / 2), footerY + 3, 0xFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        Font font = this.font;
        int panelW = Math.min(520, this.width - 24);
        int panelH = Math.min(310, this.height - 30);
        int left = (this.width - panelW) / 2;
        int top = (this.height - panelH) / 2;

        // Categorias
        int catX = left + 12;
        int catY = top + 34;
        for (String cat : CATEGORIES) {
            int catW = font.width(cat) + 12;
            if (mouseX >= catX && mouseX <= catX + catW && mouseY >= catY && mouseY <= catY + 14) {
                selectedCategory = cat;
                currentPage = 0;
                playClickSound();
                return true;
            }
            catX += catW + 4;
        }

        List<CampusWaypoint> filtered = getFilteredWaypoints();
        int maxPages = Math.max(1, (int) Math.ceil((double) filtered.size() / ITEMS_PER_PAGE));

        // Paginação
        int footerY = top + panelH - 22;
        if (currentPage > 0 && mouseX >= left + 14 && mouseX <= left + 84 && mouseY >= footerY && mouseY <= footerY + 14) {
            currentPage--;
            playClickSound();
            return true;
        }

        if (currentPage < maxPages - 1 && mouseX >= left + 90 && mouseX <= left + 160 && mouseY >= footerY && mouseY <= footerY + 14) {
            currentPage++;
            playClickSound();
            return true;
        }

        // Cancelar rota atual no rodapé
        if (CampusNavigationClient.getActiveWaypoint() != null) {
            int cancelAllX = left + 170;
            int cancelAllW = 120;
            if (mouseX >= cancelAllX && mouseX <= cancelAllX + cancelAllW && mouseY >= footerY && mouseY <= footerY + 14) {
                CampusNavigationClient.clearWaypoint();
                return true;
            }
        }

        // Fechar
        int closeW = 60;
        int closeX = left + panelW - 14 - closeW;
        if (mouseX >= closeX && mouseX <= closeX + closeW && mouseY >= footerY && mouseY <= footerY + 14) {
            this.onClose();
            return true;
        }

        // Ações nas linhas
        int startIdx = currentPage * ITEMS_PER_PAGE;
        int endIdx = Math.min(startIdx + ITEMS_PER_PAGE, filtered.size());
        int itemY = top + 66;
        int itemH = 26;

        for (int i = startIdx; i < endIdx; i++) {
            CampusWaypoint wp = filtered.get(i);
            int btnW = 82;
            int btnH = 16;
            int btnX = left + panelW - 14 - btnW;
            int btnY = itemY + 5;

            int transitW = 54;
            int transitX = btnX - transitW - 4;

            // Botão Translocar via Terminal
            if (mouseX >= transitX && mouseX <= transitX + transitW && mouseY >= btnY && mouseY <= btnY + btnH) {
                PacketDistributor.sendToServer(new com.heroacademy.common.network.ImperialTransitPayload(wp.getId()));
                this.onClose();
                return true;
            }

            // Botão Traçar / Cancelar
            if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                if (CampusNavigationClient.getActiveWaypoint() == wp) {
                    CampusNavigationClient.clearWaypoint();
                } else {
                    CampusNavigationClient.setWaypoint(wp);
                }
                return true;
            }

            // Clicar em qualquer lugar do cartão traça a rota
            if (mouseX >= left + 12 && mouseX <= left + panelW - 12 && mouseY >= itemY && mouseY <= itemY + itemH) {
                if (CampusNavigationClient.getActiveWaypoint() == wp) {
                    CampusNavigationClient.clearWaypoint();
                } else {
                    CampusNavigationClient.setWaypoint(wp);
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

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
