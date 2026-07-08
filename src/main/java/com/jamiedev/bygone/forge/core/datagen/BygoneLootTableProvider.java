package com.jamiedev.bygone.forge.core.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BygoneLootTableProvider extends LootTableProvider {
    public BygoneLootTableProvider(PackOutput pOutput, Set<ResourceLocation> pRequiredTables, List<SubProviderEntry> pSubProviders) {
        super(pOutput, pRequiredTables, pSubProviders);
    }

    public static BygoneLootTableProvider create(PackOutput pOutput) {
        return new BygoneLootTableProvider(
                pOutput,
                BuiltInLootTables.all(),
                List.of(
                        new SubProviderEntry(BygoneBlockLootSubProvider::new, LootContextParamSets.BLOCK)
                )
        );
    }
    
    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationcontext) {
        // Do not validate against all registered loot tables
    }
}