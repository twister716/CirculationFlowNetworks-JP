package com.circulation.circulation_networks.api;

//? if <1.20 {
import net.minecraft.util.math.BlockPos;
//?} else {
/*import net.minecraft.core.BlockPos;
*///?}

public interface IPhaseInterrupterBlockEntity {

    boolean checkScope(BlockPos pos);

    boolean isActive();

    int getScope();

    boolean isShowingRange();

    BlockPos getPos();
}