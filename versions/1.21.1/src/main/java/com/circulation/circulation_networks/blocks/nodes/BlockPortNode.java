package com.circulation.circulation_networks.blocks.nodes;

import com.circulation.circulation_networks.registry.CFNBlockEntityTypes;
public final class BlockPortNode extends PedestalRequiredNodeBlock {

    public BlockPortNode() {
        super(metalProperties(),
            () -> CFNBlockEntityTypes.PORT_NODE);
    }
}
