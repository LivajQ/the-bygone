package com.jamiedev.bygone.core.mixin;

import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CollisionContext.class)
public interface PhasingCollisionContextMixin {
    
    /* TODO do this properly
	@WrapMethod(method = "of")
	private static CollisionContext ofPhasingEntity(Entity entity, Operation<CollisionContext> original) {
		if (entity instanceof BlockPhasingEntity phasing && phasing.isPhasing()) return new PhasingEntityCollisionContext(entity);
		return original.call(entity);
	}
     */

}
