package com.jamiedev.bygone.forge.core.registry;

import com.jamiedev.bygone.common.weather.BygoneWeather;
import com.jamiedev.bygone.common.weather.weather_types.WeatherType;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class BGRegistriesForge {
    
    // 1.20.1 Forge does not natively support adding custom registries of Registry type, it forces IForgeRegistry
    // Since in this case it's just weather it felt more appropriate to just do it manually rather than
    // trying to invent an api for handling Vanilla vs IForge registries. IPlatformHelper interface contains all related methods
    
    public static Supplier<IForgeRegistry<WeatherType.Factory>> WEATHER_TYPES_FORGE;
    
    public static final RegistryBuilder<WeatherType.Factory> WEATHER_TYPE_BUILDER =
            new RegistryBuilder<WeatherType.Factory>()
                    .setName(BygoneWeather.WEATHER_TYPE_REGISTRY_KEY.location());
}
