package com.jamiedev.bygone.forge.core.network;

import com.jamiedev.bygone.core.network.BygonePacket;
import com.jamiedev.bygone.core.network.BygonePackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BygoneForgePayloadPacket {
    private final BygonePacket payload;
    
    public BygoneForgePayloadPacket(BygonePacket payload) {
        this.payload = payload;
    }
    
    public static void encode(BygoneForgePayloadPacket msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.payload.id());
        msg.payload.write(buf);
    }
    
    public static BygoneForgePayloadPacket decode(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        BygonePacket packet = BygonePackets.create(id, buf);
        return new BygoneForgePayloadPacket(packet);
    }
    
    public static void handle(BygoneForgePayloadPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            msg.payload.handle(player);
        });
        ctx.get().setPacketHandled(true);
    }
}
