package com.circulation.circulation_networks.packets;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.items.ItemInspectionTool;
import com.circulation.circulation_networks.items.InspectionToolModeModel;
import com.circulation.circulation_networks.items.InspectionToolState;
import com.circulation.circulation_networks.registry.CFNItems;
import com.circulation.circulation_networks.utils.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public final class UpdateItemModeMessage implements Packet<UpdateItemModeMessage> {

    public static final Type<UpdateItemModeMessage> TYPE = new Type<>(
        ResourceLocation.parse(CirculationFlowNetworks.MOD_ID + ":update_item_mode")
    );
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

    public UpdateItemModeMessage decode(RegistryFriendlyByteBuf buf) {
        int mode = buf.readByte();
        boolean hasTargetPos = buf.readBoolean();
        BlockPos targetPos = hasTargetPos ? BlockPos.of(buf.readLong()) : null;
        return new UpdateItemModeMessage(mode, targetPos);
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeByte(mode);
        buf.writeBoolean(hasTargetPos);
        if (hasTargetPos) {
            buf.writeLong(targetPos);
        }
    }

    public void handle(UpdateItemModeMessage message, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        context.enqueueWork(() -> {
            var stack = serverPlayer.getMainHandItem();
            if (stack.getItem() == CFNItems.inspectionTool) {
                var function = InspectionToolState.getFunction(stack);
                InspectionToolState.setSubMode(stack, InspectionToolModeModel.wrapSubMode(message.mode, function));
                if (function == InspectionToolModeModel.ToolFunction.INSPECTION) {
                    ItemInspectionTool.refreshInspectionRendering(
                        serverPlayer,
                        message.hasTargetPos ? BlockPos.of(message.targetPos) : null
                    );
                }
            }
        });
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
