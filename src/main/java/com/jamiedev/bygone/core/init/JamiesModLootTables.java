package com.jamiedev.bygone.core.init;

import com.jamiedev.bygone.Bygone;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JamiesModLootTables {
    public static final ResourceLocation EMPTY;
    public static final ResourceLocation ABANDONED_MINESHAFT_CHEST;
    public static final ResourceLocation LITHY_TRIP_LOOT_TABLE;
    private static final Set<ResourceLocation> LOOT_TABLES = new HashSet<>();
    private static final Set<ResourceLocation> LOOT_TABLES_READ_ONLY;
    
    static {
        LOOT_TABLES_READ_ONLY = Collections.unmodifiableSet(LOOT_TABLES);
        EMPTY = new ResourceLocation("empty");
        ABANDONED_MINESHAFT_CHEST = register("chests/abandoned_mineshaft");
        LITHY_TRIP_LOOT_TABLE = register("gameplay/lithy_trip");
    }
    
    public JamiesModLootTables() {
    }
    
    private static ResourceLocation register(String id) {
        return registerLootTable(Bygone.id(id));
    }
    
    private static ResourceLocation registerLootTable(ResourceLocation key) {
        if (LOOT_TABLES.add(key)) {
            return key;
        } else {
            throw new IllegalArgumentException(key + " is already a registered built-in loot table");
        }
    }
    
    public static Set<ResourceLocation> getAll() {
        return LOOT_TABLES_READ_ONLY;
    }
}