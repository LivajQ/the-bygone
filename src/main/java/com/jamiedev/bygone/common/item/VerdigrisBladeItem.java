package com.jamiedev.bygone.common.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class VerdigrisBladeItem extends SwordItem {
    public VerdigrisBladeItem(Tier toolMaterial, int attackDamageModifier, float attackSpeedModifier, Item.Properties settings) {
        super(toolMaterial, attackDamageModifier, attackSpeedModifier, settings);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player user, @NotNull InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        user.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }
    
    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(Blocks.COBWEB)) {
            return 15.0F;
        }
        if (state.is(BlockTags.SWORD_EFFICIENT)) {
            return 1.5F;
        }
        return super.getDestroySpeed(stack, state);
    }
    
    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.is(Blocks.COBWEB) || super.isCorrectToolForDrops(stack, state);
    }
    
    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 10000;
    }
}