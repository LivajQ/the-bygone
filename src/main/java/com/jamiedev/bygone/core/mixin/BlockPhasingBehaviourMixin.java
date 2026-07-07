package com.jamiedev.bygone.core.mixin;

import com.jamiedev.bygone.common.entity.PhasingEntityCollisionContext;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockPhasingBehaviourMixin {

	@WrapMethod(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;")
	private VoxelShape getPhasingCollisionShape(BlockGetter level, BlockPos pos, CollisionContext context, Operation<VoxelShape> original) {
		if (context instanceof PhasingEntityCollisionContext phasing) return phasing.getShape(level, pos);
		return original.call(level, pos, context);
	}

}
