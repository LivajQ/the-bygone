package com.jamiedev.bygone.core.network;

import com.jamiedev.bygone.Bygone;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class EnforcePacket implements BygonePacket {
    private final boolean enforce;
    
    public EnforcePacket(boolean enforce) {
        this.enforce = enforce;
    }
    
    public static EnforcePacket read(FriendlyByteBuf buf) {
        return new EnforcePacket(buf.readBoolean());
    }
    
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(enforce);
    }
    
    @Override
    public void handle(Player player) {
        EnforcePacket.enforcedProgression = enforce;
    }
    
    @Override
    public ResourceLocation id() {
        return Bygone.id("sync_progression_status");
    }
    
    public boolean enforce() {
        return enforce;
    }
    
    public static boolean enforcedProgression = true;
}
