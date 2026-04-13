package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.container.ContainerCirculationShielder;
import com.circulation.circulation_networks.tiles.CirculationShielderBlockEntity;
import com.circulation.circulation_networks.utils.Packet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class CirculationShielderSyncPacket implements Packet<CirculationShielderSyncPacket> {
    public static final Type<CirculationShielderSyncPacket> TYPE = new Type<>(
        ResourceLocation.parse(CirculationFlowNetworks.MOD_ID + ":circulation_shielder_sync")
    );

    private int scope;
    private boolean redstoneMode;

    public CirculationShielderSyncPacket() {
    }

    public CirculationShielderSyncPacket(CirculationShielderBlockEntity te) {
        this.scope = te.getScope();
        this.redstoneMode = te.getRedstoneMode();
    }

    private static int clampScope(int value, int maxScope) {
        return Math.clamp(value, 0, Math.max(0, maxScope));
    }

    @Override
    public CirculationShielderSyncPacket decode(RegistryFriendlyByteBuf buf) {
        CirculationShielderSyncPacket msg = new CirculationShielderSyncPacket();
        msg.scope = buf.readInt();
        msg.redstoneMode = buf.readBoolean();
        return msg;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.scope);
        buf.writeBoolean(this.redstoneMode);
    }

    @Override
    public void handle(CirculationShielderSyncPacket message, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer sender)) return;
        context.enqueueWork(() -> {
            if (sender.containerMenu instanceof ContainerCirculationShielder c) {
                var te = c.te;
                if (te != null) {
                    te.setScope(clampScope(message.scope, te.getMaxScope()));
                    te.setRedstoneMode(message.redstoneMode);
                }
            }
        });
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
