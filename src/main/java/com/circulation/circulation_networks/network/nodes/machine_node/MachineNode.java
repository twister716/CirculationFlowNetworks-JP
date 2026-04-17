package com.circulation.circulation_networks.network.nodes.machine_node;

import com.circulation.circulation_networks.api.node.IMachineNode;
import com.circulation.circulation_networks.api.node.NodeContext;
import com.circulation.circulation_networks.api.node.NodeType;
import com.circulation.circulation_networks.network.nodes.Node;
import com.circulation.circulation_networks.utils.NbtCompat;
import net.minecraft.nbt.CompoundTag;

public abstract class MachineNode extends Node implements IMachineNode {

    protected final double energyScope;
    protected final double energyScopeSq;
    private long maxEnergy;

    public MachineNode(NodeType<?> nodeType, CompoundTag compound) {
        super(nodeType, compound);
        this.energyScope = NbtCompat.getDoubleOr(compound, "energyScope", 0.0D);
        this.maxEnergy = NbtCompat.getLongOr(compound, "maxEnergy", 0L);
        this.energyScopeSq = energyScope * energyScope;
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
    public CompoundTag serialize() {
        var nbt = super.serialize();
        NbtCompat.putDouble(nbt, "energyScope", energyScope);
        NbtCompat.putLong(nbt, "maxEnergy", maxEnergy);
        return nbt;
    }
}
