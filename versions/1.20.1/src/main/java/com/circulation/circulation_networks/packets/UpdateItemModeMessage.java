package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.items.ItemInspectionTool;
import com.circulation.circulation_networks.items.InspectionToolModeModel;
import com.circulation.circulation_networks.items.InspectionToolState;
import com.circulation.circulation_networks.registry.CFNItems;
import com.circulation.circulation_networks.utils.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class UpdateItemModeMessage implements Packet<UpdateItemModeMessage> {

    private final byte mode;
    private final long targetPos;
    private final boolean hasTargetPos;

    public UpdateItemModeMessage() {
        this(0, null);
    }

    public UpdateItemModeMessage(int mode) {
        this(mode, null);
    }

    public UpdateItemModeMessage(int mode, BlockPos targetPos) {
        this.mode = (byte) mode;
        this.hasTargetPos = targetPos != null;
        this.targetPos = this.hasTargetPos ? targetPos.asLong() : 0L;
    }

    public UpdateItemModeMessage decode(FriendlyByteBuf buf) {
        int mode = buf.readByte();
        boolean hasTargetPos = buf.readBoolean();
        BlockPos targetPos = hasTargetPos ? BlockPos.of(buf.readLong()) : null;
        return new UpdateItemModeMessage(mode, targetPos);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(mode);
        buf.writeBoolean(hasTargetPos);
        if (hasTargetPos) {
            buf.writeLong(targetPos);
        }
    }

    public void handle(UpdateItemModeMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender == null) {
                return;
            }

            var stack = sender.getMainHandItem();
            if (stack.getItem() == CFNItems.inspectionTool && stack.getTag() != null) {
                var function = InspectionToolState.getFunction(stack);
                InspectionToolState.setSubMode(stack, InspectionToolModeModel.wrapSubMode(message.mode, function));
                if (function == InspectionToolModeModel.ToolFunction.INSPECTION) {
                    ItemInspectionTool.refreshInspectionRendering(
                        sender,
                        message.hasTargetPos ? BlockPos.of(message.targetPos) : null
                    );
                }
            }
        });
        context.setPacketHandled(true);
    }
}
