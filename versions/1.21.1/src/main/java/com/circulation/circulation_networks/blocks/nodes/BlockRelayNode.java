package com.circulation.circulation_networks.blocks.nodes;

import com.circulation.circulation_networks.registry.CFNBlockEntityTypes;
public final class BlockRelayNode extends PedestalRequiredNodeBlock {

    public BlockRelayNode() {
        super(metalProperties(),
            () -> CFNBlockEntityTypes.RELAY_NODE);
    }
}
