package com.circulation.circulation_networks.blocks.nodes;

import com.circulation.circulation_networks.blocks.BlockNodePedestal;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class PedestalRequiredNodeBlock extends BaseNodeBlock {

    protected PedestalRequiredNodeBlock(String name) {
        super(name);
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
