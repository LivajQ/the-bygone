package com.jamiedev.bygone.forge.core.registry;

import com.jamiedev.bygone.Bygone;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;

@Mod.EventBusSubscriber(modid = Bygone.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BGCommonSetupEvents {
    
    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        BGRegistriesForge.WEATHER_TYPES_FORGE =
                event.create(BGRegistriesForge.WEATHER_TYPE_BUILDER);
    }
    
}
