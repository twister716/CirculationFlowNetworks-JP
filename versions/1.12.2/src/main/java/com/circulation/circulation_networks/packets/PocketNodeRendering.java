package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.handlers.PocketNodeRenderingHandler;
import com.circulation.circulation_networks.manager.PocketNodeManager;
import com.circulation.circulation_networks.pocket.PocketNodeRecord;
import com.circulation.circulation_networks.registry.NodeTypes;
import com.circulation.circulation_networks.utils.Packet;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

public final class PocketNodeRendering implements Packet<PocketNodeRendering> {

    public static final int SET = 0;
    public static final int ADD = 1;
    public static final int REMOVE = 2;

    private int mode;
    private int dim;
    private long posLong;
    private ObjectList<PocketNodeRecord> records;
    private transient int parsedMode;
    private transient int parsedDim;
    private transient long parsedPosLong;
    private transient ObjectList<PocketNodeRecord> parsedRecords;

    public PocketNodeRendering() {
    }

    public PocketNodeRendering(EntityPlayerMP player) {
        this.mode = SET;
        this.dim = player.dimension;
        this.records = PocketNodeManager.INSTANCE.getActiveRecords(dim);
    }

    public PocketNodeRendering(PocketNodeRecord record) {
        this.mode = ADD;
        this.dim = record.dimensionId();
        this.records = new ObjectArrayList<>();
        this.records.add(record);
    }

    public PocketNodeRendering(int dim, BlockPos pos) {
        this.mode = REMOVE;
        this.dim = dim;
        this.posLong = pos.toLong();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        parsedMode = buf.readByte();
        parsedDim = buf.readInt();
        if (parsedMode == REMOVE) {
            parsedPosLong = buf.readLong();
            return;
        }
        int count = buf.readInt();
        parsedRecords = new ObjectArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String typeId = ByteBufUtils.readUTF8String(buf);
            var nodeType = NodeTypes.getById(typeId);
            if (nodeType == null || !nodeType.allowsPocketNode()) {
                buf.readLong();
                boolean hasFace = buf.readBoolean();
                if (hasFace) {
                    ByteBufUtils.readUTF8String(buf);
                }
                continue;
            }
            BlockPos pos = BlockPos.fromLong(buf.readLong());
            EnumFacing face = null;
            if (buf.readBoolean()) {
                face = EnumFacing.byName(ByteBufUtils.readUTF8String(buf));
            }
            parsedRecords.add(new PocketNodeRecord(parsedDim, pos, nodeType, face, null, null));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(mode);
        buf.writeInt(dim);
        if (mode == REMOVE) {
            buf.writeLong(posLong);
            return;
        }
        buf.writeInt(records.size());
        for (var record : records) {
            ByteBufUtils.writeUTF8String(buf, record.nodeType().id());
            buf.writeLong(record.pos().toLong());
            buf.writeBoolean(record.attachmentFace() != null);
            if (record.attachmentFace() != null) {
                ByteBufUtils.writeUTF8String(buf, record.attachmentFace().getName2());
            }
        }
    }

    @Override
    public @Nullable IMessage onMessage(PocketNodeRendering message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (message.parsedMode == SET) {
                PocketNodeRenderingHandler.INSTANCE.setDimensionState(message.parsedDim, message.parsedRecords == null ? new ObjectArrayList<>() : message.parsedRecords);
            } else if (message.parsedMode == ADD) {
                if (message.parsedRecords != null) {
                    for (var record : message.parsedRecords) {
                        PocketNodeRenderingHandler.INSTANCE.add(record);
                    }
                }
            } else if (message.parsedMode == REMOVE) {
                PocketNodeRenderingHandler.INSTANCE.remove(message.parsedDim, BlockPos.fromLong(message.parsedPosLong));
            }
        });
        return null;
    }
}
