package com.circulation.circulation_networks.blocks.nodes;

import com.circulation.circulation_networks.tiles.nodes.TileEntityElectromagneticInductionTower;

public final class BlockElectromagneticInductionTower extends BaseNodeBlock {

    public BlockElectromagneticInductionTower() {
        super("electromagnetic_induction_tower");
        this.setNodeTileClass(TileEntityElectromagneticInductionTower.class);
    }

}