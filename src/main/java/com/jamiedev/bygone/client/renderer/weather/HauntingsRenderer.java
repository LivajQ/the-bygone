package com.jamiedev.bygone.client.renderer.weather;

import com.jamiedev.bygone.common.weather.InvertedHeightmap;
import com.jamiedev.bygone.common.weather.weather_types.HauntingsEvent;
import com.jamiedev.bygone.core.extension.LevelChunkExtension;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;

public class HauntingsRenderer extends WeatherRenderer<HauntingsEvent> {
    public HauntingsRenderer(HauntingsEvent instance) {
        super(instance);
    }

    private int time = 0;

    @Override
    public void tick(Level level) {
        if (this.instance == null || !this.instance.isActive()) return;

        time++;

        int lowestPosition = 16;
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        RandomSource randomsource = RandomSource.create((long) this.time * 312987231L);

        BlockPos blockPos = BlockPos.containing(camera.getPosition());
        int particleAmount = 50;

        int maxSpanHalf = 30;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < particleAmount; ++i) {
            int x = randomsource.nextInt((maxSpanHalf * 2) + 1) - maxSpanHalf;
            int z = randomsource.nextInt((maxSpanHalf * 2) + 1) - maxSpanHalf;

            BlockPos offsetBlockPos = blockPos.offset(x, 0, z);
            ChunkAccess chunkAccess = level.getChunk(offsetBlockPos);
            if (chunkAccess instanceof LevelChunk levelChunk) {
                InvertedHeightmap invertedHeightmap = ((LevelChunkExtension) levelChunk).bygone$getInvertedHeightmap();
                if (invertedHeightmap == null) return;

                double d0 = randomsource.nextDouble();
                double d1 = randomsource.nextDouble();
                double d2 = randomsource.nextDouble();

                mutableBlockPos.set(
                    offsetBlockPos.getX(),
                    offsetBlockPos.getY() - lowestPosition,
                    offsetBlockPos.getZ()
                );
                if (invertedHeightmap.getHeight(mutableBlockPos.getX(), mutableBlockPos.getZ()) <= mutableBlockPos.getY()) continue;

                level.addAlwaysVisibleParticle(
                    ParticleTypes.SOUL,
                    mutableBlockPos.getX() + d0,
                    mutableBlockPos.getY() - 0.1,
                    mutableBlockPos.getZ() + d1,
                    0.0F, (1f + ((float) d2)) * .1F, 0.0F
                );
            }
        }
    }

    @Override
    public void render(Level level, LightTexture lightTexture, float partialTick, double camX, double camY, double camZ) {

    }
}
