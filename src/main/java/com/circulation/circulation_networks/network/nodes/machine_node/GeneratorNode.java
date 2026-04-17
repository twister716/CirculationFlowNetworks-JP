package com.circulation.circulation_networks.network.nodes.machine_node;

import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.api.node.NodeContext;
import com.circulation.circulation_networks.registry.NodeTypes;
import net.minecraft.nbt.CompoundTag;

public final class GeneratorNode extends MachineNode {

    public GeneratorNode(NodeContext context, double energyScope, double linkScope) {
        super(NodeTypes.GENERATOR, context, energyScope, linkScope);
    }

    public GeneratorNode(CompoundTag tag) {
        super(NodeTypes.GENERATOR, tag);
    }

    @Override
    public IEnergyHandler.EnergyType getType() {
        return IEnergyHandler.EnergyType.SEND;
    }
}
