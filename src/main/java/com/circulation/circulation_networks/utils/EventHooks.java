package com.circulation.circulation_networks.utils;

import com.circulation.circulation_networks.api.INodeBlockEntity;
import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.events.AddNodeEvent;
import com.circulation.circulation_networks.events.BlockEntityLifeCycleEvent;
import com.circulation.circulation_networks.events.RemoveNodeEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public final class EventHooks {

    private static final IEventBus eventBus = NeoForge.EVENT_BUS;
    private static boolean validateLock;
    private static boolean invalidateLock;

    private EventHooks() {
    }

    public static void postRemoveNodePre(INode node) {
        eventBus.post(new RemoveNodeEvent.Pre(node));
    }

    public static void postRemoveNodePost(INode node) {
        eventBus.post(new RemoveNodeEvent.Post(node));
    }

    public static boolean postAddNodePre(INode node, @Nullable BlockEntity tileEntity) {
        return eventBus.post(new AddNodeEvent.Pre(node, tileEntity)).isCanceled();
    }

    public static void postAddNodePost(INode node, @Nullable BlockEntity tileEntity) {
        eventBus.post(new AddNodeEvent.Post(node, tileEntity));
    }

    public static void onBlockEntityValidate(Level world, BlockPos pos, BlockEntity blockEntity) {
        if (validateLock) return;
        validateLock = true;
        if (blockEntity instanceof INodeBlockEntity nbe) {
            nbe.nodeValidate();
        }
        BlockEntityLifeCycleEvent.Validate event = new BlockEntityLifeCycleEvent.Validate(world, pos, blockEntity);
        BlockEntityLifecycleDispatcher.onValidate(event);
        eventBus.post(event);
        validateLock = false;
    }

    public static void onBlockEntityInvalidate(Level world, BlockPos pos, BlockEntity blockEntity) {
        if (invalidateLock) return;
        invalidateLock = true;
        if (blockEntity instanceof INodeBlockEntity nbe) {
            nbe.nodeInvalidate();
        }
        BlockEntityLifeCycleEvent.Invalidate event = new BlockEntityLifeCycleEvent.Invalidate(world, pos, blockEntity);
        BlockEntityLifecycleDispatcher.onInvalidate(event);
        eventBus.post(event);
        invalidateLock = false;
    }
}
