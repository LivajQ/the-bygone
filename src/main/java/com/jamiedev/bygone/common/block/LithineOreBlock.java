package com.jamiedev.bygone.common.block;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class LithineOreBlock extends Block {
    public static final BooleanProperty LIT;

    static {
        LIT = RedstoneTorchBlock.LIT;
    }

    public LithineOreBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false));
    }

    private static void interact(BlockState state, Level level, BlockPos pos) {
        spawnParticles(level, pos);
        if (!(Boolean) state.getValue(LIT)) {
            level.setBlock(pos, state.setValue(LIT, true), 3);
        }

    }

    private static void spawnParticles(Level level, BlockPos pos) {
        double d0 = 0.5625F;
        RandomSource randomsource = level.random;

        for (Direction direction : Direction.values()) {
            BlockPos blockpos = pos.relative(direction);
            if (!level.getBlockState(blockpos).isSolidRender(level, blockpos)) {
                Direction.Axis direction$axis = direction.getAxis();
                double d1 = direction$axis == Direction.Axis.X ? (double) 0.5F + (double) 0.5625F * (double) direction.getStepX() : (double) randomsource.nextFloat();
                double d2 = direction$axis == Direction.Axis.Y ? (double) 0.5F + (double) 0.5625F * (double) direction.getStepY() : (double) randomsource.nextFloat();
                double d3 = direction$axis == Direction.Axis.Z ? (double) 0.5F + (double) 0.5625F * (double) direction.getStepZ() : (double) randomsource.nextFloat();
                level.addParticle(DustParticleOptions1.REDSTONE, (double) pos.getX() + d1, (double) pos.getY() + d2, (double) pos.getZ() + d3, 0.0F, 0.0F, 0.0F);
            }
        }

    }
    
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        interact(state, level, pos);
        super.attack(state, level, pos, player);
    }

    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.isSteppingCarefully()) {
            interact(state, level, pos);
        }

        super.stepOn(level, pos, state, entity);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide) {
            spawnParticles(level, pos);
            return InteractionResult.SUCCESS;
        }
        
        interact(state, level, pos);
        
        BlockPlaceContext ctx = new BlockPlaceContext(player, hand, stack, hitResult);
        
        if (stack.getItem() instanceof BlockItem && ctx.canPlace()) {
            return InteractionResult.CONSUME;
        }
        
        return InteractionResult.SUCCESS;
    }
    
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(LIT);
    }
    
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            level.setBlock(pos, state.setValue(LIT, false), 3);
        }

    }
    
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropExperience) {
        super.spawnAfterBreak(state, level, pos, stack, dropExperience);
        if (dropExperience) {
            this.tryDropExperience(level, pos, stack, UniformInt.of(1, 5));
        }

    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            spawnParticles(level, pos);
        }

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
}

class DustParticleOptions1 implements ParticleOptions {
    
    public static final Vector3f PLASM_PARTICLE_COLOR = Vec3.fromRGB24(14151396).toVector3f();
    public static final net.minecraft.core.particles.DustParticleOptions REDSTONE = new net.minecraft.core.particles.DustParticleOptions(PLASM_PARTICLE_COLOR, 1.0F);
    public static final Codec<DustParticleOptions1> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(o -> o.color),
                    Codec.FLOAT.fieldOf("scale").forGetter(o -> o.scale)
            ).apply(instance, DustParticleOptions1::new)
    );
    
    public static final ParticleOptions.Deserializer<DustParticleOptions1> DESERIALIZER =
            new ParticleOptions.Deserializer<>() {
                @Override
                public DustParticleOptions1 fromCommand(ParticleType<DustParticleOptions1> type, StringReader reader) {
                    Vector3f color = new Vector3f(1, 1, 1);
                    float scale = 1.0f;
                    return new DustParticleOptions1(color, scale);
                }
                
                @Override
                public DustParticleOptions1 fromNetwork(ParticleType<DustParticleOptions1> type, FriendlyByteBuf buf) {
                    Vector3f color = buf.readVector3f();
                    float scale = buf.readFloat();
                    return new DustParticleOptions1(color, scale);
                }
            };
    
    private final Vector3f color;
    private final float scale;
    
    public DustParticleOptions1(Vector3f color, float scale) {
        this.color = color;
        this.scale = scale;
    }
    
    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeVector3f(color);
        buf.writeFloat(scale);
    }
    
    @Override
    public String writeToString() {
        return "dust_particle_options1";
    }
    
    @Override
    public ParticleType<DustParticleOptions> getType() {
        return ParticleTypes.DUST;
    }
    
    public Vector3f getColor() {
        return color;
    }
    
    public float getScale() {
        return scale;
    }
}
