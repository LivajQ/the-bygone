package com.jamiedev.bygone.core.network;

import com.jamiedev.bygone.Bygone;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class BygonePackets {
    private static final Map<ResourceLocation, Function<FriendlyByteBuf, BygonePacket>> REGISTRY = new HashMap<>();
    
    public static void register(ResourceLocation id, Function<FriendlyByteBuf, BygonePacket> factory) {
        REGISTRY.put(id, factory);
    }
    
    public static BygonePacket create(ResourceLocation id, FriendlyByteBuf buf) {
        Function<FriendlyByteBuf, BygonePacket> factory = REGISTRY.get(id);
        if (factory == null)
            throw new IllegalStateException("Unknown packet id: " + id);
        return factory.apply(buf);
    }
    
    public static void init() {
        register(Bygone.id("sync_weather"), SyncWeatherS2C::read);
        register(Bygone.id("updraft_movement"), UpdraftMovementS2C::read);
        register(Bygone.id("sync_player_hook"), SyncPlayerHookS2C::read);
    }
}
