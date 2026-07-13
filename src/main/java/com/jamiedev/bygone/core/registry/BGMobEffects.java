package com.jamiedev.bygone.core.registry;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.common.effect.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;
import java.util.function.Supplier;

public class BGMobEffects {

    public static Supplier<Holder<MobEffect>> HAUNTED;
    public static Supplier<Holder<MobEffect>> UPDRAFT;
    public static Supplier<Holder<MobEffect>> SATIETY;
    public static Supplier<Holder<MobEffect>> ASPHYXIATING;
	public static Supplier<Holder<MobEffect>> PLASMILK;
    public static Supplier<Holder<MobEffect>> SIPHONING;
    public static Supplier<Holder<MobEffect>> CARAPACE;
    
    @SuppressWarnings("unchecked")
    private static <T extends MobEffect> Supplier<Holder<MobEffect>> register(String name, Supplier<T> supplier) {
        Holder.Reference<MobEffect> holder = Registry.registerForHolder(
                (Registry<MobEffect>) (Registry<?>) BuiltInRegistries.MOB_EFFECT,
                Bygone.id(name),
                supplier.get()
        );
        return () -> holder;
    }

    public static void registerAll() {
        HAUNTED = register("haunted", HauntedEffect::new);
        UPDRAFT = register("updraft", UpdraftEffect::new);
        SATIETY = register("satiety", () -> new SatietyEffect(0x9a5500));
        ASPHYXIATING = register("asphyxiating", () -> new AsphyxiatingEffect(MobEffectCategory.HARMFUL, 0x5aafcf));
        PLASMILK = register("plasmilk", () -> new PlasmilkEffect(MobEffectCategory.NEUTRAL, 0x83ffe0));
        SIPHONING = register("siphoning", () -> new SiphoningEffect(MobEffectCategory.HARMFUL, 0x68b6d3));
       //TODO Forge again because me lazy
        CARAPACE = register("carapace", () ->
                new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x67CEEB)
                        .addAttributeModifier(ForgeMod.SWIM_SPEED.get(), UUID.fromString("c6f8b3e2-9d4a-4f1b-8f0a-2d9b6e3a1c77").toString(), 1, AttributeModifier.Operation.ADDITION));
    }
}