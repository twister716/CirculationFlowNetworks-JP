package com.circulation.circulation_networks.utils;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface Packet<T extends Packet<T>> extends CustomPacketPayload {

    T decode(RegistryFriendlyByteBuf buf);

    void encode(RegistryFriendlyByteBuf buf);

    void handle(T message, IPayloadContext context);

    default StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return new StreamCodec<>() {
            @Override
            public T decode(RegistryFriendlyByteBuf buf) {
                return Packet.this.decode(buf);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, T value) {
                value.encode(buf);
            }
        };
    }
}
