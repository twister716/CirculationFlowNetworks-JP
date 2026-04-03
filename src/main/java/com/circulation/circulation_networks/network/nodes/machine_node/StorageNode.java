package com.circulation.circulation_networks.network.nodes.machine_node;

import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.api.node.NodeContext;
import com.circulation.circulation_networks.registry.NodeTypes;
//~ mc_imports
import net.minecraft.nbt.NBTTagCompound;

public final class StorageNode extends MachineNode {

    public StorageNode(NodeContext context, double energyScope, double linkScope) {
        super(NodeTypes.STORAGE, context, energyScope, linkScope);
    }

    //~ if >=1.20 'NBTTagCompound' -> 'CompoundTag' {
    public StorageNode(NBTTagCompound tag) {
        super(NodeTypes.STORAGE, tag);
    }
    //~}

    @Override
    public IEnergyHandler.EnergyType getType() {
        return IEnergyHandler.EnergyType.STORAGE;
    }
}