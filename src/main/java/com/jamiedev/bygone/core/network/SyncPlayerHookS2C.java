package com.jamiedev.bygone.core.network;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.client.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class SyncPlayerHookS2C implements BygonePacket {
    private final int hookId;
    private final UUID playerUUID;
    
    public SyncPlayerHookS2C(int hookId, UUID playerUUID) {
        this.hookId = hookId;
        this.playerUUID = playerUUID;
    }
    
    public static SyncPlayerHookS2C read(FriendlyByteBuf buf) {
        int id = buf.readVarInt();
        UUID uuid = buf.readUUID();
        return new SyncPlayerHookS2C(id, uuid);
    }
    
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(hookId);
        buf.writeUUID(playerUUID);
    }
    
    @Override
    public void handle(Player player) {
        ClientPacketHandler.handle(this);
    }
    
    @Override
    public ResourceLocation id() {
        return Bygone.id("sync_player_hook");
    }
    
    public int hookId() { return hookId; }
    public UUID playerUUID() { return playerUUID; }
}
