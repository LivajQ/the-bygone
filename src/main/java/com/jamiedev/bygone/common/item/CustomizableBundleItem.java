package com.jamiedev.bygone.common.item;

import com.jamiedev.bygone.core.mixin.BundleItemAccessor;
import net.minecraft.world.item.ItemStack;

public interface CustomizableBundleItem {
    
    default int getMaxWeight() {
        return 64;
    }
    
    default boolean acceptsStack(ItemStack stack) {
        return !stack.isEmpty();
    }
    
    default int getStackWeight(ItemStack stack) {
        int result = BundleItemAccessor.invokeGetWeight(stack);
        int defaultResult = 64 / stack.getMaxStackSize();
        if (result != defaultResult) return result;
        
        return (int) Math.ceil((double) stack.getMaxStackSize() / this.getMaxWeight());
    }
}