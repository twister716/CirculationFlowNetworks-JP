package com.circulation.circulation_networks.energy.manager;

import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.api.IEnergyHandlerManager;
import com.circulation.circulation_networks.energy.handler.MMCEHandler;
import hellfirepvp.modularmachinery.common.tiles.TileEnergyInputHatch;
import hellfirepvp.modularmachinery.common.tiles.TileEnergyOutputHatch;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public final class MMCEHandlerManager implements IEnergyHandlerManager {

    @Override
    public boolean isAvailable(TileEntity tileEntity) {
        return tileEntity instanceof TileEnergyInputHatch || tileEntity instanceof TileEnergyOutputHatch;
    }

    @Override
    public boolean isAvailable(ItemStack itemStack) {
        return false;
    }

    @Override
    public Class<MMCEHandler> getEnergyHandlerClass() {
        return MMCEHandler.class;
    }

    @Override
    public int getPriority() {
        return 51;
    }

    @Override
    public IEnergyHandler newBlockEntityInstance() {
        return new MMCEHandler();
    }

    @Override
    public IEnergyHandler newItemInstance() {
        return new MMCEHandler();
    }
}