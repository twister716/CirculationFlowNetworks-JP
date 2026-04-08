package com.circulation.circulation_networks.tiles.nodes;

import com.circulation.circulation_networks.api.node.NodeType;
import com.circulation.circulation_networks.network.nodes.Node;
import com.circulation.circulation_networks.registry.NodeTypes;
import net.minecraft.util.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

public final class TileEntityRelayNode extends BaseNodeTileEntity<Node> {

    @Override
    protected @NotNull NodeType<? extends Node> getNodeType() {
        return NodeTypes.RELAY_NODE;
    }

    @Override
    public @NotNull AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.add(-1, -1, -1), pos.add(2, 2, 2));
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 4096.0D;
    }

    @Override
    public boolean canRenderBreaking() {
        return false;
    }
}
