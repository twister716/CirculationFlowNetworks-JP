package com.circulation.circulation_networks.api.node;

import com.circulation.circulation_networks.registry.RegistryEnergyHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * 标识符，确定节点可用于与设备交互能量
 */
public interface IEnergySupplyNode extends INode {

    double getEnergyScope();

    double getEnergyScopeSq();

    default boolean supplyScopeCheck(BlockPos pos) {
        return this.distanceSq(pos) <= getEnergyScopeSq();
    }

    default boolean isBlacklisted(BlockEntity blockEntity) {
        return RegistryEnergyHandler.isSupplyBlack(blockEntity);
    }
}
