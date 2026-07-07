package com.jamiedev.bygone.common.item;

import com.jamiedev.bygone.core.registry.BGSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

public class MaliciousWarHornItem extends Item {

    private static final int MAX_VEXES = 5;
    private static final int RECHARGE_TIME_SECONDS = 100;
    private static final int VEX_LIFETIME_SECONDS = 40;
    private static final int DEATH_PENALTY_SECONDS = 20;
    private static final int USE_DURATION = 10;

    InstrumentItem ref;

    public MaliciousWarHornItem(Properties properties) {
        super(properties);
    }
    
    public static void onVexDeath(Vex vex, ItemStack hornStack) {
        if (hornStack.getItem() instanceof MaliciousWarHornItem) {
            WarHornData data = WarHornData.read(hornStack);
            
            if (data.activeVexes().contains(vex.getUUID())) {
                List<UUID> newVexes = new ArrayList<>(data.activeVexes());
                newVexes.remove(vex.getUUID());
                
                WarHornData newData = new WarHornData(
                        newVexes,
                        data.cooldownSeconds() + DEATH_PENALTY_SECONDS,
                        data.vexTimeLeft()
                );
                
                WarHornData.write(hornStack, newData);
            }
        }
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            WarHornData data = WarHornData.read(itemStack);
            
            if (data.cooldownSeconds() <= 0) {
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(itemStack);
            } else {
                player.displayClientMessage(
                        Component.translatable("item.bygone.malicious_war_horn.cooldown", data.cooldownSeconds())
                                .withStyle(ChatFormatting.RED),
                        true
                );
                return InteractionResultHolder.fail(itemStack);
            }
        }
        
        return InteractionResultHolder.consume(itemStack);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (!level.isClientSide && user instanceof Player player) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    BGSoundEvents.WAR_HORN_USE.get(), SoundSource.RECORDS, 1.5F, 1.0F);
            level.gameEvent(GameEvent.INSTRUMENT_PLAY, player.position(), GameEvent.Context.of(player));
            
            spawnHornParticles(level, player);
            
            WarHornData data = releaseVexes(stack, level, player);
            WarHornData.write(stack, data);
            
            player.awardStat(Stats.ITEM_USED.get(this));
            player.getCooldowns().addCooldown(this, 20);
        }
        
        return stack;
    }
    
    private WarHornData releaseVexes(ItemStack stack, Level level, Player player) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return WarHornData.read(stack);
        }
        
        Set<UUID> vexIds = new HashSet<>();
        
        for (int i = 0; i < MAX_VEXES; i++) {
            double angle = (2 * Math.PI / MAX_VEXES) * i;
            double x = player.getX() + Math.cos(angle) * 2;
            double z = player.getZ() + Math.sin(angle) * 2;
            double y = player.getY() + 1;
            
            Vex vex = EntityType.VEX.create(serverLevel);
            if (vex != null) {
                vex.moveTo(x, y, z, player.getYRot(), 0.0F);
                vex.setBoundOrigin(player.blockPosition());
                vex.setLimitedLife(VEX_LIFETIME_SECONDS * 20);
                vex.setPersistenceRequired();
                
                serverLevel.addFreshEntity(vex);
                vexIds.add(vex.getUUID());
            }
        }
        
        return new WarHornData(new ArrayList<>(vexIds), RECHARGE_TIME_SECONDS, VEX_LIFETIME_SECONDS);
    }

    private void spawnHornParticles(Level level, Player player) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        Vec3 lookDirection = player.getLookAngle();
        double hornX = player.getX() + lookDirection.x * 0.5;
        double hornY = player.getY() + player.getEyeHeight() - 0.2;
        double hornZ = player.getZ() + lookDirection.z * 0.5;

        for (int i = 0; i < 100; i++) {
            double angle = 2 * Math.PI * level.random.nextDouble();
            double radius = level.random.nextDouble() * 3.0;
            double height = (level.random.nextDouble() - 0.5) * 2.0;

            double offsetX = Math.cos(angle) * radius * 0.3;
            double offsetY = height * 0.3;
            double offsetZ = Math.sin(angle) * radius * 0.3;

            serverLevel.sendParticles(ParticleTypes.SOUL,
                    hornX, hornY, hornZ,
                    1, offsetX, offsetY, offsetZ, 0.1);
        }
    }
    
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player && level.getGameTime() % 20 == 0) {
            WarHornData data = WarHornData.read(stack);
            
            boolean needsUpdate = false;
            int newCooldown = Math.max(0, data.cooldownSeconds() - 1);
            int newVexTime = Math.max(0, data.vexTimeLeft() - 1);
            
            if (newCooldown != data.cooldownSeconds() || newVexTime != data.vexTimeLeft()) {
                needsUpdate = true;
            }
            
            List<UUID> activeVexes = data.activeVexes();
            
            if (newVexTime <= 0 && !activeVexes.isEmpty()) {
                for (UUID vexId : activeVexes) {
                    Entity vexEntity = ((ServerLevel) level).getEntity(vexId);
                    if (vexEntity instanceof Vex vex) {
                        vex.discard();
                    }
                }
                activeVexes = new ArrayList<>();
                newVexTime = 0;
                needsUpdate = true;
            }
            
            if (needsUpdate) {
                WarHornData newData = new WarHornData(activeVexes, newCooldown, newVexTime);
                WarHornData.write(stack, newData);
            }
        }
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
        
        tooltipComponents.add(Component.translatable("item.bygone.malicious_war_horn.desc1")
                .withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("item.bygone.malicious_war_horn.desc2")
                .withStyle(ChatFormatting.GRAY));
        
        WarHornData data = WarHornData.read(stack);
        
        if (!data.activeVexes().isEmpty()) {
            tooltipComponents.add(Component.translatable("item.bygone.malicious_war_horn.vexes_active", data.activeVexes().size())
                    .withStyle(ChatFormatting.AQUA));
        }
        
        if (data.cooldownSeconds() > 0) {
            tooltipComponents.add(Component.translatable("item.bygone.malicious_war_horn.cooldown_remaining", data.cooldownSeconds())
                    .withStyle(ChatFormatting.RED));
        } else {
            tooltipComponents.add(Component.translatable("item.bygone.malicious_war_horn.ready")
                    .withStyle(ChatFormatting.GREEN));
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.TOOT_HORN;
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        WarHornData data = WarHornData.read(stack);
        return !data.activeVexes().isEmpty();
    }
    
    public record WarHornData(List<UUID> activeVexes, int cooldownSeconds, int vexTimeLeft) {
        public static final WarHornData EMPTY = new WarHornData(new ArrayList<>(), 0, 0);
        
        private static final String KEY_VEXES = "active_vexes";
        private static final String KEY_COOLDOWN = "cooldown_seconds";
        private static final String KEY_VEX_TIME = "vex_time_left";
        
        public static WarHornData read(ItemStack stack) {
            CompoundTag tag = stack.getTag();
            if (tag == null || !tag.contains("war_horn_data")) return EMPTY;
            CompoundTag data = tag.getCompound("war_horn_data");
            
            ListTag vexList = data.getList(KEY_VEXES, Tag.TAG_INT_ARRAY);
            List<UUID> vexes = new ArrayList<>();
            for (int i = 0; i < vexList.size(); i++) {
                vexes.add(NbtUtils.loadUUID(vexList.get(i)));
            }
            
            return new WarHornData(vexes, data.getInt(KEY_COOLDOWN), data.getInt(KEY_VEX_TIME));
        }
        
        public static void write(ItemStack stack, WarHornData value) {
            CompoundTag data = new CompoundTag();
            
            ListTag vexList = new ListTag();
            for (UUID id : value.activeVexes()) {
                vexList.add(NbtUtils.createUUID(id));
            }
            data.put(KEY_VEXES, vexList);
            data.putInt(KEY_COOLDOWN, value.cooldownSeconds());
            data.putInt(KEY_VEX_TIME, value.vexTimeLeft());
            
            stack.getOrCreateTag().put("war_horn_data", data);
        }
    }

}