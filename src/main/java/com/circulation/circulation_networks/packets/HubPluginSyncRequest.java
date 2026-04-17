package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.network.nodes.HubPluginSyncSupport;
import com.circulation.circulation_networks.tiles.nodes.BlockEntityHub;
import com.circulation.circulation_networks.utils.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public final class HubPluginSyncRequest implements Packet<HubPluginSyncRequest> {

    public static final Type<HubPluginSyncRequest> TYPE = new Type<>(
        Identifier.parse(CirculationFlowNetworks.MOD_ID + ":hub_plugin_sync_request")
    );

    private long posLong;

    public HubPluginSyncRequest() {
    }

    public HubPluginSyncRequest(BlockPos pos) {
        this.posLong = pos.asLong();
    }

    @Override
    public HubPluginSyncRequest decode(@NonNull RegistryFriendlyByteBuf buf) {
        HubPluginSyncRequest msg = new HubPluginSyncRequest();
        msg.posLong = buf.readLong();
        return msg;
    }

    @Override
    public void encode(@NonNull RegistryFriendlyByteBuf buf) {
        buf.writeLong(posLong);
    }

    @Override
    public void handle(@NonNull HubPluginSyncRequest message, @NonNull IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer sender)) {
            return;
        }

        context.enqueueWork(() -> {
            BlockPos pos = BlockPos.of(message.posLong);
            if (sender.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > 64 * 64) {
                return;
            }

            if (!(sender.level().getBlockEntity(pos) instanceof BlockEntityHub hub)) {
                return;
            }

            CirculationFlowNetworks.sendToPlayer(
                new HubPluginSyncData(pos, HubPluginSyncSupport.snapshotPlugins(hub.getPlugins(), hub.getPlugins().getSlots())),
                sender
            );
        });
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
