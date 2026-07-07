package com.jamiedev.bygone;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.jamiedev.bygone.client.screen.PortalOverlay;
import com.jamiedev.bygone.common.block.entity.GumboPotBlockEntity;
import com.jamiedev.bygone.common.util.ServerTickHandler;
import com.jamiedev.bygone.common.util.VexDeathTracker;
import com.jamiedev.bygone.core.datagen.BygoneDataGenerator;
import com.jamiedev.bygone.core.registry.*;
import com.jamiedev.bygone.forge.client.BygoneClientForge;
import com.jamiedev.bygone.forge.client.LithoClientExtensions;
import com.jamiedev.bygone.forge.core.network.BygoneForgeNetworkHandler;
import com.jamiedev.bygone.forge.core.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod(Bygone.MOD_ID)
public class BygoneForge {
    public static DeferredRegister<Fluid> fluidRegister = DeferredRegister.create(Registries.FLUID, Bygone.MOD_ID);

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(
            ForgeRegistries.Keys.FLUID_TYPES,
            Bygone.MOD_ID
    );

    public static final Supplier<FluidType> LITHO_TYPE = FLUID_TYPES.register(
            "litho_type",
            () -> new FluidType(
                    FluidType.Properties.create()
                            .descriptionId("block.bygone.litho")
                            .canSwim(true)
                            .canDrown(true)
                            .pathType(BlockPathTypes.WATER)
                            .adjacentPathType(null)
                            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_EMPTY)
                            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                            .lightLevel(8)
            ) {

                @Override
                public boolean canConvertToSource(@NotNull FluidState state, @NotNull LevelReader reader, @NotNull BlockPos pos) {
                    if (reader instanceof Level level) {
                        return level.getGameRules().getBoolean(GameRules.RULE_WATER_SOURCE_CONVERSION);
                    } else {
                        return super.canConvertToSource(state, reader, pos);
                    }
                }
                
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new LithoClientExtensions());
                }
            }
    );

    static {
       // BygoneWeather.WEATHER_TYPES = new RegistryBuilder<>(BygoneWeather.WEATHER_TYPE_REGISTRY_KEY).create();
    }
    
    public BygoneForge(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        
        //BGDataComponentsForge.DATA_COMPONENTS.register(modBus);
        //BGDecoratedPotPatternsNeoForge.POT_PATTERNS.register(modBus);
        BGAttributesForge.init(modBus);
        Bygone.init();

        fluidRegister.register(modBus);
        FLUID_TYPES.register(modBus);
        
        BygoneForgeNetworkHandler.register();
        if (FMLEnvironment.dist.isClient()) {
            BygoneClientForge.init(modBus);
        }
        modBus.addListener(this::registerEvent);
        modBus.addListener(BygoneDataGenerator::onInitializeDataGenerator);
        modBus.addListener(this::setup);
        modBus.addListener(this::spawnPlacements);
        modBus.addListener(this::createAttributes);
        //modBus.addListener(this::addValidBlocks);
        //modBus.addListener(this::modifyDefaultComponents);
        modBus.addListener(this::registerGuiOverlays);
        //modBus.addListener(BGDataComponentsForge::init);
        forgeBus.addListener(this::blockModifications);
        forgeBus.addListener(this::damageEvent);
        forgeBus.addListener(this::onLivingDeath);
        forgeBus.addListener(this::onServerTick);
    }
    
    private void blockModifications(final BlockEvent.BlockToolModificationEvent event) {
        if (event.getToolAction() == ToolActions.HOE_TILL && event.getLevel().getBlockState(event.getPos().above()).isAir()) {
            BlockState state = event.getState();
            if (state.is(BGBlocks.CLAYSTONE.get()))
                event.setFinalState(BGBlocks.CLAYSTONE_FARMLAND.get().defaultBlockState());
            else if (state.is(BGBlocks.COARSE_CLAYSTONE.get()))
                event.setFinalState(BGBlocks.CLAYSTONE.get().defaultBlockState());
        }
        if (event.getToolAction() == ToolActions.SHOVEL_FLATTEN && event.getLevel().getBlockState(event.getPos().above()).isAir()) {
            BlockState state = event.getState();
            if (state.is(BGBlocks.MOSSY_CLAYSTONE.get()))
                event.setFinalState(BGBlocks.MOSSY_CLAYSTONE_PATH.get().defaultBlockState());
            else if (state.is(BGBlocks.ALPHA_MOSSY_CLAYSTONE.get()))
                event.setFinalState(BGBlocks.ALPHA_MOSSY_CLAYSTONE_PATH.get().defaultBlockState());
        }
    }

    void damageEvent(LivingDamageEvent event) {

    }

    void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Vex vex && event.getEntity().level() instanceof ServerLevel serverLevel) {
            VexDeathTracker.onVexDeath(vex, serverLevel);
        }
    }
    
    private void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ServerTickHandler.onServerTick(event.getServer());
        }
    }

    void createAttributes(EntityAttributeCreationEvent event) {
        Bygone.initAttributes(event::put);
    }
    
    void spawnPlacements(SpawnPlacementRegisterEvent event) {
        Bygone.registerSpawnPlacements((entityType, spawnPlacementType, types, spawnPredicate) -> event.register(
                entityType,
                spawnPlacementType,
                types,
                spawnPredicate,
                SpawnPlacementRegisterEvent.Operation.REPLACE
        ));
    }

    void setup(FMLCommonSetupEvent event) {
        // TODO should these be enqueued (Startraveler)
        event.enqueueWork(() -> {

            Set<Block> validBlocks = Sets.newHashSet(BlockEntityType.BRUSHABLE_BLOCK.validBlocks);
            validBlocks.addAll(Sets.newHashSet(BGBlocks.SUSPICIOUS_SHELLSAND.get(), BGBlocks.SUSPICIOUS_CLAYSTONE.get()));
            BlockEntityType.BRUSHABLE_BLOCK.validBlocks = ImmutableSet.copyOf(validBlocks);

            //BGDataComponentsNeoForge.init();
            //BGDecoratedPotPatternsForge.expandVanilla();
            Bygone.registerStrippables();
            Bygone.addFlammable();
            
            //JamiesModPortalsNeoForge.init();
            GumboPotBlockEntity.GumboScooping.setFilled(Items.BOWL, BGItems.GUMBO_BOWL.get());
            GumboPotBlockEntity.GumboScooping.setFilled(Items.GLASS_BOTTLE, BGItems.GUMBO_BOTTLE.get());
            BGDataComponents.gumboBootstrap(BGDataComponents.GUMBO_INGREDIENT_REGISTRY::put);
        });
    }

    /* TODO deal with this later
    void addValidBlocks(BlockEntityTypeAddBlocksEvent event) {
        Bygone.addValidBlocks(event::modify);
    }
     */

    void registerEvent(RegisterEvent event) {
        IForgeRegistry<?> forgeRegistry = event.getForgeRegistry();
        Registry<?> vanillaRegistry = event.getVanillaRegistry();

        if (vanillaRegistry == BuiltInRegistries.BLOCK) {
            //AttachmentTypesForge.init();
            Bygone.registerBuiltIn();
        }

        /*
        if (forgeRegistry != BGRegistriesForge.WEATHER_TYPES_FORGE.get()) return;

        BygoneWeather.bootstrap(
            (weatherType) -> {
                event.register(
                    BygoneWeather.WEATHER_TYPE_REGISTRY_KEY,
                    register -> register.register(
                        weatherType.getKey(), weatherType
                    )
                );
            }
        );
         */
    }
    
    private final PortalOverlay overlay = new PortalOverlay();
    
    @SubscribeEvent
    public void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll(
                Bygone.id("portal_overlay").toString(),
                (gui, graphics, partialTicks, width, height) ->
                        overlay.render(graphics, partialTicks)
        );
    }
}