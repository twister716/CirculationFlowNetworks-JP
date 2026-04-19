package com.circulation.circulation_networks.blocks;

import com.circulation.circulation_networks.tiles.CirculationShielderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public final class BlockCirculationShielder extends BaseBlock {

    public BlockCirculationShielder() {
        super(metalPropertiesNoOcclusion());
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public boolean isOcclusionShapeFullBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return false;
    }

    @NotNull
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CirculationShielderBlockEntity(pos, state);
    }
}
