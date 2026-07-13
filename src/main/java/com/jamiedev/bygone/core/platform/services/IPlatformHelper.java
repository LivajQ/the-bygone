package com.jamiedev.bygone.core.platform.services;

import com.jamiedev.bygone.client.renderer.weather.WeatherRenderer;
import com.jamiedev.bygone.common.weather.weather_types.WeatherType;
import com.jamiedev.bygone.core.network.BygonePacket;
import com.jamiedev.bygone.core.registry.BGItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.Collection;
import java.util.function.Supplier;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }
    
    void sendToServer(BygonePacket packet);
    
    void sendToClient(ServerPlayer player, BygonePacket packet);
    
    void sendToTrackingClientsImpl(ServerLevel level, Entity entity, BygonePacket packet);
    
    default void sendToAllClients(ServerLevel level, BygonePacket packet) {
        for (ServerPlayer player : level.players()) {
            sendToClient(player, packet);
        }
    }
    
    default void sendToTrackingClients(ServerLevel level, Entity entity, BygonePacket packet) {
        sendToTrackingClientsImpl(level, entity, packet);
    }

    /* this seems unused for now
    int getTimeInBygone(Entity entity);

    void setTimeInBygone(Entity entity, int time);
     */
    
    Collection<WeatherType> getInstancedWeatherTypes(ServerLevel level);
    
    Collection<WeatherRenderer> getInstancedWeatherRenderers();
    
    default Supplier<Item> registerSpawnEgg(String id, Supplier<? extends EntityType<? extends Mob>> type, int bgColor, int hlColor, Item.Properties properties) {
        return BGItems.registerItem(id, () -> new SpawnEggItem(type.get(), bgColor, hlColor, properties));
    }
}