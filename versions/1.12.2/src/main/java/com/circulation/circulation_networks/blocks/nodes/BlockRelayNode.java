package com.circulation.circulation_networks.blocks.nodes;

import com.circulation.circulation_networks.tiles.nodes.TileEntityRelayNode;

public final class BlockRelayNode extends PedestalRequiredNodeBlock {

    public BlockRelayNode() {
        super("relay_node");
        this.setNodeTileClass(TileEntityRelayNode.class);
    }
}