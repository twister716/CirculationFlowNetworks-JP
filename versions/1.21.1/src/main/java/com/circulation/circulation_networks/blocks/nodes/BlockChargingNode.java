package com.circulation.circulation_networks.blocks.nodes;

import com.circulation.circulation_networks.registry.CFNBlockEntityTypes;

public final class BlockChargingNode extends PedestalRequiredNodeBlock {

    public BlockChargingNode() {
        super(metalPropertiesNoOcclusion(),
            () -> CFNBlockEntityTypes.CHARGING_NODE);
    }
}
