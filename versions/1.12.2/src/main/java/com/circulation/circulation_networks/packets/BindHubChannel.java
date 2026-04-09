package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.container.ContainerHub;
import com.circulation.circulation_networks.manager.HubChannelManager;
import com.circulation.circulation_networks.network.hub.HubCapabilitys;
import com.circulation.circulation_networks.utils.HubPlatformServices;
import com.circulation.circulation_networks.utils.Packet;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class BindHubChannel implements Packet<BindHubChannel> {

    private long mostSigBits;
    private long leastSigBits;

    public BindHubChannel() {
    }

    public BindHubChannel(UUID channelId) {
        this.mostSigBits = channelId.getMostSignificantBits();
        this.leastSigBits = channelId.getLeastSignificantBits();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        mostSigBits = buf.readLong();
        leastSigBits = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(mostSigBits);
        buf.writeLong(leastSigBits);
    }

    @Override
    public @Nullable IMessage onMessage(BindHubChannel message, MessageContext ctx) {
        if (!(ctx.getServerHandler().player.openContainer instanceof ContainerHub containerHub)) {
            return null;
        }
        if (!containerHub.node.hasPluginCapability(HubCapabilitys.CHANNEL_CAPABILITY)) {
            return null;
        }

        HubChannelManager.INSTANCE.bindHubToChannel(
            containerHub.node,
            ctx.getServerHandler().player.getUniqueID(),
            new UUID(message.mostSigBits, message.leastSigBits),
            HubPlatformServices.INSTANCE.hasChannelManagementOverride(ctx.getServerHandler().player)
        );
        return null;
    }
}