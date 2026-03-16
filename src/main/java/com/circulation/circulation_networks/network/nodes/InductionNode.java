package com.circulation.circulation_networks.network.nodes;

import com.circulation.circulation_networks.api.INodeBlockEntity;
import com.circulation.circulation_networks.api.node.IEnergySupplyNode;
//? if <1.20 {
import net.minecraft.nbt.NBTTagCompound;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
*///?}

public final class InductionNode extends Node implements IEnergySupplyNode {

    private final double energyScope;
    private final double energyScopeSq;

    //? if <1.20 {
    public InductionNode(NBTTagCompound tag) {
        super(tag);
        energyScope = tag.getDouble("energyScope");
        energyScopeSq = energyScope * energyScope;
    }
    //?} else {
    /*public InductionNode(CompoundTag tag) {
        super(tag);
        energyScope = tag.getDouble("energyScope");
        energyScopeSq = energyScope * energyScope;
    }
    *///?}

    public InductionNode(INodeBlockEntity blockEntity, double energyScope, double linkScope) {
        super(blockEntity, linkScope);
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

    //? if <1.20 {
    @Override
    public NBTTagCompound serialize() {
        var tag = super.serialize();
        tag.setDouble("energyScope", energyScope);
        return tag;
    }
    //?} else {
    /*@Override
    public CompoundTag serialize() {
        var tag = super.serialize();
        tag.putDouble("energyScope", energyScope);
        return tag;
    }
    *///?}
}