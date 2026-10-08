package com.heroacademy.common.network;

import com.heroacademy.HeroAcademy;
import com.heroacademy.common.attachment.HeroData;
import com.heroacademy.common.power.AbilitySlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncHeroDataPayload(
        String powerType,
        String rarity,
        int talentPoints,
        int routeAPoints,
        int routeBPoints,
        int routeCPoints,
        float currentEnergy,
        float maxEnergy,
        float currentStamina,
        float maxStamina,
        int masteryXp,
        int[] cooldowns,
        int awakenedTimer,
        int examScore,
        int imperialCredits,
        String schoolClass,
        int bodyLevel,
        int mindLevel,
        int techniqueLevel,
        int bodyPoints,
        int mindPoints,
        int techniquePoints,
        int[] allocatedStats,
        int classMasteryPoints,
        String primaryClass,
        String selectedBranch,
        int weeklyAttendanceCount,
        int activeBarIndex,
        String[] bar1Skills,
        String[] bar2Skills,
        int[] bar1Cooldowns,
        int[] bar2Cooldowns,
        int[] classAllocatedPoints,
        int[] branchAllocatedPoints
) implements CustomPacketPayload {
    public static final Type<SyncHeroDataPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(HeroAcademy.MODID, "sync_hero_data"));

    public static final StreamCodec<FriendlyByteBuf, SyncHeroDataPayload> STREAM_CODEC =
            CustomPacketPayload.codec(SyncHeroDataPayload::write, SyncHeroDataPayload::new);

    public SyncHeroDataPayload(FriendlyByteBuf buf) {
        this(
                buf.readUtf(),
                buf.readUtf(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readVarInt(),
                buf.readVarIntArray(),
                buf.readVarInt(), // awakenedTimer
                buf.readVarInt(), // examScore
                buf.readVarInt(), // imperialCredits
                buf.readUtf(),    // schoolClass
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarIntArray(),
                buf.readVarInt(),
                buf.readUtf(),
                buf.readUtf(),
                buf.readVarInt(),
                buf.readVarInt(),
                readStringArray(buf, 5),
                readStringArray(buf, 5),
                buf.readVarIntArray(),
                buf.readVarIntArray(),
                buf.readVarIntArray(),
                buf.readVarIntArray()
        );
    }

    private static String[] readStringArray(FriendlyByteBuf buf, int count) {
        String[] arr = new String[count];
        for (int i = 0; i < count; i++) {
            arr[i] = buf.readUtf();
        }
        return arr;
    }

    private static void writeStringArray(FriendlyByteBuf buf, String[] arr) {
        for (int i = 0; i < 5; i++) {
            buf.writeUtf((arr != null && i < arr.length && arr[i] != null) ? arr[i] : "");
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(powerType);
        buf.writeUtf(rarity);
        buf.writeVarInt(talentPoints);
        buf.writeVarInt(routeAPoints);
        buf.writeVarInt(routeBPoints);
        buf.writeVarInt(routeCPoints);
        buf.writeFloat(currentEnergy);
        buf.writeFloat(maxEnergy);
        buf.writeFloat(currentStamina);
        buf.writeFloat(maxStamina);
        buf.writeVarInt(masteryXp);
        buf.writeVarIntArray(cooldowns);
        buf.writeVarInt(awakenedTimer);
        buf.writeVarInt(examScore);
        buf.writeVarInt(imperialCredits);
        buf.writeUtf(schoolClass);
        buf.writeVarInt(bodyLevel);
        buf.writeVarInt(mindLevel);
        buf.writeVarInt(techniqueLevel);
        buf.writeVarInt(bodyPoints);
        buf.writeVarInt(mindPoints);
        buf.writeVarInt(techniquePoints);
        buf.writeVarIntArray(allocatedStats);
        buf.writeVarInt(classMasteryPoints);
        buf.writeUtf(primaryClass);
        buf.writeUtf(selectedBranch);
        buf.writeVarInt(weeklyAttendanceCount);
        buf.writeVarInt(activeBarIndex);
        writeStringArray(buf, bar1Skills);
        writeStringArray(buf, bar2Skills);
        buf.writeVarIntArray(bar1Cooldowns);
        buf.writeVarIntArray(bar2Cooldowns);
        buf.writeVarIntArray(classAllocatedPoints);
        buf.writeVarIntArray(branchAllocatedPoints);
    }

    public static SyncHeroDataPayload from(HeroData data) {
        int[] cds = new int[5];
        for (AbilitySlot slot : AbilitySlot.values()) {
            cds[slot.getIndex()] = data.getCooldown(slot);
        }
        return new SyncHeroDataPayload(
                data.getPowerType().name(),
                data.getRarity().name(),
                data.getTalentPoints(),
                data.getRouteAPoints(),
                data.getRouteBPoints(),
                data.getRouteCPoints(),
                data.getCurrentEnergy(),
                data.getMaxEnergy(),
                data.getCurrentStamina(),
                data.getMaxStamina(),
                data.getMasteryXp(),
                cds,
                data.getAwakenedTimer(),
                data.getExamScore(),
                data.getImperialCredits(),
                data.getSchoolClass().name(),
                data.getBodyLevel(),
                data.getMindLevel(),
                data.getTechniqueLevel(),
                data.getBodyPoints(),
                data.getMindPoints(),
                data.getTechniquePoints(),
                data.getAllocatedStats(),
                data.getClassMasteryPoints(),
                data.getDominantClass().name(),
                data.getDominantBranch().name(),
                data.getWeeklyAttendanceCount(),
                data.getActiveBarIndex(),
                data.getBar1Skills(),
                data.getBar2Skills(),
                data.getBar1Cooldowns(),
                data.getBar2Cooldowns(),
                data.getClassAllocatedPoints(),
                data.getBranchAllocatedPoints()
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
