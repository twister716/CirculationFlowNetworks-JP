package com.circulation.circulation_networks.blocks;

import com.circulation.circulation_networks.tiles.BlockEntityNodePedestal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BlockNodePedestal extends BaseBlock {

    public BlockNodePedestal(ResourceKey<Block> id) {
        super(metalPropertiesNoOcclusion().setId(id));
    }

    @Override
    public boolean hasGui() {
        return false;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BlockEntityNodePedestal(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 10;
    }

    @Override
    public boolean isCollisionShapeFullBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public boolean skipRendering(@NotNull BlockState state, @NotNull BlockState adjacentState, @NotNull Direction side) {
        return false;
    }
}
