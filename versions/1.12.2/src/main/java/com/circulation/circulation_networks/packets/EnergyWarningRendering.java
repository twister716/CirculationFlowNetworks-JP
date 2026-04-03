package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.handlers.EnergyWarningRenderingHandler;
import com.circulation.circulation_networks.utils.Packet;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

public final class EnergyWarningRendering implements Packet<EnergyWarningRendering> {

    private int dim;
    private LongCollection positions;
    private transient int parsedDim;
    private transient LongCollection parsedPositions;

    public EnergyWarningRendering() {
    }

    public EnergyWarningRendering(int dim, LongCollection positions) {
        this.dim = dim;
        this.positions = positions;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        parsedDim = buf.readInt();
        int count = buf.readInt();
        parsedPositions = new LongArrayList(count);
        for (int i = 0; i < count; i++) {
            parsedPositions.add(buf.readLong());
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dim);
        buf.writeInt(positions == null ? 0 : positions.size());
        if (positions != null) {
            for (long posLong : positions) {
                buf.writeLong(posLong);
            }
        }
    }

    @Override
    public @Nullable IMessage onMessage(EnergyWarningRendering message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> EnergyWarningRenderingHandler.INSTANCE.refreshWarnings(
            message.parsedDim,
            message.parsedPositions == null ? new LongArrayList() : message.parsedPositions
        ));
        return null;
    }
}