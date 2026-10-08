package com.heroacademy.common.network;

import com.heroacademy.HeroAcademy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenTransitTerminalPayload() implements CustomPacketPayload {
    public static final Type<OpenTransitTerminalPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(HeroAcademy.MODID, "open_transit_terminal"));

    public static final StreamCodec<FriendlyByteBuf, OpenTransitTerminalPayload> STREAM_CODEC =
            CustomPacketPayload.codec(OpenTransitTerminalPayload::write, OpenTransitTerminalPayload::new);

    public OpenTransitTerminalPayload(FriendlyByteBuf buf) {
        this();
    }

    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
