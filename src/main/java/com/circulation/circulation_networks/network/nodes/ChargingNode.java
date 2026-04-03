package com.circulation.circulation_networks.network.nodes;

import com.circulation.circulation_networks.api.node.IChargingNode;
import com.circulation.circulation_networks.api.node.NodeContext;
import com.circulation.circulation_networks.registry.NodeTypes;
//~ mc_imports
import net.minecraft.nbt.NBTTagCompound;

public final class ChargingNode extends Node implements IChargingNode {

    private final double chargingScope;
    private final double chargingScopeSq;

    //~ if >=1.20 'NBTTagCompound' -> 'CompoundTag' {
    //~ if >=1.20 '.setDouble(' -> '.putDouble(' {
    public ChargingNode(NBTTagCompound tag) {
        super(NodeTypes.CHARGING_NODE, tag);
        this.chargingScope = tag.getDouble("chargingScope");
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
    public NBTTagCompound serialize() {
        var nbt = super.serialize();
        nbt.setDouble("chargingScope", chargingScope);
        return nbt;
    }
    //~}
    //~}
}