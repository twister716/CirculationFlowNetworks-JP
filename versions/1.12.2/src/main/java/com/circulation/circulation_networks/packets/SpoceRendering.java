package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.handlers.SpoceRenderingHandler;
import com.circulation.circulation_networks.utils.Packet;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

public final class SpoceRendering implements Packet<SpoceRendering> {

    private BlockPos pos;
    private double l, e, c;

    public SpoceRendering() {

    }

    public SpoceRendering(BlockPos pos, double l, double e, double c) {
        this.pos = pos;
        this.l = l;
        this.e = e;
        this.c = c;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.l = buf.readDouble();
        this.e = buf.readDouble();
        this.c = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeDouble(l);
        buf.writeDouble(e);
        buf.writeDouble(c);
    }

    @Override
    public @Nullable IMessage onMessage(SpoceRendering message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.world == null || message.pos == null) {
                return;
            }
            SpoceRenderingHandler.INSTANCE.setStaus(mc.world.provider.getDimension(), message.pos, message.l, message.e, message.c);
        });
        return null;
    }

}
