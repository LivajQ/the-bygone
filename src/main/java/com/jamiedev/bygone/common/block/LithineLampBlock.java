package com.jamiedev.bygone.common.block;

import com.jamiedev.bygone.Bygone;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

public class LithineLampBlock extends Block {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final MapCodec<LithineLampBlock> CODEC = simpleCodec(LithineLampBlock::new);

    LithineLampBlock ref;

    public static ToIntFunction<BlockState> lithineLampValue() {
        return (state) -> state.getValue(BlockStateProperties.POWER).intValue();
    }

    public LithineLampBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(POWER, 0));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.scheduleTick(pos, this, 1, TickPriority.NORMAL);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(POWER, 0);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        AABB boundingBox = new AABB(pos).inflate(15);
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, boundingBox);
        if (entities.size() > 1) entities.sort(
            Comparator.comparingDouble(e ->
            e.distanceToSqr(pos.getX(), pos.getY(), pos.getZ())
        ));
        if (!entities.isEmpty()) {
            int lastDistance = (int) Math.floor(pos.getCenter()
                .distanceTo(entities.getFirst().position()));
            lastDistance = Math.clamp(lastDistance - 1, 0, 15);
            level.setBlock(pos, state.setValue(POWER, 15 - lastDistance), 10);
        } else level.setBlock(pos, state.setValue(POWER, 0), 10);
        level.scheduleTick(pos, this, 1, TickPriority.NORMAL);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }
}
