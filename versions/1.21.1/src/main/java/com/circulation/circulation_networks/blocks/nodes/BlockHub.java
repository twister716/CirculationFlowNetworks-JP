package com.circulation.circulation_networks.blocks.nodes;

import com.circulation.circulation_networks.api.node.IHubNode;
import com.circulation.circulation_networks.blocks.MultiblockShellBlock;
import com.circulation.circulation_networks.registry.CFNBlockEntityTypes;
import com.circulation.circulation_networks.registry.CFNBlocks;
import com.circulation.circulation_networks.tiles.MultiblockShellBlockEntity;
import com.circulation.circulation_networks.tiles.nodes.HubBlockEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class BlockHub extends BaseNodeBlock {

    private static final List<BlockPos> positions = new ObjectArrayList<>(17);

    public BlockHub() {
        super(metalPropertiesNoOcclusion().pushReaction(PushReaction.BLOCK),
            () -> CFNBlockEntityTypes.HUB);
    }

    private static List<BlockPos> shellPositions(BlockPos origin) {
        if (positions.isEmpty()) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = 0; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        positions.add(origin.offset(dx, dy, dz));
                    }
                }
            }
        }
        return positions;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockPos origin = context.getClickedPos();
        Level level = context.getLevel();
        for (BlockPos shellPos : shellPositions(origin)) {
            if (shellPos.getY() < level.getMinBuildHeight() || shellPos.getY() >= level.getMaxBuildHeight())
                return null;
            if (!level.getBlockState(shellPos).canBeReplaced(context)) return null;
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer instanceof Player player && !level.isClientSide()) {
            var be = level.getBlockEntity(pos);
            if (be instanceof HubBlockEntity hub) {
                IHubNode node = hub.getNode();
                if (node != null) {
                    node.setOwner(player.getUUID());
                }
            }
        }
        if (!level.isClientSide()) {
            var shellState = CFNBlocks.blockMultiblockShell.defaultBlockState();
            for (BlockPos shellPos : shellPositions(pos)) {
                level.setBlock(shellPos, shellState, 3);
                var shellBE = level.getBlockEntity(shellPos);
                if (shellBE instanceof MultiblockShellBlockEntity shell) {
                    shell.setOriginPos(pos);
                }
            }
        }
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            var be = level.getBlockEntity(pos);
            if (be instanceof HubBlockEntity hub) {
                var inv = hub.getPlugins();
                for (int i = 0; i < inv.getSlots(); i++) {
                    var plugin = inv.getStackInSlot(i);
                    if (!plugin.isEmpty()) {
                        level.addFreshEntity(new ItemEntity(level,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            plugin.copy()));
                    }
                }
            }
            for (BlockPos shellPos : shellPositions(pos)) {
                if (level.getBlockState(shellPos).getBlock() instanceof MultiblockShellBlock) {
                    level.removeBlock(shellPos, false);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
