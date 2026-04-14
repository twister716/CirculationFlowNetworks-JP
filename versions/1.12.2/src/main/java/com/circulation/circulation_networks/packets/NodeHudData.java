package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.handlers.NodeHudRenderingHandler;
import com.circulation.circulation_networks.utils.Packet;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

public final class NodeHudData implements Packet<NodeHudData> {

    private long posLong;
    private String displayName;
    private String input;
    private String output;
    private String interactionTimeMicros;
    private int nodeCount;

    public NodeHudData() {
    }

    public NodeHudData(BlockPos pos, String displayName, String input, String output, String interactionTimeMicros, int nodeCount) {
        this.posLong = pos.toLong();
        this.displayName = displayName;
        this.input = input;
        this.output = output;
        this.interactionTimeMicros = interactionTimeMicros;
        this.nodeCount = nodeCount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        posLong = buf.readLong();
        displayName = ByteBufUtils.readUTF8String(buf);
        input = ByteBufUtils.readUTF8String(buf);
        output = ByteBufUtils.readUTF8String(buf);
        interactionTimeMicros = ByteBufUtils.readUTF8String(buf);
        nodeCount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(posLong);
        ByteBufUtils.writeUTF8String(buf, displayName);
        ByteBufUtils.writeUTF8String(buf, input);
        ByteBufUtils.writeUTF8String(buf, output);
        ByteBufUtils.writeUTF8String(buf, interactionTimeMicros);
        buf.writeInt(nodeCount);
    }

    @Override
    public @Nullable IMessage onMessage(NodeHudData message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> NodeHudRenderingHandler.INSTANCE.updateData(
            message.posLong,
            message.displayName,
            message.input,
            message.output,
            message.interactionTimeMicros,
            message.nodeCount
        ));
        return null;
    }
}
