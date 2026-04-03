package com.circulation.circulation_networks.events;

import com.circulation.circulation_networks.api.node.INode;
//? if <1.20 {
import net.minecraftforge.fml.common.eventhandler.Event;
//?} else if <1.21 {
/*import net.minecraftforge.eventbus.api.Event;
 *///?} else {
/*import net.neoforged.bus.api.Event;
 *///?}

public abstract class NodeEvent extends Event {

    private final INode node;

    public NodeEvent(INode node) {
        this.node = node;
    }

    public INode getNode() {
        return node;
    }
}