package com.circulation.circulation_networks.blocks.nodes;

import com.circulation.circulation_networks.CFNConfig;
import com.circulation.circulation_networks.registry.CFNBlockEntityTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public final class BlockChargingNode extends PedestalRequiredNodeBlock {

    public BlockChargingNode(ResourceKey<Block> id) {
        super(metalPropertiesNoOcclusion().setId(id),
            () -> CFNBlockEntityTypes.CHARGING_NODE);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return CFNConfig.NODE.rendering.animatedSpecialModels
            ? RenderShape.INVISIBLE
            : RenderShape.MODEL;
    }
}
