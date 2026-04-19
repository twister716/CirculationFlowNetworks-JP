package com.circulation.circulation_networks.blocks;

import com.circulation.circulation_networks.tiles.BaseTileEntity;
import com.circulation.circulation_networks.tiles.TileEntityCirculationShielder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("deprecation")
public final class BlockCirculationShielder extends BaseBlock {

    public BlockCirculationShielder() {
        super("circulation_shielder");
        TileEntity.register(Objects.requireNonNull(getRegistryName()).toString(), TileEntityCirculationShielder.class);
    }

    public boolean hasGui() {
        return true;
    }

    @Override
    public boolean isOpaqueCube(@NotNull IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(@NotNull IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(@NotNull IBlockState state) {
        return false;
    }

    @Override
    public int getLightOpacity(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos) {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos, @NotNull EnumFacing side) {
        return true;
    }

    @Override
    public boolean doesSideBlockRendering(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos, @NotNull EnumFacing face) {
        return false;
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
