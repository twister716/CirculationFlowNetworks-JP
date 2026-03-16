package com.circulation.circulation_networks.network.nodes.machine_node;

import com.circulation.circulation_networks.api.IMachineNodeBlockEntity;
import com.circulation.circulation_networks.api.node.IMachineNode;
import com.circulation.circulation_networks.network.nodes.Node;
//? if <1.20 {
import net.minecraft.nbt.NBTTagCompound;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
*///?}

public abstract class MachineNode extends Node implements IMachineNode {

    protected final double energyScope;
    protected final double energyScopeSq;
    private long maxEnergy;

    //? if <1.20 {
    public MachineNode(NBTTagCompound compound) {
        super(compound);
        this.energyScope = compound.getDouble("energyScope");
        this.energyScopeSq = energyScope * energyScope;
    }
    //?} else {
    /*public MachineNode(CompoundTag compound) {
        super(compound);
        this.energyScope = compound.getDouble("energyScope");
        this.energyScopeSq = energyScope * energyScope;
    }
    *///?}

    public MachineNode(IMachineNodeBlockEntity blockEntity, double energyScope, double linkScope) {
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

    public long getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(long maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    //? if <1.20 {
    @Override
    public NBTTagCompound serialize() {
        var nbt = super.serialize();
        nbt.setDouble("energyScope", energyScope);
        nbt.setLong("maxEnergy", maxEnergy);
        return nbt;
    }
    //?} else {
    /*@Override
    public CompoundTag serialize() {
        var nbt = super.serialize();
        nbt.putDouble("energyScope", energyScope);
        nbt.putLong("maxEnergy", maxEnergy);
        return nbt;
    }
    *///?}
}