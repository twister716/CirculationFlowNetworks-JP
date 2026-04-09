package com.circulation.circulation_networks.blocks.nodes;

import com.circulation.circulation_networks.CFNConfig;
import com.circulation.circulation_networks.registry.CFNBlockEntityTypes;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public final class BlockRelayNode extends PedestalRequiredNodeBlock {

    public BlockRelayNode() {
        super(metalProperties(),
            () -> CFNBlockEntityTypes.RELAY_NODE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return CFNConfig.NODE.rendering.animatedSpecialModels
            ? RenderShape.ENTITYBLOCK_ANIMATED
            : RenderShape.MODEL;
    }
}
