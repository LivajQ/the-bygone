package com.jamiedev.bygone.common.entity.projectile;

import com.google.common.collect.Sets;
import com.jamiedev.bygone.common.particle.BygoneColorParticleOption;
import com.jamiedev.bygone.core.registry.BGEntityTypes;
import com.jamiedev.bygone.core.registry.BGItems;
import com.jamiedev.bygone.core.registry.BGParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

import java.util.Set;

public class NectaurPetalEntity extends AbstractArrow {
    private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR;
    private ItemStack pickupItem = new ItemStack(BGItems.NECTAUR_PETAL.get());
    private Potion potion = Potions.EMPTY;
    private final Set<MobEffectInstance> effects = Sets.newHashSet();
    
    static {
        ID_EFFECT_COLOR = SynchedEntityData.defineId(NectaurPetalEntity.class, EntityDataSerializers.INT);
    }

    public NectaurPetalEntity(EntityType<? extends NectaurPetalEntity> entityType, Level world) {
        super(entityType, world);
    }
    
    public NectaurPetalEntity(Level world, LivingEntity owner, ItemStack stack) {
        super(BGEntityTypes.NECTAUR_PETAL.get(), owner, world);
        this.setPickupItem(stack);
    }
    
    public NectaurPetalEntity(Level world, double x, double y, double z, ItemStack stack) {
        super(BGEntityTypes.NECTAUR_PETAL.get(), x, y, z, world);
        this.setPickupItem(stack);
    }
    
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_EFFECT_COLOR, -1);
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.MUD_HIT;
    }
    
    protected ItemStack getPickupItem() {
        return this.pickupItem.copy();
    }
    
    public void setPickupItem(ItemStack stack) {
        this.pickupItem = stack;
        this.updateColor();
    }
    
    private void updateColor() {
        boolean fixedColor = this.effects.isEmpty() && this.potion == Potions.EMPTY;
        this.entityData.set(ID_EFFECT_COLOR, fixedColor ? -1 : PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
    }
    
    public void addEffect(MobEffectInstance effectInstance) {
        this.effects.add(effectInstance);
        this.updateColor();
    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.makeParticle(1);
                }

                if (this.inGroundTime > 8) {
                    this.kill();
                }
            } else {
                this.makeParticle(2);
            }
        } else if (this.inGround && this.inGroundTime != 0 && (this.potion != Potions.EMPTY || !this.effects.isEmpty()) && this.inGroundTime >= 600) {
            this.level().broadcastEntityEvent(this, (byte) 0);
            this.setPickupItem(new ItemStack(BGItems.NECTAUR_PETAL.get()));
        }

        if (this.inGroundTime > 8) {
            this.kill();
        }

    }

    private void makeParticle(int particleAmount) {
        int i = this.getColor();
        if (i != -1 && particleAmount > 0) {
            for (int j = 0; j < particleAmount; ++j) {
                this.level().addParticle(BygoneColorParticleOption.create(BGParticleTypes.COLOR_PARTICLE, i), this.getRandomX(0.5F), this.getRandomY(), this.getRandomZ(0.5F), 0.0F, 0.0F, 0.0F);
            }
        }

    }

    public int getColor() {
        return this.entityData.get(ID_EFFECT_COLOR);
    }
    
    protected void doPostHurtEffects(LivingEntity living) {
        super.doPostHurtEffects(living);
        Entity entity = this.getEffectSource();
        
        if (this.potion != Potions.EMPTY) {
            for (MobEffectInstance mobeffectinstance : this.potion.getEffects()) {
                living.addEffect(new MobEffectInstance(mobeffectinstance.getEffect(),
                        Math.max(mobeffectinstance.mapDuration(p_268168_ -> p_268168_ / 8), 1),
                        mobeffectinstance.getAmplifier(), mobeffectinstance.isAmbient(), mobeffectinstance.isVisible()), entity);
            }
        }
        
        for (MobEffectInstance mobeffectinstance1 : this.effects) {
            living.addEffect(mobeffectinstance1, entity);
        }
    }

    public void handleEntityEvent(byte id) {
        if (id == 0) {
            int i = this.getColor();
            if (i != -1) {
                float f = (float) (i >> 16 & 255) / 255.0F;
                float f1 = (float) (i >> 8 & 255) / 255.0F;
                float f2 = (float) (i & 255) / 255.0F;

                for (int j = 0; j < 20; ++j) {
                    this.level().addParticle(BygoneColorParticleOption.create(BGParticleTypes.COLOR_PARTICLE, f, f1, f2), this.getRandomX(0.5F), this.getRandomY(), this.getRandomZ(0.5F), 0.0F, 0.0F, 0.0F);
                }
            }
        } else {
            super.handleEntityEvent(id);
        }

    }
}
