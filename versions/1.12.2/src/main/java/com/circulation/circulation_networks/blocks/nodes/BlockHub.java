package com.circulation.circulation_networks.blocks.nodes;

import com.circulation.circulation_networks.api.node.IHubNode;
import com.circulation.circulation_networks.blocks.MultiblockShellBlock;
import com.circulation.circulation_networks.registry.CFNBlocks;
import com.circulation.circulation_networks.tiles.TileEntityMultiblockShell;
import com.circulation.circulation_networks.tiles.nodes.TileEntityHub;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
public final class BlockHub extends BaseNodeBlock {

    public BlockHub() {
        super("hub");
        this.setNodeTileClass(TileEntityHub.class);
    }

    private static final Long2ObjectMap<List<BlockPos>> positions = new Long2ObjectOpenHashMap<>();

    private static List<BlockPos> shellPositions(BlockPos origin) {
        if (!positions.containsKey(origin.toLong())) {
            var list = new ObjectArrayList<BlockPos>();
            positions.put(origin.toLong(), list);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = 0; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        list.add(origin.add(dx, dy, dz));
                    }
                }
            }
            return list;
        }
        return positions.get(origin.toLong());
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (BlockPos shellPos : shellPositions(pos)) {
            if (shellPos.getY() < 0 || shellPos.getY() > 255) return false;
            if (!worldIn.getBlockState(shellPos).getBlock().isReplaceable(worldIn, shellPos)) return false;
        }
        return super.canPlaceBlockAt(worldIn, pos);
    }

    @NotNull
    @Override
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
                                EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (placer instanceof EntityPlayer player && !worldIn.isRemote) {
            var te = worldIn.getTileEntity(pos);
            if (te instanceof TileEntityHub hub) {
                IHubNode node = hub.getNode();
                if (node != null) {
                    node.setOwner(player.getUniqueID());
                }
            }
        }
        if (!worldIn.isRemote) {
            var shellState = CFNBlocks.blockMultiblockShell.getDefaultState();
            for (BlockPos shellPos : shellPositions(pos)) {
                worldIn.setBlockState(shellPos, shellState, 3);
                var shellTE = worldIn.getTileEntity(shellPos);
                if (shellTE instanceof TileEntityMultiblockShell shell) {
                    shell.setOriginPos(pos);
                }
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        var te = worldIn.getTileEntity(pos);
        if (te instanceof TileEntityHub hub) {
            var inv = hub.getPlugins();
            for (int i = 0; i < inv.getSlots(); i++) {
                var plugin = inv.getStackInSlot(i);
                if (!plugin.isEmpty()) {
                    worldIn.spawnEntity(new EntityItem(worldIn,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        plugin.copy()));
                }
            }
        }
        for (BlockPos shellPos : shellPositions(pos)) {
            if (worldIn.getBlockState(shellPos).getBlock() instanceof MultiblockShellBlock) {
                worldIn.setBlockToAir(shellPos);
            }
        }
        positions.remove(pos.toLong());
        super.breakBlock(worldIn, pos, state);
    }

}