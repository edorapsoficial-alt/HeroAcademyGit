package com.heroacademy.common.network;

import com.heroacademy.HeroAcademy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record EquipSkillPayload(
        int barIndex,
        int slotIndex,
        String skillId
) implements CustomPacketPayload {
    public static final Type<EquipSkillPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(HeroAcademy.MODID, "equip_skill"));

    public static final StreamCodec<FriendlyByteBuf, EquipSkillPayload> STREAM_CODEC =
            CustomPacketPayload.codec(EquipSkillPayload::write, EquipSkillPayload::new);

    public EquipSkillPayload(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt(), buf.readUtf());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(barIndex);
        buf.writeVarInt(slotIndex);
        buf.writeUtf(skillId != null ? skillId : "");
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
