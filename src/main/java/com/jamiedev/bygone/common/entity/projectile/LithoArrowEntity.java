package com.jamiedev.bygone.common.entity.projectile;

import com.jamiedev.bygone.core.registry.BGDamageTypes;
import com.jamiedev.bygone.core.registry.BGEntityTypes;
import com.jamiedev.bygone.core.registry.BGItems;
import com.jamiedev.bygone.core.registry.BGMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class LithoArrowEntity extends AbstractArrow
{

    private int duration = 200;

    public LithoArrowEntity(EntityType<? extends LithoArrowEntity> entityType, Level world) {
        super(entityType, world);
    }

    public LithoArrowEntity(Level world, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super((EntityType<? extends AbstractArrow>) BGEntityTypes.LITHOPLASM_ARROW.get(), x, y, z, world, stack, shotFrom);
    }

    public LithoArrowEntity(Level world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(BGEntityTypes.LITHOPLASM_ARROW.get(), owner, world, stack, shotFrom);
    }

    public static void dropArrow(Level world, BlockPos pos) {
        dropStack(world, pos, new ItemStack(BGItems.LITHOPLASM_ARROW.get(), 1));
    }

    private static void dropStack(Level world, Supplier<ItemEntity> itemEntitySupplier, ItemStack stack) {
        if (!world.isClientSide && !stack.isEmpty() && world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            ItemEntity itemEntity = itemEntitySupplier.get();
            itemEntity.setDefaultPickUpDelay();
            world.addFreshEntity(itemEntity);
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Duration")) {
            this.duration = compound.getInt("Duration");
        }

    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Duration", this.duration);
    }

    protected void doPostHurtEffects(LivingEntity living) {
        super.doPostHurtEffects(living);
        MobEffectInstance mobeffectinstance = new MobEffectInstance(BGMobEffects.HAUNTED.get(), this.duration, 0);
        living.hurt(BGDamageTypes.source(living.level(), BGDamageTypes.HAUNTED, living, living.getLastAttacker()), 1);
        living.addEffect(mobeffectinstance, this.getEffectSource());
    }

    public static void dropStack(Level world, BlockPos pos, ItemStack stack) {
        double d = (double) EntityType.ITEM.getHeight() / 2.0;
        double e = (double) pos.getX() + 0.5 + Mth.nextDouble(world.random, -0.25, 0.25);
        double f = (double) pos.getY() + 0.5 + Mth.nextDouble(world.random, -0.25, 0.25) - d;
        double g = (double) pos.getZ() + 0.5 + Mth.nextDouble(world.random, -0.25, 0.25);
        dropStack(world, () -> {
            return new ItemEntity(world, e, f, g, stack);
        }, stack);
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return new ItemStack(BGItems.LITHOPLASM_ARROW.get());
    }
}
