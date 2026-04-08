package com.circulation.circulation_networks.blocks;

import com.circulation.circulation_networks.CFNConfig;
import com.circulation.circulation_networks.tiles.TileEntityNodePedestal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
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
public final class BlockNodePedestal extends BaseBlock {

    public BlockNodePedestal() {
        super("node_pedestal");
        TileEntity.register(Objects.requireNonNull(getRegistryName()).toString(), TileEntityNodePedestal.class);
    }

    @Override
    public boolean hasGui() {
        return false;
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
    public int getLightOpacity(@NotNull IBlockState state) {
        return 10;
    }

    @Override
    public boolean hasTileEntity(@NotNull IBlockState state) {
        return true;
    }

    @Override
    public @Nullable TileEntityNodePedestal createNewTileEntity(@Nullable World world, int meta) {
        return new TileEntityNodePedestal();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public @NotNull BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public @NotNull EnumBlockRenderType getRenderType(@NotNull IBlockState state) {
        return CFNConfig.NODE.rendering.animatedSpecialModels
            ? EnumBlockRenderType.ENTITYBLOCK_ANIMATED
            : EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isTranslucent(@NotNull IBlockState state) {
        return true;
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

    @Deprecated
    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress(@NotNull IBlockState state) {
        return true;
    }
}
