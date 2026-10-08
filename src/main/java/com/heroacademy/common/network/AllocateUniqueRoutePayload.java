package com.heroacademy.common.network;

import com.heroacademy.HeroAcademy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record AllocateUniqueRoutePayload(int routeIndex) implements CustomPacketPayload {
    public static final Type<AllocateUniqueRoutePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(HeroAcademy.MODID, "allocate_unique_route"));

    public static final StreamCodec<ByteBuf, AllocateUniqueRoutePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    AllocateUniqueRoutePayload::routeIndex,
                    AllocateUniqueRoutePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
