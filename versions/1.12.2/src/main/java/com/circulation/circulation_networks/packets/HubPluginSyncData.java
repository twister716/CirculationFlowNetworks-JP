package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.tiles.nodes.TileEntityHub;
import com.circulation.circulation_networks.utils.Packet;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

public final class HubPluginSyncData implements Packet<HubPluginSyncData> {

    private long posLong;
    private ItemStack[] snapshot;

    public HubPluginSyncData() {
    }

    public HubPluginSyncData(BlockPos pos, ItemStack[] snapshot) {
        this.posLong = pos.toLong();
        this.snapshot = copySnapshot(snapshot);
    }

    private static ItemStack[] copySnapshot(ItemStack[] snapshot) {
        ItemStack[] copy = new ItemStack[snapshot.length];
        for (int i = 0; i < snapshot.length; i++) {
            ItemStack stack = snapshot[i];
            copy[i] = stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        }
        return copy;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        posLong = buf.readLong();
        int size = buf.readInt();
        snapshot = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            snapshot[i] = ByteBufUtils.readItemStack(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(posLong);
        buf.writeInt(snapshot.length);
        for (ItemStack stack : snapshot) {
            ByteBufUtils.writeItemStack(buf, stack);
        }
    }

    @Override
    public @Nullable IMessage onMessage(HubPluginSyncData message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().world == null) {
                return;
            }

            BlockPos pos = BlockPos.fromLong(message.posLong);
            if (Minecraft.getMinecraft().world.getTileEntity(pos) instanceof TileEntityHub hub) {
                hub.applyPluginSnapshot(message.snapshot);
            }
        });
        return null;
    }
}
