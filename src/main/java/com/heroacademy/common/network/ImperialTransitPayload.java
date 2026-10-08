package com.heroacademy.common.network;

import com.heroacademy.HeroAcademy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ImperialTransitPayload(
        String waypointId
) implements CustomPacketPayload {
    public static final Type<ImperialTransitPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(HeroAcademy.MODID, "imperial_transit"));

    public static final StreamCodec<FriendlyByteBuf, ImperialTransitPayload> STREAM_CODEC =
            CustomPacketPayload.codec(ImperialTransitPayload::write, ImperialTransitPayload::new);

    public ImperialTransitPayload(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(waypointId != null ? waypointId : "");
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
