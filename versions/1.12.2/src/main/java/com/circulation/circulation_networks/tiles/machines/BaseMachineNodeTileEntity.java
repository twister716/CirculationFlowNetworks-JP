package com.circulation.circulation_networks.tiles.machines;

import com.circulation.circulation_networks.api.EnergyAmount;
import com.circulation.circulation_networks.api.IMachineNodeBlockEntity;
import com.circulation.circulation_networks.api.node.IMachineNode;
import com.circulation.circulation_networks.energy.handler.CEHandler;
import com.circulation.circulation_networks.tiles.nodes.BaseNodeTileEntity;
import com.circulation.circulation_networks.utils.CirculationEnergy;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

public abstract class BaseMachineNodeTileEntity extends BaseNodeTileEntity implements IMachineNodeBlockEntity {

    protected CEHandler ceHandler;
    private transient NBTTagCompound initNbt;

    @Override
    public @NotNull IMachineNode getNode() {
        return (IMachineNode) super.getNode();
    }

    @Override
    public @NotNull CEHandler getEnergyHandler() {
        assert ceHandler != null;
        return ceHandler;
    }

    public final CirculationEnergy getCirculationEnergy() {
        return getEnergyHandler().getEnergy();
    }

    public long getMaxEnergy() {
        return getNode().getMaxEnergy();
    }

    public void setMaxEnergy(long energy) {
        getNode().setMaxEnergy(energy);
    }

    public long getEnergy() {
        return getCirculationEnergy().getEnergy();
    }

    public long addEnergy(long energy, boolean simulate) {
        EnergyAmount request = EnergyAmount.obtain(energy);
        try {
            EnergyAmount received = getCirculationEnergy().receiveEnergy(request, simulate);
            try {
                return received.asLongClamped();
            } finally {
                received.recycle();
            }
        } finally {
            request.recycle();
        }
    }

    public long removeEnergy(long energy, boolean simulate) {
        EnergyAmount request = EnergyAmount.obtain(energy);
        try {
            EnergyAmount extracted = getCirculationEnergy().extractEnergy(request, simulate);
            try {
                return extracted.asLongClamped();
            } finally {
                extracted.recycle();
            }
        } finally {
            request.recycle();
        }
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
        var nbt = super.writeToNBT(compound);
        if (ceHandler != null) ceHandler.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public final void readFromNBT(@NotNull NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (ceHandler == null) {
            initNbt = compound;
        } else {
            delayedReadFromNBT(compound);
        }
    }

    public void delayedReadFromNBT(@NotNull NBTTagCompound compound) {
        ceHandler.readNBT(initNbt);
    }

    protected abstract @NotNull IMachineNode createNode();

    protected void onValidate() {
        super.onValidate();
        if (ceHandler == null) ceHandler = new CEHandler(this);
        if (initNbt != null) {
            delayedReadFromNBT(initNbt);
            initNbt = null;
        }
    }
}