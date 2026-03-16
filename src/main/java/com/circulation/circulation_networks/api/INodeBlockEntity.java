package com.circulation.circulation_networks.api;

import com.circulation.circulation_networks.api.node.INode;
//? if <1.20 {
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
//?} else {
/*import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
*///?}

import javax.annotation.Nonnull;

public interface INodeBlockEntity {

    @Nonnull
    INode getNode();

    @Nonnull
    BlockPos getNodePos();

    //? if <1.20 {
    World getNodeWorld();
    //?} else {
    /*Level getNodeWorld();
    *///?}
}