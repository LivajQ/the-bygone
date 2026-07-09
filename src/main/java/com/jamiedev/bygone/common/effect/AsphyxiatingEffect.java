package com.jamiedev.bygone.common.effect;

import com.jamiedev.bygone.core.init.JamiesModTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class AsphyxiatingEffect extends MobEffect {

    protected static final int ROUNDING_TO = 20;

    public AsphyxiatingEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        super.applyEffectTick(entity, amplifier);

        if (!entity.level().isClientSide() && !entity.canBreatheUnderwater()) {
            float amountOfAirToTake = 2 * amplifier + 5.5f;

            for (MobEffectInstance effectInstance : entity.getActiveEffects()) {
                
                boolean hasAirlessBreathing = BuiltInRegistries.MOB_EFFECT.getResourceKey(effectInstance.getEffect())
                        .flatMap(BuiltInRegistries.MOB_EFFECT::getHolder)
                        .map(holder -> holder.is(JamiesModTag.AIRLESS_BREATHING))
                        .orElse(false);
                
                if (hasAirlessBreathing) {
                    amountOfAirToTake -= 2 * (1 + effectInstance.getAmplifier());
                }
            }

            //amountOfAirToTake -= 0.5f * (float) entity.getAttributeValue(Attributes.OXYGEN_BONUS);

            int finalAmountOfAirToTake = (int) amountOfAirToTake;
            amountOfAirToTake -= finalAmountOfAirToTake;
            if (amountOfAirToTake * ROUNDING_TO < entity.level().getGameTime() % ROUNDING_TO) {
                finalAmountOfAirToTake++;
            }

            if (finalAmountOfAirToTake > 0) {
                int newAirSupply = entity.getAirSupply() - finalAmountOfAirToTake;
                if (newAirSupply <= 0) {
                    entity.hurt(entity.damageSources().drown(), 2.0f);
                } else {
                    entity.setAirSupply(entity.getAirSupply() - finalAmountOfAirToTake);

                }
            }

        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}

