package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.utils.Packet;
import com.circulation.circulation_networks.tiles.nodes.HubBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class HubPluginSyncData implements Packet<HubPluginSyncData> {

    private long posLong;
    private ItemStack[] snapshot;

    private transient long parsedPosLong;
    private transient ItemStack[] parsedSnapshot;

    public HubPluginSyncData() {
    }

    public HubPluginSyncData(BlockPos pos, ItemStack[] snapshot) {
        this.posLong = pos.asLong();
        this.snapshot = copySnapshot(snapshot);
    }

    @Override
    public HubPluginSyncData decode(FriendlyByteBuf buf) {
        HubPluginSyncData msg = new HubPluginSyncData();
        msg.parsedPosLong = buf.readLong();
        int size = buf.readVarInt();
        msg.parsedSnapshot = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            msg.parsedSnapshot[i] = buf.readItem();
        }
        return msg;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(posLong);
        buf.writeVarInt(snapshot.length);
        for (ItemStack stack : snapshot) {
            buf.writeItem(stack);
        }
    }

    @Override
    public void handle(HubPluginSyncData message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (!FMLEnvironment.dist.isClient() || Minecraft.getInstance().level == null) {
                return;
            }

            BlockPos pos = BlockPos.of(message.parsedPosLong);
            if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof HubBlockEntity hub) {
                hub.applyPluginSnapshot(message.parsedSnapshot);
            }
        });
        context.setPacketHandled(true);
    }

    private static ItemStack[] copySnapshot(ItemStack[] snapshot) {
        ItemStack[] copy = new ItemStack[snapshot.length];
        for (int i = 0; i < snapshot.length; i++) {
            ItemStack stack = snapshot[i];
            copy[i] = stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        }
        return copy;
    }
}
