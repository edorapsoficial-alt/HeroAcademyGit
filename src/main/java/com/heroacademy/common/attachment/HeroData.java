package com.heroacademy.common.attachment;

import com.heroacademy.common.academic.SchoolClass;
import com.heroacademy.common.academic.SchoolGrade;
import com.heroacademy.common.power.AbilitySlot;
import com.heroacademy.common.power.ClassBranch;
import com.heroacademy.common.power.ClassType;
import com.heroacademy.common.power.HeroSkill;
import com.heroacademy.common.power.HeroSkillRegistry;
import com.heroacademy.common.power.PowerRarity;
import com.heroacademy.common.power.PowerType;
import com.heroacademy.common.power.StatType;
import com.heroacademy.common.power.TrainingAttribute;
import com.heroacademy.common.power.UniqueSkillRoute;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class HeroData implements INBTSerializable<CompoundTag> {
    public static final int MAX_ROUTE_POINTS = 5;

    // Unique Skill & 3 Rotas Evolutivas
    private PowerType powerType = PowerType.NONE;
    private PowerRarity rarity = PowerRarity.COMMON;
    private int talentPoints = 0; // Pontos livres para gastar nas rotas
    private int routeAPoints = 0; // Rota A - Ofensiva (0 a 5)
    private int routeBPoints = 0; // Rota B - Mobilidade (0 a 5)
    private int routeCPoints = 0; // Rota C - Despertar (0 a 5)

    // Recursos de Combate (Mana & Vigor)
    private float currentEnergy = 100.0f;
    private float maxEnergy = 100.0f;
    private float currentStamina = 100.0f;
    private float maxStamina = 100.0f;
    private int masteryXp = 0;
    private int[] cooldowns = new int[5]; // Retrocompatibilidade
    private int awakenedTimer = 0;

    // Slots de Habilidades: 2 Barras com 5 Slots cada (ZXCVG) e Cooldowns Independentes
    private String[] bar1Skills = new String[]{"", "", "", "", ""};
    private String[] bar2Skills = new String[]{"", "", "", "", ""};
    private int activeBarIndex = 0; // 0 = Barra 1, 1 = Barra 2
    private int[][] barCooldowns = new int[2][5];

    // Vida Acadêmica & Economia
    private SchoolClass schoolClass = SchoolClass.NONE;
    private int examScore = 0;
    private int imperialCredits = 100; // Moeda oficial da Academia Imperial Destiny
    private boolean restedBonus = false;
    private int classPresenceTicks = 0;
    private int weeklyAttendanceCount = 0;

    // Training Skills (Níveis e XP)
    private int bodyLevel = 1;
    private int bodyXp = 0;
    private int mindLevel = 1;
    private int mindXp = 0;
    private int techniqueLevel = 1;
    private int techniqueXp = 0;

    // Pontos Livres Disponíveis para Alocação
    private int bodyPoints = 0;
    private int mindPoints = 0;
    private int techniquePoints = 0;

    // Alocação dos Atributos Secundários
    private int[] allocatedStats = new int[StatType.values().length];

    // Class Skills (11 Classes e 33 Subclasses)
    private int classMasteryPoints = 0;
    private ClassType primaryClass = ClassType.WARRIOR;
    private ClassBranch selectedBranch = ClassBranch.SAMURAI;
    private int[] classAllocatedPoints = new int[ClassType.values().length];
    private int[] branchAllocatedPoints = new int[ClassBranch.values().length];

    // Efeitos de Exaustão (5 segundos ao zerar recursos com -60% de regeneração)
    private int staminaExhaustionTicks = 0;
    private int manaExhaustionTicks = 0;

    public HeroData() {}

    public void tick() {
        tick(null);
    }

    public void tick(ServerPlayer player) {
        // Redução dos timers de exaustão
        if (staminaExhaustionTicks > 0) staminaExhaustionTicks--;
        if (manaExhaustionTicks > 0) manaExhaustionTicks--;

        float restMultiplier = restedBonus ? 1.5f : 1.0f;

        // Regeneração de Mana contínua (com penalidade de 60% se exausto)
        float manaRegenBonus = 1.0f + (allocatedStats[StatType.MIND_MANA_REGEN.ordinal()] * 0.15f);
        if (manaExhaustionTicks > 0) {
            manaRegenBonus *= 0.40f; // Reduz em 60%
        }
        if (currentEnergy < maxEnergy) {
            currentEnergy = Math.min(maxEnergy, currentEnergy + (0.25f * restMultiplier * manaRegenBonus));
        }

        // Regeneração de Vigor contínua (com stat de Recuperação de Vigor e penalidade de 60%)
        float staminaRegenBonus = 1.0f + (allocatedStats[StatType.BODY_STAMINA_REGEN.ordinal()] * 0.15f);
        if (staminaExhaustionTicks > 0) {
            staminaRegenBonus *= 0.40f; // Reduz em 60%
        }
        if (currentStamina < maxStamina) {
            currentStamina = Math.min(maxStamina, currentStamina + (0.50f * restMultiplier * staminaRegenBonus));
        }

        // Redução independente de cooldowns para as duas barras de habilidades (ZXCVG)
        for (int b = 0; b < 2; b++) {
            for (int s = 0; s < 5; s++) {
                if (barCooldowns[b][s] > 0) {
                    barCooldowns[b][s]--;
                }
            }
        }

        // Redução retrocompatível de cooldowns
        for (int i = 0; i < cooldowns.length; i++) {
            if (cooldowns[i] > 0) {
                cooldowns[i]--;
            }
        }

        // Timer de Despertar
        if (awakenedTimer > 0) {
            awakenedTimer--;
        }
    }

    public boolean useEnergy(float amount) {
        return useEnergy(amount, null);
    }

    public boolean useEnergy(float amount, ServerPlayer player) {
        if (currentEnergy >= amount) {
            currentEnergy -= amount;
            if (currentEnergy <= 0.05f) {
                currentEnergy = 0.0f;
                triggerManaExhaustion(player);
            }
            return true;
        } else {
            if (currentEnergy > 0) {
                currentEnergy = 0.0f;
                triggerManaExhaustion(player);
            }
            return false;
        }
    }

    public boolean useStamina(float amount) {
        return useStamina(amount, null);
    }

    public boolean useStamina(float amount, ServerPlayer player) {
        if (currentStamina >= amount) {
            currentStamina -= amount;
            if (currentStamina <= 0.05f) {
                currentStamina = 0.0f;
                triggerStaminaExhaustion(player);
            }
            return true;
        } else {
            if (currentStamina > 0) {
                currentStamina = 0.0f;
                triggerStaminaExhaustion(player);
            }
            return false;
        }
    }

    public void triggerStaminaExhaustion(ServerPlayer player) {
        this.staminaExhaustionTicks = 100; // 5 segundos
        if (player != null) {
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, 100, 1));
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
            player.setSprinting(false);
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("§c§l[EXAUSTÃO DE VIGOR] §cSem fôlego! Fraqueza, Lentidão e -60% Regen por 5s!"), true);
        }
    }

    public void triggerManaExhaustion(ServerPlayer player) {
        this.manaExhaustionTicks = 100; // 5 segundos
        if (player != null) {
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, 100, 1));
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("§9§l[ESGOTAMENTO DE MANA] §9Sobrecarga arcana! Fraqueza, Lentidão e -60% Regen por 5s!"), true);
        }
    }

    public void addMastery(int xp) {
        this.masteryXp += xp;
        checkRankUp();
    }

    public void addTrainingXp(TrainingAttribute attr, int xp) {
        switch (attr) {
            case BODY -> {
                bodyXp += xp;
                if (bodyXp >= bodyLevel * 150) {
                    bodyXp -= bodyLevel * 150;
                    bodyLevel++;
                    bodyPoints++;
                }
            }
            case MIND -> {
                mindXp += xp;
                if (mindXp >= mindLevel * 150) {
                    mindXp -= mindLevel * 150;
                    mindLevel++;
                    mindPoints++;
                    recalculateDerivedStats(null);
                }
            }
            case TECHNIQUE -> {
                techniqueXp += xp;
                if (techniqueXp >= techniqueLevel * 150) {
                    techniqueXp -= techniqueLevel * 150;
                    techniqueLevel++;
                    techniquePoints++;
                }
            }
        }
    }

    public boolean allocateUniqueRoute(int routeIndex) {
        if (talentPoints <= 0) return false;

        switch (routeIndex) {
            case 0 -> {
                if (routeAPoints < MAX_ROUTE_POINTS) {
                    routeAPoints++;
                    talentPoints--;
                    return true;
                }
            }
            case 1 -> {
                if (routeBPoints < MAX_ROUTE_POINTS) {
                    routeBPoints++;
                    talentPoints--;
                    return true;
                }
            }
            case 2 -> {
                if (routeCPoints < MAX_ROUTE_POINTS) {
                    routeCPoints++;
                    talentPoints--;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean allocateStat(StatType stat, ServerPlayer player) {
        TrainingAttribute parent = stat.getParentAttribute();
        boolean hasPoints = switch (parent) {
            case BODY -> bodyPoints > 0;
            case MIND -> mindPoints > 0;
            case TECHNIQUE -> techniquePoints > 0;
        };

        if (!hasPoints) {
            return false;
        }

        switch (parent) {
            case BODY -> bodyPoints--;
            case MIND -> mindPoints--;
            case TECHNIQUE -> techniquePoints--;
        }

        allocatedStats[stat.ordinal()]++;
        recalculateDerivedStats(player);
        return true;
    }

    public void recalculateDerivedStats(ServerPlayer player) {
        this.maxEnergy = 100.0f + (allocatedStats[StatType.MIND_MAX_MANA.ordinal()] * 15.0f);
        this.maxStamina = 100.0f + (allocatedStats[StatType.BODY_STAMINA.ordinal()] * 10.0f);

        if (player != null) {
            AttributeInstance hpAttr = player.getAttribute(Attributes.MAX_HEALTH);
            if (hpAttr != null) {
                double targetHp = 20.0 + (allocatedStats[StatType.BODY_HP.ordinal()] * 2.0);
                hpAttr.setBaseValue(targetHp);
            }

            AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speedAttr != null) {
                double targetSpeed = 0.10 + (allocatedStats[StatType.BODY_SPEED.ordinal()] * 0.0015);
                speedAttr.setBaseValue(targetSpeed);
            }
        }
    }

    public float getCooldownReductionPercent() {
        int points = allocatedStats[StatType.MIND_CDR.ordinal()];
        return 1.0f - (1.0f / (1.0f + (points * 0.02f)));
    }

    public int getAdjustedCooldown(int baseCooldown) {
        float cdr = getCooldownReductionPercent();
        return Math.max(10, (int) (baseCooldown * (1.0f - cdr)));
    }

    public float getPhysicalDamageReduction() {
        int points = allocatedStats[StatType.BODY_DEFENSE.ordinal()];
        return 1.0f - (1.0f / (1.0f + (points * 0.015f)));
    }

    public float getMagicDamageMultiplier() {
        return getMagicDamageMultiplier(null);
    }

    public float getMagicDamageMultiplier(StatType artStat) {
        float mindBonus = allocatedStats[StatType.MIND_MAGIC_DAMAGE.ordinal()] * 0.025f;
        float artBonus = (artStat != null) ? allocatedStats[artStat.ordinal()] * 0.06f : 0.0f;
        float routeBonus = routeAPoints * 0.08f;
        return 1.0f + mindBonus + artBonus + routeBonus;
    }

    public float getPhysicalDamageMultiplier(StatType artStat) {
        float strengthBonus = allocatedStats[StatType.BODY_STRENGTH.ordinal()] * 0.025f;
        float artBonus = (artStat != null) ? allocatedStats[artStat.ordinal()] * 0.06f : 0.0f;
        return 1.0f + strengthBonus + artBonus;
    }

    public float getWeaponBonusDamage(StatType weaponStat) {
        return (getPhysicalDamageMultiplier(weaponStat) - 1.0f);
    }

    private void checkRankUp() {
        int requiredXp = switch (rarity) {
            case COMMON -> 500;
            case UNCOMMON -> 1500;
            case RARE -> 3500;
            case EPIC -> 8000;
            case LEGENDARY -> Integer.MAX_VALUE;
        };

        if (masteryXp >= requiredXp && rarity != PowerRarity.LEGENDARY) {
            this.rarity = this.rarity.getNext();
            this.talentPoints += 3; // Ganha 3 pontos de rota de Unique Skill por subida de raridade!
            this.classMasteryPoints += 3;
            this.bodyPoints += 1;
            this.mindPoints += 1;
            this.techniquePoints += 1;
        }
    }

    public boolean isCooldownReady(AbilitySlot slot) {
        return cooldowns[slot.getIndex()] <= 0;
    }

    public void setCooldown(AbilitySlot slot, int ticks) {
        cooldowns[slot.getIndex()] = ticks;
    }

    public int getCooldown(AbilitySlot slot) {
        return cooldowns[slot.getIndex()];
    }

    public String getSkillInSlot(int barIndex, int slotIndex) {
        if (barIndex < 0 || barIndex > 1 || slotIndex < 0 || slotIndex >= 5) return "";
        return barIndex == 0 ? bar1Skills[slotIndex] : bar2Skills[slotIndex];
    }

    public void setSkillInSlot(int barIndex, int slotIndex, String skillId) {
        if (barIndex < 0 || barIndex > 1 || slotIndex < 0 || slotIndex >= 5) return;
        if (skillId == null) skillId = "";
        if (barIndex == 0) {
            bar1Skills[slotIndex] = skillId;
        } else {
            bar2Skills[slotIndex] = skillId;
        }
    }

    public int getActiveBarIndex() {
        return activeBarIndex;
    }

    public void setActiveBarIndex(int index) {
        this.activeBarIndex = Math.max(0, Math.min(1, index));
    }

    public void toggleSkillBar() {
        this.activeBarIndex = (this.activeBarIndex == 0) ? 1 : 0;
    }

    public int getBarCooldown(int barIndex, int slotIndex) {
        if (barIndex < 0 || barIndex > 1 || slotIndex < 0 || slotIndex >= 5) return 0;
        return barCooldowns[barIndex][slotIndex];
    }

    public void setBarCooldown(int barIndex, int slotIndex, int ticks) {
        if (barIndex < 0 || barIndex > 1 || slotIndex < 0 || slotIndex >= 5) return;
        barCooldowns[barIndex][slotIndex] = ticks;
    }

    public boolean isBarSlotReady(int barIndex, int slotIndex) {
        return getBarCooldown(barIndex, slotIndex) <= 0;
    }

    public String[] getBar1Skills() { return bar1Skills; }
    public String[] getBar2Skills() { return bar2Skills; }
    public int[] getBar1Cooldowns() { return barCooldowns[0]; }
    public int[] getBar2Cooldowns() { return barCooldowns[1]; }

    public void ensureDefaultSkillsEquipped() {
        if (powerType == PowerType.NONE) return;
        boolean hasAny = false;
        for (String s : bar1Skills) {
            if (s != null && !s.isEmpty()) { hasAny = true; break; }
        }
        if (!hasAny) {
            java.util.List<HeroSkill> unlocked = HeroSkillRegistry.getUnlockedSkills(this);
            int idx = 0;
            for (HeroSkill skill : unlocked) {
                if (idx < 5) {
                    bar1Skills[idx++] = skill.getId();
                } else {
                    break;
                }
            }
        }
    }

    // Getters e Setters
    public PowerType getPowerType() { return powerType; }
    public void setPowerType(PowerType powerType) { this.powerType = powerType; }

    public PowerRarity getRarity() { return rarity; }
    public void setRarity(PowerRarity rarity) { this.rarity = rarity; }

    public int getTalentPoints() { return talentPoints; }
    public void setTalentPoints(int talentPoints) { this.talentPoints = talentPoints; }

    public int getRouteAPoints() { return routeAPoints; }
    public int getRouteBPoints() { return routeBPoints; }
    public int getRouteCPoints() { return routeCPoints; }

    public float getCurrentEnergy() { return currentEnergy; }
    public void setCurrentEnergy(float currentEnergy) { this.currentEnergy = currentEnergy; }

    public float getMaxEnergy() { return maxEnergy; }
    public void setMaxEnergy(float maxEnergy) { this.maxEnergy = maxEnergy; }

    public float getCurrentStamina() { return currentStamina; }
    public void setCurrentStamina(float currentStamina) { this.currentStamina = currentStamina; }

    public float getMaxStamina() { return maxStamina; }
    public void setMaxStamina(float maxStamina) { this.maxStamina = maxStamina; }

    public int getMasteryXp() { return masteryXp; }
    public void setMasteryXp(int masteryXp) { this.masteryXp = masteryXp; }

    public SchoolClass getSchoolClass() { return schoolClass; }
    public void setSchoolClass(SchoolClass schoolClass) { this.schoolClass = schoolClass; }

    public int getExamScore() { return examScore; }
    public void setExamScore(int examScore) { this.examScore = examScore; }

    public SchoolGrade getGrade() { return SchoolGrade.fromScore(examScore); }

    public boolean isAwakened() { return awakenedTimer > 0; }
    public int getAwakenedTimer() { return awakenedTimer; }
    public void setAwakenedTimer(int ticks) { this.awakenedTimer = ticks; }

    public boolean hasRestedBonus() { return restedBonus; }
    public void setRestedBonus(boolean restedBonus) { this.restedBonus = restedBonus; }

    public int getClassPresenceTicks() { return classPresenceTicks; }
    public void addClassPresenceTicks(int ticks) { this.classPresenceTicks += ticks; }
    public void resetClassPresenceTicks() { this.classPresenceTicks = 0; }

    public int getWeeklyAttendanceCount() { return weeklyAttendanceCount; }
    public void incrementWeeklyAttendance() { this.weeklyAttendanceCount++; }

    public int getBodyLevel() { return bodyLevel; }
    public int getMindLevel() { return mindLevel; }
    public int getTechniqueLevel() { return techniqueLevel; }

    public int getBodyPoints() { return bodyPoints; }
    public int getMindPoints() { return mindPoints; }
    public int getTechniquePoints() { return techniquePoints; }

    public int getStatPoints(StatType stat) {
        return allocatedStats[stat.ordinal()];
    }

    public int[] getAllocatedStats() {
        return allocatedStats;
    }

    public int getClassMasteryPoints() { return classMasteryPoints; }
    public void addClassMasteryPoints(int points) { this.classMasteryPoints += points; }

    public ClassType getPrimaryClass() { return primaryClass; }
    public void setPrimaryClass(ClassType primaryClass) { this.primaryClass = primaryClass; }

    public ClassBranch getSelectedBranch() { return selectedBranch; }
    public void setSelectedBranch(ClassBranch selectedBranch) { this.selectedBranch = selectedBranch; }

    public ClassType getDominantClass() {
        int max = -1;
        ClassType best = primaryClass;
        for (ClassType c : ClassType.values()) {
            if (classAllocatedPoints[c.ordinal()] > max) {
                max = classAllocatedPoints[c.ordinal()];
                best = c;
            }
        }
        return (max > 0) ? best : primaryClass;
    }

    public ClassBranch getDominantBranch() {
        int max = -1;
        ClassBranch best = selectedBranch;
        for (ClassBranch b : ClassBranch.values()) {
            if (branchAllocatedPoints[b.ordinal()] > max) {
                max = branchAllocatedPoints[b.ordinal()];
                best = b;
            }
        }
        return (max > 0) ? best : selectedBranch;
    }

    public boolean allocateClassPoint(ClassType type) {
        if (classMasteryPoints > 0) {
            classMasteryPoints--;
            classAllocatedPoints[type.ordinal()]++;
            this.primaryClass = getDominantClass();
            return true;
        }
        return false;
    }

    public boolean allocateBranchPoint(ClassBranch branch) {
        if (classMasteryPoints > 0) {
            classMasteryPoints--;
            branchAllocatedPoints[branch.ordinal()]++;
            this.selectedBranch = getDominantBranch();
            return true;
        }
        return false;
    }

    public int[] getClassAllocatedPoints() { return classAllocatedPoints; }
    public int[] getBranchAllocatedPoints() { return branchAllocatedPoints; }

    public int getImperialCredits() { return imperialCredits; }
    public void setImperialCredits(int credits) { this.imperialCredits = Math.max(0, credits); }
    public void addImperialCredits(int amount) { this.imperialCredits += Math.max(0, amount); }
    public boolean spendImperialCredits(int amount) {
        if (this.imperialCredits >= amount) {
            this.imperialCredits -= amount;
            return true;
        }
        return false;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putString("PowerType", powerType.name());
        tag.putString("Rarity", rarity.name());
        tag.putInt("TalentPoints", talentPoints);
        tag.putInt("RouteAPoints", routeAPoints);
        tag.putInt("RouteBPoints", routeBPoints);
        tag.putInt("RouteCPoints", routeCPoints);
        tag.putFloat("CurrentEnergy", currentEnergy);
        tag.putFloat("MaxEnergy", maxEnergy);
        tag.putFloat("CurrentStamina", currentStamina);
        tag.putFloat("MaxStamina", maxStamina);
        tag.putInt("MasteryXp", masteryXp);
        tag.putString("SchoolClass", schoolClass.name());
        tag.putInt("ExamScore", examScore);
        tag.putInt("ImperialCredits", imperialCredits);
        tag.putIntArray("Cooldowns", cooldowns);
        tag.putInt("AwakenedTimer", awakenedTimer);
        tag.putBoolean("RestedBonus", restedBonus);
        tag.putInt("ClassPresenceTicks", classPresenceTicks);
        tag.putInt("WeeklyAttendanceCount", weeklyAttendanceCount);
        tag.putInt("BodyLevel", bodyLevel);
        tag.putInt("BodyXp", bodyXp);
        tag.putInt("MindLevel", mindLevel);
        tag.putInt("MindXp", mindXp);
        tag.putInt("TechniqueLevel", techniqueLevel);
        tag.putInt("TechniqueXp", techniqueXp);
        tag.putInt("BodyPoints", bodyPoints);
        tag.putInt("MindPoints", mindPoints);
        tag.putInt("TechniquePoints", techniquePoints);
        tag.putIntArray("AllocatedStats", allocatedStats);
        tag.putInt("ClassMasteryPoints", classMasteryPoints);
        tag.putString("PrimaryClass", getDominantClass().name());
        tag.putString("SelectedBranch", getDominantBranch().name());
        tag.putIntArray("ClassAllocatedPoints", classAllocatedPoints);
        tag.putIntArray("BranchAllocatedPoints", branchAllocatedPoints);
        tag.putInt("ActiveBarIndex", activeBarIndex);
        for (int i = 0; i < 5; i++) {
            tag.putString("Bar1Skill_" + i, bar1Skills[i]);
            tag.putString("Bar2Skill_" + i, bar2Skills[i]);
        }
        tag.putIntArray("Bar1Cooldowns", barCooldowns[0]);
        tag.putIntArray("Bar2Cooldowns", barCooldowns[1]);
        tag.putInt("StaminaExhaustionTicks", staminaExhaustionTicks);
        tag.putInt("ManaExhaustionTicks", manaExhaustionTicks);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.contains("PowerType")) {
            try { powerType = PowerType.valueOf(tag.getString("PowerType")); } catch (Exception ignored) {}
        }
        if (tag.contains("Rarity")) {
            try { rarity = PowerRarity.valueOf(tag.getString("Rarity")); } catch (Exception ignored) {}
        }
        if (tag.contains("TalentPoints")) talentPoints = tag.getInt("TalentPoints");
        if (tag.contains("RouteAPoints")) routeAPoints = tag.getInt("RouteAPoints");
        if (tag.contains("RouteBPoints")) routeBPoints = tag.getInt("RouteBPoints");
        if (tag.contains("RouteCPoints")) routeCPoints = tag.getInt("RouteCPoints");
        if (tag.contains("CurrentEnergy")) currentEnergy = tag.getFloat("CurrentEnergy");
        if (tag.contains("MaxEnergy")) maxEnergy = tag.getFloat("MaxEnergy");
        if (tag.contains("CurrentStamina")) currentStamina = tag.getFloat("CurrentStamina");
        if (tag.contains("MaxStamina")) maxStamina = tag.getFloat("MaxStamina");
        if (tag.contains("MasteryXp")) masteryXp = tag.getInt("MasteryXp");
        if (tag.contains("SchoolClass")) {
            try { schoolClass = SchoolClass.valueOf(tag.getString("SchoolClass")); } catch (Exception ignored) {}
        }
        if (tag.contains("ExamScore")) examScore = tag.getInt("ExamScore");
        if (tag.contains("ImperialCredits")) imperialCredits = tag.getInt("ImperialCredits");
        if (tag.contains("Cooldowns")) {
            int[] loaded = tag.getIntArray("Cooldowns");
            System.arraycopy(loaded, 0, cooldowns, 0, Math.min(loaded.length, cooldowns.length));
        }
        if (tag.contains("AwakenedTimer")) awakenedTimer = tag.getInt("AwakenedTimer");
        if (tag.contains("RestedBonus")) restedBonus = tag.getBoolean("RestedBonus");
        if (tag.contains("ClassPresenceTicks")) classPresenceTicks = tag.getInt("ClassPresenceTicks");
        if (tag.contains("WeeklyAttendanceCount")) weeklyAttendanceCount = tag.getInt("WeeklyAttendanceCount");
        if (tag.contains("BodyLevel")) bodyLevel = tag.getInt("BodyLevel");
        if (tag.contains("BodyXp")) bodyXp = tag.getInt("BodyXp");
        if (tag.contains("MindLevel")) mindLevel = tag.getInt("MindLevel");
        if (tag.contains("MindXp")) mindXp = tag.getInt("MindXp");
        if (tag.contains("TechniqueLevel")) techniqueLevel = tag.getInt("TechniqueLevel");
        if (tag.contains("TechniqueXp")) techniqueXp = tag.getInt("TechniqueXp");
        if (tag.contains("BodyPoints")) bodyPoints = tag.getInt("BodyPoints");
        if (tag.contains("MindPoints")) mindPoints = tag.getInt("MindPoints");
        if (tag.contains("TechniquePoints")) techniquePoints = tag.getInt("TechniquePoints");
        if (tag.contains("AllocatedStats")) {
            int[] loaded = tag.getIntArray("AllocatedStats");
            System.arraycopy(loaded, 0, allocatedStats, 0, Math.min(loaded.length, allocatedStats.length));
        }
        if (tag.contains("ClassMasteryPoints")) classMasteryPoints = tag.getInt("ClassMasteryPoints");
        if (tag.contains("PrimaryClass")) {
            try { primaryClass = ClassType.valueOf(tag.getString("PrimaryClass")); } catch (Exception ignored) {}
        }
        if (tag.contains("SelectedBranch")) {
            try { selectedBranch = ClassBranch.valueOf(tag.getString("SelectedBranch")); } catch (Exception ignored) {}
        }
        if (tag.contains("ClassAllocatedPoints")) {
            int[] loaded = tag.getIntArray("ClassAllocatedPoints");
            System.arraycopy(loaded, 0, classAllocatedPoints, 0, Math.min(loaded.length, classAllocatedPoints.length));
        }
        if (tag.contains("BranchAllocatedPoints")) {
            int[] loaded = tag.getIntArray("BranchAllocatedPoints");
            System.arraycopy(loaded, 0, branchAllocatedPoints, 0, Math.min(loaded.length, branchAllocatedPoints.length));
        }
        if (tag.contains("ActiveBarIndex")) activeBarIndex = tag.getInt("ActiveBarIndex");
        for (int i = 0; i < 5; i++) {
            if (tag.contains("Bar1Skill_" + i)) bar1Skills[i] = tag.getString("Bar1Skill_" + i);
            if (tag.contains("Bar2Skill_" + i)) bar2Skills[i] = tag.getString("Bar2Skill_" + i);
        }
        if (tag.contains("Bar1Cooldowns")) {
            int[] cds1 = tag.getIntArray("Bar1Cooldowns");
            System.arraycopy(cds1, 0, barCooldowns[0], 0, Math.min(cds1.length, 5));
        }
        if (tag.contains("Bar2Cooldowns")) {
            int[] cds2 = tag.getIntArray("Bar2Cooldowns");
            System.arraycopy(cds2, 0, barCooldowns[1], 0, Math.min(cds2.length, 5));
        }
        if (tag.contains("StaminaExhaustionTicks")) staminaExhaustionTicks = tag.getInt("StaminaExhaustionTicks");
        if (tag.contains("ManaExhaustionTicks")) manaExhaustionTicks = tag.getInt("ManaExhaustionTicks");
    }
}
