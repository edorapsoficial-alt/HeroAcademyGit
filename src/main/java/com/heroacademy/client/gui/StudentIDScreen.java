package com.heroacademy.client.gui;

import com.heroacademy.HeroAcademy;
import com.heroacademy.client.ClientHeroData;
import com.heroacademy.common.academic.AcademicCalendar;
import com.heroacademy.common.network.EquipSkillPayload;
import com.heroacademy.common.power.AbilitySlot;
import com.heroacademy.common.power.HeroSkill;
import com.heroacademy.common.power.HeroSkillRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class StudentIDScreen extends Screen {
    private static final ResourceLocation CARD_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(HeroAcademy.MODID, "textures/gui/student_id_card.png");

    private static final int CARD_TEXTURE_W = 320;
    private static final int CARD_TEXTURE_H = 180;

    // Abas: 0 = Carteirinha de Estudante, 1 = Equipar Habilidades
    private int currentTab = 0;

    // Estado da Aba de Habilidades
    private int selectedBar = 0; // 0 = Barra 1, 1 = Barra 2
    private int skillsPage = 0;
    private static final int SKILLS_PER_PAGE = 8;

    // Arrastar e Soltar (Drag and Drop)
    private String draggedSkillId = null;
    private int draggedFromSlot = -1;

    public StudentIDScreen() {
        super(Component.literal("Carteirinha & Habilidades do Estudante"));
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Desativa o Gaussian Blur do vanilla 1.21.1 para impedir a tela de ficar borrada
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Fundo escuro translúcido limpo e nítido em tela cheia
        graphics.fillGradient(0, 0, this.width, this.height, 0xD5050812, 0xF0070C18);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int panelW = (currentTab == 0) ? CARD_TEXTURE_W : Math.min(560, this.width - 32);
        int panelH = (currentTab == 0) ? CARD_TEXTURE_H : Math.min(270, this.height - 48);
        int left = (this.width - panelW) / 2;
        int top = (this.height - panelH) / 2;

        // Abas superiores
        renderTabButtons(graphics, left, top - 20, mouseX, mouseY);

        if (currentTab == 0) {
            renderStudentCard(graphics, mc, left, top, mouseX, mouseY);
        } else {
            renderSkillManagement(graphics, mc, left, top, panelW, panelH, mouseX, mouseY);
        }

        // Renderiza o item sendo arrastado sob o cursor
        if (draggedSkillId != null) {
            HeroSkill skill = HeroSkillRegistry.getSkill(draggedSkillId);
            if (skill != null) {
                int ghostW = 85;
                int ghostH = 24;
                int ghostX = mouseX - (ghostW / 2);
                int ghostY = mouseY - (ghostH / 2);

                graphics.fill(ghostX, ghostY, ghostX + ghostW, ghostY + ghostH, 0xEE152030);
                graphics.fill(ghostX, ghostY, ghostX + 3, ghostY + ghostH, skill.getCategory().getColorHex() | 0xFF000000);
                graphics.drawString(this.font, "§f" + skill.getName(), ghostX + 6, ghostY + 3, 0xFFFFFF);
                String cost = skill.getManaCost() > 0 ? (int) skill.getManaCost() + " MP" : (int) skill.getStaminaCost() + " VP";
                graphics.drawString(this.font, "§7" + cost, ghostX + 6, ghostY + 13, 0xAAAAAA);
            }
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderTabButtons(GuiGraphics graphics, int left, int y, int mouseX, int mouseY) {
        Font font = this.font;

        // Aba 0: Carteirinha
        int tab0W = 120;
        int tab0H = 18;
        boolean hover0 = mouseX >= left && mouseX <= left + tab0W && mouseY >= y && mouseY <= y + tab0H;
        int bg0 = (currentTab == 0) ? 0xEE1E2838 : (hover0 ? 0xAA223348 : 0x88101622);
        graphics.fill(left, y, left + tab0W, y + tab0H, bg0);
        graphics.fill(left, y, left + tab0W, y + 2, (currentTab == 0) ? 0xFF00AAFF : 0xFF334455);
        graphics.drawCenteredString(font, "§f🆔 Carteirinha", left + (tab0W / 2), y + 5, 0xFFFFFF);

        // Aba 1: Habilidades
        int tab1X = left + tab0W + 4;
        int tab1W = 150;
        int tab1H = 18;
        boolean hover1 = mouseX >= tab1X && mouseX <= tab1X + tab1W && mouseY >= y && mouseY <= y + tab1H;
        int bg1 = (currentTab == 1) ? 0xEE1E2838 : (hover1 ? 0xAA223348 : 0x88101622);
        graphics.fill(tab1X, y, tab1X + tab1W, y + tab1H, bg1);
        graphics.fill(tab1X, y, tab1X + tab1W, y + 2, (currentTab == 1) ? 0xFFFFAA00 : 0xFF334455);
        graphics.drawCenteredString(font, "§f⚡ Habilidades (ZXCVG)", tab1X + (tab1W / 2), y + 5, 0xFFFFFF);

        // Aba 2: Guia & Mapa do Campus
        int tab2X = tab1X + tab1W + 4;
        int tab2W = 140;
        int tab2H = 18;
        boolean hover2 = mouseX >= tab2X && mouseX <= tab2X + tab2W && mouseY >= y && mouseY <= y + tab2H;
        int bg2 = hover2 ? 0xAA2B4058 : 0x88101622;
        graphics.fill(tab2X, y, tab2X + tab2W, y + tab2H, bg2);
        graphics.fill(tab2X, y, tab2X + tab2W, y + 2, 0xFF5BC0BE);
        graphics.drawCenteredString(font, "§f🧭 Guia do Campus", tab2X + (tab2W / 2), y + 5, 0xFFFFFF);
    }

    private void renderStudentCard(GuiGraphics graphics, Minecraft mc, int left, int top, int mouseX, int mouseY) {
        graphics.blit(CARD_TEXTURE, left, top, 0, 0, CARD_TEXTURE_W, CARD_TEXTURE_H, CARD_TEXTURE_W, CARD_TEXTURE_H);

        int photoLeft = left + 20;
        int photoTop = top + 42;
        int photoWidth = 58;
        int photoHeight = 88;

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics,
                photoLeft,
                photoTop,
                photoLeft + photoWidth,
                photoTop + photoHeight,
                38,
                0.0625f,
                mouseX,
                mouseY,
                mc.player
        );

        Font font = this.font;
        String signature = "§8" + mc.player.getName().getString();
        graphics.drawCenteredString(font, signature, photoLeft + (photoWidth / 2), top + 133, 0x333333);

        int textX = left + 88;
        int textY = top + 46;
        int lineSpacing = 13;

        graphics.drawString(font, "§8Nome: §0§l" + mc.player.getName().getString(), textX, textY, 0x000000, false);
        textY += lineSpacing;

        graphics.drawString(font, "§8Turma: §1§l" + ClientHeroData.schoolClass.getDisplayName(), textX, textY, 0x000000, false);
        textY += lineSpacing;

        int rarityColor = ClientHeroData.rarity.getColorHex();
        graphics.drawString(font, "§8Poder: §0" + ClientHeroData.powerType.getDisplayName() + " §7[" + ClientHeroData.rarity.getDisplayName() + "]", textX, textY, rarityColor, false);
        textY += lineSpacing;

        graphics.drawString(font, "§8Classe: §2§l" + ClientHeroData.primaryClass.getDisplayName(), textX, textY, 0x000000, false);
        textY += lineSpacing;

        graphics.drawString(font, "§8Especialização: §6§l" + ClientHeroData.selectedBranch.getDisplayName(), textX, textY, 0x000000, false);
        textY += lineSpacing;

        String attrsText = "§8Treino: §c" + ClientHeroData.bodyLevel + "C §8| §9" + ClientHeroData.mindLevel + "M §8| §6" + ClientHeroData.techniqueLevel + "T";
        graphics.drawString(font, attrsText, textX, textY, 0x000000, false);
        textY += lineSpacing;

        if (mc.level != null) {
            long gameTime = mc.level.getDayTime();
            int period = AcademicCalendar.getPeriod(gameTime);
            int dayOfPeriod = AcademicCalendar.getDayOfPeriod(gameTime);
            graphics.drawString(font, "§8Ano Letivo: §0Período " + period + " • Dia " + dayOfPeriod + "/180", textX, textY, 0x000000, false);
            textY += lineSpacing;
        }

        graphics.drawString(font, "§8Créditos: §6§l" + ClientHeroData.imperialCredits + " 🪙", textX, textY, 0x000000, false);

        int sealX = left + 246;
        int sealY = top + 118;
        graphics.drawCenteredString(font, "§e§lRANK", sealX, sealY - 14, 0xFFFFFF);
        graphics.drawCenteredString(font, "§f§l§n" + ClientHeroData.getGrade().getGradeSymbol(), sealX, sealY - 4, 0xFFFFFF);
        graphics.drawCenteredString(font, "§6" + ClientHeroData.examScore + " pts", sealX, sealY + 8, 0xFFFFFF);
    }

    private void renderSkillManagement(GuiGraphics graphics, Minecraft mc, int left, int top, int panelW, int panelH, int mouseX, int mouseY) {
        Font font = this.font;

        // Fundo escuro tecnológico amplo
        graphics.fill(left, top, left + panelW, top + panelH, 0xF50E1420);
        graphics.fill(left, top, left + panelW, top + 2, 0xFF354865);
        graphics.fill(left, top + panelH - 2, left + panelW, top + panelH, 0xFF354865);
        graphics.fill(left, top, left + 2, top + panelH, 0xFF354865);
        graphics.fill(left + panelW - 2, top, left + panelW, top + panelH, 0xFF354865);

        // Cabeçalho e Seletor de Barras
        graphics.drawString(font, "§e§lDECK DE HABILIDADES TÁTICO", left + 12, top + 10, 0xFFFFFF);

        // Botões de Barra 1 e Barra 2
        int btn1W = 80;
        int btn1H = 16;
        int btn2W = 80;
        int btn2X = left + panelW - 12 - btn2W;
        int btn1X = btn2X - 8 - btn1W;

        boolean hoverB1 = mouseX >= btn1X && mouseX <= btn1X + btn1W && mouseY >= top + 8 && mouseY <= top + 8 + btn1H;
        boolean hoverB2 = mouseX >= btn2X && mouseX <= btn2X + btn2W && mouseY >= top + 8 && mouseY <= top + 8 + btn1H;

        int b1Bg = (selectedBar == 0) ? 0xFF0066AA : (hoverB1 ? 0xAA224466 : 0x77152030);
        int b2Bg = (selectedBar == 1) ? 0xFFAA6600 : (hoverB2 ? 0xAA664422 : 0x77152030);

        graphics.fill(btn1X, top + 8, btn1X + btn1W, top + 8 + btn1H, b1Bg);
        graphics.drawCenteredString(font, "Barra 1" + (ClientHeroData.activeBarIndex == 0 ? " §a● (Ativa)" : ""), btn1X + (btn1W / 2), top + 12, 0xFFFFFF);

        graphics.fill(btn2X, top + 8, btn2X + btn2W, top + 8 + btn1H, b2Bg);
        graphics.drawCenteredString(font, "Barra 2" + (ClientHeroData.activeBarIndex == 1 ? " §a● (Ativa)" : ""), btn2X + (btn2W / 2), top + 12, 0xFFFFFF);

        // 5 SLOTS (Z, X, C, V, G) DA BARRA SELECIONADA
        int slotY = top + 32;
        int slotSpacing = 8;
        int slotW = (panelW - 24 - (slotSpacing * 4)) / 5;
        int slotH = 46;
        AbilitySlot[] slots = AbilitySlot.values();

        HeroSkill hoveredSkill = null;

        for (int i = 0; i < 5; i++) {
            int slotX = left + 12 + (i * (slotW + slotSpacing));
            String skillId = ClientHeroData.getSkillInBar(selectedBar, i);
            HeroSkill skill = HeroSkillRegistry.getSkill(skillId);
            int cd = ClientHeroData.getCooldownInBar(selectedBar, i);

            boolean isHovered = mouseX >= slotX && mouseX <= slotX + slotW && mouseY >= slotY && mouseY <= slotY + slotH;
            if (isHovered && skill != null) {
                hoveredSkill = skill;
            }

            int bg = (skill != null) ? 0xDD162030 : 0xAA0D121C;
            if (isHovered) bg = 0xFF243348;
            int border = (skill != null) ? (skill.getCategory().getColorHex() | 0xFF000000) : 0xFF334455;

            graphics.fill(slotX, slotY, slotX + slotW, slotY + slotH, bg);
            graphics.fill(slotX, slotY, slotX + slotW, slotY + 1, border);
            graphics.fill(slotX, slotY + slotH - 1, slotX + slotW, slotY + slotH, border);
            graphics.fill(slotX, slotY, slotX + 1, slotY + slotH, border);
            graphics.fill(slotX + slotW - 1, slotY, slotX + slotW, slotY + slotH, border);

            // Tecla
            graphics.drawCenteredString(font, "§e[" + slots[i].getKeyName() + "]", slotX + (slotW / 2), slotY + 4, 0xFFFF55);

            if (skill != null) {
                String name = skill.getName();
                if (name.length() > 11) name = name.substring(0, 10) + "…";
                graphics.drawCenteredString(font, "§f" + name, slotX + (slotW / 2), slotY + 17, 0xFFFFFF);

                String costText = (skill.getManaCost() > 0) ? "§b" + (int) skill.getManaCost() + " MP" : "§a" + (int) skill.getStaminaCost() + " VP";
                graphics.drawCenteredString(font, costText, slotX + (slotW / 2), slotY + 29, 0xAAAAAA);

                if (cd > 0) {
                    graphics.fill(slotX + 1, slotY + 1, slotX + slotW - 1, slotY + slotH - 1, 0xCC1A0808);
                    graphics.drawCenteredString(font, "§c" + (cd / 20) + "s", slotX + (slotW / 2), slotY + 20, 0xFF5555);
                }
            } else {
                graphics.drawCenteredString(font, "§8(Vazio)", slotX + (slotW / 2), slotY + 22, 0x777777);
            }
        }

        // Subtítulo e Instruções
        int subTitleY = slotY + slotH + 10;
        graphics.drawString(font, "§7Habilidades Desbloqueadas §8(Arraste para um slot ou puxe para fora para desequipar):", left + 12, subTitleY, 0xCCCCCC);

        // LISTA DE HABILIDADES DESBLOQUEADAS
        List<HeroSkill> unlocked = HeroSkillRegistry.getUnlockedSkills(
                ClientHeroData.powerType,
                ClientHeroData.routeAPoints,
                ClientHeroData.routeBPoints,
                ClientHeroData.routeCPoints,
                ClientHeroData.bodyLevel,
                ClientHeroData.mindLevel,
                ClientHeroData.techniqueLevel,
                ClientHeroData.primaryClass,
                ClientHeroData.selectedBranch
        );

        int totalPages = Math.max(1, (int) Math.ceil((double) unlocked.size() / SKILLS_PER_PAGE));
        if (skillsPage >= totalPages) skillsPage = totalPages - 1;

        int listY = subTitleY + 14;
        int cols = 4;
        int cardSpacingX = 6;
        int cardSpacingY = 5;
        int cardW = (panelW - 24 - (cardSpacingX * (cols - 1))) / cols;
        int cardH = 38;

        int startIndex = skillsPage * SKILLS_PER_PAGE;
        int endIndex = Math.min(startIndex + SKILLS_PER_PAGE, unlocked.size());

        for (int i = startIndex; i < endIndex; i++) {
            HeroSkill skill = unlocked.get(i);
            int localIdx = i - startIndex;
            int col = localIdx % cols;
            int row = localIdx / cols;

            int cardX = left + 12 + (col * (cardW + cardSpacingX));
            int cardCurY = listY + (row * (cardH + cardSpacingY));

            boolean isHovered = mouseX >= cardX && mouseX <= cardX + cardW && mouseY >= cardCurY && mouseY <= cardCurY + cardH;
            if (isHovered) {
                hoveredSkill = skill;
            }

            int cardBg = isHovered ? 0xFF253448 : 0xDD121A26;
            graphics.fill(cardX, cardCurY, cardX + cardW, cardCurY + cardH, cardBg);
            graphics.fill(cardX, cardCurY, cardX + 3, cardCurY + cardH, skill.getCategory().getColorHex() | 0xFF000000);

            // Nome da Skill
            String name = skill.getName();
            if (name.length() > 14) name = name.substring(0, 13) + "…";
            graphics.drawString(font, "§f" + name, cardX + 6, cardCurY + 4, 0xFFFFFF);

            // Origem
            String orig = skill.getOrigin();
            if (orig.length() > 14) orig = orig.substring(0, 13) + "…";
            graphics.drawString(font, "§8" + orig, cardX + 6, cardCurY + 15, 0x888888);

            // Custo e Recarga
            String cost = (skill.getManaCost() > 0) ? "§b" + (int) skill.getManaCost() + "MP" : "§a" + (int) skill.getStaminaCost() + "VP";
            graphics.drawString(font, cost + " §7• " + (skill.getBaseCooldownTicks() / 20) + "s", cardX + 6, cardCurY + 25, 0xAAAAAA);
        }

        // Navegação de Página
        int navY = panelH + top - 14;
        graphics.drawCenteredString(font, "§8Página " + (skillsPage + 1) + "/" + totalPages, left + (panelW / 2), navY, 0x888888);

        if (skillsPage > 0) {
            graphics.drawString(font, "§6[< Anterior]", left + 16, navY, 0xFFAA00);
        }
        if (skillsPage < totalPages - 1) {
            graphics.drawString(font, "§6[Próxima >]", left + panelW - 75, navY, 0xFFAA00);
        }

        // Tooltip rica ao passar o mouse
        if (hoveredSkill != null && draggedSkillId == null) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("§6§l" + hoveredSkill.getName()));
            tooltip.add(Component.literal("§7Origem: §f" + hoveredSkill.getOrigin()));
            if (hoveredSkill.getManaCost() > 0) {
                tooltip.add(Component.literal("§bCusto de Mana: " + (int) hoveredSkill.getManaCost() + " MP"));
            }
            if (hoveredSkill.getStaminaCost() > 0) {
                tooltip.add(Component.literal("§aCusto de Vigor: " + (int) hoveredSkill.getStaminaCost() + " VP"));
            }
            tooltip.add(Component.literal("§eTempo de Recarga: " + (hoveredSkill.getBaseCooldownTicks() / 20) + "s"));
            tooltip.add(Component.literal("§8" + hoveredSkill.getDescription()));
            graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int panelW = (currentTab == 0) ? CARD_TEXTURE_W : Math.min(560, this.width - 32);
        int panelH = (currentTab == 0) ? CARD_TEXTURE_H : Math.min(270, this.height - 48);
        int left = (this.width - panelW) / 2;
        int top = (this.height - panelH) / 2;

        // Clique nas abas superiores
        if (mouseY >= top - 20 && mouseY <= top - 2) {
            if (mouseX >= left && mouseX <= left + 120) {
                currentTab = 0;
                return true;
            } else if (mouseX >= left + 124 && mouseX <= left + 274) {
                currentTab = 1;
                return true;
            } else if (mouseX >= left + 278 && mouseX <= left + 418) {
                Minecraft.getInstance().setScreen(new CampusNavigationScreen());
                return true;
            }
        }

        if (currentTab == 1) {
            // Seletor de Barra (Barra 1 / Barra 2)
            int btn1W = 80;
            int btn1H = 16;
            int btn2W = 80;
            int btn2X = left + panelW - 12 - btn2W;
            int btn1X = btn2X - 8 - btn1W;

            if (mouseY >= top + 8 && mouseY <= top + 8 + btn1H) {
                if (mouseX >= btn1X && mouseX <= btn1X + btn1W) {
                    selectedBar = 0;
                    return true;
                } else if (mouseX >= btn2X && mouseX <= btn2X + btn2W) {
                    selectedBar = 1;
                    return true;
                }
            }

            // Clique nos 5 Slots da barra
            int slotY = top + 32;
            int slotSpacing = 8;
            int slotW = (panelW - 24 - (slotSpacing * 4)) / 5;
            int slotH = 46;

            for (int i = 0; i < 5; i++) {
                int slotX = left + 12 + (i * (slotW + slotSpacing));
                if (mouseX >= slotX && mouseX <= slotX + slotW && mouseY >= slotY && mouseY <= slotY + slotH) {
                    String curSkill = ClientHeroData.getSkillInBar(selectedBar, i);
                    if (button == 1) {
                        // Botão direito: desequipa direto!
                        if (curSkill != null && !curSkill.isEmpty()) {
                            PacketDistributor.sendToServer(new EquipSkillPayload(selectedBar, i, ""));
                            return true;
                        }
                    } else if (button == 0) {
                        // Botão esquerdo: começa a puxar/arrastar para mover ou desequipar fora
                        if (curSkill != null && !curSkill.isEmpty()) {
                            this.draggedSkillId = curSkill;
                            this.draggedFromSlot = i;
                            return true;
                        }
                    }
                }
            }

            // Clique nas Habilidades Desbloqueadas da grade
            List<HeroSkill> unlocked = HeroSkillRegistry.getUnlockedSkills(
                    ClientHeroData.powerType,
                    ClientHeroData.routeAPoints,
                    ClientHeroData.routeBPoints,
                    ClientHeroData.routeCPoints,
                    ClientHeroData.bodyLevel,
                    ClientHeroData.mindLevel,
                    ClientHeroData.techniqueLevel,
                    ClientHeroData.primaryClass,
                    ClientHeroData.selectedBranch
            );

            int subTitleY = slotY + slotH + 10;
            int listY = subTitleY + 14;
            int cols = 4;
            int cardSpacingX = 6;
            int cardSpacingY = 5;
            int cardW = (panelW - 24 - (cardSpacingX * (cols - 1))) / cols;
            int cardH = 38;

            int startIndex = skillsPage * SKILLS_PER_PAGE;
            int endIndex = Math.min(startIndex + SKILLS_PER_PAGE, unlocked.size());

            for (int i = startIndex; i < endIndex; i++) {
                int localIdx = i - startIndex;
                int col = localIdx % cols;
                int row = localIdx / cols;

                int cardX = left + 12 + (col * (cardW + cardSpacingX));
                int cardCurY = listY + (row * (cardH + cardSpacingY));

                if (mouseX >= cardX && mouseX <= cardX + cardW && mouseY >= cardCurY && mouseY <= cardCurY + cardH) {
                    if (button == 0) {
                        this.draggedSkillId = unlocked.get(i).getId();
                        this.draggedFromSlot = -1;
                        return true;
                    }
                }
            }

            // Paginação
            int navY = panelH + top - 14;
            if (mouseY >= navY - 4 && mouseY <= navY + 12) {
                if (mouseX >= left + 12 && mouseX <= left + 80 && skillsPage > 0) {
                    skillsPage--;
                    return true;
                } else if (mouseX >= left + panelW - 80 && mouseX <= left + panelW - 10) {
                    int totalPages = Math.max(1, (int) Math.ceil((double) unlocked.size() / SKILLS_PER_PAGE));
                    if (skillsPage < totalPages - 1) {
                        skillsPage++;
                        return true;
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggedSkillId != null && currentTab == 1) {
            int panelW = Math.min(560, this.width - 32);
            int panelH = Math.min(270, this.height - 48);
            int left = (this.width - panelW) / 2;
            int top = (this.height - panelH) / 2;

            int slotY = top + 32;
            int slotSpacing = 8;
            int slotW = (panelW - 24 - (slotSpacing * 4)) / 5;
            int slotH = 46;

            int targetSlot = -1;
            for (int i = 0; i < 5; i++) {
                int slotX = left + 12 + (i * (slotW + slotSpacing));
                if (mouseX >= slotX && mouseX <= slotX + slotW && mouseY >= slotY && mouseY <= slotY + slotH) {
                    targetSlot = i;
                    break;
                }
            }

            if (targetSlot != -1) {
                // Soltou em um slot: EQUIPA!
                PacketDistributor.sendToServer(new EquipSkillPayload(selectedBar, targetSlot, draggedSkillId));
                if (draggedFromSlot != -1 && draggedFromSlot != targetSlot) {
                    // Se moveu de outro slot, limpa o antigo
                    PacketDistributor.sendToServer(new EquipSkillPayload(selectedBar, draggedFromSlot, ""));
                }
            } else {
                // Soltou FORA dos slots: "puxando para fora para desequipar"
                if (draggedFromSlot != -1) {
                    PacketDistributor.sendToServer(new EquipSkillPayload(selectedBar, draggedFromSlot, ""));
                }
            }

            this.draggedSkillId = null;
            this.draggedFromSlot = -1;
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
