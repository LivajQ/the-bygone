package com.jamiedev.bygone.core.network;

public class PacketHandler {

    /*
    public static void registerPackets() {
        Services.PLATFORM.registerClientPlayPacket(SyncPlayerHookS2C.PACkET_ID, SyncPlayerHookS2C.CODEC);
        Services.PLATFORM.registerClientPlayPacket(UpdraftMovementS2C.PACKET_ID, UpdraftMovementS2C.CODEC);
        Services.PLATFORM.registerClientPlayPacket(SyncWeatherS2C.PACkET_ID, SyncWeatherS2C.CODEC);
    }

    public static void sendToServer(C2SModPacket<?> packet) {
        Services.PLATFORM.sendToServer(packet);
    }

    public static void sendTo(S2CModPacket<?> packet, ServerPlayer player) {//todo check for fake players
        Services.PLATFORM.sendToClient(packet, player);
    }

    public static void sendPacketToAllInArea(ServerLevel level, S2CModPacket<?> packet, BlockPos center, int rangesqr) {
        List<ServerPlayer> playerList = level.players();
        for (ServerPlayer player : playerList) {
            if (player.distanceToSqr(center.getX(), center.getY(), center.getZ()) < rangesqr) {
                sendTo(packet, player);
            }
        }
    }

    public static void sendPacketToAllInLevel(ServerLevel level, S2CModPacket<?> packet) {
        List<ServerPlayer> playerList = level.players();
        for (ServerPlayer player : playerList) sendTo(packet, player);
    }

    public static void sendPacketToAll(MinecraftServer server, S2CModPacket<?> packet) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendTo(packet, player);
        }
    }


    public static ResourceLocation packet(Class<?> clazz) {
        return Bygone.id(clazz.getName().toLowerCase(Locale.ROOT));
    }


     */

}
