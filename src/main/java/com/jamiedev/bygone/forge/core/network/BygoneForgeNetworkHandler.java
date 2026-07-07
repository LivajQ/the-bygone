package com.jamiedev.bygone.forge.core.network;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.core.network.BygonePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class BygoneForgeNetworkHandler {
    private static final String PROTOCOL = "1";
    public static SimpleChannel CHANNEL;
    
    public static void register() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                Bygone.id("main"),
                () -> PROTOCOL,
                PROTOCOL::equals,
                PROTOCOL::equals
        );
        
        CHANNEL.registerMessage(
                0,
                BygoneForgePayloadPacket.class,
                BygoneForgePayloadPacket::encode,
                BygoneForgePayloadPacket::decode,
                BygoneForgePayloadPacket::handle
        );
    }
    
    public static void sendToServer(BygonePacket packet) {
        CHANNEL.sendToServer(new BygoneForgePayloadPacket(packet));
    }
    
    public static void sendToClient(ServerPlayer player, BygonePacket packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new BygoneForgePayloadPacket(packet));
    }
    
    public static void sendToTracking(ServerLevel level, Entity entity, BygonePacket packet) {
        level.getChunkSource().broadcast(
                entity,
                CHANNEL.toVanillaPacket(new BygoneForgePayloadPacket(packet), NetworkDirection.PLAY_TO_CLIENT)
        );
    }
}
