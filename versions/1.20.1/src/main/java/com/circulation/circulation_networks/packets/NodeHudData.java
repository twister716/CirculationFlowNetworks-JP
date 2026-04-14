package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.handlers.NodeHudRenderingHandler;
import com.circulation.circulation_networks.utils.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class NodeHudData implements Packet<NodeHudData> {

    private long posLong;
    private String displayName;
    private String input;
    private String output;
    private String interactionTimeMicros;
    private int nodeCount;

    private transient long parsedPosLong;
    private transient String parsedDisplayName;
    private transient String parsedInput;
    private transient String parsedOutput;
    private transient String parsedInteractionTimeMicros;
    private transient int parsedNodeCount;

    public NodeHudData() {
    }

    public NodeHudData(BlockPos pos, String displayName, String input, String output, String interactionTimeMicros, int nodeCount) {
        this.posLong = pos.asLong();
        this.displayName = displayName;
        this.input = input;
        this.output = output;
        this.interactionTimeMicros = interactionTimeMicros;
        this.nodeCount = nodeCount;
    }

    @Override
    public NodeHudData decode(FriendlyByteBuf buf) {
        NodeHudData msg = new NodeHudData();
        msg.parsedPosLong = buf.readLong();
        msg.parsedDisplayName = buf.readUtf();
        msg.parsedInput = buf.readUtf();
        msg.parsedOutput = buf.readUtf();
        msg.parsedInteractionTimeMicros = buf.readUtf();
        msg.parsedNodeCount = buf.readVarInt();
        return msg;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(posLong);
        buf.writeUtf(displayName);
        buf.writeUtf(input);
        buf.writeUtf(output);
        buf.writeUtf(interactionTimeMicros);
        buf.writeVarInt(nodeCount);
    }

    @Override
    public void handle(NodeHudData message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (!FMLEnvironment.dist.isClient() || Minecraft.getInstance().player == null) {
                return;
            }
            NodeHudRenderingHandler.INSTANCE.updateData(
                message.parsedPosLong,
                message.parsedDisplayName,
                message.parsedInput,
                message.parsedOutput,
                message.parsedInteractionTimeMicros,
                message.parsedNodeCount
            );
        });
        context.setPacketHandled(true);
    }
}
