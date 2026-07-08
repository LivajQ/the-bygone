package com.jamiedev.bygone.common.worldgen;

import com.jamiedev.bygone.common.block.BygonePortalBlock;
import com.jamiedev.bygone.core.registry.BGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.Optional;
import java.util.function.Function;

public class BygoneTeleporter implements ITeleporter {
    
    private final BlockPos target;
    private final boolean valid;
    
    public BygoneTeleporter(Entity entity, BlockPos entrancePos, ServerLevel destLevel) {
        WorldBorder border = destLevel.getWorldBorder();
        
        double scale = DimensionType.getTeleportationScale(
                entity.level().dimensionType(),
                destLevel.dimensionType()
        );
        
        BlockPos scaledPos = new BlockPos(
                (int) Math.max(border.getMinX(), Math.min(border.getMaxX(), entity.getX() * scale)),
                (int) entity.getY(),
                (int) Math.max(border.getMinZ(), Math.min(border.getMaxZ(), entity.getZ() * scale))
        );
        
        Direction.Axis axis = entity.level().getBlockState(entrancePos)
                .getOptionalValue(BygonePortalBlock.AXIS)
                .orElse(Direction.Axis.X);
        
        Optional<BlockPos> result = getOrMakePortal(destLevel, scaledPos, axis);
        this.target = result.orElse(null);
        this.valid = result.isPresent();
    }
    
    public boolean isValid() {
        return valid;
    }
    
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destLevel, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        if (target == null) return null;
        return new PortalInfo(
                Vec3.atCenterOf(target),
                Vec3.ZERO,
                entity.getYRot(),
                entity.getXRot()
        );
    }
    
    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        return repositionEntity.apply(false);
    }
    
    public static Optional<BlockPos> getExistingPortal(ServerLevel level, BlockPos pos) {
        int maxHeight = level.getMaxBuildHeight();
        int minHeight = level.getMinBuildHeight();
        WorldBorder border = level.getWorldBorder();
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        
        for (int x = pos.getX() - 16; x <= pos.getX() + 16; x++) {
            for (int z = pos.getZ() - 16; z <= pos.getZ() + 16; z++) {
                for (int y = minHeight; y <= maxHeight; y++) {
                    blockPos.set(x, y, z);
                    BlockState state = level.getBlockState(blockPos);
                    
                    if (border.isWithinBounds(blockPos)
                            && state.is(BGBlocks.BYGONE_PORTAL.get())
                            && state.getValue(BygonePortalBlock.CENTER)) {
                        return Optional.of(blockPos.immutable());
                    }
                }
            }
        }
        return Optional.empty();
    }
    
    public static Optional<BlockPos> makePortal(ServerLevel level, BlockPos pos, Direction.Axis axis) {
        int maxHeight = level.getMaxBuildHeight();
        int minHeight = level.getMinBuildHeight();
        WorldBorder border = level.getWorldBorder();
        
        for (BlockPos.MutableBlockPos cursor : BlockPos.spiralAround(pos, 32, Direction.EAST, Direction.SOUTH)) {
            
            int surface = level.getHeight(Heightmap.Types.WORLD_SURFACE, cursor.getX(), cursor.getZ());
            int from = Math.max(minHeight, surface - 10);
            int to = Math.min(maxHeight, surface + 10);
            
            if (from < to) {
                for (int y = from; y < to; y++) {
                    cursor.setY(y);
                    
                    if (border.isWithinBounds(cursor)
                            && BygonePortalBlock.placePortal(level, cursor, axis, 2)) {
                        return Optional.of(cursor.relative(Direction.UP).immutable());
                    }
                }
            }
            
            cursor.setY(pos.getY());
            for (int y = pos.getY(); y < maxHeight; y++) {
                cursor.setY(y);
                if (border.isWithinBounds(cursor)
                        && BygonePortalBlock.placePortal(level, cursor, axis, 2)) {
                    return Optional.of(cursor.relative(Direction.UP).immutable());
                }
            }
            
            cursor.setY(pos.getY());
            for (int y = pos.getY(); y > minHeight; y--) {
                cursor.setY(y);
                if (border.isWithinBounds(cursor)
                        && BygonePortalBlock.placePortal(level, cursor, axis, 2)) {
                    return Optional.of(cursor.relative(Direction.UP).immutable());
                }
            }
        }
        
        return Optional.empty();
    }
    
    public static Optional<BlockPos> getOrMakePortal(ServerLevel level, BlockPos pos, Direction.Axis axis) {
        return getExistingPortal(level, pos)
                .or(() -> makePortal(level, pos, axis));
    }
    
    public BlockPos getTarget() {
        return target;
    }
}
