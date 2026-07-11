package com.jamiedev.bygone.core.registry;

import com.jamiedev.bygone.Bygone;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class BGPaintings {
    public static final ResourceKey<PaintingVariant> HALLUCIGENIA = of("hallucigenia");

    public static void bootstrap(BootstapContext<PaintingVariant> registry) {
        register(registry, HALLUCIGENIA, 4, 2);
    }

    private static void register(BootstapContext<PaintingVariant> registry, ResourceKey<PaintingVariant> key, int width, int height) {
        registry.register(key, new PaintingVariant(width, height));
    }

    private static ResourceKey<PaintingVariant> of(String id) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, Bygone.id(id));
    }
}
