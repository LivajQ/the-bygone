package com.jamiedev.bygone.forge.core.datagen;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;

import java.util.Set;

public class BygoneBlockLootSubProvider extends BlockLootSubProvider {
    
    public BygoneBlockLootSubProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    public void generate() {

    }


}