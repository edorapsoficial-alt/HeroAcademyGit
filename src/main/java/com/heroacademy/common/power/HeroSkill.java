package com.heroacademy.common.power;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
interface SkillAction {
    boolean execute(ServerPlayer player, HeroSkill skill);
}

public class HeroSkill {
    public enum SkillCategory {
        UNIQUE("Habilidade Única", 0xFF9900),
        TRAINING_BODY("Treino: Corpo", 0xFF4444),
        TRAINING_MIND("Treino: Mente", 0x4488FF),
        TRAINING_TECHNIQUE("Treino: Técnica", 0xEEAA22),
        CLASS("Classe / Subclasse", 0x44CC88);

        private final String displayName;
        private final int colorHex;

        SkillCategory(String displayName, int colorHex) {
            this.displayName = displayName;
            this.colorHex = colorHex;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getColorHex() {
            return colorHex;
        }
    }

    private final String id;
    private final String name;
    private final SkillCategory category;
    private final String origin;
    private final float manaCost;
    private final float staminaCost;
    private final int baseCooldownTicks;
    private final String description;
    private final int requiredLevelOrPoints;
    private final SkillAction action;

    public HeroSkill(
            String id,
            String name,
            SkillCategory category,
            String origin,
            float manaCost,
            float staminaCost,
            int baseCooldownTicks,
            String description,
            int requiredLevelOrPoints,
            SkillAction action
    ) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.origin = origin;
        this.manaCost = manaCost;
        this.staminaCost = staminaCost;
        this.baseCooldownTicks = baseCooldownTicks;
        this.description = description;
        this.requiredLevelOrPoints = requiredLevelOrPoints;
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SkillCategory getCategory() {
        return category;
    }

    public String getOrigin() {
        return origin;
    }

    public float getManaCost() {
        return manaCost;
    }

    public float getStaminaCost() {
        return staminaCost;
    }

    public int getBaseCooldownTicks() {
        return baseCooldownTicks;
    }

    public String getDescription() {
        return description;
    }

    public int getRequiredLevelOrPoints() {
        return requiredLevelOrPoints;
    }

    public boolean execute(ServerPlayer player) {
        if (action != null) {
            return action.execute(player, this);
        }
        return false;
    }
}
