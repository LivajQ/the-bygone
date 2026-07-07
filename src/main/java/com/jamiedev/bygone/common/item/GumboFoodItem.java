package com.jamiedev.bygone.common.item;

import com.jamiedev.bygone.common.block.entity.GumboPotBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class GumboFoodItem extends Item {
    
    public GumboFoodItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("gumbo_food_data")) {
            return GumboPotBlockEntity.GumboIngredientComponent.readFoodProperties(tag.getCompound("gumbo_food_data"));
        }
        return super.getFoodProperties(stack, entity);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        Item emptied = GumboPotBlockEntity.GumboScooping.getEmptied(this);
        return emptied != null ? new ItemStack(emptied) : result;
    }
}