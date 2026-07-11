package com.jamiedev.bygone.core.registry;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.common.enchantment.LeechingEnchantment;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class BGEnchantments {
    
    public static final Enchantment LEECHING = register("leeching",
            new LeechingEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
    
    private static Enchantment register(String id, Enchantment enchantment) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT, Bygone.id(id), enchantment);
    }
    
    public static void init() {
    
    }
}