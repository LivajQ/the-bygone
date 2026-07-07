package com.jamiedev.bygone.forge.core.registry;

public class BGDataComponentsForge {

    /*
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(
            Registries.DATA_COMPONENT_TYPE,
            Bygone.MOD_ID
    );

    private static final Supplier<DataComponentType<MaliciousWarHornItem.WarHornData>> WAR_HORN_DATA_SUPPLIER = DATA_COMPONENTS.register(
            "war_horn_data", () -> {
                Codec<MaliciousWarHornItem.WarHornData> codec = RecordCodecBuilder.create(instance -> instance.group(
                        UUIDUtil.CODEC.listOf()
                                .fieldOf("active_vexes")
                                .forGetter(MaliciousWarHornItem.WarHornData::activeVexes),
                        Codec.INT.fieldOf("cooldown_seconds")
                                .forGetter(MaliciousWarHornItem.WarHornData::cooldownSeconds),
                        Codec.INT.fieldOf("vex_time_left").forGetter(MaliciousWarHornItem.WarHornData::vexTimeLeft)
                ).apply(instance, MaliciousWarHornItem.WarHornData::new));

                StreamCodec<RegistryFriendlyByteBuf, MaliciousWarHornItem.WarHornData> streamCodec = StreamCodec.composite(
                        UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()),
                        MaliciousWarHornItem.WarHornData::activeVexes,
                        ByteBufCodecs.VAR_INT,
                        MaliciousWarHornItem.WarHornData::cooldownSeconds,
                        ByteBufCodecs.VAR_INT,
                        MaliciousWarHornItem.WarHornData::vexTimeLeft,
                        MaliciousWarHornItem.WarHornData::new
                );

                return DataComponentType.<MaliciousWarHornItem.WarHornData>builder()
                        .persistent(codec)
                        .networkSynchronized(streamCodec)
                        .build();
            }
    );

    private static final Supplier<DataComponentType<BGDataComponentTypes.EchoGongData>> ECHO_GONG_DATA_SUPPLIER = DATA_COMPONENTS.register(
            "echo_gong_data", () -> {
                Codec<BGDataComponentTypes.EchoGongData> codec = RecordCodecBuilder.create(instance -> instance.group(
                                Codec.INT.fieldOf("charge").forGetter(BGDataComponentTypes.EchoGongData::charge))
                        .apply(instance, BGDataComponentTypes.EchoGongData::new));

                StreamCodec<RegistryFriendlyByteBuf, BGDataComponentTypes.EchoGongData> streamCodec = StreamCodec.composite(
                        ByteBufCodecs.VAR_INT,
                        BGDataComponentTypes.EchoGongData::charge,
                        BGDataComponentTypes.EchoGongData::new
                );

                return DataComponentType.<BGDataComponentTypes.EchoGongData>builder()
                        .persistent(codec)
                        .networkSynchronized(streamCodec)
                        .build();
            }
    );

    private static final Supplier<DataComponentType<GumboPotBlockEntity.GumboIngredientComponent>> GUMBO_INGREDIENT_DATA_SUPPLIER = DATA_COMPONENTS.register(
            "gumbo_ingredient_data",
            () -> DataComponentType.<GumboPotBlockEntity.GumboIngredientComponent>builder()
                    .persistent(GumboPotBlockEntity.GumboIngredientComponent.CODEC)
                    .networkSynchronized(GumboPotBlockEntity.GumboIngredientComponent.STREAM_CODEC)
                    .build()
    );

    public static void init(RegisterEvent event) {
        event.register(Registries.DATA_COMPONENT_TYPE,(helper)-> {
            DataComponentType<MaliciousWarHornItem.WarHornData> dataComponentType = WAR_HORN_DATA_SUPPLIER.get();
            BGDataComponents.WAR_HORN_DATA = (Holder<DataComponentType<MaliciousWarHornItem.WarHornData>>) (Object) BuiltInRegistries.DATA_COMPONENT_TYPE.wrapAsHolder(
                    dataComponentType);

            DataComponentType<BGDataComponentTypes.EchoGongData> echoGongDataComponentType = ECHO_GONG_DATA_SUPPLIER.get();
            BGDataComponents.ECHO_GONG_DATA = (Holder<DataComponentType<BGDataComponentTypes.EchoGongData>>) (Object) BuiltInRegistries.DATA_COMPONENT_TYPE.wrapAsHolder(
                    echoGongDataComponentType);

            DataComponentType<GumboPotBlockEntity.GumboIngredientComponent> gumboIngredientDataComponentType = GUMBO_INGREDIENT_DATA_SUPPLIER.get();

            BGDataComponents.GUMBO_INGREDIENT_DATA = (Holder<DataComponentType<GumboPotBlockEntity.GumboIngredientComponent>>) (Object) BuiltInRegistries.DATA_COMPONENT_TYPE.wrapAsHolder(
                    gumboIngredientDataComponentType);
        });
    }
    
     */
}