package com.jamiedev.bygone.common.block.shelf;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShelfFungiFanBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

    public ShelfFungiFanBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    public static boolean shouldConnectTo(BlockGetter world, BlockPos pos, Direction direction) {
        return MultifaceBlock.canAttachTo(world, direction, pos, world.getBlockState(pos));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

}
