package com.circulation.circulation_networks.energy.manager;

import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.api.IEnergyHandlerManager;
import com.circulation.circulation_networks.energy.handler.RFHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public final class RFHandlerManager implements IEnergyHandlerManager {

    @Override
    public boolean isAvailable(TileEntity tileEntity) {
        if (!(tileEntity instanceof IEnergyProvider) && !(tileEntity instanceof IEnergyReceiver)) {
            return false;
        }
        if (!(tileEntity instanceof IEnergyConnection connection)) {
            return false;
        }
        for (int i = 0; i < EnumFacing.VALUES.length; i++) {
            if (connection.canConnectEnergy(EnumFacing.VALUES[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAvailable(ItemStack itemStack) {
        return itemStack.getItem() instanceof IEnergyContainerItem containerItem && containerItem.getMaxEnergyStored(itemStack) > 0;
    }

    @Override
    public Class<RFHandler> getEnergyHandlerClass() {
        return RFHandler.class;
    }

    @Override
    public int getPriority() {
        return 11;
    }

    @Override
    public IEnergyHandler newBlockEntityInstance() {
        return new RFHandler();
    }

    @Override
    public IEnergyHandler newItemInstance() {
        return new RFHandler();
    }

    @Override
    public String getUnit() {
        return "RF";
    }
}