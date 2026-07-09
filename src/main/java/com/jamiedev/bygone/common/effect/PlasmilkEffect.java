package com.jamiedev.bygone.common.effect;

import com.jamiedev.bygone.core.init.JamiesModTag;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class PlasmilkEffect extends MobEffect {

	protected static int TICK_DURATION = 20;

	public PlasmilkEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return duration % TICK_DURATION == 0;
	}
    
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        
        List<MobEffect> toRemove = new ArrayList<>();
        
        for (MobEffectInstance instance : entity.getActiveEffects()) {
            Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(instance.getEffect());
            if (!holder.is(JamiesModTag.IGNORES_PLASMILK)) {
                toRemove.add(instance.getEffect());
            }
        }
        
        for (MobEffect effect : toRemove) {
            entity.removeEffect(effect);
        }
    }

}
