package com.heroacademy.client;

import com.heroacademy.common.academic.SchoolClass;
import com.heroacademy.common.academic.SchoolGrade;
import com.heroacademy.common.network.SyncHeroDataPayload;
import com.heroacademy.common.power.AbilitySlot;
import com.heroacademy.common.power.ClassBranch;
import com.heroacademy.common.power.ClassType;
import com.heroacademy.common.power.PowerRarity;
import com.heroacademy.common.power.PowerType;
import com.heroacademy.common.power.StatType;

public class ClientHeroData {
    public static PowerType powerType = PowerType.NONE;
    public static PowerRarity rarity = PowerRarity.COMMON;

    // Unique Skill: Pontos de Talento e 3 Rotas
    public static int talentPoints = 0;
    public static int routeAPoints = 0; // 0 a 5
    public static int routeBPoints = 0; // 0 a 5
    public static int routeCPoints = 0; // 0 a 5

    public static float currentEnergy = 100.0f;
    public static float maxEnergy = 100.0f;
    public static float currentStamina = 100.0f;
    public static float maxStamina = 100.0f;
    public static int masteryXp = 0;
    public static int[] cooldowns = new int[5];
    public static int awakenedTimer = 0;
    public static int examScore = 0;
    public static int imperialCredits = 100;
    public static SchoolClass schoolClass = SchoolClass.NONE;

    // Slots de Habilidade: 2 Barras de 5 Slots e Cooldowns Independentes
    public static int activeBarIndex = 0; // 0 = Barra 1, 1 = Barra 2
    public static String[] bar1Skills = new String[]{"", "", "", "", ""};
    public static String[] bar2Skills = new String[]{"", "", "", "", ""};
    public static int[] bar1Cooldowns = new int[5];
    public static int[] bar2Cooldowns = new int[5];

    // Training Skills (Níveis)
    public static int bodyLevel = 1;
    public static int mindLevel = 1;
    public static int techniqueLevel = 1;

    // Pontos Livres Disponíveis
    public static int bodyPoints = 0;
    public static int mindPoints = 0;
    public static int techniquePoints = 0;

    // 13 Stats Secundários
    public static int[] allocatedStats = new int[StatType.values().length];

    // Class Skills
    public static int classMasteryPoints = 0;
    public static ClassType primaryClass = ClassType.WARRIOR;
    public static ClassBranch selectedBranch = ClassBranch.SAMURAI;
    public static int[] classAllocatedPoints = new int[ClassType.values().length];
    public static int[] branchAllocatedPoints = new int[ClassBranch.values().length];
    public static int weeklyAttendanceCount = 0;

    public static void update(SyncHeroDataPayload payload) {
        try { powerType = PowerType.valueOf(payload.powerType()); } catch (Exception ignored) {}
        try { rarity = PowerRarity.valueOf(payload.rarity()); } catch (Exception ignored) {}

        talentPoints = payload.talentPoints();
        routeAPoints = payload.routeAPoints();
        routeBPoints = payload.routeBPoints();
        routeCPoints = payload.routeCPoints();

        currentEnergy = payload.currentEnergy();
        maxEnergy = payload.maxEnergy();
        currentStamina = payload.currentStamina();
        maxStamina = payload.maxStamina();
        masteryXp = payload.masteryXp();
        if (payload.cooldowns() != null && payload.cooldowns().length >= 5) {
            System.arraycopy(payload.cooldowns(), 0, cooldowns, 0, 5);
        }
        awakenedTimer = payload.awakenedTimer();
        examScore = payload.examScore();
        imperialCredits = payload.imperialCredits();
        try { schoolClass = SchoolClass.valueOf(payload.schoolClass()); } catch (Exception ignored) {}

        bodyLevel = payload.bodyLevel();
        mindLevel = payload.mindLevel();
        techniqueLevel = payload.techniqueLevel();

        bodyPoints = payload.bodyPoints();
        mindPoints = payload.mindPoints();
        techniquePoints = payload.techniquePoints();

        if (payload.allocatedStats() != null && payload.allocatedStats().length >= allocatedStats.length) {
            System.arraycopy(payload.allocatedStats(), 0, allocatedStats, 0, allocatedStats.length);
        }

        classMasteryPoints = payload.classMasteryPoints();
        try { primaryClass = ClassType.valueOf(payload.primaryClass()); } catch (Exception ignored) {}
        try { selectedBranch = ClassBranch.valueOf(payload.selectedBranch()); } catch (Exception ignored) {}
        if (payload.classAllocatedPoints() != null && payload.classAllocatedPoints().length >= classAllocatedPoints.length) {
            System.arraycopy(payload.classAllocatedPoints(), 0, classAllocatedPoints, 0, classAllocatedPoints.length);
        }
        if (payload.branchAllocatedPoints() != null && payload.branchAllocatedPoints().length >= branchAllocatedPoints.length) {
            System.arraycopy(payload.branchAllocatedPoints(), 0, branchAllocatedPoints, 0, branchAllocatedPoints.length);
        }
        weeklyAttendanceCount = payload.weeklyAttendanceCount();

        activeBarIndex = payload.activeBarIndex();
        if (payload.bar1Skills() != null) {
            System.arraycopy(payload.bar1Skills(), 0, bar1Skills, 0, Math.min(payload.bar1Skills().length, 5));
        }
        if (payload.bar2Skills() != null) {
            System.arraycopy(payload.bar2Skills(), 0, bar2Skills, 0, Math.min(payload.bar2Skills().length, 5));
        }
        if (payload.bar1Cooldowns() != null) {
            System.arraycopy(payload.bar1Cooldowns(), 0, bar1Cooldowns, 0, Math.min(payload.bar1Cooldowns().length, 5));
        }
        if (payload.bar2Cooldowns() != null) {
            System.arraycopy(payload.bar2Cooldowns(), 0, bar2Cooldowns, 0, Math.min(payload.bar2Cooldowns().length, 5));
        }
    }

    public static int getClassPoints(ClassType type) {
        if (type == null || type.ordinal() >= classAllocatedPoints.length) return 0;
        return classAllocatedPoints[type.ordinal()];
    }

    public static int getBranchPoints(ClassBranch branch) {
        if (branch == null || branch.ordinal() >= branchAllocatedPoints.length) return 0;
        return branchAllocatedPoints[branch.ordinal()];
    }

    public static String getActiveSkillInSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= 5) return "";
        return activeBarIndex == 0 ? bar1Skills[slotIndex] : bar2Skills[slotIndex];
    }

    public static int getActiveBarCooldown(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= 5) return 0;
        return activeBarIndex == 0 ? bar1Cooldowns[slotIndex] : bar2Cooldowns[slotIndex];
    }

    public static String getSkillInBar(int barIndex, int slotIndex) {
        if (barIndex < 0 || barIndex > 1 || slotIndex < 0 || slotIndex >= 5) return "";
        return barIndex == 0 ? bar1Skills[slotIndex] : bar2Skills[slotIndex];
    }

    public static int getCooldownInBar(int barIndex, int slotIndex) {
        if (barIndex < 0 || barIndex > 1 || slotIndex < 0 || slotIndex >= 5) return 0;
        return barIndex == 0 ? bar1Cooldowns[slotIndex] : bar2Cooldowns[slotIndex];
    }

    public static int getStat(StatType stat) {
        return allocatedStats[stat.ordinal()];
    }

    public static int getTotalAvailablePoints() {
        return bodyPoints + mindPoints + techniquePoints + talentPoints;
    }

    public static int getCooldown(AbilitySlot slot) {
        return getActiveBarCooldown(slot.getIndex());
    }

    public static boolean isAwakened() {
        return awakenedTimer > 0;
    }

    public static SchoolGrade getGrade() {
        return SchoolGrade.fromScore(examScore);
    }
}
