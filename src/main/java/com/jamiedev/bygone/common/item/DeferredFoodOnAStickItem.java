package com.jamiedev.bygone.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.stats.Stats;

import java.util.function.Supplier;

public class DeferredFoodOnAStickItem<T extends Entity & ItemSteerable> extends Item {
    private final Supplier<EntityType<T>> canInteractWithSupplier;
    private final int consumeItemDamage;
    
    public DeferredFoodOnAStickItem(Item.Properties properties, Supplier<EntityType<T>> canInteractWithSupplier, int consumeItemDamage) {
        super(properties);
        this.canInteractWithSupplier = canInteractWithSupplier;
        this.consumeItemDamage = consumeItemDamage;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.pass(itemstack);
        } else {
            Entity entity = player.getControlledVehicle();
            if (player.isPassenger() && entity instanceof ItemSteerable itemsteerable) {
                if (entity.getType() == this.canInteractWithSupplier.get() && itemsteerable.boost()) {
                    itemstack.hurtAndBreak(this.consumeItemDamage, player, (p) -> p.broadcastBreakEvent(hand));
                    if (itemstack.isEmpty()) {
                        ItemStack itemstack1 = new ItemStack(Items.FISHING_ROD);
                        itemstack1.setTag(itemstack.getTag());
                        return InteractionResultHolder.success(itemstack1);
                    }
                    
                    return InteractionResultHolder.success(itemstack);
                }
            }
            
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.pass(itemstack);
        }
    }
}