package com.circulation.circulation_networks.api;

import com.circulation.circulation_networks.api.node.IMachineNode;

import javax.annotation.Nonnull;

public interface IMachineNodeBlockEntity extends INodeBlockEntity {

    @Nonnull
    IMachineNode getNode();

    @Nonnull
    IEnergyHandler getEnergyHandler();
}