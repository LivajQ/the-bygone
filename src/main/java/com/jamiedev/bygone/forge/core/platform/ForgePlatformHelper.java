package com.jamiedev.bygone.forge.core.platform;

import com.jamiedev.bygone.client.renderer.weather.WeatherRenderer;
import com.jamiedev.bygone.common.weather.weather_types.WeatherType;
import com.jamiedev.bygone.core.network.BygonePacket;
import com.jamiedev.bygone.core.platform.services.IPlatformHelper;
import com.jamiedev.bygone.forge.core.network.BygoneForgeNetworkHandler;
import com.jamiedev.bygone.forge.core.registry.BGRegistriesForge;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.Collection;
import java.util.stream.Collectors;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }
    
    @Override
    public void sendToServer(BygonePacket packet) {
        BygoneForgeNetworkHandler.sendToServer(packet);
    }
    
    @Override
    public void sendToClient(ServerPlayer player, BygonePacket packet) {
        BygoneForgeNetworkHandler.sendToClient(player, packet);
    }
    
    @Override
    public void sendToTrackingClientsImpl(ServerLevel level, Entity entity, BygonePacket packet) {
        BygoneForgeNetworkHandler.sendToTracking(level, entity, packet);
    }

    /*
    @Override
    public int getTimeInBygone(Entity entity) {
        return entity.getData(AttachmentTypesNeoForge.TIME_IN_BYGONE);
    }

    @Override
    public void setTimeInBygone(Entity entity, int time) {
        entity.setData(AttachmentTypesNeoForge.TIME_IN_BYGONE, time);
    }
     */
    
    @Override
    public Collection<WeatherType> getInstancedWeatherTypes(ServerLevel level) {
        return BGRegistriesForge.WEATHER_TYPES_FORGE.get().getValues().stream()
                .map(factory -> factory.get(level))
                .collect(Collectors.toSet());
    }
    
    @Override
    public Collection<WeatherRenderer> getInstancedWeatherRenderers() {
        return BGRegistriesForge.WEATHER_TYPES_FORGE.get().getValues().stream()
                .map(WeatherType.Factory::getRenderer)
                .collect(Collectors.toSet());
    }
}