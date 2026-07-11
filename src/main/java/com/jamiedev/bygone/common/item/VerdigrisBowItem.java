package com.jamiedev.bygone.common.item;

import com.jamiedev.bygone.core.registry.BGItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class VerdigrisBowItem extends BowItem {
    public VerdigrisBowItem(Properties settings) {
        super(settings);
    }
    
    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }
    
    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        // no-op: firing happens continuously in onUseTick instead
    }
    
    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.onUseTick(world, user, stack, remainingUseTicks);
        arrowShootLogic(user, stack, world);
    }
    
    public void arrowShootLogic(LivingEntity user, ItemStack stack, Level world) {
        if (user instanceof Player player) {
            ItemStack projectileStack = player.getProjectile(stack);
            boolean infinite = player.getAbilities().instabuild
                    || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
            
            if (projectileStack.isEmpty() && !infinite) {
                return;
            }
            
            if (projectileStack.isEmpty()) {
                projectileStack = new ItemStack(Items.ARROW);
            }
            
            if (!(world instanceof ServerLevel serverLevel)) {
                return;
            }
            
            ArrowItem arrowItem = (ArrowItem) (projectileStack.getItem() instanceof ArrowItem
                    ? projectileStack.getItem() : Items.ARROW);
            AbstractArrow arrow = arrowItem.createArrow(serverLevel, projectileStack, player);
            arrow = this.customArrow(arrow);
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, 1.0F);
            
            boolean creativeAmmo = player.getAbilities().instabuild
                    || (arrowItem.isInfinite(projectileStack, stack, player));
            if (creativeAmmo) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            
            serverLevel.addFreshEntity(arrow);
            
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT,
                    SoundSource.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            
            if (!creativeAmmo && !player.getAbilities().instabuild) {
                projectileStack.shrink(1);
                if (projectileStack.isEmpty()) {
                    player.getInventory().removeItem(projectileStack);
                }
            }
            
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }
    
    @Override
    public AbstractArrow customArrow(AbstractArrow arrow) {
        arrow.setBaseDamage(arrow.getBaseDamage() * 0.1);
        return arrow;
    }
    
    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(BGItems.VERDIGRIS_INGOT.get());
    }
}