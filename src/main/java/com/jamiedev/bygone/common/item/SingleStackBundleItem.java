package com.jamiedev.bygone.common.item;

import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;

public class SingleStackBundleItem extends BundleItem implements CustomizableBundleItem {
    
    public SingleStackBundleItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public int getMaxWeight() {
        return 9;
    }
    
    @Override
    public boolean acceptsStack(ItemStack stack) {
        return stack.getMaxStackSize() == 1;
    }
    
    @Override
    public int getStackWeight(ItemStack stack) {
        return stack.getCount();
    }
}