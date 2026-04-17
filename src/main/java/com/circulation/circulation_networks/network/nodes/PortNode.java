package com.circulation.circulation_networks.network.nodes;

import com.circulation.circulation_networks.api.node.IEnergySupplyNode;
import com.circulation.circulation_networks.api.node.NodeContext;
import com.circulation.circulation_networks.registry.NodeTypes;
import com.circulation.circulation_networks.utils.NbtCompat;
import net.minecraft.nbt.CompoundTag;

public final class PortNode extends Node implements IEnergySupplyNode {

    private final double energyScope;
    private final double energyScopeSq;

    public PortNode(CompoundTag tag) {
        super(NodeTypes.PORT_NODE, tag);
        energyScope = NbtCompat.getDoubleOr(tag, "energyScope", 0.0D);
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
    public CompoundTag serialize() {
        var tag = super.serialize();
        NbtCompat.putDouble(tag, "energyScope", energyScope);
        return tag;
    }
}
