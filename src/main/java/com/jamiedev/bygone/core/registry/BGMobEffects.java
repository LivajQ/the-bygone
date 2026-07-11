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

import java.util.function.Supplier;

public class BGMobEffects {

    public static Supplier<Holder<MobEffect>> HAUNTED = register("haunted", HauntedEffect::new);
    public static Supplier<Holder<MobEffect>> UPDRAFT = register("updraft", UpdraftEffect::new);
    public static Supplier<Holder<MobEffect>> SATIETY = register("satiety", () -> new SatietyEffect(0x9a5500));
    public static Supplier<Holder<MobEffect>> ASPHYXIATING = register(
            "asphyxiating",
            () -> new AsphyxiatingEffect(MobEffectCategory.HARMFUL, 0x5aafcf)
    );
	public static Supplier<Holder<MobEffect>> PLASMILK = register(
			"plasmilk",
			() -> new PlasmilkEffect(MobEffectCategory.NEUTRAL, 0x83ffe0)
	);
    public static Supplier<Holder<MobEffect>> SIPHONING = register(
            "siphoning",
            () -> new SiphoningEffect(MobEffectCategory.HARMFUL, 0x68b6d3)
    );

    public static Supplier<Holder<MobEffect>> CARAPACE = register(
            "carapace", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x67CEEB).addAttributeModifier(
                    ForgeMod.SWIM_SPEED.get(), //TODO Forge because lazy again
                    Bygone.id("effect.carapace").toString(),
                    1,
                    AttributeModifier.Operation.ADDITION
            )
    );
    
    @SuppressWarnings("unchecked")
    private static <T extends MobEffect> Supplier<Holder<MobEffect>> register(String name, Supplier<T> supplier) {
        Holder.Reference<MobEffect> holder = Registry.registerForHolder(
                (Registry<MobEffect>) (Registry<?>) BuiltInRegistries.MOB_EFFECT,
                Bygone.id(name),
                supplier.get()
        );
        return () -> holder;
    }

    public static void init() {
    }
}