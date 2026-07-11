package com.jamiedev.bygone.common.item;

import com.jamiedev.bygone.Bygone;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class CustomAnimalArmorItem extends Item {
    private final ResourceLocation textureLocation;
    @Nullable
    private final ResourceLocation overlayTextureLocation;
    private final int protection;
    private final BodyType bodyType;
    
    public CustomAnimalArmorItem(int protection, BodyType bodyType, String id, boolean hasOverlay, Item.Properties properties) {
        super(properties);
        this.protection = protection;
        this.bodyType = bodyType;
        ResourceLocation resourcelocation = bodyType.textureLocator.apply(Bygone.id(id));
        this.textureLocation = resourcelocation.withSuffix(".png");
        this.overlayTextureLocation = hasOverlay ? resourcelocation.withSuffix("_overlay.png") : null;
    }
    
    public ResourceLocation getTexture() {
        return this.textureLocation;
    }
    
    @Nullable
    public ResourceLocation getOverlayTexture() {
        return this.overlayTextureLocation;
    }
    
    public int getProtection() {
        return this.protection;
    }
    
    public BodyType getBodyType() {
        return this.bodyType;
    }
    
    //@Override
    public SoundEvent getBreakingSound() {
        return this.bodyType.breakingSound;
    }
    
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
    
    public enum BodyType {
        BIG_BEAK(id -> Bygone.id("textures/entity/big_beak/beak_" + id.getPath()), SoundEvents.ITEM_BREAK);
        
        final Function<ResourceLocation, ResourceLocation> textureLocator;
        final SoundEvent breakingSound;
        
        BodyType(Function<ResourceLocation, ResourceLocation> textureLocator, SoundEvent breakingSound) {
            this.textureLocator = textureLocator;
            this.breakingSound = breakingSound;
        }
    }
}