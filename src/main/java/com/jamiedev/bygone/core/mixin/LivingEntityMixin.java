package com.jamiedev.bygone.core.mixin;

import com.jamiedev.bygone.common.item.VerdigrisBladeItem;
import com.jamiedev.bygone.core.init.JamiesModTag;
import com.jamiedev.bygone.core.registry.BGBlocks;
import com.jamiedev.bygone.core.registry.BGMobEffects;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected ItemStack useItem;
    @Shadow
    protected int useItemRemaining;

	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Shadow
    public abstract boolean isUsingItem();

	@Shadow
	public abstract boolean hasEffect(Holder<MobEffect> effect);

	@WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V"))
    public void scaleWaterTravelSpeed(LivingEntity instance, float distance, Vec3 direction, Operation<Void> original) {
        MobEffectInstance carapaceEffect = instance.getEffect(BGMobEffects.CARAPACE.get());
        float modifiedDistance = distance;
        if (carapaceEffect != null && (instance.isInWater() && instance.getFluidHeight(FluidTags.WATER) > 0.1F)) {
            modifiedDistance = distance * (1 + 0.2f * (carapaceEffect.getAmplifier() + 1));
        }
        original.call(instance, modifiedDistance, direction);
    }

    @Inject(method = "isBlocking", at = @At("HEAD"), cancellable = true)
    //Fix for compatibility with Guarding mod git issue #20
    private void isBlocking(CallbackInfoReturnable<Boolean> cir) {
        Item item = this.useItem.getItem();
        if (item instanceof VerdigrisBladeItem) {
            if (this.isUsingItem() && !this.useItem.isEmpty()) {

                boolean canItemBlock = item.getUseAnimation(this.useItem) == UseAnim.BLOCK;
                boolean isUsingItemForLongEnough = (item.getUseDuration(
                        this.useItem,
                        (LivingEntity) (Object) this
                ) - this.useItemRemaining) >= ShieldItem.EFFECTIVE_BLOCK_DELAY;
                cir.setReturnValue(canItemBlock && isUsingItemForLongEnough);
            } else {
                cir.setReturnValue(false);
            }
        }
    }

	@Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
	private void beforeAddingEffect(MobEffectInstance instance, CallbackInfoReturnable<Boolean> cir) {
		if (!this.hasEffect(BGMobEffects.PLASMILK.get())) return;
		if (instance.getEffect().is(JamiesModTag.IGNORES_PLASMILK)) return;
		cir.setReturnValue(false);
	}

	@WrapMethod(method = "isInvulnerableTo")
	private boolean wrapIsInvulnerableTo(DamageSource source, Operation<Boolean> original) {

		if (this.getType().is(JamiesModTag.SPECTRAL)) {
			if (source.getDirectEntity() != null && source.getDirectEntity().getType().is(JamiesModTag.SPECTRAL_VULNERABLE_TO_ENTITY)) {
				return false;
			} else if (source.is(JamiesModTag.SPECTRAL_VULNERABLE_TO_DAMAGE)) {
				return false;
			} else if (source.getWeaponItem() != null && source.getWeaponItem().is(JamiesModTag.SPECTRAL_VULNERABLE_TO_ITEM)) {
				return false;
			}
			return true;
		}
		return original.call(source);
	}

	@WrapMethod(method = "die")
	private void spawnHauntedGround(DamageSource source, Operation<Void> original) {
		original.call(source);
		if (!this.getType().is(JamiesModTag.SPECTRAL)) return;

		BlockState groundState = BGBlocks.HAUNTED_GROUND.get().defaultBlockState();
		BlockPos.MutableBlockPos pos = this.getOnPos().above(2).mutable();
		for (int i = 0; i < 16; i++) {
			pos.move(Direction.DOWN);
			BlockState state = this.level().getBlockState(pos);
			if (!(state.isAir() || state.canBeReplaced())) continue;
			if (!groundState.canSurvive(this.level(), pos)) continue;

			this.level().setBlock(pos, groundState, Block.UPDATE_ALL);
			break;
		}
	}

}
