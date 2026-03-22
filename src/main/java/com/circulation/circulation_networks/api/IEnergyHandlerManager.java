package com.circulation.circulation_networks.api;

import org.jetbrains.annotations.NotNull;
//? if <1.20 {
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
//?} else {
/*import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
*///?}

public interface IEnergyHandlerManager extends Comparable<IEnergyHandlerManager> {

    //? if <1.20 {
    boolean isAvailable(TileEntity tileEntity);
    //?} else {
    /*boolean isAvailable(BlockEntity blockEntity);
    *///?}

    boolean isAvailable(ItemStack itemStack);

    Class<? extends IEnergyHandler> getEnergyHandlerClass();

    int getPriority();

    //? if <1.20 {
    IEnergyHandler newInstance(TileEntity tileEntity);
    //?} else {
    /*IEnergyHandler newInstance(BlockEntity blockEntity);
    *///?}

    IEnergyHandler newInstance(ItemStack itemStack);

    default String getUnit() {
        return "FE";
    }

    default double getMultiplying() {
        return 1;
    }

    @Override
    default int compareTo(@NotNull IEnergyHandlerManager o) {
        return Integer.compare(this.getPriority(), o.getPriority());
    }
}