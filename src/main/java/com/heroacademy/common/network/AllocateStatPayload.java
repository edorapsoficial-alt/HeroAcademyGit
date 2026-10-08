package com.heroacademy.common.network;

import com.heroacademy.HeroAcademy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record AllocateStatPayload(int statOrdinal) implements CustomPacketPayload {
    public static final Type<AllocateStatPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(HeroAcademy.MODID, "allocate_stat"));

    public static final StreamCodec<ByteBuf, AllocateStatPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    AllocateStatPayload::statOrdinal,
                    AllocateStatPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
