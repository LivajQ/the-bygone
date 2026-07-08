package com.jamiedev.bygone.common.block.entity;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.core.registry.BGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.ticks.ContainerSingleItem;

import javax.annotation.Nullable;
import java.util.List;

public class AmphoraBlockEntity extends RandomizableContainerBlockEntity {
    public static final String TAG_SHERDS = "sherds";
    public static final String TAG_ITEM = "item";
    public static final int EVENT_POT_WOBBLES = 1;
    public long wobbleStartedAtTick;
    @Nullable
    public AmphoraBlockEntity.WobbleStyle lastWobbleStyle;
    @Nullable
    protected ResourceKey<LootTable> lootTable;
    protected long lootTableSeed;
    private DecoratedPotBlockEntity.Decorations decorations;
    private ItemStack item;

    public AmphoraBlockEntity(BlockPos pos, BlockState state) {
        super(BGBlockEntities.AMPHORA.get(), pos, state);
        this.item = ItemStack.EMPTY;
        this.decorations = DecoratedPotBlockEntity.Decorations.EMPTY;
    }
    
    public static ItemStack createDecoratedPotItem(DecoratedPotBlockEntity.Decorations decorations) {
        ItemStack itemstack = Items.DECORATED_POT.getDefaultInstance();
        CompoundTag tag = decorations.save(new CompoundTag());
        BlockItem.setBlockEntityData(itemstack, BlockEntityType.DECORATED_POT, tag);
        return itemstack;
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        this.decorations.save(tag);
        if (!this.trySaveLootTable(tag) && !this.item.isEmpty()) {
            tag.put("item", this.item.save(new CompoundTag()));
        }
    }
    
    @Override
    protected Component getDefaultName() {
        return Component.translatable("container." + Bygone.MOD_ID + ".amphora");
    }
    
    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory)
    {
        return null;
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.decorations = DecoratedPotBlockEntity.Decorations.load(tag);
        if (!this.tryLoadLootTable(tag)) {
            if (tag.contains("item", 10)) {
                this.item = ItemStack.of(tag.getCompound("item"));
            } else {
                this.item = ItemStack.EMPTY;
            }
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public Direction getDirection() {
        return this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }
    
    public DecoratedPotBlockEntity.Decorations getDecorations() {
        return this.decorations;
    }
    
    public void setFromItem(ItemStack item) {
        this.decorations = DecoratedPotBlockEntity.Decorations.load(BlockItem.getBlockEntityData(item));
    }
    
    public ItemStack getPotAsItem() {
        ItemStack itemstack = Items.DECORATED_POT.getDefaultInstance();
        CompoundTag tag = this.decorations.save(new CompoundTag());
        if (!this.item.isEmpty()) {
            tag.put("item", this.item.save(new CompoundTag()));
        }
        BlockItem.setBlockEntityData(itemstack, BlockEntityType.DECORATED_POT, tag);
        return itemstack;
    }

    @Nullable
    public ResourceKey<LootTable> getLootTable() {
        return this.lootTable;
    }

    public void setLootTable(@Nullable ResourceKey<LootTable> lootTable) {
        this.lootTable = lootTable;
    }

    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    public void setLootTableSeed(long seed) {
        this.lootTableSeed = seed;
    }

    public ItemStack getTheItem() {
        this.unpackLootTable(null);
        return this.item;
    }

    public void setTheItem(ItemStack item) {
        this.unpackLootTable(null);
        this.item = item;
    }

    public ItemStack splitTheItem(int amount) {
        this.unpackLootTable(null);
        ItemStack itemstack = this.item.split(amount);
        if (this.item.isEmpty()) {
            this.item = ItemStack.EMPTY;
        }

        return itemstack;
    }
    
    @Override
    protected NonNullList<ItemStack> getItems() {
        return NonNullList.of(ItemStack.EMPTY, this.item);
    }
    
    @Override
    protected void setItems(NonNullList<ItemStack> itemStacks) {
        this.item = itemStacks.isEmpty() ? ItemStack.EMPTY : itemStacks.get(0);
    }
    
    @Override
    public int getContainerSize() {
        return 1;
    }

    public BlockEntity getContainerBlockEntity() {
        return this;
    }

    public void wobble(AmphoraBlockEntity.WobbleStyle style) {
        if (this.level != null && !this.level.isClientSide()) {
            this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), 1, style.ordinal());
        }

    }

    public boolean triggerEvent(int id, int type) {
        if (this.level != null && id == 1 && type >= 0 && type < AmphoraBlockEntity.WobbleStyle.values().length) {
            this.wobbleStartedAtTick = this.level.getGameTime();
            this.lastWobbleStyle = AmphoraBlockEntity.WobbleStyle.values()[type];
            return true;
        } else {
            return super.triggerEvent(id, type);
        }
    }

    public enum WobbleStyle {
        POSITIVE(7),
        NEGATIVE(10);

        public final int duration;

        WobbleStyle(int duration) {
            this.duration = duration;
        }
    }
}
