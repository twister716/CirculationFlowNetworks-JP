package com.circulation.circulation_networks.blocks;

import com.circulation.circulation_networks.tiles.CirculationShielderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public final class BlockCirculationShielder extends BaseBlock {

    public BlockCirculationShielder() {
        super(metalProperties());
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @NotNull
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CirculationShielderBlockEntity(pos, state);
    }
}
