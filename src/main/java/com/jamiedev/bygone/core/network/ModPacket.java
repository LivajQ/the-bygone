package com.jamiedev.bygone.core.network;

public interface ModPacket<T extends FriendlyByteBuf> extends CustomPacketPayload {

    static <T extends FriendlyByteBuf, P extends ModPacket<T>> Type<P> type(Class<P> pClass) {
        return new Type<>(PacketHandler.packet(pClass));
    }

}
