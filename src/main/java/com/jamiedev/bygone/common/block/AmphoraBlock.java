package com.jamiedev.bygone.common.block;

import com.jamiedev.bygone.common.block.entity.AmphoraBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;


public class AmphoraBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final ResourceLocation SHERDS_DYNAMIC_DROP_ID = ResourceLocation.withDefaultNamespace("sherds");
    public static final BooleanProperty CRACKED;
    public static final IntegerProperty WATER_LEVEL = IntegerProperty.create("water_level", 0, 8);
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

    private static final VoxelShape BOUNDING_BOX = Shapes.or(
        Block.box(5.0, 0.0, 5.0, 11.0, 27.0, 11.0),
        Block.box(3.0, 2.0, 3.0, 13.0, 12.0, 13.0)
    );
    private static final BooleanProperty WATERLOGGED;

    static {
        CRACKED = BlockStateProperties.CRACKED;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }

    public AmphoraBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, false)
                .setValue(CRACKED, false)
                .setValue(WATER_LEVEL, 0)
        );
    }

    public static void updateWaterLevel(Level level, int waterLevel, BlockState state, BlockPos pos) {
        BlockState newState = state.setValue(WATER_LEVEL, Integer.valueOf(waterLevel));
        level.setBlockAndUpdate(pos, newState);
    }

    public static void doWaterParticles(Level level, BlockPos pos) {
        if (level.isClientSide) {
            level.addParticle(ParticleTypes.SPLASH,
                    pos.getX() + (double) level.random.nextFloat(),
                    pos.getY() + 1,
                    pos.getZ() + (double) level.random.nextFloat(),
                    0.0,
                    0.0,
                    0.0);
        }
    }

    @Deprecated
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
    
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER).setValue(CRACKED, false);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        
        if (blockEntity instanceof AmphoraBlockEntity amphoraBlockEntity) {
            if (stack.is(Items.WATER_BUCKET) && state.getValue(WATER_LEVEL) < 8) {
                amphoraBlockEntity.wobble(AmphoraBlockEntity.WobbleStyle.POSITIVE);
                doWaterParticles(level, pos);
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                updateWaterLevel(level, state.getValue(WATER_LEVEL) + 1, state, pos);
                level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
                if (!player.getAbilities().instabuild)
                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else if (stack.is(Items.BUCKET) && state.getValue(WATER_LEVEL) > 0) {
                amphoraBlockEntity.wobble(AmphoraBlockEntity.WobbleStyle.POSITIVE);
                doWaterParticles(level, pos);
                level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                updateWaterLevel(level, state.getValue(WATER_LEVEL) - 1, state, pos);
                level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
                if (!player.getAbilities().instabuild)
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.WATER_BUCKET)));
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            
            level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            amphoraBlockEntity.wobble(AmphoraBlockEntity.WobbleStyle.NEGATIVE);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        
        return InteractionResult.PASS;
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
    
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BOUNDING_BOX;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, CRACKED, WATER_LEVEL);
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AmphoraBlockEntity(pos, state);
    }
    
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof Container container) {
                Containers.dropContents(level, pos, container);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
    
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof AmphoraBlockEntity amphora) {
            params.withDynamicDrop(SHERDS_DYNAMIC_DROP_ID, consumer -> {
                amphora.getDecorations().sorted()
                        .forEach(item -> consumer.accept(item.getDefaultInstance()));
            });
        }
        
        return super.getDrops(state, params);
    }
    
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.is(ItemTags.BREAKS_DECORATED_POTS) && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
            level.setBlock(pos, state.setValue(CRACKED, true), 4);
        }
        
        super.playerWillDestroy(level, pos, state, player);
    }
    
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
    
    public SoundType getSoundType(BlockState state) {
        return state.getValue(CRACKED) ? SoundType.DECORATED_POT_CRACKED : SoundType.DECORATED_POT;
    }
    
    @Override
    public void appendHoverText(ItemStack pStack, @javax.annotation.Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        DecoratedPotBlockEntity.Decorations decorations = DecoratedPotBlockEntity.Decorations.load(BlockItem.getBlockEntityData(pStack));
        if (!decorations.equals(DecoratedPotBlockEntity.Decorations.EMPTY)) {
            pTooltip.add(CommonComponents.EMPTY);
            decorations.sorted().forEach((item) -> pTooltip.add(
                    new ItemStack(item, 1).getHoverName().plainCopy().withStyle(ChatFormatting.GRAY)
            ));
        }
    }
    
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        BlockPos blockPos = hit.getBlockPos();
        if (!level.isClientSide && projectile.mayInteract(level, blockPos)) {
            level.setBlock(blockPos, state.setValue(CRACKED, true), 4);
            level.destroyBlock(blockPos, true, projectile);
        }
    }
    
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }
    
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        //return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
        return state.getValue(WATER_LEVEL);
    }
    
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));

    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }
}