package com.jamiedev.bygone.common.entity;

import com.jamiedev.bygone.core.init.JamiesModTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class WispEntity extends Allay {
    public WispEntity(EntityType<? extends WispEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    public static boolean canSpawn(EntityType<? extends Mob> type, LevelAccessor level, MobSpawnType reason, BlockPos blockPos, RandomSource random) {
        return level.getBlockState(blockPos.below()).is(JamiesModTag.WISP_SPAWNABLE_ON);
    }

    public void aiStep() {
        if (this.level().isClientSide) {
            for (int i = 0; i < 0.1; ++i) {
                this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        this.getRandomX(0.5D),
                        this.getRandomY(),
                        this.getRandomZ(0.5D),
                        0.0D, 0.0D, 0.0D);
            }
        }

        super.aiStep();
    }
    
    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.36F;
    }
    
    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction callback) {
        super.positionRider(passenger, callback);
        callback.accept(passenger, this.getX(), this.getY() + 0.04, this.getZ());
    }
}
