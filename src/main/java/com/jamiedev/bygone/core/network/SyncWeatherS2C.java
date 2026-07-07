package com.jamiedev.bygone.core.network;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.common.weather.BygoneWeather;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SyncWeatherS2C implements BygonePacket {
    private final CompoundTag tag;
    
    public SyncWeatherS2C(CompoundTag tag) {
        this.tag = tag;
    }
    
    public static SyncWeatherS2C read(FriendlyByteBuf buf) {
        return new SyncWeatherS2C(buf.readNbt());
    }
    
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }
    
    @Override
    public void handle(Player player) {
        BygoneWeather.Client.getInstance().updateContext(tag);
    }
    
    @Override
    public ResourceLocation id() {
        return Bygone.id("sync_weather");
    }
    
    public CompoundTag tag() {
        return tag;
    }
}
