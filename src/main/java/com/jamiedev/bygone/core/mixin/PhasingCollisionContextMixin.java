package com.jamiedev.bygone.core.mixin;

import com.jamiedev.bygone.common.entity.BlockPhasingEntity;
import com.jamiedev.bygone.common.entity.PhasingEntityCollisionContext;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CollisionContext.class)
public interface PhasingCollisionContextMixin {

	@WrapMethod(method = "of")
	private static CollisionContext ofPhasingEntity(Entity entity, Operation<CollisionContext> original) {
		if (entity instanceof BlockPhasingEntity phasing && phasing.isPhasing()) return new PhasingEntityCollisionContext(entity);
		return original.call(entity);
	}

}
