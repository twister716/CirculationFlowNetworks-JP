package com.circulation.circulation_networks.network.nodes.machine_node;

import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.api.IMachineNodeBlockEntity;
//? if <1.20 {
import net.minecraft.nbt.NBTTagCompound;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
*///?}

public final class StorageNode extends MachineNode {

    public StorageNode(IMachineNodeBlockEntity blockEntity, double energyScope, double linkScope) {
        super(blockEntity, energyScope, linkScope);
    }

    //? if <1.20 {
    public StorageNode(NBTTagCompound tag) {
        super(tag);
    }
    //?} else {
    /*public StorageNode(CompoundTag tag) {
        super(tag);
    }
    *///?}

    @Override
    public IEnergyHandler.EnergyType getType() {
        return IEnergyHandler.EnergyType.STORAGE;
    }
}