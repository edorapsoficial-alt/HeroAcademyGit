package com.heroacademy.common.network;

import com.heroacademy.HeroAcademy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenCampusNavigationPayload() implements CustomPacketPayload {
    public static final Type<OpenCampusNavigationPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(HeroAcademy.MODID, "open_campus_navigation"));

    public static final StreamCodec<FriendlyByteBuf, OpenCampusNavigationPayload> STREAM_CODEC =
            CustomPacketPayload.codec(OpenCampusNavigationPayload::write, OpenCampusNavigationPayload::new);

    public OpenCampusNavigationPayload(FriendlyByteBuf buf) {
        this();
    }

    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
