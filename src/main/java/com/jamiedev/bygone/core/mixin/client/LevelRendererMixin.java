package com.jamiedev.bygone.core.mixin.client;

import com.jamiedev.bygone.common.weather.BygoneWeather;
import com.jamiedev.bygone.core.registry.BGDimensions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    
    @Shadow @Nullable
    private ClientLevel level;
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void bygone$tickRenderers(CallbackInfo ci) {
        assert level != null;
        if (!level.dimension().equals(BGDimensions.BYGONE_LEVEL_KEY)) return;
        
        BygoneWeather.Client clientWeather = BygoneWeather.Client.getInstance();
        clientWeather.stream().forEach((renderer) -> renderer.tick(level));
    }
}