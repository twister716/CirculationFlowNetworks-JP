package com.circulation.circulation_networks.network.nodes.machine_node;

import com.circulation.circulation_networks.api.node.IMachineNode;
import com.circulation.circulation_networks.api.node.NodeContext;
import com.circulation.circulation_networks.api.node.NodeType;
import com.circulation.circulation_networks.network.nodes.Node;
//~ mc_imports
import net.minecraft.nbt.NBTTagCompound;

public abstract class MachineNode extends Node implements IMachineNode {

    protected final double energyScope;
    protected final double energyScopeSq;
    private long maxEnergy;

    //~ if >=1.20 'NBTTagCompound' -> 'CompoundTag' {
    //~ if >=1.20 '.set' -> '.put' {
    public MachineNode(NodeType<?> nodeType, NBTTagCompound compound) {
        super(nodeType, compound);
        this.energyScope = compound.getDouble("energyScope");
        this.energyScopeSq = energyScope * energyScope;
        this.maxEnergy = compound.getLong("maxEnergy");
    }

    public MachineNode(NodeType<?> nodeType, NodeContext context, double energyScope, double linkScope) {
        super(nodeType, context, linkScope);
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
        var nbt = super.serialize();
        nbt.setDouble("energyScope", energyScope);
        nbt.setLong("maxEnergy", maxEnergy);
        return nbt;
    }
    //~}
    //~}
}