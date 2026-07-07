package com.jamiedev.bygone.forge.core.registry;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.core.registry.BGAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BGAttributesForge {
    
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Bygone.MOD_ID);
    
    public static final RegistryObject<Attribute> PHASING_DURATION =
            ATTRIBUTES.register("phasing_duration",
                    () -> BGAttributes.PHASING_DURATION_VALUE
            );
    
    public static void init(IEventBus modEventBus) {
        ATTRIBUTES.register(modEventBus);
        BGAttributes.PHASING_DURATION = () -> ForgeRegistries.ATTRIBUTES.getHolder(PHASING_DURATION.getId()).orElseThrow();
    }
}

