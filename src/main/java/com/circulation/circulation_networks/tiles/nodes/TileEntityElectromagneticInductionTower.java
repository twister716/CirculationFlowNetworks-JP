package com.circulation.circulation_networks.tiles.nodes;

import com.circulation.circulation_networks.CFNConfig;
import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.network.nodes.ChargingNode;
import org.jetbrains.annotations.NotNull;

public class TileEntityElectromagneticInductionTower extends BaseNodeTileEntity {

    @Override
    protected @NotNull INode createNode() {
        return new ChargingNode(this,
            CFNConfig.NODE.electromagneticInductionTower.chargingScope,
            CFNConfig.NODE.electromagneticInductionTower.linkScope);
    }

}
