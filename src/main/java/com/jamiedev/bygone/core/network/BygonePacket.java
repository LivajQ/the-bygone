package com.jamiedev.bygone.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface BygonePacket {
    void write(FriendlyByteBuf buf);
    void handle(Player player);
    ResourceLocation id();
}
