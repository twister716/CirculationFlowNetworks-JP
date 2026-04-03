package com.circulation.circulation_networks.blocks.nodes;

import com.circulation.circulation_networks.tiles.nodes.TileEntityPortNode;

public final class BlockPortNode extends PedestalRequiredNodeBlock {

    public BlockPortNode() {
        super("port_node");
        this.setNodeTileClass(TileEntityPortNode.class);
    }
}