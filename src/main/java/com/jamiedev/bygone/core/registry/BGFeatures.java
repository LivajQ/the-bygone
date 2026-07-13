package com.jamiedev.bygone.core.registry;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.common.worldgen.feature.*;
import com.jamiedev.bygone.common.worldgen.feature.config.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;

public final class BGFeatures {
    
    public static Feature<AncientTreeFeatureConfig> ANCIENT_TREE;
    public static Feature<NoneFeatureConfiguration> ANCIENT_VINES;
    public static Feature<RandomPatchConfiguration> ANCIENT_FLOWERS;
    public static Feature<AncientForestVegetationFeatureConfig> ANCIENT_FOREST_VEGETATION;
    public static Feature<SmallCloudConfig> SMALL_CLOUD;
    public static Feature<BlockStateConfiguration> AMBER;
    public static Feature<NoneFeatureConfiguration> AMBER_UNDER;
    public static Feature<PointedAmberFeatureConfig> POINTED_AMBER;
    public static Feature<PointedAmberClusterFeatureConfig> AMBER_CLUSTER;
    public static Feature<BlockStateConfiguration> BYCORAL_CLAW;
    public static Feature<BlockStateConfiguration> BYCORAL_MUSHROOM;
    public static Feature<BlockStateConfiguration> BYCORAL_TREE;
    public static Feature<BlockStateConfiguration> BYCORAL_PILLARS;
    public static Feature<DiskShelfFungiConfig> DISK_SHELF_FUNGI;
    public static Feature<FeatureFilledGeodeConfig> FEATURE_FILLED_GEODE;
    public static Feature<NoneFeatureConfiguration> FUNGI_VINES;
    public static Feature<MegalithConfig> MEGALITH;
    public static Feature<SableBranchConfig> SABLE_BRANCH;
    public static Feature<ThornySableBranchConfig> THORNY_SABLE_BRANCH;
    public static Feature<SableBranchConfig> SABLE_GRASS;
    
    public static void registerAll() {
        ANCIENT_TREE = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("ancient_tree"),
                new AncientTreeFeature(AncientTreeFeatureConfig.CODEC));
        
        ANCIENT_VINES = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("ancient_vines"),
                new AncientVinesFeature(NoneFeatureConfiguration.CODEC));
        
        ANCIENT_FLOWERS = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("ancient_flowers"),
                new RandomPatchFeature(RandomPatchConfiguration.CODEC));
        
        ANCIENT_FOREST_VEGETATION = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("underhang_vegetation"),
                new AncientForestVegetationFeature(AncientForestVegetationFeatureConfig.VEGETATION_CODEC));
        
        SMALL_CLOUD = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("small_cloud"),
                new SmallCloudFeature(SmallCloudConfig.CODEC));
        
        AMBER = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("amber"),
                new AmberFeature(BlockStateConfiguration.CODEC));
        
        AMBER_UNDER = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("amber_under"),
                new AmberUnderFeature(NoneFeatureConfiguration.CODEC));
        
        POINTED_AMBER = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("pointed_amber"),
                new PointedAmberFeature(PointedAmberFeatureConfig.CODEC));
        
        AMBER_CLUSTER = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("amber_cluster"),
                new PointedAmberClusterFeature(PointedAmberClusterFeatureConfig.CODEC));
        
        BYCORAL_CLAW = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("bycoral_claw"),
                new BycoralClawFeature(BlockStateConfiguration.CODEC));
        
        BYCORAL_MUSHROOM = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("bycoral_mushroom"),
                new BycoralMushroomFeature(BlockStateConfiguration.CODEC));
        
        BYCORAL_TREE = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("bycoral_tree"),
                new BycoralTreeFeature(BlockStateConfiguration.CODEC));
        
        BYCORAL_PILLARS = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("bycoral_pillars"),
                new BycoralPillarsFeature(BlockStateConfiguration.CODEC));
        
        DISK_SHELF_FUNGI = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("disk_shelf_fungi"),
                new DiskShelfFungiFeature(DiskShelfFungiConfig.CODEC));
        
        FEATURE_FILLED_GEODE = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("feature_filled_geode"),
                new FeatureFilledGeodeFeature(FeatureFilledGeodeConfig.CODEC));
        
        FUNGI_VINES = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("fungi_vines"),
                new TestFungiVineFeature(NoneFeatureConfiguration.CODEC));
        
        MEGALITH = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("megalith"),
                new MegalithFeature(MegalithConfig.CODEC));
        
        SABLE_BRANCH = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("sable_branches"),
                new SableBranchFeature(SableBranchConfig.CODEC));
        
        THORNY_SABLE_BRANCH = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("thorny_sable_branches"),
                new ThornySableBranchFeature(ThornySableBranchConfig.CODEC));
        
        SABLE_GRASS = Registry.register(BuiltInRegistries.FEATURE, Bygone.id("sable_grass"),
                new SableGrassFeature(SableBranchConfig.CODEC));
    }
}
