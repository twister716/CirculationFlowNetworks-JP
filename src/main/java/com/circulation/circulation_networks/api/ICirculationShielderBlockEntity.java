package com.circulation.circulation_networks.api;


import net.minecraft.core.BlockPos;

public interface ICirculationShielderBlockEntity {

    boolean checkScope(BlockPos pos);

    boolean isActive();

    int getScope();

    int getMaxScope();

    boolean isShowingRange();

    BlockPos getBEPos();
}
