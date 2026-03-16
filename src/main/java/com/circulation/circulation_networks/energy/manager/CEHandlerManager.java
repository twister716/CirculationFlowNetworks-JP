package com.circulation.circulation_networks.energy.manager;

import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.api.IEnergyHandlerManager;
import com.circulation.circulation_networks.api.IMachineNodeBlockEntity;
import com.circulation.circulation_networks.energy.handler.CEHandler;
//? if <1.20 {
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
//?} else {
/*import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
*///?}

public final class CEHandlerManager implements IEnergyHandlerManager {

    @Override
    //? if <1.20 {
    public boolean isAvailable(TileEntity tileEntity) {
    //?} else {
    /*public boolean isAvailable(BlockEntity tileEntity) {
    *///?}
        return tileEntity instanceof IMachineNodeBlockEntity;
    }

    @Override
    public boolean isAvailable(ItemStack itemStack) {
        return false;
    }

    @Override
    public Class<CEHandler> getEnergyHandlerClass() {
        return CEHandler.class;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    //? if <1.20 {
    public IEnergyHandler newInstance(TileEntity tileEntity) {
    //?} else {
    /*public IEnergyHandler newInstance(BlockEntity tileEntity) {
    *///?}
        return ((IMachineNodeBlockEntity) tileEntity).getEnergyHandler();
    }

    @Override
    public IEnergyHandler newInstance(ItemStack itemStack) {
        return null;
    }

    @Override
    public String getUnit() {
        return "CE";
    }
}
