package com.jamiedev.bygone.core.mixin;

import com.jamiedev.bygone.common.entity.PrimordialFishEntity;
import com.jamiedev.bygone.core.registry.BGEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(MobBucketItem.class)
public abstract class MobBucketItemMixin {
    
    @Shadow
    protected abstract EntityType<?> getFishType();
    
    @Inject(method = "appendHoverText", at = @At("RETURN"))
    public void appendPrimordialFishHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag, CallbackInfo ci) {
        if (this.getFishType() == BGEntityTypes.PRIMORDIAL_FISH.get()) {
            CompoundTag compoundTag = stack.getTag();
            if (compoundTag == null || !compoundTag.contains(PrimordialFishEntity.BUCKET_VARIANT_TAG, CompoundTag.TAG_INT)) {
                return;
            }
            
            int packedVariant = compoundTag.getInt(PrimordialFishEntity.BUCKET_VARIANT_TAG);
            PrimordialFishEntity.Variant variant = new PrimordialFishEntity.Variant(packedVariant);
            
            ChatFormatting[] formatting = new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY};
            int variantIndex = PrimordialFishEntity.COMMON_VARIANTS.indexOf(variant);
            if (variantIndex != -1) {
                tooltipComponents.add(Component.translatable(PrimordialFishEntity.getPredefinedName(variantIndex))
                        .withStyle(formatting));
                return;
            }
            
            String baseColor = "color.minecraft." + variant.baseColor();
            String patternColor = "color.minecraft." + variant.patternColor();
            
            tooltipComponents.add(variant.pattern()
                    .displayName()
                    .plainCopy()
                    .withStyle(formatting));
            MutableComponent baseColorComponent = Component.translatable(baseColor);
            if (!baseColor.equals(patternColor)) {
                baseColorComponent.append(", ").append(Component.translatable(patternColor));
            }
            
            baseColorComponent.withStyle(formatting);
            tooltipComponents.add(baseColorComponent);
        }
    }
}