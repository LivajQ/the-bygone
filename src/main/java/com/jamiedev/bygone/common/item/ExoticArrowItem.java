package com.jamiedev.bygone.common.item;

import com.jamiedev.bygone.common.entity.projectile.ExoticArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;

public class ExoticArrowItem extends ArrowItem {
    TridentItem ref;

    public ExoticArrowItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public AbstractArrow createArrow(Level world, ItemStack stack, LivingEntity shooter) {
        return new ExoticArrowEntity(world, shooter, stack.copyWithCount(1), null);
    }

    /*
    @Override
    public Projectile asProjectile(Level world, Position pos, ItemStack stack, Direction direction) {
        ExoticArrowEntity arrowEntity = new ExoticArrowEntity(world, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1), null);
        arrowEntity.pickup = AbstractArrow.Pickup.DISALLOWED;
        return arrowEntity;
    }
     */
}