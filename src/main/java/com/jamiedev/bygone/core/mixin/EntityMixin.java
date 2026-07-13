package com.jamiedev.bygone.core.mixin;

import com.jamiedev.bygone.core.init.JamiesModTag;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixin {
    
    @WrapMethod(method = "isInvulnerableTo")
    private boolean wrapIsInvulnerableTo(DamageSource source, Operation<Boolean> original) {
        Entity self = (Entity) (Object) this;
        if (self.getType().is(JamiesModTag.SPECTRAL)) {
            if (source.getDirectEntity() != null && source.getDirectEntity().getType().is(JamiesModTag.SPECTRAL_VULNERABLE_TO_ENTITY)) {
                return false;
            } else if (source.is(JamiesModTag.SPECTRAL_VULNERABLE_TO_DAMAGE)) {
                return false;
            } else if (source.getDirectEntity() instanceof LivingEntity livingAttacker
                    && !livingAttacker.getMainHandItem().isEmpty()
                    && livingAttacker.getMainHandItem().is(JamiesModTag.SPECTRAL_VULNERABLE_TO_ITEM)) {
                return false;
            }
            return true;
        }
        return original.call(source);
    }
}
