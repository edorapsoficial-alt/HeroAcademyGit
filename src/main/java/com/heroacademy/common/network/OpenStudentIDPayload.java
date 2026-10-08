package com.heroacademy.common.network;

import com.heroacademy.HeroAcademy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenStudentIDPayload() implements CustomPacketPayload {
    public static final Type<OpenStudentIDPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(HeroAcademy.MODID, "open_student_id"));

    public static final StreamCodec<ByteBuf, OpenStudentIDPayload> STREAM_CODEC =
            StreamCodec.unit(new OpenStudentIDPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
