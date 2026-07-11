package com.jamiedev.bygone.core.mixin.client;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.common.entity.BlockPhasingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class EntityPhaseMeterGuiMixin {
    
    @Unique private static final int PHASE_METER_SPRITE_COUNT = 3;
    @Unique private static final ResourceLocation[] PHASE_METER_SPRITES = new ResourceLocation[PHASE_METER_SPRITE_COUNT];
    
    static {
        for (int i = 0; i < PHASE_METER_SPRITE_COUNT; i++) {
            PHASE_METER_SPRITES[i] = new ResourceLocation(Bygone.MOD_ID, "textures/gui/sprites/hud/phase_meter/" + i + ".png");
        }
    }
    
    @Shadow @Final private Minecraft minecraft;
    
    @Inject(method = "render", at = @At("TAIL"))
    private void bygone$renderPhaseMeter(GuiGraphics graphics, float partialTick, CallbackInfo ci) {
        Player player = this.minecraft.player;
        if (player instanceof BlockPhasingEntity phasing) {
            if (this.minecraft.options.hideGui) return;
            
            int maxTicks = phasing.getMaxPhasingTicks();
            if (maxTicks <= 0) return;
            
            int ticks = phasing.getPhasingTicks();
            float value = (float) ticks / maxTicks;
            int spriteIndex = (PHASE_METER_SPRITE_COUNT - 1) - (int) (value * (PHASE_METER_SPRITE_COUNT - 1));
            ResourceLocation sprite = PHASE_METER_SPRITES[spriteIndex];
            
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            graphics.blit(sprite, (graphics.guiWidth() / 2) - 8, (graphics.guiHeight() / 2) + 16, 0, 0, 16, 16, 16, 16);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        }
    }
}
