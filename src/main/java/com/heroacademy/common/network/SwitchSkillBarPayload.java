package com.heroacademy.common.network;

import com.heroacademy.HeroAcademy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SwitchSkillBarPayload() implements CustomPacketPayload {
    public static final Type<SwitchSkillBarPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(HeroAcademy.MODID, "switch_skill_bar"));

    public static final StreamCodec<FriendlyByteBuf, SwitchSkillBarPayload> STREAM_CODEC =
            CustomPacketPayload.codec(SwitchSkillBarPayload::write, SwitchSkillBarPayload::new);

    public SwitchSkillBarPayload(FriendlyByteBuf buf) {
        this();
    }

    public void write(FriendlyByteBuf buf) {
        // Sem parâmetros necessários (apenas ação de alternar)
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
