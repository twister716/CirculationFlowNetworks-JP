package com.circulation.circulation_networks.blocks;

import com.circulation.circulation_networks.tiles.BaseTileEntity;
import com.circulation.circulation_networks.tiles.TileEntityCirculationShielder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class BlockCirculationShielder extends BaseBlock {

    public BlockCirculationShielder() {
        super("circulation_shielder");
        TileEntity.register(Objects.requireNonNull(getRegistryName()).toString(), TileEntityCirculationShielder.class);
    }

    public boolean hasGui() {
        return true;
    }

    @Override
    public boolean hasTileEntity(@NotNull IBlockState state) {
        return true;
    }

    @Override
    public @NotNull BaseTileEntity createNewTileEntity(@Nullable World world, int meta) {
        return new TileEntityCirculationShielder();
    }
}
