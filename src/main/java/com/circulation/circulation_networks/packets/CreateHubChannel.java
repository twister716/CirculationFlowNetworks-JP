package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.api.hub.PermissionMode;
import com.circulation.circulation_networks.container.ContainerHub;
import com.circulation.circulation_networks.manager.HubChannelManager;
import com.circulation.circulation_networks.network.hub.HubCapabilitys;
import com.circulation.circulation_networks.network.nodes.HubNode;
import com.circulation.circulation_networks.utils.HubPlatformServices;
import com.circulation.circulation_networks.utils.Packet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public final class CreateHubChannel implements Packet<CreateHubChannel> {

    public static final Type<CreateHubChannel> TYPE = new Type<>(
        Identifier.parse(CirculationFlowNetworks.MOD_ID + ":create_hub_channel")
    );

    private String name;
    private byte permissionModeId;

    public CreateHubChannel() {
    }

    public CreateHubChannel(String name, PermissionMode permissionMode) {
        this.name = name == null ? "" : name;
        this.permissionModeId = (byte) (permissionMode != null ? permissionMode.getId() : PermissionMode.PRIVATE.getId());
    }

    @Override
    public CreateHubChannel decode(@NonNull RegistryFriendlyByteBuf buf) {
        CreateHubChannel msg = new CreateHubChannel();
        msg.name = buf.readUtf();
        msg.permissionModeId = buf.readByte();
        return msg;
    }

    @Override
    public void encode(@NonNull RegistryFriendlyByteBuf buf) {
        buf.writeUtf(name == null ? "" : name);
        buf.writeByte(permissionModeId);
    }

    @Override
    public void handle(@NonNull CreateHubChannel message, @NonNull IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer sender)) return;
        context.enqueueWork(() -> {
            if (!(sender.containerMenu instanceof ContainerHub containerHub)) return;
            if (!containerHub.node.hasPluginCapability(HubCapabilitys.CHANNEL_CAPABILITY)) return;
            if (!containerHub.node.getChannelId().equals(HubNode.EMPTY)
                && !containerHub.node.canEditPermissions(sender.getUUID())
                && !HubPlatformServices.INSTANCE.hasChannelManagementOverride(sender)) return;

            HubChannelManager.INSTANCE.createChannel(
                containerHub.node,
                sender.getUUID(),
                message.name,
                PermissionMode.fromId(message.permissionModeId)
            );
        });
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
