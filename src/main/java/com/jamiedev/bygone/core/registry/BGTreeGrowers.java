package com.jamiedev.bygone.core.registry;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import javax.annotation.Nullable;

public class BGTreeGrowers {
    
    public static final AbstractTreeGrower SABLE_TREE = new AbstractTreeGrower() {
        @Nullable
        @Override
        protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
            return hasFlowers ? BGConfiguredFeatures.SABLE_TREE_MEDIUM : BGConfiguredFeatures.SABLE_TREE;
        }
    };
    
    public static final AbstractTreeGrower ANCIENT_TREE = new AbstractTreeGrower() {
        @Nullable
        @Override
        protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
            return BGConfiguredFeatures.ANCIENT_TREE;
        }
    };
}