package com.circulation.circulation_networks.api;

import com.circulation.circulation_networks.api.node.IMachineNode;
import com.circulation.circulation_networks.network.nodes.HubNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IMachineNodeBlockEntity extends INodeBlockEntity {

    @NotNull
    IMachineNode getNode();

    /**
     * 为当前调用创建一个上下文隔离的 handler 实例。
     * 实现不得返回可跨 network/grid 复用的共享实例。
     */
    @NotNull
    IEnergyHandler createEnergyHandler(@Nullable HubNode.HubMetadata hubMetadata);
}