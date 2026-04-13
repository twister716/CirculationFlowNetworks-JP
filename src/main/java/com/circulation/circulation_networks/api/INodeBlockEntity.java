package com.circulation.circulation_networks.api;

import com.circulation.circulation_networks.api.node.INode;
//~ mc_imports
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public interface INodeBlockEntity {

    @NotNull
    INode getNode();

    @NotNull
    BlockPos getNodePos();

    //~ if >=1.20 'World ' -> 'Level ' {
    World getNodeWorld();
    //~}

    void nodeValidate();

    void nodeInvalidate();

    void syncNodeAfterNetworkInit();
}