package com.circulation.circulation_networks.tiles.nodes;

import com.circulation.circulation_networks.api.node.NodeType;
import com.circulation.circulation_networks.network.nodes.PortNode;
import com.circulation.circulation_networks.registry.NodeTypes;
import org.jetbrains.annotations.NotNull;

public final class TileEntityPortNode extends BaseNodeTileEntity<PortNode> {

    @Override
    protected @NotNull NodeType<? extends PortNode> getNodeType() {
        return NodeTypes.PORT_NODE;
    }
}