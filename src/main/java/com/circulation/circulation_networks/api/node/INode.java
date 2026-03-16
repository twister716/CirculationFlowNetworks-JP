package com.circulation.circulation_networks.api.node;

import com.circulation.circulation_networks.api.IGrid;
import com.circulation.circulation_networks.math.Vec3d;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
//? if <1.20 {
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
//?} else {
/*import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
*///?}

import javax.annotation.Nonnull;

public interface INode {

    @Nonnull
    BlockPos getPos();

    @Nonnull
    Vec3d getVec3d();

    @Nonnull
    // Only the world carrier type differs between versions.
    //? if <1.20 {
    World getWorld();
    //?} else {
    /*Level getWorld();
    *///?}

    boolean isActive();

    void setActive(boolean active);

    double getLinkScope();

    double getLinkScopeSq();

    ReferenceSet<INode> getNeighbors();

    void addNeighbor(INode neighbor);

    void removeNeighbor(INode neighbor);

    void clearNeighbors();

    IGrid getGrid();

    void setGrid(IGrid grid);

    // Only the block entity carrier type differs between versions.
    //? if <1.20 {
    TileEntity getBlockEntity();
    //?} else {
    /*BlockEntity getBlockEntity();
    *///?}

    double distanceSq(INode node);

    double distanceSq(BlockPos node);

    double distanceSq(Vec3d node);

    LinkType linkScopeCheck(INode node);

    // Only the NBT carrier type differs between versions.
    //? if <1.20 {
    NBTTagCompound serialize();
    //?} else {
    /*CompoundTag serialize();
    *///?}

    enum LinkType {
        DOUBLY,
        A_TO_B,
        B_TO_A,
        DISCONNECT
    }
}
