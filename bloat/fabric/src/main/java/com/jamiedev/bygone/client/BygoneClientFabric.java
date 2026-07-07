package com.jamiedev.bygone.client;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.client.fluids.BGFluidRenderer;
import com.jamiedev.bygone.client.renderer.entity.BygoneDimensionEffects;
import com.jamiedev.bygone.common.block.JamiesModWoodType;
import com.jamiedev.bygone.common.commands.BygoneWeatherCommand;
import com.jamiedev.bygone.common.item.VerdigrisBladeItem;
import com.jamiedev.bygone.common.weather.BygoneWeather;
import com.jamiedev.bygone.core.registry.BGDimensions;
import com.jamiedev.bygone.core.registry.BGFluids;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.intellij.lang.annotations.Identifier;

import java.util.Objects;

public class BygoneClientFabric implements ClientModInitializer {
    public static ResourceLocation BYGONE = Bygone.id("bygone");

    public static boolean isBlockingOnViaVersion(LivingEntity entity) {
        Item item = entity.getMainHandItem().getItem() instanceof VerdigrisBladeItem ? entity.getMainHandItem().getItem() : entity.getOffhandItem().getItem();
        return item instanceof VerdigrisBladeItem && item.components() != null && item.components().has(DataComponents.FOOD) && Objects.requireNonNull(item.components().get(DataComponents.FOOD)).eatSeconds() == 3600;
    }


    @Override
    public void onInitializeClient() {
        BGFluidRenderer.initialize();
        BygoneClient.registerRenderLayers(BlockRenderLayerMap.INSTANCE::putBlock);
        BygoneClient.createEntityRenderers();
        BygoneClient.createModelLayers((modelLayerLocation, layerDefinitionSupplier) -> EntityModelLayerRegistry.registerModelLayer(modelLayerLocation, layerDefinitionSupplier::get));
        BygoneClient.registerParticleFactories((particleType, spriteParticleRegistration) -> ParticleFactoryRegistry.getInstance().register(particleType, spriteParticleRegistration::create));

        DimensionRenderingRegistry.registerWeatherRenderer(BGDimensions.BYGONE_LEVEL_KEY,
        worldRenderContext -> {
            Level level = worldRenderContext.world();
            if (!level.dimension().equals(BGDimensions.BYGONE_LEVEL_KEY)) return;

            BygoneWeather.Client clientWeather = BygoneWeather.Client.getInstance();
            Vec3 cameraPosition = worldRenderContext.camera().getPosition();
            clientWeather.stream().forEach((renderer)
                    -> renderer.render(
                    level, worldRenderContext.lightmapTextureManager(),
                    worldRenderContext.camera().getPartialTickTime(),
                    cameraPosition.x, cameraPosition.y, cameraPosition.z
                )
            );
        });
        DimensionRenderingRegistry.registerDimensionEffects(BYGONE, BygoneDimensionEffects.INSTANCE);
        DimensionRenderingRegistry.registerSkyRenderer(BGDimensions.BYGONE_LEVEL_KEY, BygoneSkyRenderer.INSTANCE);

        BygoneClient.registerModelPredicateProviders();

        Sheets.SIGN_MATERIALS.put(JamiesModWoodType.ANCIENT, Sheets.getSignMaterial(JamiesModWoodType.ANCIENT));
        Sheets.HANGING_SIGN_MATERIALS.put(JamiesModWoodType.ANCIENT, Sheets.getHangingSignMaterial(JamiesModWoodType.ANCIENT));

        //   BlockEntityRendererFactories.register(JamiesModBlockEntities.BRUSHABLE_BLOCK, BygoneBrushableBlockEntityRenderer::new);
    }
}
