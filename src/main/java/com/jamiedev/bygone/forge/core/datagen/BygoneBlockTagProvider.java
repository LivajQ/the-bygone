package com.jamiedev.bygone.forge.core.datagen;

import com.jamiedev.bygone.Bygone;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BygoneBlockTagProvider extends BlockTagsProvider {

    public BygoneBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Bygone.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        swordEfficient();
        flowers();
        insideStepSoundBlocks();
        //mineableWithHoe();
        endermanHoldable();
        goatSpawnableOn();
        bambooPlantable();
        sculkReplaceable();


    }

    private void swordEfficient() {

    }

    private void flowers() {

    }

    private void insideStepSoundBlocks() {

    }

    private void mineableWithHoe() {

    }

    private void endermanHoldable() {

    }

    private void bambooPlantable() {

    }

    private void goatSpawnableOn() {

    }

    private void sculkReplaceable() {

    }
}
