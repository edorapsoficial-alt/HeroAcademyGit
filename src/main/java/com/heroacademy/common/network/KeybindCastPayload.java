package com.heroacademy.common.network;

import com.heroacademy.HeroAcademy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record KeybindCastPayload(int slotIndex) implements CustomPacketPayload {
    public static final Type<KeybindCastPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(HeroAcademy.MODID, "keybind_cast"));

    public static final StreamCodec<ByteBuf, KeybindCastPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    KeybindCastPayload::slotIndex,
                    KeybindCastPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
