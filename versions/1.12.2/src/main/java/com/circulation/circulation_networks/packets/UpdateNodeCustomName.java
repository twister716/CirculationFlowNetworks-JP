package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.api.API;
import com.circulation.circulation_networks.api.node.IHubNode;
import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.container.ContainerHub;
import com.circulation.circulation_networks.manager.PocketNodeManager;
import com.circulation.circulation_networks.utils.HubPlatformServices;
import com.circulation.circulation_networks.utils.Packet;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class UpdateNodeCustomName implements Packet<UpdateNodeCustomName> {

    private long posLong;
    private String customName;

    public UpdateNodeCustomName() {
    }

    public UpdateNodeCustomName(BlockPos pos, String customName) {
        this.posLong = pos.toLong();
        this.customName = customName == null ? "" : customName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        posLong = buf.readLong();
        customName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(posLong);
        ByteBufUtils.writeUTF8String(buf, customName == null ? "" : customName);
    }

    @Override
    public @Nullable IMessage onMessage(UpdateNodeCustomName message, MessageContext ctx) {
        if (!(ctx.getServerHandler().player.openContainer instanceof ContainerHub containerHub)) {
            return null;
        }
        IHubNode hubNode = containerHub.node;
        if (hubNode == null || hubNode.getWorld() == null || hubNode.getGrid() == null) {
            return null;
        }
        if (!hubNode.canEditPermissions(ctx.getServerHandler().player.getUniqueID())
            && !HubPlatformServices.INSTANCE.hasChannelManagementOverride(ctx.getServerHandler().player)) {
            return null;
        }

        BlockPos pos = BlockPos.fromLong(message.posLong);
        INode node = API.getNodeAt(hubNode.getWorld(), pos);
        if (node == null || node.getGrid() == null) {
            return null;
        }
        if (!Objects.equals(node.getGrid().getId(), hubNode.getGrid().getId())) {
            return null;
        }

        node.setCustomName(message.customName);
        if (PocketNodeManager.INSTANCE.isActivePocketNode(containerHub.node.getWorld(), pos, node.getNodeType())) {
            PocketNodeManager.INSTANCE.markDirty();
        }
        return null;
    }
}
