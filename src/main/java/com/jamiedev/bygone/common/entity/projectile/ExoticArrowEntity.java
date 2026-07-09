package com.jamiedev.bygone.common.entity.projectile;

import com.google.common.collect.Sets;
import com.jamiedev.bygone.common.particle.BygoneColorParticleOption;
import com.jamiedev.bygone.core.registry.BGEntityTypes;
import com.jamiedev.bygone.core.registry.BGItems;
import com.jamiedev.bygone.core.registry.BGParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

public class ExoticArrowEntity extends AbstractArrow {
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(ExoticArrowEntity.class, EntityDataSerializers.INT);
    
    Arrow ref;
    boolean returns = false;
    private ItemStack pickupItem = this.getDefaultPickupItem();
    
    private Potion potion = Potions.EMPTY;
    private final Set<MobEffectInstance> effects = Sets.newHashSet();

    public ExoticArrowEntity(EntityType<? extends ExoticArrowEntity> entityType, Level world) {
        super(entityType, world);
    }

    public ExoticArrowEntity(Level world, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(BGEntityTypes.EXOTIC_ARROW.get(), x, y, z, world);
        this.initColor();
    }

    public ExoticArrowEntity(Level world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(BGEntityTypes.EXOTIC_ARROW.get(), owner, world);
        this.initColor();
    }

    public static void dropArrow(Level world, BlockPos pos) {
        dropStack(world, pos, new ItemStack(BGItems.EXOTIC_ARROW.get(), 1));
    }

    private static void dropStack(Level world, Supplier<ItemEntity> itemEntitySupplier, ItemStack stack) {
        if (!world.isClientSide && !stack.isEmpty() && world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            ItemEntity itemEntity = itemEntitySupplier.get();
            itemEntity.setDefaultPickUpDelay();
            world.addFreshEntity(itemEntity);
        }
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
    
    public void setEffectsFromItem(ItemStack stack) {
        if (stack.is(Items.TIPPED_ARROW)) {
            this.potion = PotionUtils.getPotion(stack);
            Collection<MobEffectInstance> customEffects = PotionUtils.getCustomEffects(stack);
            if (!customEffects.isEmpty()) {
                for (MobEffectInstance effect : customEffects) {
                    this.effects.add(new MobEffectInstance(effect));
                }
            }
        } else if (!stack.isEmpty()) {
            this.potion = Potions.EMPTY;
            this.effects.clear();
        }
        this.initColor();
    }
    
    private void initColor() {
        boolean fixedColor = this.effects.isEmpty() && this.potion == Potions.EMPTY;
        this.entityData.set(COLOR, fixedColor ? -1 : PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
    }
    
    public void addEffect(MobEffectInstance effect) {
        this.effects.add(effect);
        this.initColor();
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COLOR, -1);
    }
    
    public int getColor() {
        return this.entityData.get(COLOR);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.spawnParticles(2);
                }
            } else {
                this.spawnParticles(3);
            }
        }

    }

    private void spawnParticles(int amount) {
        int i = this.getColor();
        if (i != -1 && amount > 0) {
            for (int j = 0; j < amount; ++j) {
                this.level().addParticle(BygoneColorParticleOption.create(BGParticleTypes.COLOR_PARTICLE, i),
                        this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
            }

        }
    }
    
    @Override
    protected void doPostHurtEffects(LivingEntity target) {
        super.doPostHurtEffects(target);
        Entity entity = this.getEffectSource();
        
        if (this.potion != Potions.EMPTY) {
            for (MobEffectInstance effectInstance : this.potion.getEffects()) {
                target.addEffect(new MobEffectInstance(effectInstance.getEffect(),
                        Math.max(effectInstance.mapDuration(i -> i / 8), 1),
                        effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.isVisible()), entity);
            }
        }
        
        if (!this.effects.isEmpty()) {
            for (MobEffectInstance effectInstance : this.effects) {
                target.addEffect(effectInstance, entity);
            }
        }
        
        this.returns = true;
    }
    
    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        
        this.level().broadcastEntityEvent(this, (byte) 0);
        
        this.setPickupItem(new ItemStack(BGItems.EXOTIC_ARROW.get()));
        
        // whatever logic you had in hitBlockEnchantmentEffects goes here now, e.g.:
        this.setPickupItem(new ItemStack(BGItems.EXOTIC_ARROW.get(), 64));
        if (this.getOwner() != null) {
            dropArrow((ServerLevel) this.level(), this.getOwner().blockPosition());
        }
        this.discard();
    }
    
    /*
    @Override
    protected void hitBlockEnchantmentEffects(ServerLevel world, BlockHitResult blockHitResult, ItemStack weaponStack) {
        super.hitBlockEnchantmentEffects(world, blockHitResult, weaponStack);
        this.level().broadcastEntityEvent(this, (byte) 0);
        this.setPickupItemStack(new ItemStack(BGItems.EXOTIC_ARROW.get(), 64));
        dropArrow(world, Objects.requireNonNull(getOwner()).blockPosition());
        this.discard();

    }
     */
    
    
    @Override
    protected ItemStack getPickupItem() {
        return this.pickupItem.copy();
    }
    
    public void setPickupItem(ItemStack stack) {
        this.pickupItem = stack;
    }
    
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(BGItems.EXOTIC_ARROW.get());
    }

    @Override
    public double getBaseDamage() {
        return 1.0D;
    }

    /*
    @Override
    protected double getDefaultGravity() {
        return 0.07;
    }
     */

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 0) {
            int i = this.getColor();
            if (i != -1) {
                float f = (float) (i >> 16 & 255) / 255.0F;
                float g = (float) (i >> 8 & 255) / 255.0F;
                float h = (float) (i & 255) / 255.0F;

                for (int j = 0; j < 20; ++j) {
                    this.level().addParticle(BygoneColorParticleOption.create(BGParticleTypes.COLOR_PARTICLE, f, g, h), this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
                }
            }
        } else {
            super.handleEntityEvent(status);
        }

    }
}
