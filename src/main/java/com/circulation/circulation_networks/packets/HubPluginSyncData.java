package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.tiles.nodes.BlockEntityHub;
import com.circulation.circulation_networks.utils.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public final class HubPluginSyncData implements Packet<HubPluginSyncData> {

    public static final Type<HubPluginSyncData> TYPE = new Type<>(
        Identifier.parse(CirculationFlowNetworks.MOD_ID + ":hub_plugin_sync_data")
    );

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

    private static ItemStack[] copySnapshot(ItemStack[] snapshot) {
        ItemStack[] copy = new ItemStack[snapshot.length];
        for (int i = 0; i < snapshot.length; i++) {
            ItemStack stack = snapshot[i];
            copy[i] = stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        }
        return copy;
    }

    @Override
    public HubPluginSyncData decode(@NonNull RegistryFriendlyByteBuf buf) {
        HubPluginSyncData msg = new HubPluginSyncData();
        msg.parsedPosLong = buf.readLong();
        int size = buf.readVarInt();
        msg.parsedSnapshot = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            msg.parsedSnapshot[i] = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
        }
        return msg;
    }

    @Override
    public void encode(@NonNull RegistryFriendlyByteBuf buf) {
        buf.writeLong(posLong);
        buf.writeVarInt(snapshot.length);
        for (ItemStack stack : snapshot) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
        }
    }

    @Override
    public void handle(@NonNull HubPluginSyncData message, @NonNull IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level == null) {
                return;
            }

            BlockPos pos = BlockPos.of(message.parsedPosLong);
            if (minecraft.level.getBlockEntity(pos) instanceof BlockEntityHub hub) {
                hub.applyPluginSnapshot(message.parsedSnapshot);
            }
        });
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
