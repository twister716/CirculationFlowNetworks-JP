package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.CFNConfig;
import com.circulation.circulation_networks.container.ContainerCirculationShielder;
import com.circulation.circulation_networks.tiles.TileEntityCirculationShielder;
import com.circulation.circulation_networks.utils.Packet;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

public class CirculationShielderSyncPacket implements Packet<CirculationShielderSyncPacket> {
    private int scope;
    private boolean redstoneMode;

    public CirculationShielderSyncPacket() {
    }

    public CirculationShielderSyncPacket(TileEntityCirculationShielder te) {
        this.scope = te.getScope();
        this.redstoneMode = te.getRedstoneMode();
    }

    private static int clampScope(int value) {
        int maxScope = Math.max(0, CFNConfig.SHIELDER.maxScope);
        return Math.max(0, Math.min(maxScope, value));
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.scope = buf.readInt();
        this.redstoneMode = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.scope);
        buf.writeBoolean(this.redstoneMode);
    }

    @Override
    public @Nullable IMessage onMessage(CirculationShielderSyncPacket message, MessageContext ctx) {
        if (ctx.getServerHandler().player.openContainer instanceof ContainerCirculationShielder c) {
            var te = c.te;
            if (te != null) {
                te.setScope(clampScope(message.scope));
                te.setRedstoneMode(message.redstoneMode);
                te.markDirty();
            }
        }
        return null;
    }
}
