package com.circulation.circulation_networks.network.nodes.machine_node;

import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.api.node.NodeContext;
import com.circulation.circulation_networks.registry.NodeTypes;
import net.minecraft.nbt.CompoundTag;

public class ConsumerNode extends MachineNode {

    public ConsumerNode(NodeContext context, double linkScope) {
        super(NodeTypes.CONSUMER, context, 0, linkScope);
    }

    public ConsumerNode(CompoundTag compound) {
        super(NodeTypes.CONSUMER, compound);
    }

    @Override
    public IEnergyHandler.EnergyType getType() {
        return IEnergyHandler.EnergyType.RECEIVE;
    }
}
