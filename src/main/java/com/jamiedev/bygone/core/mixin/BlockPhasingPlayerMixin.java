package com.jamiedev.bygone.core.mixin;

import com.jamiedev.bygone.common.entity.BlockPhasingEntity;
import com.jamiedev.bygone.core.registry.BGAttributes;
import com.jamiedev.bygone.core.registry.BGItems;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class BlockPhasingPlayerMixin extends LivingEntity implements BlockPhasingEntity {

	protected BlockPhasingPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Shadow @Final private Abilities abilities;

	@Unique boolean phasing = false;
	@Unique int phasingTicks = 0;

	@WrapMethod(method = "tryToStartFallFlying")
	private boolean tryToStartPhasing(Operation<Boolean> original) {
		if (this.isPhasing()) {
			this.stopPhasing();
			return true;
		}
		if (this.canStartPhasing()) {
			this.startPhasing();
			return true;
		}
		return original.call();
	}

	@WrapMethod(method = "tick")
	private void tick(Operation<Void> original) {
		original.call();
		this.phasingTicks = Math.min(this.phasingTicks + 1, this.getMaxPhasingTicks());
		if (this.isPhasing()) this.tickPhasing();
	}

	@WrapMethod(method = "createAttributes")
	private static AttributeSupplier.Builder addPhasingAttribute(Operation<AttributeSupplier.Builder> original) {
		return original.call().add(BGAttributes.PHASING_DURATION.get(), 0);
	}

	@WrapMethod(method = "readAdditionalSaveData")
	private void readPhasingData(CompoundTag compound, Operation<Void> original) {
		original.call(compound);
		this.phasingTicks = compound.getInt("phasing_ticks");
	}

	@WrapMethod(method = "addAdditionalSaveData")
	private void addPhasingData(CompoundTag compound, Operation<Void> original) {
		original.call(compound);
		compound.putInt("phasing_ticks", this.phasingTicks);
	}

	@Override
	public boolean isPhasing() {
		return this.phasing;
	}

	@Override
	public boolean canStartPhasing() {
		if (this.onGround() || this.isFallFlying()) return false;
		ItemStack stack = this.getItemBySlot(EquipmentSlot.CHEST);
		return stack.is(BGItems.WALLOW_SHAWL.get());
	}

	@Override
	public void startPhasing() {
		this.sendSystemMessage(Component.literal("THIS IS A TEMPORARY MESSAGE TELLING YOU THAT YOU HAVE STARTED PHASING"));
		this.phasing = true;
		this.phasingTicks = 0;
		this.abilities.flying = true;
	}

	@Override
	public void stopPhasing() {
		this.sendSystemMessage(Component.literal("THIS IS A TEMPORARY MESSAGE TELLING YOU THAT YOU HAVE STOPPED PHASING"));
		this.phasing = false;
		this.phasingTicks = 0;
		this.abilities.flying = false;
	}

	@Override
	public void tickPhasing() {
		this.phasingTicks--;
		if (this.onGround() || this.phasingTicks <= 0) this.stopPhasing();
	}

	@Override
	public int getPhasingTicks() {
		return this.phasingTicks;
	}

	@Override
	public int getMaxPhasingTicks() {
		return (int) (this.level().tickRateManager().tickrate() * this.getAttributeValue(BGAttributes.PHASING_DURATION.get()));
	}

}
