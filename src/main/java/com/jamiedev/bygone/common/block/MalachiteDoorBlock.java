package com.jamiedev.bygone.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class MalachiteDoorBlock extends DoorBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public MalachiteDoorBlock(BlockSetType type, BlockBehaviour.Properties properties) {
        super(properties, type);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(WATERLOGGED, false));
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }
    
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && (player.isCreative() || !player.hasCorrectToolForDrops(state))) {
            if (state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
                BlockPos below = pos.below();
                BlockState belowState = level.getBlockState(below);
                
                if (belowState.getBlock() == state.getBlock()) {
                    level.setBlock(below, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, below, Block.getId(belowState));
                }
            }
        }
        
        super.playerWillDestroy(level, pos, state, player);
    }
    
    // This needs an override, since we need to set the state to waterlogged or not.
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @NotNull LivingEntity placer, @NotNull ItemStack stack) {
        BlockPos abovePos = pos.above();
        level.setBlock(abovePos, state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(WATERLOGGED, level.getFluidState(abovePos).is(Fluids.WATER)), 3);
    }
    
    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return type == PathComputationType.WATER
                && state.getValue(OPEN)
                && state.getValue(WATERLOGGED);
    }
    
    
    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        boolean isWaterlogged = fluidState.getType() == Fluids.WATER;

        BlockState stateForPlacement = super.getStateForPlacement(context);
        if (stateForPlacement != null) {
            stateForPlacement = stateForPlacement.setValue(WATERLOGGED, isWaterlogged);
        }
        return stateForPlacement;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
