package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.handlers.NodeNetworkRenderingHandler;
import com.circulation.circulation_networks.handlers.SpoceRenderingHandler;
import com.circulation.circulation_networks.utils.Packet;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RenderingClear implements Packet<RenderingClear> {

    public static final RenderingClear INSTANCE = new RenderingClear();

    public RenderingClear() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    @Override
    public IMessage onMessage(RenderingClear message, MessageContext ctx) {
        NodeNetworkRenderingHandler.INSTANCE.clearLinks();
        SpoceRenderingHandler.INSTANCE.clear();
        return null;
    }
}
