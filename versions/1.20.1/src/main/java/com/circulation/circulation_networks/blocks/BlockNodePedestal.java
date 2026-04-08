package com.circulation.circulation_networks.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public final class BlockNodePedestal extends BaseBlock {

    public BlockNodePedestal() {
        super(metalPropertiesNoOcclusion());
    }

    @Override
    public boolean hasGui() {
        return false;
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
