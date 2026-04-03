package com.circulation.circulation_networks.blocks.nodes;

import com.circulation.circulation_networks.tiles.nodes.TileEntityChargingNode;

public final class BlockChargingNode extends PedestalRequiredNodeBlock {

    public BlockChargingNode() {
        super("charging_node");
        this.setNodeTileClass(TileEntityChargingNode.class);
    }
}