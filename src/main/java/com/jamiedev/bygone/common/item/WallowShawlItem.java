package com.jamiedev.bygone.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.core.registry.BGAttributes;
import com.jamiedev.bygone.core.registry.BGItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class WallowShawlItem extends Item implements Equipable {
    
    private final Multimap<Attribute, AttributeModifier> modifiers;
    
    public WallowShawlItem(Item.Properties properties) {
        super(properties);
        
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
        
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        
        builder.put(
                BGAttributes.PHASING_DURATION.get().get(),
                new AttributeModifier(
                        Bygone.id("wallow_shawl.phase_duration").toString(),
                        8,
                        AttributeModifier.Operation.ADDITION
                )
        );
        
        this.modifiers = builder.build();
    }
    
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.CHEST) {
            return modifiers;
        }
        return super.getDefaultAttributeModifiers(slot);
    }
    
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.is(BGItems.WALLOW_SHAWL_SCRAP.get());
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return this.swapWithEquipmentSlot(this, level, player, hand);
    }
    
    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_ELYTRA;
    }
    
    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }
}
