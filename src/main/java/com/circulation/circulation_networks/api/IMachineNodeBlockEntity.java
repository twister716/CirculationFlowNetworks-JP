package com.circulation.circulation_networks.api;

import com.circulation.circulation_networks.api.node.IMachineNode;

import javax.annotation.Nonnull;

public interface IMachineNodeBlockEntity extends INodeBlockEntity {

    @Nonnull
    IMachineNode getNode();

    /**
     * @return IEnergyHandler必须重写recycle方法，防止被回收进POOL
     */
    @Nonnull
    IEnergyHandler getEnergyHandler();
}