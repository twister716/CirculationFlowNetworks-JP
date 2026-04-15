package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.network.nodes.HubPluginSyncSupport;
import com.circulation.circulation_networks.tiles.nodes.TileEntityHub;
import com.circulation.circulation_networks.utils.Packet;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

public final class HubPluginSyncRequest implements Packet<HubPluginSyncRequest> {

    private long posLong;

    public HubPluginSyncRequest() {
    }

    public HubPluginSyncRequest(BlockPos pos) {
        this.posLong = pos.toLong();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        posLong = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(posLong);
    }

    @Override
    public @Nullable IMessage onMessage(HubPluginSyncRequest message, MessageContext ctx) {
        EntityPlayerMP sender = ctx.getServerHandler().player;
        BlockPos pos = BlockPos.fromLong(message.posLong);
        if (sender.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > 64 * 64) {
            return null;
        }

        if (!(sender.world.getTileEntity(pos) instanceof TileEntityHub hub)) {
            return null;
        }

        CirculationFlowNetworks.sendToPlayer(
            new HubPluginSyncData(pos, HubPluginSyncSupport.snapshotPlugins(hub.getPlugins(), hub.getPlugins().getSlots())),
            sender
        );
        return null;
    }
}
