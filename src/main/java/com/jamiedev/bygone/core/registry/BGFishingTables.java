package com.jamiedev.bygone.core.registry;

import com.jamiedev.bygone.Bygone;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public class BGFishingTables {
    
    private static final Map<ResourceKey<Biome>, ResourceLocation> BIOME_FISHING_TABLES = new HashMap<>();
    public static final ResourceLocation ALPHAHANG_FISHING = register("gameplay/fishing/alphahang", BGBiomes.ALPHAHANG);
    public static final ResourceLocation ANCIENT_FOREST_FISHING = register("gameplay/fishing/ancient_forest", BGBiomes.ANCIENT_FOREST);
    public static final ResourceLocation AMBER_DESERT_FISHING = register("gameplay/fishing/amber_desert", BGBiomes.AMBER_DESERT);
    public static final ResourceLocation MEGALITH_FIELD_FISHING = register("gameplay/fishing/megalith_field", BGBiomes.MEGALITH_FIELD);
    public static final ResourceLocation PRIMORDIAL_BEACH_FISHING = register("gameplay/fishing/primordial_shores", BGBiomes.PRIMORDIAL_BEACH);
    public static final ResourceLocation PRIMORDIAL_OCEAN_FISHING = register("gameplay/fishing/primordial_ocean", BGBiomes.PRIMORDIAL_OCEAN);
    public static final ResourceLocation SABLE_FOREST_FISHING = register("gameplay/fishing/sable_forest", BGBiomes.SABLE_FOREST);
    public static final ResourceLocation SHELFHOLLOW_FISHING = register("gameplay/fishing/shelfhollow", BGBiomes.SHELFHOLLOW);
    
    private static final Map<ResourceKey<Biome>, ResourceLocation> BIOME_FISHING_TABLES_RARE = new HashMap<>();
    public static final ResourceLocation ALPHAHANG_FISHING_RARE = registerRare("gameplay/fishing/alphahang_rare", BGBiomes.ALPHAHANG);
    public static final ResourceLocation ANCIENT_FOREST_FISHING_RARE = registerRare("gameplay/fishing/ancient_forest_rare", BGBiomes.ANCIENT_FOREST);
    public static final ResourceLocation AMBER_DESERT_FISHING_RARE = registerRare("gameplay/fishing/amber_desert_rare", BGBiomes.AMBER_DESERT);
    public static final ResourceLocation MEGALITH_FIELD_FISHING_RARE = registerRare("gameplay/fishing/megalith_field_rare", BGBiomes.MEGALITH_FIELD);
    public static final ResourceLocation PRIMORDIAL_BEACH_FISHING_RARE = registerRare("gameplay/fishing/primordial_shores_rare", BGBiomes.PRIMORDIAL_BEACH);
    public static final ResourceLocation PRIMORDIAL_OCEAN_FISHING_RARE = registerRare("gameplay/fishing/primordial_ocean_rare", BGBiomes.PRIMORDIAL_OCEAN);
    public static final ResourceLocation SABLE_FOREST_FISHING_RARE = registerRare("gameplay/fishing/sable_forest_rare", BGBiomes.SABLE_FOREST);
    public static final ResourceLocation SHELFHOLLOW_FISHING_RARE = registerRare("gameplay/fishing/shelfhollow_rare", BGBiomes.SHELFHOLLOW);
    
    private static ResourceLocation register(String path, ResourceKey<Biome> biome) {
        ResourceLocation lootTable = Bygone.id(path);
        BIOME_FISHING_TABLES.put(biome, lootTable);
        return lootTable;
    }
    
    private static ResourceLocation registerRare(String path, ResourceKey<Biome> biome) {
        ResourceLocation lootTable = Bygone.id(path);
        BIOME_FISHING_TABLES_RARE.put(biome, lootTable);
        return lootTable;
    }
    
    public static ResourceLocation getFishingTableForBiome(Holder<Biome> biomeHolder) {
        return getFishingTableForBiome(biomeHolder, false);
    }
    
    public static ResourceLocation getFishingTableForBiome(Holder<Biome> biomeHolder, boolean useRare) {
        ResourceKey<Biome> biomeKey = biomeHolder.unwrapKey().orElse(null);
        if (useRare) {
            return BIOME_FISHING_TABLES_RARE.get(biomeKey);
        }
        return BIOME_FISHING_TABLES.get(biomeKey);
    }
    
    public static void init() {
    }
}