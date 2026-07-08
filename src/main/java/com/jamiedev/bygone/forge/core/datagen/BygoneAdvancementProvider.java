package com.jamiedev.bygone.forge.core.datagen;

import com.jamiedev.bygone.Bygone;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class BygoneAdvancementProvider extends AdvancementProvider {
    public BygoneAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper) {
        super(output, registries, List.of(new BygoneAdvancements()));
    }
    
    private static Advancement addInBiome(Consumer<Advancement> consumer, Advancement parent, String id, Item display, ResourceKey<Biome> biome) {
        return Advancement.Builder.advancement().parent(parent).display(
                        display,
                        Component.translatable("advancements." + Bygone.MOD_ID + "." + id + ".title"),
                        Component.translatable("advancements." + Bygone.MOD_ID + "." + id + ".description"),
                        null, FrameType.TASK, true, true, false)
                .addCriterion("in_biome", PlayerTrigger.TriggerInstance.located(LocationPredicate.inBiome(biome)))
                .save(consumer, Bygone.MOD_ID + ":" + id);
    }
    
    private static ItemUsedOnLocationTrigger.TriggerInstance itemUsedOnLocationCheckAbove(LocationPredicate.Builder location, LocationPredicate.Builder above, ItemPredicate.Builder item) {
        ContextAwarePredicate contextawarepredicate = ContextAwarePredicate.create(LocationCheck.checkLocation(location).build(), LocationCheck.checkLocation(above, BlockPos.ZERO.above()).build(), MatchTool.toolMatches(item).build());
        return new ItemUsedOnLocationTrigger.TriggerInstance(CriteriaTriggers.ITEM_USED_ON_BLOCK.getId(), ContextAwarePredicate.ANY, contextawarepredicate);
    }
    
    public static ItemUsedOnLocationTrigger.TriggerInstance itemUsedOnBlockCheckAbove(LocationPredicate.Builder location, LocationPredicate.Builder above, ItemPredicate.Builder item) {
        return itemUsedOnLocationCheckAbove(location, above, item);
    }
    
    public static class BygoneAdvancements implements AdvancementSubProvider {
        
        @Override
        public void generate(HolderLookup.Provider provider, Consumer<Advancement> consumer) {
            HolderGetter<Biome> biomes = provider.lookupOrThrow(Registries.BIOME);
            HolderGetter<Structure> structures = provider.lookupOrThrow(Registries.STRUCTURE);
        }
    }
}
