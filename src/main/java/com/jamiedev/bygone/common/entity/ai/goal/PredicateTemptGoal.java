package com.jamiedev.bygone.common.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class PredicateTemptGoal extends Goal {
    
    private final Mob mob;
    private final double speed;
    private final Predicate<ItemStack> predicate;
    private final boolean canScare;
    private LivingEntity target;
    
    public PredicateTemptGoal(Mob mob, double speed, Predicate<ItemStack> predicate, boolean canScare) {
        this.mob = mob;
        this.speed = speed;
        this.predicate = predicate;
        this.canScare = canScare;
    }
    
    @Override
    public boolean canUse() {
        LivingEntity nearest = this.mob.level().getNearestPlayer(this.mob, 10.0);
        if (nearest == null) return false;
        
        ItemStack held = nearest.getMainHandItem();
        if (!predicate.test(held)) return false;
        
        this.target = nearest;
        return true;
    }
    
    @Override
    public void start() {
    
    }
    
    @Override
    public void stop() {
        this.target = null;
    }
    
    @Override
    public void tick() {
        if (this.target != null) {
            this.mob.getNavigation().moveTo(this.target, this.speed);
        }
    }
}
