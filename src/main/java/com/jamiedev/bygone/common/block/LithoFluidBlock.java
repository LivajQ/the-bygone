package com.jamiedev.bygone.common.block;

import com.google.common.collect.Lists;
import com.jamiedev.bygone.core.init.JamiesModTag;
import com.jamiedev.bygone.core.registry.BGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;

import java.util.List;
import java.util.function.ToIntFunction;

public class LithoFluidBlock extends LiquidBlock
{

    protected FlowingFluid fluid;
    private List<FluidState> stateCache;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = p_153701_ -> p_153701_.getValue(LEVEL);

    public LithoFluidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
        this.fluid = fluid;
        this.stateCache = Lists.newArrayList();
        this.stateCache.add(fluid.getSource(false));

        for(int i = 1; i < 8; ++i) {
            this.stateCache.add(fluid.getFlowing(8 - i, false));
        }

        this.stateCache.add(fluid.getFlowing(8, true));
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, 0));
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles).
     */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(2) == 1)
        {
            double d0 = (double)pos.getX() + 0.5 + (0.5 - random.nextDouble());
            double d1 = (double)pos.getY() + 1.0;
            double d2 = (double)pos.getZ() + 0.5 + (0.5 - random.nextDouble());
            double d3 = (double)random.nextFloat() * 0.04;
            level.addParticle(ParticleTypes.END_ROD, d0, d1, d2, 0.0, d3, 0.0);
        }
    }
    
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (this.shouldSpreadLiquid(level, pos, state)) {
            level.scheduleTick(pos, state.getFluidState().getType(), this.fluid.getTickDelay(level));
        }

    }
    
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getFluidState().isSource() || facingState.getFluidState().isSource()) {
            level.scheduleTick(currentPos, state.getFluidState().getType(), this.fluid.getTickDelay(level));
        }

        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }
    
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (this.shouldSpreadLiquid(level, pos, state)) {
            level.scheduleTick(pos, state.getFluidState().getType(), this.fluid.getTickDelay(level));
        }

    }

    private void fizz(LevelAccessor level, BlockPos pos) {
        level.levelEvent(1501, pos, 0);
    }

    private boolean shouldSpreadLiquid(Level level, BlockPos pos, BlockState state) {
        if (this.fluid.is(JamiesModTag.LITHO))
        {
            boolean flag = level.getBlockState(pos.below()).is(Blocks.SOUL_SOIL);

            for (Direction direction : POSSIBLE_FLOW_DIRECTIONS) {
                BlockPos blockpos = pos.relative(direction.getOpposite());
                if (level.getFluidState(blockpos).is(FluidTags.LAVA)) {
                    Block block = level.getFluidState(pos).isSource() ? BGBlocks.PLASMIC_BYSLATE.get() : BGBlocks.BYSLATE.get();
                    level.setBlockAndUpdate(pos, block.defaultBlockState());
                    this.fizz(level, pos);
                    return false;
                }
                if (level.getFluidState(blockpos).is(FluidTags.WATER)) {
                    Block block = BGBlocks.LITHOPLASMIC_POWDER_BLOCK.get();
                    level.setBlockAndUpdate(pos, block.defaultBlockState());
                    this.fizz(level, pos);
                    return false;
                }

                if (flag && level.getBlockState(blockpos).is(Blocks.BLUE_ICE)) {
                    level.setBlockAndUpdate(pos, Blocks.BASALT.defaultBlockState());
                    this.fizz(level, pos);
                    return false;
                }
            }
        }

        return true;
    }
}
