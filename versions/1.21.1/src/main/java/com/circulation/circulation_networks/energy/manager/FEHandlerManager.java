package com.circulation.circulation_networks.energy.manager;

import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.api.IEnergyHandlerManager;
import com.circulation.circulation_networks.energy.handler.FEHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;

public final class FEHandlerManager implements IEnergyHandlerManager {

    @Override
    public boolean isAvailable(BlockEntity blockEntity) {
        var level = blockEntity.getLevel();
        if (level == null) return false;
        var pos = blockEntity.getBlockPos();
        for (Direction direction : Direction.values()) {
            if (level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, direction) != null) return true;
        }
        return false;
    }

    @Override
    public boolean isAvailable(ItemStack itemStack) {
        return itemStack.getCapability(Capabilities.EnergyStorage.ITEM) != null;
    }

    @Override
    public Class<FEHandler> getEnergyHandlerClass() {
        return FEHandler.class;
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public IEnergyHandler newInstance(BlockEntity blockEntity) {
        return new FEHandler(blockEntity);
    }

    @Override
    public IEnergyHandler newInstance(ItemStack itemStack) {
        return new FEHandler(itemStack);
    }

    @Override
    public String getUnit() {
        return "FE";
    }
}
