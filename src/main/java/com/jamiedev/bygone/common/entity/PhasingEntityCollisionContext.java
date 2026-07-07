package com.jamiedev.bygone.common.entity;

import com.jamiedev.bygone.core.init.JamiesModTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PhasingEntityCollisionContext extends EntityCollisionContext {

	public PhasingEntityCollisionContext(Entity entity) {
		super(entity);
	}

	public VoxelShape getShape(BlockGetter level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.is(JamiesModTag.SPECTRAL_BLOCKS)) return Block.box(0, 0, 0, 0, 0, 0);
		return Block.box(0, 0, 0, 0, 0, 0);
	}
}
