package com.jamiedev.bygone.common.block.entity;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.common.block.GumboPotBlock;
import com.jamiedev.bygone.core.init.JamiesModTag;
import com.jamiedev.bygone.core.registry.BGBlockEntities;
import com.jamiedev.bygone.core.registry.BGDataComponents;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GumboPotBlockEntity extends BlockEntity {

    public static final int BASE_SERVINGS_PER_INGREDIENT = 12;
    public static final Codec<Pair<Integer, FoodProperties>> INGREDIENT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("servings").forGetter(Pair::getFirst),
            GumboIngredientComponent.FOOD_PROPERTIES_CODEC.fieldOf("properties").forGetter(Pair::getSecond)
    ).apply(instance, Pair::new));
    public static final Codec<List<Pair<Integer, FoodProperties>>> CONTENTS_CODEC = INGREDIENT_CODEC.listOf();
    public static final String NBT_KEY_CONTENTS = "contents";
    protected final List<Pair<Integer, FoodProperties>> potContents;


    public GumboPotBlockEntity(BlockPos pos, BlockState blockState) {
        super(BGBlockEntities.GUMBO_POT.get(), pos, blockState);
        potContents = new ArrayList<>();
    }

    public ImmutableList<Pair<Integer, FoodProperties>> ingredientsView() {
        return ImmutableList.copyOf(potContents);
    }

    public void addIngredient(FoodProperties ingredient) {
        this.potContents.add(Pair.of(BASE_SERVINGS_PER_INGREDIENT, ingredient));
    }
    
    public boolean canAddIngredient(ItemStack ingredient) {
        BlockState state = this.getBlockState();
        
        if (GumboPotBlock.canFitAdditionalIngredients(state) && !ingredient.is(JamiesModTag.CANNOT_ADD_TO_GUMBO)) {
            
            FoodProperties foodProperties = ingredient.getItem().getFoodProperties(ingredient, null);
            if (foodProperties != null) {
                return true;
            }
            GumboIngredientComponent gumboIngredientComponent = BGDataComponents.GUMBO_INGREDIENT_REGISTRY.get(ingredient.getItem());
            return gumboIngredientComponent != null;
        }
        return false;
    }

    // Returns the remainder itemstack if the ingredient is accepted, empty itemstack otherwise. Does not modify the stack.
    public void addIngredient(ItemStack ingredient) {
        BlockState state = this.getBlockState();

        if (GumboPotBlock.canFitAdditionalIngredients(state) && !ingredient.is(JamiesModTag.CANNOT_ADD_TO_GUMBO)) {
            
            List<Pair<MobEffectInstance, Float>> suspiciousStewEffects = readSuspiciousStewEffects(ingredient);
            
            FoodProperties foodProperties = ingredient.getItem().getFoodProperties(ingredient, null);
            if (foodProperties == null) {
                GumboIngredientComponent gumboIngredientComponent = BGDataComponents.GUMBO_INGREDIENT_REGISTRY.get(ingredient.getItem());
                if (gumboIngredientComponent != null) {
                    foodProperties = gumboIngredientComponent.properties();
                }
            }

            if (foodProperties != null) {
                this.addIngredient(removeNegativeEffectsIf(
                        spliceStewEffects(foodProperties, suspiciousStewEffects),
                        ingredient.is(JamiesModTag.GUMBO_MAKES_SAFE) || this.getBlockState()
                                .getOptionalValue(GumboPotBlock.HEATED)
                                .orElse(false)
                ));
            }
        }
    }
    
    private static List<Pair<MobEffectInstance, Float>> readSuspiciousStewEffects(ItemStack stack) {
        List<Pair<MobEffectInstance, Float>> result = new ArrayList<>();
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("Effects", Tag.TAG_LIST)) return result;
        
        ListTag effectsList = tag.getList("Effects", Tag.TAG_COMPOUND);
        for (int i = 0; i < effectsList.size(); i++) {
            CompoundTag entry = effectsList.getCompound(i);
            MobEffect effect = entry.contains("EffectId", Tag.TAG_ANY_NUMERIC)
                    ? MobEffect.byId(entry.getInt("EffectId"))
                    : null;
            int duration = entry.contains("EffectDuration", Tag.TAG_ANY_NUMERIC)
                    ? entry.getInt("EffectDuration")
                    : 160;
            
            if (effect != null) {
                result.add(Pair.of(new MobEffectInstance(effect, duration), 1.0f));
            }
        }
        return result;
    }

    public boolean canScoopBowl(@Nullable ItemStack bowl) {
        if (bowl == null) {
            return false;
        }
        BlockState state = this.getBlockState();
        if (GumboPotBlock.canScoopBowl(state)) {
            return GumboScooping.getFilled(bowl.getItem()) != null;
        }
        return false;
    }

    // Returns the result of scooping a bowl (empty if the stack doesn't have the proper component). Does not modify the stack.
    public @NotNull ItemStack scoopBowl(@Nullable ItemStack bowl) {
        BlockState state = this.getBlockState();
        
        if ((bowl != null) && GumboPotBlock.canScoopBowl(state)) {
            
            Item filledItem = GumboScooping.getFilled(bowl.getItem());
            if (filledItem != null) {
                ItemStack filledStack = new ItemStack(filledItem);
                FoodProperties extracted = this.extractFood();
                filledStack.getOrCreateTag().put(
                        "gumbo_food_data",
                        GumboIngredientComponent.writeFoodProperties(extracted)
                );
                return filledStack;
            }
        }
        return ItemStack.EMPTY;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private FoodProperties extractFood() {
        boolean thisIsTheLastOfIt = this.getBlockState()
                .getOptionalValue(GumboPotBlock.LEVEL)
                .orElse(GumboPotBlock.MIN_LEVEL) <= (GumboPotBlock.MIN_LEVEL + 1);
        List<Pair<Integer, FoodProperties>> resultingContents = new ArrayList<>();
        List<FoodProperties> toCombine = new ArrayList<>();
        for (Pair<Integer, FoodProperties> pair : this.potContents) {
            FoodProperties currentIngredient = pair.getSecond();
            int count = pair.getFirst();
            if (count > 1 && !thisIsTheLastOfIt) {
                resultingContents.add(Pair.of(count - 1, currentIngredient));
            }
            toCombine.add(currentIngredient);
        }
        this.potContents.clear();
        this.potContents.addAll(resultingContents);
        return this.combineFoods(toCombine);
    }

    // Maybe scale down for dilution?
    //        effects.replaceAll(possibleEffect -> new FoodProperties.PossibleEffect(
    //                possibleEffect.effect(),
    //                scaleFactor * possibleEffect.probability()
    //        ));
    private FoodProperties combineFoods(List<FoodProperties> toCombine) {
        int count = toCombine.size();
        float scaleFactor = 1.0f / count;
        int totalNutrition = 0;
        float totalSaturationMod = 0;
        boolean canAlwaysEat = false;
        boolean isMeat = false;
        boolean isFast = false;
        List<Pair<MobEffectInstance, Float>> effects = new ArrayList<>();
        
        for (FoodProperties properties : toCombine) {
            totalNutrition += properties.getNutrition();
            totalSaturationMod += properties.getSaturationModifier();
            canAlwaysEat |= properties.canAlwaysEat();
            isMeat |= properties.isMeat();
            isFast |= properties.isFastFood();
            effects.addAll(properties.getEffects());
        }
        
        totalNutrition = Mth.ceil(scaleFactor * totalNutrition) + 1;
        totalSaturationMod = scaleFactor * totalSaturationMod + 0.05f;
        
        FoodProperties.Builder builder = new FoodProperties.Builder()
                .nutrition(totalNutrition)
                .saturationMod(totalSaturationMod);
        if (canAlwaysEat) builder.alwaysEat();
        if (isMeat) builder.meat();
        if (isFast) builder.fast();
        for (Pair<MobEffectInstance, Float> pair : effects) {
            builder.effect(pair.getFirst(), scaleFactor * pair.getSecond());
        }
        
        return builder.build();
    }

    protected FoodProperties removeNegativeEffectsIf(FoodProperties properties, boolean removeIfTrue) {
        return removeIfTrue ? removeNegativeEffects(properties) : properties;
    }
    
    protected FoodProperties removeNegativeEffects(FoodProperties properties) {
        FoodProperties.Builder builder = new FoodProperties.Builder()
                .nutrition(properties.getNutrition())
                .saturationMod(properties.getSaturationModifier());
        if (properties.canAlwaysEat()) builder.alwaysEat();
        if (properties.isMeat()) builder.meat();
        if (properties.isFastFood()) builder.fast();
        
        for (Pair<MobEffectInstance, Float> pair : properties.getEffects()) {
            if (pair.getFirst().getEffect().isBeneficial()) {
                builder.effect(pair.getFirst(), pair.getSecond());
            }
        }
        
        return builder.build();
    }
    
    protected FoodProperties spliceStewEffects(FoodProperties properties, List<Pair<MobEffectInstance, Float>> stewEffects) {
        if (stewEffects.isEmpty()) return properties;
        
        FoodProperties.Builder builder = new FoodProperties.Builder()
                .nutrition(properties.getNutrition())
                .saturationMod(properties.getSaturationModifier());
        if (properties.canAlwaysEat()) builder.alwaysEat();
        if (properties.isMeat()) builder.meat();
        if (properties.isFastFood()) builder.fast();
        
        for (Pair<MobEffectInstance, Float> pair : properties.getEffects()) {
            builder.effect(pair.getFirst(), pair.getSecond());
        }
        for (Pair<MobEffectInstance, Float> pair : stewEffects) {
            builder.effect(pair.getFirst(), pair.getSecond());
        }
        
        return builder.build();
    }
    
    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        
        this.potContents.clear();
        Tag contents = nbt.get(NBT_KEY_CONTENTS);
        this.potContents.addAll(
                CONTENTS_CODEC.decode(NbtOps.INSTANCE, contents)
                        .getOrThrow(false, error -> Bygone.LOGGER.warn(
                                "Warning! Gumbo pot block entity encountered an error while loading: " + error))
                        .getFirst()
        );
    }
    
    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        
        CONTENTS_CODEC.encodeStart(NbtOps.INSTANCE, this.potContents)
                .resultOrPartial(error -> System.out.println(
                        "Warning! Gumbo pot block entity encountered an error while saving: " + error))
                .ifPresent(contents -> nbt.put(NBT_KEY_CONTENTS, contents));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag nbtCompound = super.getUpdateTag();
        CONTENTS_CODEC.encodeStart(NbtOps.INSTANCE, this.potContents)
                .resultOrPartial(error -> System.out.println(
                        "Warning! Gumbo pot block entity encountered an error while constructing an update tag: " + error))
                .ifPresent(contents -> nbtCompound.put(NBT_KEY_CONTENTS, contents));
        
        return nbtCompound;
    }

    public static class GumboScooping {

        protected static final BiMap<Item, Item> SCOOPING_MAP = HashBiMap.create();

        public static @Nullable Item getFilled(Item in) {
            return SCOOPING_MAP.get(in);
        }

        public static @Nullable Item getEmptied(Item out) {
            return SCOOPING_MAP.inverse().get(out);
        }


        public static @Nullable Item setFilled(Item in, Item out) {
            return SCOOPING_MAP.put(in, out);
        }
    }
    
    public record GumboIngredientComponent(FoodProperties properties) {
        
        private static final Codec<MobEffectInstance> MOB_EFFECT_INSTANCE_CODEC = Codec.PASSTHROUGH.comapFlatMap(
                dynamic -> {
                    Tag tag = dynamic.convert(NbtOps.INSTANCE).getValue();
                    if (!(tag instanceof CompoundTag compoundTag)) {
                        return DataResult.error(() -> "Not a compound tag");
                    }
                    MobEffectInstance instance = MobEffectInstance.load(compoundTag);
                    return instance != null
                            ? DataResult.success(instance)
                            : DataResult.error(() -> "Could not load MobEffectInstance");
                },
                instance -> new Dynamic<>(NbtOps.INSTANCE, instance.save(new CompoundTag()))
        );
        
        private static final Codec<Pair<MobEffectInstance, Float>> EFFECT_ENTRY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                MOB_EFFECT_INSTANCE_CODEC.fieldOf("effect").forGetter(Pair::getFirst),
                Codec.FLOAT.fieldOf("probability").forGetter(Pair::getSecond)
        ).apply(instance, Pair::new));
        
        public static final Codec<FoodProperties> FOOD_PROPERTIES_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("nutrition").forGetter(FoodProperties::getNutrition),
                Codec.FLOAT.fieldOf("saturation_modifier").forGetter(FoodProperties::getSaturationModifier),
                Codec.BOOL.optionalFieldOf("meat", false).forGetter(FoodProperties::isMeat),
                Codec.BOOL.optionalFieldOf("always_eat", false).forGetter(FoodProperties::canAlwaysEat),
                Codec.BOOL.optionalFieldOf("fast", false).forGetter(FoodProperties::isFastFood),
                EFFECT_ENTRY_CODEC.listOf().fieldOf("effects").forGetter(FoodProperties::getEffects)
        ).apply(instance, (nutrition, satMod, meat, alwaysEat, fast, effects) -> {
            FoodProperties.Builder builder = new FoodProperties.Builder()
                    .nutrition(nutrition)
                    .saturationMod(satMod);
            if (meat) builder.meat();
            if (alwaysEat) builder.alwaysEat();
            if (fast) builder.fast();
            effects.forEach(pair -> builder.effect(pair.getFirst(), pair.getSecond()));
            return builder.build();
        }));
        
        public static final Codec<GumboIngredientComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                FOOD_PROPERTIES_CODEC.fieldOf("food_properties").forGetter(GumboIngredientComponent::properties)
        ).apply(instance, GumboIngredientComponent::new));
      
        public static GumboIngredientComponent read(CompoundTag tag) {
            return new GumboIngredientComponent(readFoodProperties(tag));
        }
        
        public static CompoundTag write(GumboIngredientComponent component) {
            return writeFoodProperties(component.properties());
        }
        
        public static FoodProperties readFoodProperties(CompoundTag tag) {
            FoodProperties.Builder builder = new FoodProperties.Builder()
                    .nutrition(tag.getInt("nutrition"))
                    .saturationMod(tag.getFloat("saturation_modifier"));
            
            if (tag.getBoolean("meat")) builder.meat();
            if (tag.getBoolean("always_eat")) builder.alwaysEat();
            if (tag.getBoolean("fast")) builder.fast();
            
            ListTag effectsList = tag.getList("effects", Tag.TAG_COMPOUND);
            for (int i = 0; i < effectsList.size(); i++) {
                CompoundTag entry = effectsList.getCompound(i);
                MobEffectInstance instance = MobEffectInstance.load(entry.getCompound("effect"));
                float probability = entry.getFloat("probability");
                if (instance != null) builder.effect(instance, probability);
            }
            
            return builder.build();
        }
        
        static CompoundTag writeFoodProperties(FoodProperties properties) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("nutrition", properties.getNutrition());
            tag.putFloat("saturation_modifier", properties.getSaturationModifier());
            tag.putBoolean("meat", properties.isMeat());
            tag.putBoolean("always_eat", properties.canAlwaysEat());
            tag.putBoolean("fast", properties.isFastFood());
            
            ListTag effectsList = new ListTag();
            for (Pair<MobEffectInstance, Float> pair : properties.getEffects()) {
                CompoundTag entry = new CompoundTag();
                entry.put("effect", pair.getFirst().save(new CompoundTag()));
                entry.putFloat("probability", pair.getSecond());
                effectsList.add(entry);
            }
            tag.put("effects", effectsList);
            
            return tag;
        }
    }
}
