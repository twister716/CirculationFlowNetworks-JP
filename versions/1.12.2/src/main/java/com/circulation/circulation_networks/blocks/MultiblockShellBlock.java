package com.circulation.circulation_networks.blocks;

import com.circulation.circulation_networks.tiles.TileEntityMultiblockShell;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.circulation.circulation_networks.CirculationFlowNetworks.MOD_ID;

@SuppressWarnings("deprecation")
public class MultiblockShellBlock extends Block {

    public MultiblockShellBlock() {
        super(Material.IRON);
        this.setRegistryName(new ResourceLocation(MOD_ID, "multiblock_shell"));
        this.setTranslationKey(MOD_ID + ".multiblock_shell");
        this.setHardness(1.5F);
        this.setResistance(10.0F);
        TileEntity.register(MOD_ID + ":multiblock_shell", TileEntityMultiblockShell.class);
    }

    @Override
    public boolean hasTileEntity(@NotNull IBlockState state) {
        return true;
    }

    @Override
    @NotNull
    public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
        return new TileEntityMultiblockShell();
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
    @NotNull
    public EnumBlockRenderType getRenderType(@NotNull IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    @NotNull
    public EnumPushReaction getPushReaction(@NotNull IBlockState state) {
        return EnumPushReaction.BLOCK;
    }

    @Override
    public boolean canPlaceBlockAt(@NotNull World worldIn, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    @Nullable
    private static BlockPos getOriginPos(@NotNull IBlockAccess world, @NotNull BlockPos shellPos) {
        TileEntity te = world.getTileEntity(shellPos);
        if (te instanceof TileEntityMultiblockShell shell && shell.canRedirect()) {
            return shell.getOriginPos();
        }
        return null;
    }

    @Nullable
    private static IBlockState getOriginState(@NotNull IBlockAccess world, @NotNull BlockPos shellPos) {
        BlockPos origin = getOriginPos(world, shellPos);
        return origin != null ? world.getBlockState(origin) : null;
    }

    @Override
    public boolean onBlockActivated(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state,
                                    @NotNull EntityPlayer playerIn, @NotNull EnumHand hand, @NotNull EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        BlockPos originPos = getOriginPos(worldIn, pos);
        if (originPos != null) {
            IBlockState originState = worldIn.getBlockState(originPos);
            return originState.getBlock().onBlockActivated(worldIn, originPos, originState, playerIn, hand, facing, hitX, hitY, hitZ);
        }
        return false;
    }

    @Override
    public boolean removedByPlayer(@NotNull IBlockState state, @NotNull World world, @NotNull BlockPos pos,
                                   @NotNull EntityPlayer player, boolean willHarvest) {
        if (willHarvest) {
            return true;
        }
        BlockPos originPos = getOriginPos(world, pos);
        if (originPos != null && !world.isAirBlock(originPos)) {
            world.setBlockToAir(originPos);
        }
        return super.removedByPlayer(state, world, pos, player, false);
    }

    @Override
    public void harvestBlock(@NotNull World worldIn, @NotNull EntityPlayer player, @NotNull BlockPos pos,
                             @NotNull IBlockState state, @Nullable TileEntity te, @NotNull ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        worldIn.setBlockToAir(pos);
    }

    @Override
    public void getDrops(@NotNull NonNullList<ItemStack> drops, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                         @NotNull IBlockState state, int fortune) {
        BlockPos originPos = getOriginPos(world, pos);
        if (originPos != null) {
            IBlockState originState = world.getBlockState(originPos);
            originState.getBlock().getDrops(drops, world, originPos, originState, fortune);
        }
    }

    @Override
    public void breakBlock(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
        BlockPos originPos = getOriginPos(worldIn, pos);
        if (originPos != null && !worldIn.isAirBlock(originPos)) {
            worldIn.setBlockToAir(originPos);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public float getPlayerRelativeBlockHardness(@NotNull IBlockState state, @NotNull EntityPlayer player,
                                                @NotNull World worldIn, @NotNull BlockPos pos) {
        BlockPos originPos = getOriginPos(worldIn, pos);
        if (originPos != null) {
            IBlockState originState = worldIn.getBlockState(originPos);
            return originState.getPlayerRelativeBlockHardness(player, worldIn, originPos);
        }
        return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
    }

    @Override
    @NotNull
    public ItemStack getPickBlock(@NotNull IBlockState state, @NotNull RayTraceResult target,
                                  @NotNull World world, @NotNull BlockPos pos, @NotNull EntityPlayer player) {
        BlockPos originPos = getOriginPos(world, pos);
        if (originPos != null) {
            IBlockState originState = world.getBlockState(originPos);
            return originState.getBlock().getPickBlock(originState, target, world, originPos, player);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void neighborChanged(@NotNull IBlockState state, @NotNull World worldIn, @NotNull BlockPos pos,
                                @NotNull Block blockIn, @NotNull BlockPos fromPos) {
        BlockPos originPos = getOriginPos(worldIn, pos);
        if (originPos != null) {
            IBlockState originState = worldIn.getBlockState(originPos);
            originState.neighborChanged(worldIn, originPos, blockIn, fromPos);
        }
    }

    @Override
    public boolean hasComparatorInputOverride(@NotNull IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(@NotNull IBlockState state, @NotNull World worldIn, @NotNull BlockPos pos) {
        BlockPos originPos = getOriginPos(worldIn, pos);
        if (originPos != null) {
            IBlockState originState = worldIn.getBlockState(originPos);
            return originState.getComparatorInputOverride(worldIn, originPos);
        }
        return 0;
    }

    @Override
    @NotNull
    public BlockFaceShape getBlockFaceShape(@NotNull IBlockAccess worldIn, @NotNull IBlockState state,
                                            @NotNull BlockPos pos, @NotNull EnumFacing face) {
        BlockPos originPos = getOriginPos(worldIn, pos);
        if (originPos != null) {
            IBlockState originState = worldIn.getBlockState(originPos);
            return originState.getBlockFaceShape(worldIn, originPos, face);
        }
        return BlockFaceShape.SOLID;
    }
}
