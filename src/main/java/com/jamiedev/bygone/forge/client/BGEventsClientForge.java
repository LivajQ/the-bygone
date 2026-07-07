package com.jamiedev.bygone.forge.client;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.core.registry.BGDimensions;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Bygone.MOD_ID, value = Dist.CLIENT)
public class BGEventsClientForge {
    
    @SubscribeEvent
    public static void fog(ViewportEvent.RenderFog event) {
        Level level = event.getCamera().getEntity().level();
        if (!level.dimension().equals(BGDimensions.BYGONE_LEVEL_KEY)) return;

        event.setCanceled(true);
        event.scaleFarPlaneDistance(3f);
    }
}