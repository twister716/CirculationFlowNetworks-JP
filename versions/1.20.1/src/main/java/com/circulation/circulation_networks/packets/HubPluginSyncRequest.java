package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.network.nodes.HubPluginSyncSupport;
import com.circulation.circulation_networks.tiles.nodes.HubBlockEntity;
import com.circulation.circulation_networks.utils.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class HubPluginSyncRequest implements Packet<HubPluginSyncRequest> {

    private long posLong;

    public HubPluginSyncRequest() {
    }

    public HubPluginSyncRequest(BlockPos pos) {
        this.posLong = pos.asLong();
    }

    @Override
    public HubPluginSyncRequest decode(FriendlyByteBuf buf) {
        HubPluginSyncRequest msg = new HubPluginSyncRequest();
        msg.posLong = buf.readLong();
        return msg;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(posLong);
    }

    @Override
    public void handle(HubPluginSyncRequest message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender == null) {
                return;
            }

            BlockPos pos = BlockPos.of(message.posLong);
            if (sender.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > 64 * 64) {
                return;
            }

            if (!(sender.level().getBlockEntity(pos) instanceof HubBlockEntity hub)) {
                return;
            }

            CirculationFlowNetworks.sendToPlayer(
                new HubPluginSyncData(pos, HubPluginSyncSupport.snapshotPlugins(hub.getPlugins(), hub.getPlugins().getSlots())),
                sender
            );
        });
        context.setPacketHandled(true);
    }
}
