package com.jamiedev.bygone.core.registry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class BGDataComponentTypes {

    public record EchoGongData(int charge) {
        public static final EchoGongData EMPTY = new EchoGongData(0);
        
        public static EchoGongData read(ItemStack stack) {
            CompoundTag tag = stack.getTag();
            return tag != null && tag.contains("echo_gong_charge")
                    ? new EchoGongData(tag.getInt("echo_gong_charge"))
                    : EMPTY;
        }
        
        public static void write(ItemStack stack, EchoGongData value) {
            stack.getOrCreateTag().putInt("echo_gong_charge", value.charge());
        }
    }
}
