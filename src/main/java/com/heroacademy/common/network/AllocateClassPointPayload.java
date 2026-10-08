package com.heroacademy.common.network;

import com.heroacademy.HeroAcademy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record AllocateClassPointPayload(
        int classOrdinal,
        int branchOrdinal
) implements CustomPacketPayload {
    public static final Type<AllocateClassPointPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(HeroAcademy.MODID, "allocate_class_point"));

    public static final StreamCodec<FriendlyByteBuf, AllocateClassPointPayload> STREAM_CODEC =
            CustomPacketPayload.codec(AllocateClassPointPayload::write, AllocateClassPointPayload::new);

    public AllocateClassPointPayload(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(classOrdinal);
        buf.writeVarInt(branchOrdinal);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
