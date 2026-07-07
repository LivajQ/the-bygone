package com.jamiedev.bygone.forge.client;

import com.jamiedev.bygone.client.BygoneClient;
import com.jamiedev.bygone.client.particles.BlemishParticle;
import com.jamiedev.bygone.client.particles.UpsidedownDropParticle;
import com.jamiedev.bygone.common.block.JamiesModWoodType;
import com.jamiedev.bygone.core.registry.BGParticleTypes;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.Sheets;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class BygoneClientForge {
    public static void init(IEventBus eventBus) {
        eventBus.addListener(BygoneClientForge::setup);
        //eventBus.addListener(BygoneClientForge::fluidRegister);
        eventBus.addListener(BygoneClientForge::createRenderers);
        eventBus.addListener(BygoneClientForge::createModelLayers);
        eventBus.addListener(BygoneClientForge::registerParticleFactories);
    }

    static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BygoneClient.registerRenderLayers(ItemBlockRenderTypes::setRenderLayer);
            BygoneClient.registerModelPredicateProviders();
            Sheets.addWoodType(JamiesModWoodType.ANCIENT);


        });
    }

    static void createRenderers(EntityRenderersEvent.RegisterRenderers event) {
        BygoneClient.createEntityRenderers();
    }

    static void createModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        BygoneClient.createModelLayers(event::registerLayerDefinition);
    }

    static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        BygoneClient.registerParticleFactories(event::registerSpriteSet);
        event.registerSpriteSet(BGParticleTypes.BLEMISH, BlemishParticle.BlemishBlockProvider::new);
        event.registerSpriteSet(BGParticleTypes.UPSIDEDOWN, UpsidedownDropParticle.Provider::new);

    }

}
