package com.circulation.circulation_networks.api;

import com.circulation.circulation_networks.api.node.IHubNode;
import com.circulation.circulation_networks.inventory.CFNInternalInventory;
import org.jetbrains.annotations.NotNull;

public interface IHubNodeBlockEntity extends INodeBlockEntity {

    @Override
    @NotNull
    IHubNode getNode();

    CFNInternalInventory getPlugins();
}
