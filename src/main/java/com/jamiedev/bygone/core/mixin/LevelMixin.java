package com.jamiedev.bygone.core.mixin;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.common.weather.BygoneWeather;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public class LevelMixin {

    @Inject(method = "prepareWeather", at = @At("HEAD"))
    private void bygone$prepareWeather(CallbackInfo ci) {

    }
}
