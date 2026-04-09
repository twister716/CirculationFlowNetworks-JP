package com.circulation.circulation_networks.blocks;

import com.circulation.circulation_networks.CFNConfig;
import com.circulation.circulation_networks.registry.CFNBlockEntityTypes;
import com.circulation.circulation_networks.tiles.NodePedestalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BlockNodePedestal extends BaseBlock {

    public BlockNodePedestal() {
        super(metalPropertiesNoOcclusion());
    }

    @Override
    public boolean hasGui() {
        return false;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new NodePedestalBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return CFNConfig.NODE.rendering.animatedSpecialModels
            ? RenderShape.ENTITYBLOCK_ANIMATED
            : RenderShape.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 10;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOcclusionShapeFullBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean skipRendering(@NotNull BlockState state, @NotNull BlockState adjacentState, @NotNull Direction side) {
        return false;
    }
}
