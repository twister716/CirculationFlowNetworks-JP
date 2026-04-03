package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.handlers.EnergyWarningRenderingHandler;
import com.circulation.circulation_networks.utils.Packet;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class EnergyWarningRendering implements Packet<EnergyWarningRendering> {

    private int dim;
    private LongList positions;
    private transient int parsedDim;
    private transient LongList parsedPositions;

    public EnergyWarningRendering() {
    }

    public EnergyWarningRendering(int dim, LongCollection positions) {
        this.dim = dim;
        this.positions = new LongArrayList(positions);
    }

    @Override
    public EnergyWarningRendering decode(FriendlyByteBuf buf) {
        EnergyWarningRendering message = new EnergyWarningRendering();
        message.parsedDim = buf.readInt();
        int count = buf.readVarInt();
        message.parsedPositions = new LongArrayList(count);
        for (int i = 0; i < count; i++) {
            message.parsedPositions.add(buf.readLong());
        }
        return message;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(dim);
        buf.writeVarInt(positions == null ? 0 : positions.size());
        if (positions != null) {
            for (long posLong : positions) {
                buf.writeLong(posLong);
            }
        }
    }

    @Override
    public void handle(EnergyWarningRendering message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (!FMLEnvironment.dist.isClient() || Minecraft.getInstance().player == null) {
                return;
            }
            EnergyWarningRenderingHandler.INSTANCE.refreshWarnings(message.parsedDim, message.parsedPositions == null ? new LongArrayList() : message.parsedPositions);
        });
        context.setPacketHandled(true);
    }
}