package com.jamiedev.bygone.common.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.FastColor;

public class BygoneColorParticleOption implements ParticleOptions {
    
    public static MapCodec<BygoneColorParticleOption> codec(ParticleType<BygoneColorParticleOption> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.FLOAT.fieldOf("r").forGetter(o -> o.r),
                Codec.FLOAT.fieldOf("g").forGetter(o -> o.g),
                Codec.FLOAT.fieldOf("b").forGetter(o -> o.b)
        ).apply(instance, (r, g, b) -> new BygoneColorParticleOption(type, r, g, b)));
    }
    
    public static final Deserializer<BygoneColorParticleOption> DESERIALIZER = new Deserializer<>() {
        @Override
        public BygoneColorParticleOption fromCommand(ParticleType<BygoneColorParticleOption> type, StringReader reader) throws CommandSyntaxException
        {
            reader.expect(' ');
            float r = (float) reader.readDouble();
            reader.expect(' ');
            float g = (float) reader.readDouble();
            reader.expect(' ');
            float b = (float) reader.readDouble();
            return new BygoneColorParticleOption(type, r, g, b);
        }
        
        @Override
        public BygoneColorParticleOption fromNetwork(ParticleType<BygoneColorParticleOption> type, FriendlyByteBuf buf) {
            return new BygoneColorParticleOption(type, buf.readFloat(), buf.readFloat(), buf.readFloat());
        }
    };
    
    private final ParticleType<BygoneColorParticleOption> type;
    private final float r, g, b;
    
    public BygoneColorParticleOption(ParticleType<BygoneColorParticleOption> type, float r, float g, float b) {
        this.type = type;
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    @Override
    public ParticleType<?> getType() { return type; }
    
    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(r);
        buf.writeFloat(g);
        buf.writeFloat(b);
    }
    
    @Override
    public String writeToString() {
        return String.format("%s %.2f %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(type), r, g, b);
    }
    
    public float getR() { return r; }
    public float getG() { return g; }
    public float getB() { return b; }
    
    public static BygoneColorParticleOption create(ParticleType<BygoneColorParticleOption> type, int argb) {
        return new BygoneColorParticleOption(type,
                FastColor.ARGB32.red(argb) / 255f,
                FastColor.ARGB32.green(argb) / 255f,
                FastColor.ARGB32.blue(argb) / 255f);
    }
    
    public static BygoneColorParticleOption create(ParticleType<BygoneColorParticleOption> type, float r, float g, float b) {
        return new BygoneColorParticleOption(type, r, g, b);
    }
}