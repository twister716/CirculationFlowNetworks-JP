package com.circulation.circulation_networks.network.nodes.machine_node;

import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.api.node.NodeContext;
import com.circulation.circulation_networks.registry.NodeTypes;
import net.minecraft.nbt.CompoundTag;

public final class StorageNode extends MachineNode {

    public StorageNode(NodeContext context, double energyScope, double linkScope) {
        super(NodeTypes.STORAGE, context, energyScope, linkScope);
    }

    public StorageNode(CompoundTag tag) {
        super(NodeTypes.STORAGE, tag);
    }

    @Override
    public IEnergyHandler.EnergyType getType() {
        return IEnergyHandler.EnergyType.STORAGE;
    }
}
