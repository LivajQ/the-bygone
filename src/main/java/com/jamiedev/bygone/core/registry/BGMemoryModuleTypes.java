package com.jamiedev.bygone.core.registry;

import com.jamiedev.bygone.Bygone;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Optional;
import java.util.UUID;

public class BGMemoryModuleTypes<U> {
    private static final Codec<Unit> UNIT_CODEC = Codec.unit(Unit.INSTANCE);
    
    public static MemoryModuleType<UUID> GROUP_LEADER;
    public static MemoryModuleType<Boolean> IS_LEADER;
    public static MemoryModuleType<Boolean> IS_STALKING;
    public static MemoryModuleType<Boolean> IS_IN_GROUP;
    public static MemoryModuleType<LivingEntity> NEAREST_NECTAUR_ALLY;
    public static MemoryModuleType<Unit> NECTAUR_RANGED_COOLDOWN;


    private static <U> MemoryModuleType<U> register(String identifier, Codec<U> codec) {
        return Registry.register(
                BuiltInRegistries.MEMORY_MODULE_TYPE, Bygone.id(identifier), new MemoryModuleType<>(Optional.of(codec))
        );
    }

    private static <U> MemoryModuleType<U> register(String identifier) {
        return Registry.register(
                BuiltInRegistries.MEMORY_MODULE_TYPE, Bygone.id(identifier), new MemoryModuleType<>(Optional.empty())
        );
    }
    
    public static void registerAll() {
        Bygone.LOGGER.info("Registering {} memory module types", Bygone.MOD_ID);
        
        GROUP_LEADER = register("group_leader", UUIDUtil.CODEC);
        IS_LEADER = register("is_leader");
        IS_STALKING = register("is_stalking");
        IS_IN_GROUP = register("is_in_group");
        NEAREST_NECTAUR_ALLY = register("nearest_nectaur_ally");
        NECTAUR_RANGED_COOLDOWN = register("nectaur_ranged_cooldown", UNIT_CODEC);
    }
}
