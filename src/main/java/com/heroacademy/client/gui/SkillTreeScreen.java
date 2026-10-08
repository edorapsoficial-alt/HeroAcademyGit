package com.heroacademy.client.gui;

import com.heroacademy.client.ClientHeroData;
import com.heroacademy.common.network.AllocateClassPointPayload;
import com.heroacademy.common.network.AllocateStatPayload;
import com.heroacademy.common.network.AllocateUniqueRoutePayload;
import com.heroacademy.common.power.ClassBranch;
import com.heroacademy.common.power.ClassType;
import com.heroacademy.common.power.StatType;
import com.heroacademy.common.power.TrainingAttribute;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class SkillTreeScreen extends Screen {

    public enum Tab {
        UNIQUE_SKILL("DOM INATO", "Habilidade Única & Rotas"),
        TRAINING_STATS("ATRIBUTOS", "Corpo & Mente"),
        ARTS("ARTES", "Maestrias Marciais & Arcanas"),
        CLASSES("CLASSES", "Especializações & Subclasses");

        private final String label;
        private final String subtitle;

        Tab(String label, String subtitle) {
            this.label = label;
            this.subtitle = subtitle;
        }

        public String getLabel() { return label; }
        public String getSubtitle() { return subtitle; }
    }

    private Tab currentTab = Tab.UNIQUE_SKILL;

    // Estado da Aba de Atributos: Sub-Aba selecionada (CORPO ou MENTE)
    private TrainingAttribute selectedAttrTab = TrainingAttribute.BODY;

    // Estado da Aba de Artes: Sub-Aba selecionada (0 = Marciais/Físicas, 1 = Arcanas/Mágicas)
    private int selectedArtsSubTab = 0;

    // Estado da Aba de Classes: Classe atualmente inspecionada
    private ClassType inspectedClass = ClassType.WARRIOR;

    public SkillTreeScreen() {
        super(Component.literal("Terminal de Habilidades da Academia"));
    }

    @Override
    protected void init() {
        super.init();
        if (ClientHeroData.primaryClass != null) {
            this.inspectedClass = ClientHeroData.primaryClass;
        }
        rebuildWidgets();
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Vazio para impedir o Gaussian Blur do vanilla 1.21.1 de borrar a interface
    }

    @Override
    protected void rebuildWidgets() {
        this.clearWidgets();

        int panelMarginX = 8;
        int panelMarginY = 28;
        int panelW = this.width - (panelMarginX * 2);
        int panelH = this.height - panelMarginY - 8;
        int panelX = panelMarginX;
        int panelY = panelMarginY;

        // ==============================================================
        // 1. ABAS PRINCIPAIS SUPERIORES (DOM INATO | ATRIBUTOS | ARTES | CLASSES)
        // ==============================================================
        Tab[] tabs = Tab.values();
        int tabW = Math.min(105, (panelW - 16) / tabs.length);
        int tabH = 18;
        int tabTopY = 6;
        int tabStartX = (this.width - (tabW * tabs.length + (tabs.length - 1) * 4)) / 2;

        for (int i = 0; i < tabs.length; i++) {
            Tab t = tabs[i];
            int x = tabStartX + (i * (tabW + 4));
            String prefix = (t == currentTab) ? "§b§l▶ " : "§7";

            this.addRenderableWidget(Button.builder(Component.literal(prefix + t.getLabel()), btn -> {
                this.currentTab = t;
                rebuildWidgets();
            }).bounds(x, tabTopY, tabW, tabH).build());
        }

        // ==============================================================
        // 2. WIDGETS DA ABA 1: DOM INATO
        // ==============================================================
        if (currentTab == Tab.UNIQUE_SKILL) {
            int modelW = Math.max(90, Math.min(130, panelW / 4));
            int routesW = panelW - modelW - 24;
            int routeStartY = panelY + 44;
            int routeBoxH = 38;
            int spacing = 6;

            for (int r = 0; r < 3; r++) {
                final int routeIdx = r;
                int rowY = routeStartY + (r * (routeBoxH + spacing));
                int btnX = panelX + routesW - 32;
                int btnY = rowY + 9;

                this.addRenderableWidget(Button.builder(Component.literal("+"), btn -> {
                    PacketDistributor.sendToServer(new AllocateUniqueRoutePayload(routeIdx));
                }).bounds(btnX, btnY, 22, 20).build());
            }
        }

        // ==============================================================
        // 3. WIDGETS DA ABA 2: ATRIBUTOS (CORPO & MENTE)
        // ==============================================================
        if (currentTab == Tab.TRAINING_STATS) {
            int subTabY = panelY + 22;
            int subTabW = (panelW - 28) / 2;

            TrainingAttribute[] attrs = new TrainingAttribute[]{TrainingAttribute.BODY, TrainingAttribute.MIND};
            for (int i = 0; i < attrs.length; i++) {
                TrainingAttribute attr = attrs[i];
                int subTabX = panelX + 12 + (i * (subTabW + 4));

                int level = (attr == TrainingAttribute.BODY) ? ClientHeroData.bodyLevel : ClientHeroData.mindLevel;
                String label = ((attr == selectedAttrTab) ? "§f§l" : "§7") + attr.getDisplayName() + " (Nv." + level + ")";
                this.addRenderableWidget(Button.builder(Component.literal(label), btn -> {
                    this.selectedAttrTab = attr;
                    rebuildWidgets();
                }).bounds(subTabX, subTabY, subTabW, 18).build());
            }

            // Botões [+] para os atributos da sub-aba ativa (Corpo: 6 stats, Mente: 4 stats)
            List<StatType> stats = getStatsFor(selectedAttrTab);
            int statStartY = panelY + 46;
            int statCardH = 26;
            int statSpacing = 3;

            for (int row = 0; row < stats.size(); row++) {
                StatType stat = stats.get(row);
                int rowY = statStartY + (row * (statCardH + statSpacing));
                final int statIndex = stat.ordinal();

                int btnX = panelX + panelW - 38;
                int btnY = rowY + 3;

                this.addRenderableWidget(Button.builder(Component.literal("+"), btn -> {
                    PacketDistributor.sendToServer(new AllocateStatPayload(statIndex));
                }).bounds(btnX, btnY, 22, 20).build());
            }
        }

        // ==============================================================
        // 4. WIDGETS DA ABA 3: ARTES (SUB-ABAS: MARCIAIS / ARCANAS)
        // ==============================================================
        if (currentTab == Tab.ARTS) {
            int subTabY = panelY + 22;
            int subTabW = (panelW - 28) / 2;

            String labelMartial = ((selectedArtsSubTab == 0) ? "§f§l" : "§7") + "⚔️ Artes Marciais (Físicas)";
            this.addRenderableWidget(Button.builder(Component.literal(labelMartial), btn -> {
                this.selectedArtsSubTab = 0;
                rebuildWidgets();
            }).bounds(panelX + 12, subTabY, subTabW, 18).build());

            String labelArcane = ((selectedArtsSubTab == 1) ? "§f§l" : "§7") + "🔮 Artes Arcanas (Mágicas)";
            this.addRenderableWidget(Button.builder(Component.literal(labelArcane), btn -> {
                this.selectedArtsSubTab = 1;
                rebuildWidgets();
            }).bounds(panelX + 16 + subTabW, subTabY, subTabW, 18).build());

            List<StatType> arts = (selectedArtsSubTab == 0) ? getMartialArts() : getArcaneArts();
            int statStartY = panelY + 46;
            int statCardH = 26;
            int statSpacing = 3;

            for (int row = 0; row < arts.size(); row++) {
                StatType art = arts.get(row);
                int rowY = statStartY + (row * (statCardH + statSpacing));
                final int statIndex = art.ordinal();

                int btnX = panelX + panelW - 38;
                int btnY = rowY + 3;

                this.addRenderableWidget(Button.builder(Component.literal("+"), btn -> {
                    PacketDistributor.sendToServer(new AllocateStatPayload(statIndex));
                }).bounds(btnX, btnY, 22, 20).build());
            }
        }

        // ==============================================================
        // 5. WIDGETS DA ABA 4: CLASSES (11 CLASSES TOTALMENTE NAVEGÁVEIS)
        // ==============================================================
        if (currentTab == Tab.CLASSES) {
            int leftPanelW = 126;
            int classListY = panelY + 28;

            // Lista vertical das 11 Classes no painel esquerdo
            ClassType[] classes = ClassType.values();
            int btnH = 16;
            int btnSpacing = 2;

            for (int i = 0; i < classes.length; i++) {
                final ClassType c = classes[i];
                int by = classListY + (i * (btnH + btnSpacing));

                boolean isInspected = (c == inspectedClass);
                String prefix = isInspected ? "§b▶ " : (c == ClientHeroData.primaryClass ? "§a★ " : "§f");
                int pts = ClientHeroData.getClassPoints(c);
                String btnText = prefix + c.getDisplayName() + " §7(" + pts + ")";

                this.addRenderableWidget(Button.builder(Component.literal(btnText), btn -> {
                    this.inspectedClass = c;
                    rebuildWidgets();
                }).bounds(panelX + 8, by, leftPanelW - 10, btnH).build());
            }

            // Painel Direito: Botão para Alocar Ponto na Classe Inspecionada
            int rightX = panelX + leftPanelW + 10;
            int rightW = panelW - leftPanelW - 18;

            this.addRenderableWidget(Button.builder(Component.literal("+1 Ponto na Classe"), btn -> {
                PacketDistributor.sendToServer(new AllocateClassPointPayload(inspectedClass.ordinal(), -1));
            }).bounds(rightX + rightW - 120, panelY + 28, 115, 18).build());

            // Botões [+] para as 3 Subclasses da Classe Inspecionada
            List<ClassBranch> branches = getBranchesFor(inspectedClass);
            int branchStartY = panelY + 78;
            int branchBoxH = 44;
            int branchSpacing = 6;

            for (int b = 0; b < branches.size(); b++) {
                final ClassBranch branch = branches.get(b);
                int by = branchStartY + (b * (branchBoxH + branchSpacing));
                int btnX = rightX + rightW - 28;
                int btnY = by + 12;

                this.addRenderableWidget(Button.builder(Component.literal("+"), btn -> {
                    PacketDistributor.sendToServer(new AllocateClassPointPayload(-1, branch.ordinal()));
                }).bounds(btnX, btnY, 22, 20).build());
            }
        }
    }

    private List<StatType> getStatsFor(TrainingAttribute attr) {
        List<StatType> list = new ArrayList<>();
        for (StatType s : StatType.values()) {
            if (s.getParentAttribute() == attr) {
                list.add(s);
            }
        }
        return list;
    }

    private List<StatType> getMartialArts() {
        return List.of(
                StatType.TECHNIQUE_WESTERN,
                StatType.TECHNIQUE_POLEARMS,
                StatType.TECHNIQUE_EASTERN,
                StatType.TECHNIQUE_EXOTIC,
                StatType.TECHNIQUE_MARTIAL,
                StatType.TECHNIQUE_DAGGERS
        );
    }

    private List<StatType> getArcaneArts() {
        return List.of(
                StatType.TECHNIQUE_ORBS,
                StatType.TECHNIQUE_STAVES,
                StatType.TECHNIQUE_TOMES,
                StatType.TECHNIQUE_RUNES
        );
    }

    private List<ClassBranch> getBranchesFor(ClassType type) {
        List<ClassBranch> list = new ArrayList<>();
        for (ClassBranch b : ClassBranch.values()) {
            if (b.getParentClass() == type) {
                list.add(b);
            }
        }
        return list;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Fundo escurecido translúcido em tela cheia (sem shader blur)
        graphics.fillGradient(0, 0, this.width, this.height, 0xDC050914, 0xF4070C18);

        int panelMarginX = 8;
        int panelMarginY = 28;
        int panelW = this.width - (panelMarginX * 2);
        int panelH = this.height - panelMarginY - 8;
        int panelX = panelMarginX;
        int panelY = panelMarginY;

        // Moldura do Painel
        graphics.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xEA0B101D);
        graphics.fill(panelX, panelY, panelX + panelW, panelY + 1, 0xFF00AAFF);
        graphics.fill(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, 0xFF005588);
        graphics.fill(panelX, panelY, panelX + 1, panelY + panelH, 0xFF005588);
        graphics.fill(panelX + panelW - 1, panelY, panelX + panelW, panelY + panelH, 0xFF005588);

        Font font = this.font;

        switch (currentTab) {
            case UNIQUE_SKILL -> renderUniqueSkillTab(graphics, font, panelX, panelY, panelW, panelH, mouseX, mouseY);
            case TRAINING_STATS -> renderTrainingStatsTab(graphics, font, panelX, panelY, panelW, panelH);
            case ARTS -> renderArtsTab(graphics, font, panelX, panelY, panelW, panelH);
            case CLASSES -> renderClassesTab(graphics, font, panelX, panelY, panelW, panelH);
        }

        // Renderiza os botões (renderables) por cima do conteúdo com 100% de nitidez
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    // ==============================================================
    // ABA 1: DOM INATO
    // ==============================================================
    private void renderUniqueSkillTab(GuiGraphics graphics, Font font, int panelX, int panelY, int panelW, int panelH, int mouseX, int mouseY) {
        int centerX = panelX + (panelW / 2);

        graphics.drawCenteredString(font, "§b§lTERMINAL DO DOM INATO", centerX, panelY + 6, 0xFFFFFF);

        String powerTitle = "§e§l" + ClientHeroData.powerType.getDisplayName() + " §7[" + ClientHeroData.rarity.getDisplayName() + "] §8| §fPontos Livres: §e§l" + ClientHeroData.talentPoints;
        graphics.drawCenteredString(font, powerTitle, centerX, panelY + 18, ClientHeroData.rarity.getColorHex());

        graphics.drawString(font, "§7" + ClientHeroData.powerType.getDescription(), panelX + 12, panelY + 30, 0xAAAAAA);

        int modelW = Math.max(90, Math.min(130, panelW / 4));
        int routesW = panelW - modelW - 24;

        int routeStartY = panelY + 44;
        int routeBoxH = 38;
        int spacing = 6;

        // Rota A
        int boxA = routeStartY;
        graphics.fill(panelX + 10, boxA, panelX + routesW, boxA + routeBoxH, 0x90181525);
        graphics.fill(panelX + 10, boxA, panelX + 13, boxA + routeBoxH, 0xFFFF4444);
        graphics.drawString(font, "§c§lROTA A - OFENSIVA §e(" + ClientHeroData.routeAPoints + "/5)", panelX + 18, boxA + 6, 0xFF5555);
        graphics.drawString(font, "§7+8% de dano mágico e impacto por nível.", panelX + 18, boxA + 20, 0xAAAAAA);

        // Rota B
        int boxB = boxA + routeBoxH + spacing;
        graphics.fill(panelX + 10, boxB, panelX + routesW, boxB + routeBoxH, 0x90151828);
        graphics.fill(panelX + 10, boxB, panelX + 13, boxB + routeBoxH, 0xFF44AAFF);
        graphics.drawString(font, "§b§lROTA B - MOBILIDADE §e(" + ClientHeroData.routeBPoints + "/5)", panelX + 18, boxB + 6, 0x55FFFF);
        graphics.drawString(font, "§7Velocidade supersônica e reposicionamento.", panelX + 18, boxB + 20, 0xAAAAAA);

        // Rota C
        int boxC = boxB + routeBoxH + spacing;
        graphics.fill(panelX + 10, boxC, panelX + routesW, boxC + routeBoxH, 0x90201815);
        graphics.fill(panelX + 10, boxC, panelX + 13, boxC + routeBoxH, 0xFFFFAA00);
        graphics.drawString(font, "§6§lROTA C - DESPERTAR §e(" + ClientHeroData.routeCPoints + "/5)", panelX + 18, boxC + 6, 0xFFAA00);
        graphics.drawString(font, "§7Duração do Despertar e Técnica Final Suprema.", panelX + 18, boxC + 20, 0xAAAAAA);

        // Pedestal 3D do Jogador
        int modelBoxX = panelX + routesW + 6;
        int modelBoxY = panelY + 44;
        int modelBoxH = (routeBoxH * 3) + (spacing * 2);

        graphics.fill(modelBoxX, modelBoxY, modelBoxX + modelW, modelBoxY + modelBoxH, 0x900E1624);
        graphics.fill(modelBoxX, modelBoxY, modelBoxX + modelW, modelBoxY + 1, 0xFF00AAFF);
        graphics.fill(modelBoxX, modelBoxY + modelBoxH - 1, modelBoxX + modelW, modelBoxY + modelBoxH, 0xFF00AAFF);
        graphics.fill(modelBoxX, modelBoxY, modelBoxX + 1, modelBoxY + modelBoxH, 0xFF00AAFF);
        graphics.fill(modelBoxX + modelW - 1, modelBoxY, modelBoxX + modelW, modelBoxY + modelBoxH, 0xFF00AAFF);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            int entityScale = Math.min(46, modelBoxH / 3);
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    graphics,
                    modelBoxX + 8,
                    modelBoxY + 8,
                    modelBoxX + modelW - 8,
                    modelBoxY + modelBoxH - 18,
                    entityScale,
                    0.0625f,
                    mouseX,
                    mouseY,
                    mc.player
            );
            graphics.drawCenteredString(font, "§f" + mc.player.getName().getString(), modelBoxX + (modelW / 2), modelBoxY + modelBoxH - 12, 0xFFFFFF);
        }
    }

    // ==============================================================
    // ABA 2: ATRIBUTOS (CORPO & MENTE)
    // ==============================================================
    private void renderTrainingStatsTab(GuiGraphics graphics, Font font, int panelX, int panelY, int panelW, int panelH) {
        int centerX = panelX + (panelW / 2);

        int availablePoints = (selectedAttrTab == TrainingAttribute.BODY) ? ClientHeroData.bodyPoints : ClientHeroData.mindPoints;

        graphics.drawCenteredString(font, "§b§lATRIBUTOS DE TREINAMENTO: §e" + selectedAttrTab.getDisplayName().toUpperCase(), centerX, panelY + 6, 0xFFFFFF);

        String info = "§7Pontos Livres para investir em " + selectedAttrTab.getDisplayName() + ": " + selectedAttrTab.getColorTag() + "§l" + availablePoints + " pts";
        graphics.drawCenteredString(font, info, centerX, panelY + 16, 0xFFFFFF);

        List<StatType> stats = getStatsFor(selectedAttrTab);
        int statStartY = panelY + 46;
        int statCardH = 26;
        int statSpacing = 3;
        int cardW = panelW - 24;

        for (int row = 0; row < stats.size(); row++) {
            StatType s = stats.get(row);
            int rowY = statStartY + (row * (statCardH + statSpacing));

            graphics.fill(panelX + 12, rowY, panelX + 12 + cardW, rowY + statCardH, 0x60121A28);
            graphics.fill(panelX + 12, rowY, panelX + 15, rowY + statCardH, selectedAttrTab.getColorHex() | 0xFF000000);

            int pts = ClientHeroData.getStat(s);

            graphics.drawString(font, "§f§l" + s.getDisplayName() + ": §e" + pts + " pts", panelX + 20, rowY + 4, 0xFFFFFF);
            graphics.drawString(font, "§7" + s.getDescription(), panelX + 20, rowY + 15, 0xAAAAAA);
        }
    }

    // ==============================================================
    // ABA 3: ARTES (SUB-ABAS LARGAS, ZERO COLISÃO)
    // ==============================================================
    private void renderArtsTab(GuiGraphics graphics, Font font, int panelX, int panelY, int panelW, int panelH) {
        int centerX = panelX + (panelW / 2);

        graphics.drawCenteredString(font, "§d§lMAESTRIAS MARCIAIS & ARCANAS (ARTES)", centerX, panelY + 6, 0xFFFFFF);

        String info = "§7Pontos Livres de Artes: §d§l" + ClientHeroData.techniquePoints + " pts §8| §a+6% de dano §7por ponto investido";
        graphics.drawCenteredString(font, info, centerX, panelY + 16, 0xFFFFFF);

        List<StatType> arts = (selectedArtsSubTab == 0) ? getMartialArts() : getArcaneArts();
        int statStartY = panelY + 46;
        int statCardH = 26;
        int statSpacing = 3;
        int cardW = panelW - 24;

        int accentColor = (selectedArtsSubTab == 0) ? 0xFFFF8C00 : 0xFFBA55D3;

        for (int row = 0; row < arts.size(); row++) {
            StatType art = arts.get(row);
            int rowY = statStartY + (row * (statCardH + statSpacing));

            graphics.fill(panelX + 12, rowY, panelX + 12 + cardW, rowY + statCardH, 0x60121A28);
            graphics.fill(panelX + 12, rowY, panelX + 15, rowY + statCardH, accentColor);

            int pts = ClientHeroData.getStat(art);
            int bonus = pts * 6;
            String bonusStr = (bonus > 0) ? " §a(+" + bonus + "% Dano)" : " §8(Base)";

            graphics.drawString(font, "§f§l" + art.getDisplayName() + ": §e" + pts + " pts" + bonusStr, panelX + 20, rowY + 4, 0xFFFFFF);
            graphics.drawString(font, "§7" + art.getDescription(), panelX + 20, rowY + 15, 0xAAAAAA);
        }
    }

    // ==============================================================
    // ABA 4: CLASSES (11 CLASSES TOTALMENTE NAVEGÁVEIS)
    // ==============================================================
    private void renderClassesTab(GuiGraphics graphics, Font font, int panelX, int panelY, int panelW, int panelH) {
        int centerX = panelX + (panelW / 2);

        String header = "§a§lESPECIALIZAÇÕES MARCIAIS & ARCANAS §8| §6Maestria Livre: §e§l" + ClientHeroData.classMasteryPoints + " pts";
        graphics.drawCenteredString(font, header, centerX, panelY + 6, 0xFFFFFF);

        String dominantLine = "§7Classe Dominante: §a§l" + ClientHeroData.primaryClass.getDisplayName() +
                " §7| Subclasse Dominante: §e§l" + ClientHeroData.selectedBranch.getDisplayName();
        graphics.drawCenteredString(font, dominantLine, centerX, panelY + 16, 0xCCCCCC);

        int leftPanelW = 126;

        // Divisória vertical
        graphics.fill(panelX + leftPanelW + 4, panelY + 26, panelX + leftPanelW + 5, panelY + panelH - 6, 0xFF223548);

        // Lado Direito: Detalhes da Classe Inspecionada e suas 3 Subclasses
        int rightX = panelX + leftPanelW + 10;
        int rightW = panelW - leftPanelW - 18;

        // Caixa da Classe Inspecionada
        int classBoxH = 42;
        graphics.fill(rightX, panelY + 26, rightX + rightW, panelY + 26 + classBoxH, 0x70101826);
        graphics.fill(rightX, panelY + 26, rightX + 3, panelY + 26 + classBoxH, 0xFF00AAFF);

        int classPts = ClientHeroData.getClassPoints(inspectedClass);
        graphics.drawString(font, "§b§lClasse: " + inspectedClass.getDisplayName() + " §8| §ePontos Alocados: §l" + classPts + " pts", rightX + 8, panelY + 31, 0x55FFFF);
        graphics.drawString(font, "§7" + inspectedClass.getDescription(), rightX + 8, panelY + 45, 0xAAAAAA);

        graphics.drawString(font, "§6Subclasses de Especialização:", rightX, panelY + 74, 0xFFAA00);

        List<ClassBranch> branches = getBranchesFor(inspectedClass);
        int branchStartY = panelY + 86;
        int branchBoxH = 40;
        int branchSpacing = 5;

        for (int b = 0; b < branches.size(); b++) {
            ClassBranch branch = branches.get(b);
            int by = branchStartY + (b * (branchBoxH + branchSpacing));

            boolean isDominant = (branch == ClientHeroData.selectedBranch);
            int bg = isDominant ? 0x90282010 : 0x60141C2B;
            graphics.fill(rightX, by, rightX + rightW, by + branchBoxH, bg);
            graphics.fill(rightX, by, rightX + 3, by + branchBoxH, isDominant ? 0xFFFFAA00 : 0xFF335577);

            int branchPts = ClientHeroData.getBranchPoints(branch);
            String title = (isDominant ? "§6★ " : "§f§l") + branch.getDisplayName() + " §8| §ePontos: §l" + branchPts + " pts";
            graphics.drawString(font, title, rightX + 8, by + 5, 0xFFFFFF);
            graphics.drawString(font, "§7" + branch.getDescription(), rightX + 8, by + 17, 0xAAAAAA);
            graphics.drawString(font, "§8Especialização dedicada do currículo da Academia.", rightX + 8, by + 28, 0x777777);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
