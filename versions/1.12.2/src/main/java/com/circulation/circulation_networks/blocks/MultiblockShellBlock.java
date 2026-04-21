package com.circulation.circulation_networks.blocks;

import com.circulation.circulation_networks.tiles.TileEntityMultiblockShell;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.circulation.circulation_networks.CirculationFlowNetworks.MOD_ID;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
public class MultiblockShellBlock extends Block {

    public MultiblockShellBlock() {
        super(Material.IRON);
        this.setRegistryName(new ResourceLocation(MOD_ID, "multiblock_shell"));
        this.setTranslationKey(MOD_ID + ".multiblock_shell");
        this.setHardness(1.5F);
        this.setResistance(10.0F);
        TileEntity.register(MOD_ID + ":multiblock_shell", TileEntityMultiblockShell.class);
    }

    @Nullable
    private static BlockPos getOriginPos(IBlockAccess world, BlockPos shellPos) {
        TileEntity te = world.getTileEntity(shellPos);
        if (te instanceof TileEntityMultiblockShell shell && shell.canRedirect()) {
            return shell.getOriginPos();
        }
        return null;
    }

    @NotNull
    public static BlockPos resolveRedirectedPos(IBlockAccess world, BlockPos pos) {
        BlockPos originPos = getOriginPos(world, pos);
        return originPos != null ? originPos : pos;
    }

    public static boolean canBeReplacedAt(World world, BlockPos shellPos) {
        BlockPos origin = getOriginPos(world, shellPos);
        if (origin == null) {
            return false;
        }
        IBlockState originState = world.getBlockState(origin);
        return world.isAirBlock(origin) || originState.getBlock() instanceof MultiblockShellBlock;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    @NotNull
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityMultiblockShell();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return true;
    }

    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos, IBlockState blockState, EntityLivingBase entity, int numberOfParticles) {
        return true;
    }

    @Override
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity) {
        return true;
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 0;
    }

    @Override
    public float getAmbientOcclusionLightValue(IBlockState state) {
        return 1.0F;
    }

    @Override
    @NotNull
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    @NotNull
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return false;
    }

    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                    EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        BlockPos originPos = getOriginPos(worldIn, pos);
        if (originPos != null) {
            IBlockState originState = worldIn.getBlockState(originPos);
            return originState.getBlock().onBlockActivated(worldIn, originPos, originState, playerIn, hand, facing, hitX, hitY, hitZ);
        }
        return false;
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos,
                                   EntityPlayer player, boolean willHarvest) {
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
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos,
                             IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        worldIn.setBlockToAir(pos);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos,
                         IBlockState state, int fortune) {
        BlockPos originPos = getOriginPos(world, pos);
        if (originPos != null) {
            IBlockState originState = world.getBlockState(originPos);
            originState.getBlock().getDrops(drops, world, originPos, originState, fortune);
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        BlockPos originPos = getOriginPos(worldIn, pos);
        if (originPos != null && !worldIn.isAirBlock(originPos)) {
            worldIn.setBlockToAir(originPos);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player,
                                                World worldIn, BlockPos pos) {
        BlockPos originPos = getOriginPos(worldIn, pos);
        if (originPos != null) {
            IBlockState originState = worldIn.getBlockState(originPos);
            return originState.getPlayerRelativeBlockHardness(player, worldIn, originPos);
        }
        return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
    }

    @Override
    @NotNull
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target,
                                  World world, BlockPos pos, EntityPlayer player) {
        BlockPos originPos = getOriginPos(world, pos);
        if (originPos != null) {
            IBlockState originState = world.getBlockState(originPos);
            return originState.getBlock().getPickBlock(originState, target, world, originPos, player);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos,
                                Block blockIn, BlockPos fromPos) {
        BlockPos originPos = getOriginPos(worldIn, pos);
        if (originPos != null) {
            IBlockState originState = worldIn.getBlockState(originPos);
            originState.neighborChanged(worldIn, originPos, blockIn, fromPos);
        }
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos) {
        BlockPos originPos = getOriginPos(worldIn, pos);
        if (originPos != null) {
            IBlockState originState = worldIn.getBlockState(originPos);
            return originState.getComparatorInputOverride(worldIn, originPos);
        }
        return 0;
    }

    @Override
    @NotNull
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state,
                                            BlockPos pos, EnumFacing face) {
        BlockPos originPos = getOriginPos(worldIn, pos);
        if (originPos != null) {
            IBlockState originState = worldIn.getBlockState(originPos);
            return originState.getBlockFaceShape(worldIn, originPos, face);
        }
        return BlockFaceShape.SOLID;
    }
}
