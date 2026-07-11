package com.jamiedev.bygone.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class LeechingEnchantment extends Enchantment {
 
    public LeechingEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] applicableSlots) {
        super(rarity, category, applicableSlots);
    }
    
    @Override
    public int getMinCost(int level) {
        return 1 + (level - 1) * 11;
    }
    
    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 20;
    }
    
    @Override
    public int getMaxLevel() {
        return 5;
    }
    
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem || super.canApplyAtEnchantingTable(stack);
    }
}