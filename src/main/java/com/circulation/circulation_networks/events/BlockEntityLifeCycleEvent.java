package com.circulation.circulation_networks.events;

//? if <1.20 {
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;
//?} else if <1.21 {
/*import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.eventbus.api.Event;
*///?} else {
/*import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.Event;
*///?}

// Shared lifecycle event for block entities across supported loaders.
public class BlockEntityLifeCycleEvent extends Event {

    //? if <1.20 {
    private final World world;
    private final BlockPos pos;
    private final TileEntity blockEntity;

    public BlockEntityLifeCycleEvent(World world, BlockPos pos, TileEntity blockEntity) {
    //?} else {
    /*private final Level world;
    private final BlockPos pos;
    private final BlockEntity blockEntity;

    public BlockEntityLifeCycleEvent(Level world, BlockPos pos, BlockEntity blockEntity) {
    *///?}
        this.world = world;
        this.pos = pos;
        this.blockEntity = blockEntity;
    }

    //? if <1.20 {
    public World getWorld() {
        return world;
    }
    //?} else {
    /*public Level getWorld() {
        return world;
    }
    *///?}

    public BlockPos getPos() {
        return pos;
    }

    //? if <1.20 {
    public TileEntity getBlockEntity() {
        return blockEntity;
    }
    //?} else {
    /*public BlockEntity getBlockEntity() {
        return blockEntity;
    }
    *///?}

    public static class Validate extends BlockEntityLifeCycleEvent {

        //? if <1.20 {
        public Validate(World world, BlockPos pos, TileEntity blockEntity) {
        //?} else {
        /*public Validate(Level world, BlockPos pos, BlockEntity blockEntity) {
        *///?}
            super(world, pos, blockEntity);
        }
    }

    public static class Invalidate extends BlockEntityLifeCycleEvent {

        //? if <1.20 {
        public Invalidate(World world, BlockPos pos, TileEntity blockEntity) {
        //?} else {
        /*public Invalidate(Level world, BlockPos pos, BlockEntity blockEntity) {
        *///?}
            super(world, pos, blockEntity);
        }
    }
}