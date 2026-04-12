package com.circulation.circulation_networks.api;

import com.circulation.circulation_networks.api.node.IMachineNode;
import com.circulation.circulation_networks.network.nodes.HubNode;

import javax.annotation.Nullable;

import javax.annotation.Nonnull;

public interface IMachineNodeBlockEntity extends INodeBlockEntity {

    @Nonnull
    IMachineNode getNode();

    /**
     * 为当前调用创建一个上下文隔离的 handler 实例。
     * 实现不得返回可跨 network/grid 复用的共享实例。
     */
    @Nonnull
    IEnergyHandler createEnergyHandler(@Nullable HubNode.HubMetadata hubMetadata);
}