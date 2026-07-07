package com.jamiedev.bygone.core.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public final class MapHeightHelper {
    private static final String KEY = "map_height";
    
    private MapHeightHelper() {}
    
    public static void set(ItemStack stack, int height) {
        stack.getOrCreateTag().putInt(KEY, height);
    }
    
    public static boolean isSet(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(KEY, Tag.TAG_INT);
    }
    
    public static int get(ItemStack stack, int fallback) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(KEY, Tag.TAG_INT) ? tag.getInt(KEY) : fallback;
    }
}