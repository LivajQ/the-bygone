package com.jamiedev.bygone.core.registry;

import com.jamiedev.bygone.Bygone;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.resources.ResourceLocation;

public class BGCriteria {
    public static final KilledTrigger KILLED_BY_BLEMISH_CRITERION =
            new KilledTrigger(new ResourceLocation(Bygone.MOD_ID, "killed_by_blemish"));
    
    public static final ResourceLocation ENTER_BYGONE_ADVANCEMENT =
            new ResourceLocation(Bygone.MOD_ID, "bygone/enter_bygone");
    
    public static void init() {
        CriteriaTriggers.register(KILLED_BY_BLEMISH_CRITERION);
    }
    
    public static <T extends CriterionTrigger<?>> T register(T criterion) {
        return CriteriaTriggers.register(criterion);
    }
}