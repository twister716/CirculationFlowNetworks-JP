package com.circulation.circulation_networks.network.nodes;

import com.circulation.circulation_networks.api.node.IEnergySupplyNode;
import com.circulation.circulation_networks.api.node.NodeContext;
import com.circulation.circulation_networks.registry.NodeTypes;
//~ mc_imports
import net.minecraft.nbt.NBTTagCompound;

public final class PortNode extends Node implements IEnergySupplyNode {

    private final double energyScope;
    private final double energyScopeSq;

    //~ if >=1.20 'NBTTagCompound' -> 'CompoundTag' {
    //~ if >=1.20 '.set' -> '.put' {
    public PortNode(NBTTagCompound tag) {
        super(NodeTypes.PORT_NODE, tag);
        energyScope = tag.getDouble("energyScope");
        energyScopeSq = energyScope * energyScope;
    }

    public PortNode(NodeContext context, double energyScope, double linkScope) {
        super(NodeTypes.PORT_NODE, context, linkScope);
        this.energyScope = energyScope;
        this.energyScopeSq = energyScope * energyScope;
    }

    @Override
    public double getEnergyScope() {
        return energyScope;
    }

    @Override
    public double getEnergyScopeSq() {
        return energyScopeSq;
    }

    @Override
    public NBTTagCompound serialize() {
        var tag = super.serialize();
        tag.setDouble("energyScope", energyScope);
        return tag;
    }
    //~}
    //~}
}