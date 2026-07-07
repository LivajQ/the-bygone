package com.jamiedev.bygone.forge.core.registry;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.common.commands.BygoneWeatherCommand;
import com.jamiedev.bygone.common.weather.BygoneWeather;
import com.jamiedev.bygone.core.init.JamiesModTag;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Bygone.MOD_ID)
public class BGEvents {
    private static final VillagerTrades.ItemListing bygoneMapTrade = new VillagerTrades.TreasureMapForEmeralds(
            10,
            JamiesModTag.ON_BYGONE_PORTAL_MAPS,
            "Bygone Portal Map",
            MapDecoration.Type.BANNER_GREEN,
            12,
            5
    );

    @SubscribeEvent
    public static void addWanderingTraderTrades(WandererTradesEvent event) {
        List<VillagerTrades.ItemListing> rareList = event.getRareTrades();
        List<VillagerTrades.ItemListing> commonList = event.getGenericTrades();
        rareList.add(bygoneMapTrade);
    }

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if(event.getType() == VillagerProfession.CARTOGRAPHER) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            event.getTrades().get(1).add(new VillagerTrades.TreasureMapForEmeralds(
                    10,
                    JamiesModTag.ON_BYGONE_PORTAL_MAPS,
                    "Bygone Portal Map",
                    MapDecoration.Type.BANNER_GREEN,
                    12,
                    5
            ));
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(final EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer)
            BygoneWeather.getOrDefault(serverPlayer.serverLevel()).informPlayerOfState(serverPlayer);
    }

    @SubscribeEvent
    public static void addCommands(final RegisterCommandsEvent event) {
        BygoneWeatherCommand.register(event.getDispatcher(), event.getBuildContext());
    }
}
