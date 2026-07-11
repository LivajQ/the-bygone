package com.jamiedev.bygone.core.registry;

import com.jamiedev.bygone.Bygone;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class BGDamageTypes 
{
    public static final ResourceKey<DamageType> HAUNTED = createKey("haunted");

    private static ResourceKey<DamageType> createKey(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Bygone.id(name));
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> damageType, Entity target, Entity attacker) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType), target, attacker);
    }

    public static void register(BootstapContext<DamageType> context) {
        context.register(HAUNTED, new DamageType(Bygone.MOD_ID + ".haunted", 0.1F));
  }

}
