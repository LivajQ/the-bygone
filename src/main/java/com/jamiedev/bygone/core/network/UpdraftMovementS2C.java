package com.jamiedev.bygone.core.network;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.client.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class UpdraftMovementS2C implements BygonePacket {
    private final double velocityY;
    private final boolean isDescending;
    
    public UpdraftMovementS2C(double velocityY, boolean isDescending) {
        this.velocityY = velocityY;
        this.isDescending = isDescending;
    }
    
    public static UpdraftMovementS2C read(FriendlyByteBuf buf) {
        double vel = buf.readDouble();
        boolean desc = buf.readBoolean();
        return new UpdraftMovementS2C(vel, desc);
    }
    
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(velocityY);
        buf.writeBoolean(isDescending);
    }
    
    @Override
    public void handle(Player player) {
        ClientPacketHandler.handle(this);
    }
    
    @Override
    public ResourceLocation id() {
        return Bygone.id("updraft_movement");
    }
    
    public double velocityY() { return velocityY; }
    public boolean isDescending() { return isDescending; }
}
