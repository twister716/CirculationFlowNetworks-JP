package com.circulation.circulation_networks.events;

import com.circulation.circulation_networks.api.node.INode;
import net.neoforged.bus.api.Event;

public abstract class NodeEvent extends Event {

    private final INode node;

    public NodeEvent(INode node) {
        this.node = node;
    }

    public INode getNode() {
        return node;
    }
}
