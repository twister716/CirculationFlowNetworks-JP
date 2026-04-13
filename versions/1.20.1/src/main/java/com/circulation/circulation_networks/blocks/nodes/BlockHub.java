package com.circulation.circulation_networks.blocks.nodes;

import com.circulation.circulation_networks.api.node.IHubNode;
import com.circulation.circulation_networks.registry.CFNBlockEntityTypes;
import com.circulation.circulation_networks.tiles.nodes.HubBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BlockHub extends BaseNodeBlock {

    public BlockHub() {
        super(metalPropertiesNoOcclusion(),
            () -> CFNBlockEntityTypes.HUB);
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
    }

    @Override
    @SuppressWarnings("deprecation")
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
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
