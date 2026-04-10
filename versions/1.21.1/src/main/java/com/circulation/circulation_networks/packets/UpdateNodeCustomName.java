package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.api.node.IHubNode;
import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.container.ContainerHub;
import com.circulation.circulation_networks.utils.HubPlatformServices;
import com.circulation.circulation_networks.manager.NetworkManager;
import com.circulation.circulation_networks.manager.PocketNodeManager;
import com.circulation.circulation_networks.utils.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class UpdateNodeCustomName implements Packet<UpdateNodeCustomName> {

    public static final Type<UpdateNodeCustomName> TYPE = new Type<>(
        ResourceLocation.parse(CirculationFlowNetworks.MOD_ID + ":update_node_custom_name")
    );

    private long posLong;
    private String customName;

    public UpdateNodeCustomName() {
    }

    public UpdateNodeCustomName(BlockPos pos, String customName) {
        this.posLong = pos.asLong();
        this.customName = customName == null ? "" : customName;
    }

    @Override
    public UpdateNodeCustomName decode(RegistryFriendlyByteBuf buf) {
        UpdateNodeCustomName msg = new UpdateNodeCustomName();
        msg.posLong = buf.readLong();
        msg.customName = buf.readUtf();
        return msg;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeLong(posLong);
        buf.writeUtf(customName == null ? "" : customName);
    }

    @Override
    public void handle(UpdateNodeCustomName message, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer sender)) return;
        context.enqueueWork(() -> {
            if (!(sender.containerMenu instanceof ContainerHub containerHub)) return;
            IHubNode hubNode = containerHub.node;
            if (hubNode == null || hubNode.getWorld() == null || hubNode.getGrid() == null)
                return;
            if (!hubNode.canEditPermissions(sender.getUUID())
                && !HubPlatformServices.INSTANCE.hasChannelManagementOverride(sender)) return;

            BlockPos pos = BlockPos.of(message.posLong);
            INode node = NetworkManager.INSTANCE.getNodeFromPos(hubNode.getWorld(), pos);
            if (node == null || node.getGrid() == null) return;
            if (!Objects.equals(node.getGrid().getId(), hubNode.getGrid().getId())) return;

            node.setCustomName(message.customName);
            if (PocketNodeManager.INSTANCE.isActivePocketNode(containerHub.node.getWorld(), pos, node.getNodeType())) {
                PocketNodeManager.INSTANCE.markDirty();
            }
        });
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
