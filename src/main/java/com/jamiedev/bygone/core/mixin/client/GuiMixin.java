package com.jamiedev.bygone.core.mixin.client;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.core.registry.BGMobEffects;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    
    @Shadow
    protected RandomSource random;
    
    @Inject(
            method = "renderHearts",
            at = @At("HEAD"),
            cancellable = true
    )
    private void bygone$renderHauntedHearts(GuiGraphics gfx, Player player, int x, int y, int height, int offsetIndex, float maxHealth, int currentHealth, int displayHealth, int absorption, boolean highlight, CallbackInfo ci) {
        if (!player.hasEffect(BGMobEffects.HAUNTED.get().get())) {
            return;
        }

        ci.cancel();
        
        boolean hardcore = player.level().getLevelData().isHardcore();
        boolean blinking = highlight;
        
        ResourceLocation full = hardcore ? Bygone.id("hud/heart/haunted_hardcore_full") :
                Bygone.id("hud/heart/haunted_full");
        
        ResourceLocation fullBlink = hardcore ? Bygone.id("hud/heart/haunted_hardcore_full_blinking") :
                Bygone.id("hud/heart/haunted_full_blinking");
        
        ResourceLocation half = hardcore ? Bygone.id("hud/heart/haunted_hardcore_half") :
                Bygone.id("hud/heart/haunted_half");
        
        ResourceLocation halfBlink = hardcore ? Bygone.id("hud/heart/haunted_hardcore_half_blinking") :
                Bygone.id("hud/heart/haunted_half_blinking");
        
        int totalHearts = Mth.ceil(maxHealth / 2.0f);
        int totalAbsorb = Mth.ceil(absorption / 2.0f);
        
        for (int i = totalHearts + totalAbsorb - 1; i >= 0; --i) {
            
            int row = i / 10;
            int col = i % 10;
            
            int hx = x + col * 8;
            int hy = y - row * height;
            
            if (currentHealth + absorption <= 4) {
                hy += random.nextInt(2);
            }
            
            if (i < totalHearts && i == offsetIndex) {
                hy -= 2;
            }
            
            int heartIndex = i * 2;
            boolean isAbsorb = i >= totalHearts;
            
            boolean halfHeart;
            boolean blink;
            
            if (isAbsorb) {
                int absorbIndex = heartIndex - (totalHearts * 2);
                halfHeart = absorbIndex + 1 == absorption;
                blink = false;
            } else {
                halfHeart = heartIndex + 1 == currentHealth;
                blink = blinking && heartIndex < displayHealth;
            }
            
            ResourceLocation tex = halfHeart ? (blink ? halfBlink : half) : (blink ? fullBlink : full);
            
            gfx.blit(tex, hx, hy, 0, 0, 9, 9);
        }
    }
}

