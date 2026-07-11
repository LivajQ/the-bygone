package com.jamiedev.bygone.client.particles;

import com.jamiedev.bygone.core.registry.BGParticleTypes;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class LithoParticleOptions //extends DustParticleOptionsBase
{
   /*
    public static final Vector3f PLASM_PARTICLE_COLOR = Vec3.fromRGB24(14151396).toVector3f();
    public static final LithoParticleOptions PLASM = new LithoParticleOptions(PLASM_PARTICLE_COLOR, 1.0F);
    public static final DustParticleOptions PLASM_DUST = new DustParticleOptions(Vec3.fromRGB24(14151396).toVector3f(), 1.0F);
    
    public static final Codec<LithoParticleOptions> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ExtraCodecs.VECTOR3F.fieldOf("color").forGetter((options) -> options.color),
            Codec.FLOAT.fieldOf("scale").forGetter((options) -> options.scale)
    ).apply(instance, LithoParticleOptions::new));
    
    public static final ParticleOptions.Deserializer<LithoParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<LithoParticleOptions>() {
        public LithoParticleOptions fromCommand(ParticleType<LithoParticleOptions> type, StringReader reader) throws CommandSyntaxException
        {
            Vector3f vector3f = DustParticleOptionsBase.readVector3f(reader);
            reader.expect(' ');
            float scale = reader.readFloat();
            return new LithoParticleOptions(vector3f, scale);
        }
        
        public LithoParticleOptions fromNetwork(ParticleType<LithoParticleOptions> type, FriendlyByteBuf buf) {
            return new LithoParticleOptions(DustParticleOptionsBase.readVector3f(buf), buf.readFloat());
        }
    };
    
    public LithoParticleOptions(Vector3f color, float scale) {
        super(color, scale);
    }
    
    @Override
    public ParticleType<?> getType()
    {
        return null;
    }
    */
}

