package com.circulation.circulation_networks.tiles;

import com.circulation.circulation_networks.registry.CFNBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockEntityNodePedestal extends BaseCFNBlockEntity {

    public BlockEntityNodePedestal(BlockPos pos, BlockState state) {
        super(CFNBlockEntityTypes.NODE_PEDESTAL, pos, state);
    }
}
