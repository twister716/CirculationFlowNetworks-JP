package com.circulation.circulation_networks.blocks.nodes;

import com.circulation.circulation_networks.CFNConfig;
import com.circulation.circulation_networks.blocks.BlockNodePedestal;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public abstract class PedestalRequiredNodeBlock extends BaseNodeBlock {

    protected PedestalRequiredNodeBlock(String name) {
        super(name);
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
    public boolean canPlaceBlockAt(@NotNull World worldIn, @NotNull BlockPos pos) {
        BlockPos below = pos.down();
        IBlockState stateBelow = worldIn.getBlockState(below);
        return stateBelow.getBlock() instanceof BlockNodePedestal && super.canPlaceBlockAt(worldIn, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@NotNull IBlockState state, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos) {
        if (!worldIn.isRemote && !(worldIn.getBlockState(pos.down()).getBlock() instanceof BlockNodePedestal)) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }
}
