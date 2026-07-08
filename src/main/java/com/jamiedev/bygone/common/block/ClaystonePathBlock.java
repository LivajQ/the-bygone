package com.jamiedev.bygone.common.block;

import com.jamiedev.bygone.core.registry.BGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClaystonePathBlock extends Block {
    protected static final VoxelShape SHAPE = ClaystoneFarmlandBlock.SHAPE;

    public ClaystonePathBlock(BlockBehaviour.Properties p_153129_) {
        super(p_153129_);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState p_153159_) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_153131_) {
        return !this.defaultBlockState().canSurvive(p_153131_.getLevel(), p_153131_.getClickedPos())
                ? Block.pushEntitiesUp(this.defaultBlockState(), BGBlocks.CLAYSTONE.get().defaultBlockState(), p_153131_.getLevel(), p_153131_.getClickedPos())
                : super.getStateForPlacement(p_153131_);
    }

    @Override
    public BlockState updateShape(
            BlockState p_153152_, Direction p_153153_, BlockState p_153154_, LevelAccessor p_153155_, BlockPos p_153156_, BlockPos p_153157_
    ) {
        if (p_153153_ == Direction.UP && !p_153152_.canSurvive(p_153155_, p_153156_)) {
            p_153155_.scheduleTick(p_153156_, this, 1);
        }

        return super.updateShape(p_153152_, p_153153_, p_153154_, p_153155_, p_153156_, p_153157_);
    }

    @Override
    public void tick(BlockState p_221070_, ServerLevel p_221071_, BlockPos p_221072_, RandomSource p_221073_) {
        ClaystoneFarmlandBlock.setToDirt(null, p_221070_, p_221071_, p_221072_);
    }

    @Override
    public boolean canSurvive(BlockState p_153148_, LevelReader p_153149_, BlockPos p_153150_) {
        BlockState blockstate = p_153149_.getBlockState(p_153150_.above());
        return !blockstate.isSolid() || blockstate.getBlock() instanceof FenceGateBlock;
    }

    @Override
    public VoxelShape getShape(BlockState p_153143_, BlockGetter p_153144_, BlockPos p_153145_, CollisionContext p_153146_) {
        return SHAPE;
    }
    
    @Override
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }
}
