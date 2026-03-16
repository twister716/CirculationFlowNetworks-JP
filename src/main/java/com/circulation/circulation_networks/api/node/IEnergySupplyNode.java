package com.circulation.circulation_networks.api.node;

import com.circulation.circulation_networks.registry.RegistryEnergyHandler;
//? if <1.20 {
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
//?} else {
/*import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
*///?}

/**
 * 标识符，确定节点可用于与设备交互能量
 */
public interface IEnergySupplyNode extends INode {

    double getEnergyScope();

    double getEnergyScopeSq();

    default boolean supplyScopeCheck(BlockPos pos) {
        return this.distanceSq(pos) <= getEnergyScopeSq();
    }

    //? if <1.20 {
    default boolean isBlacklisted(TileEntity blockEntity) {
        return RegistryEnergyHandler.isSupplyBlack(blockEntity);
    }
    //?} else {
    /*default boolean isBlacklisted(BlockEntity blockEntity) {
        return RegistryEnergyHandler.isSupplyBlack(blockEntity);
    }
    *///?}
}
