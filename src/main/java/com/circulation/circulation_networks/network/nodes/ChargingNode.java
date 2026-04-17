package com.circulation.circulation_networks.network.nodes;

import com.circulation.circulation_networks.api.node.IChargingNode;
import com.circulation.circulation_networks.api.node.NodeContext;
import com.circulation.circulation_networks.registry.NodeTypes;
import com.circulation.circulation_networks.utils.NbtCompat;
import net.minecraft.nbt.CompoundTag;

public final class ChargingNode extends Node implements IChargingNode {

    private final double chargingScope;
    private final double chargingScopeSq;

    public ChargingNode(CompoundTag tag) {
        super(NodeTypes.CHARGING_NODE, tag);
        this.chargingScope = NbtCompat.getDoubleOr(tag, "chargingScope", 0.0D);
        this.chargingScopeSq = chargingScope * chargingScope;
    }

    public ChargingNode(NodeContext context, double chargingScope, double linkScope) {
        super(NodeTypes.CHARGING_NODE, context, linkScope);
        this.chargingScope = chargingScope;
        this.chargingScopeSq = chargingScope * chargingScope;
    }

    @Override
    public double getChargingScope() {
        return chargingScope;
    }

    @Override
    public double getChargingScopeSq() {
        return chargingScopeSq;
    }

    @Override
    public CompoundTag serialize() {
        var nbt = super.serialize();
        NbtCompat.putDouble(nbt, "chargingScope", chargingScope);
        return nbt;
    }
}
