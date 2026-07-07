package com.jamiedev.bygone.core.mixin;

import com.jamiedev.bygone.BygoneForge;
import com.jamiedev.bygone.common.block.fluids.LithoFluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LithoFluid.class)
public abstract class FlowingFluidMixin extends FlowingFluid {
    @Override
    public @NotNull FluidType getFluidType() {
        return BygoneForge.LITHO_TYPE.get();
    }
}
