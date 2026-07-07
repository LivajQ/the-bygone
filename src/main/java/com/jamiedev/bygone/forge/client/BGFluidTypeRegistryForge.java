package com.jamiedev.bygone.forge.client;

import com.jamiedev.bygone.Bygone;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BGFluidTypeRegistryForge {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Bygone.MOD_ID);
}