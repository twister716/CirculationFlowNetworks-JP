package com.circulation.circulation_networks.events;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.Event;

// Shared lifecycle event for block entities across supported loaders and versions.
public class BlockEntityLifeCycleEvent extends Event {

    private final Level world;
    private final BlockPos pos;
    private final BlockEntity blockEntity;

    public BlockEntityLifeCycleEvent(Level world, BlockPos pos, BlockEntity blockEntity) {
        this.world = world;
        this.pos = pos;
        this.blockEntity = blockEntity;
    }

    public Level getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    public static class Validate extends BlockEntityLifeCycleEvent {

        public Validate(Level world, BlockPos pos, BlockEntity blockEntity) {
            super(world, pos, blockEntity);
        }
    }

    public static class Invalidate extends BlockEntityLifeCycleEvent {

        public Invalidate(Level world, BlockPos pos, BlockEntity blockEntity) {
            super(world, pos, blockEntity);
        }
    }
}
