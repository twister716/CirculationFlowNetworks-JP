package com.circulation.circulation_networks.utils;

import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.events.AddNodeEvent;
import com.circulation.circulation_networks.events.RemoveNodeEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public final class NodeEventHooks {

    private static final IEventBus EVENT_BUS = NeoForge.EVENT_BUS;

    private NodeEventHooks() {
    }

    public static void postRemoveNodePre(INode node) {
        EVENT_BUS.post(new RemoveNodeEvent.Pre(node));
    }

    public static void postRemoveNodePost(INode node) {
        EVENT_BUS.post(new RemoveNodeEvent.Post(node));
    }

    public static boolean postAddNodePre(INode node, @Nullable BlockEntity blockEntity) {
        return EVENT_BUS.post(new AddNodeEvent.Pre(node, blockEntity)).isCanceled();
    }

    public static void postAddNodePost(INode node, @Nullable BlockEntity blockEntity) {
        EVENT_BUS.post(new AddNodeEvent.Post(node, blockEntity));
    }
}
