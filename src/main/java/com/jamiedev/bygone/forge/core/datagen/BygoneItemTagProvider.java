package com.jamiedev.bygone.forge.core.datagen;

import com.jamiedev.bygone.Bygone;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BygoneItemTagProvider extends ItemTagsProvider {

    public BygoneItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Bygone.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        signs();
        slabs();
        stairs();
        walls();
    }

    private void signs() {

    }

    private void slabs() {

    }

    private void stairs() {

    }

    private void walls() {

    }
}
