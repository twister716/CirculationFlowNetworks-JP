package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.handlers.EnergyWarningRenderingHandler;
import com.circulation.circulation_networks.utils.Packet;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public final class EnergyWarningRendering implements Packet<EnergyWarningRendering> {

    public static final Type<EnergyWarningRendering> TYPE = new Type<>(
        Identifier.parse(CirculationFlowNetworks.MOD_ID + ":energy_warning_rendering")
    );

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
    public EnergyWarningRendering decode(@NonNull RegistryFriendlyByteBuf buf) {
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
    public void encode(@NonNull RegistryFriendlyByteBuf buf) {
        buf.writeInt(dim);
        buf.writeVarInt(positions == null ? 0 : positions.size());
        if (positions != null) {
            for (long posLong : positions) {
                buf.writeLong(posLong);
            }
        }
    }

    @Override
    public void handle(@NonNull EnergyWarningRendering message, @NonNull IPayloadContext context) {
        context.enqueueWork(() -> EnergyWarningRenderingHandler.INSTANCE.refreshWarnings(
            message.parsedDim,
            message.parsedPositions == null ? new LongArrayList() : message.parsedPositions
        ));
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
