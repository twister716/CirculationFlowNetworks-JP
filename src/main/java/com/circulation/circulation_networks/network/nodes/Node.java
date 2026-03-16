package com.circulation.circulation_networks.network.nodes;

import com.circulation.circulation_networks.api.IGrid;
import com.circulation.circulation_networks.api.INodeBlockEntity;
import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.math.Vec3d;
import com.circulation.circulation_networks.math.Vec3i;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
//? if <1.20 {
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
//?} else {
/*import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
*///?}
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

public abstract class Node implements INode {

    private final BlockPos pos;
    private final Vec3d vec3d;
    //? if <1.20 {
    private final WeakReference<World> world;
    //?} else {
    /*private final WeakReference<Level> world;
    *///?}
    private final int dimensionId;
    private final ReferenceSet<INode> neighbors = new ReferenceOpenHashSet<>();
    private final Reference2DoubleMap<INode> distanceMap = new Reference2DoubleOpenHashMap<>();
    private final double linkScope;
    private final double linkScopeSq;
    private boolean active;
    private IGrid grid;

    //? if <1.20 {
    public Node(NBTTagCompound nbt) {
        this.dimensionId = nbt.getInteger("dim");
        this.world = new WeakReference<>(resolveWorld(dimensionId));
        this.pos = BlockPos.fromLong(nbt.getLong("pos"));
        this.vec3d = Vec3d.ofCenter(new Vec3i(pos.getX(), pos.getY(), pos.getZ()));
        this.linkScope = nbt.getDouble("linkScope");
        this.linkScopeSq = linkScope * linkScope;
    }
    //?} else {
    /*public Node(CompoundTag nbt) {
        this.dimensionId = nbt.getInt("dim");
        this.world = new WeakReference<>(null);
        this.pos = BlockPos.of(nbt.getLong("pos"));
        this.vec3d = Vec3d.ofCenter(new Vec3i(pos.getX(), pos.getY(), pos.getZ()));
        this.linkScope = nbt.getDouble("linkScope");
        this.linkScopeSq = linkScope * linkScope;
    }
    *///?}

    public Node(INodeBlockEntity blockEntity, double linkScope) {
        this.dimensionId = getDimensionId(blockEntity);
        this.world = new WeakReference<>(blockEntity.getNodeWorld());
        this.pos = blockEntity.getNodePos();
        this.vec3d = Vec3d.ofCenter(new Vec3i(pos.getX(), pos.getY(), pos.getZ()));
        this.linkScope = linkScope;
        this.linkScopeSq = linkScope * linkScope;
    }

    public @NotNull BlockPos getPos() {
        return pos;
    }

    public @NotNull Vec3d getVec3d() {
        return vec3d;
    }

    public boolean isActive() {
        return active;
    }

    //? if <1.20 {
    @Override
    public NBTTagCompound serialize() {
        var nbt = new NBTTagCompound();
        nbt.setString("name", this.getClass().getName());
        nbt.setLong("pos", pos.toLong());
        nbt.setInteger("dim", dimensionId);
        var list = new NBTTagList();
        neighbors.forEach(neighbor -> list.appendTag(new NBTTagLong(neighbor.getPos().toLong())));
        nbt.setTag("neighbors", list);
        nbt.setDouble("linkScope", linkScope);
        return nbt;
    }
    //?} else {
    /*@Override
    public CompoundTag serialize() {
        var nbt = new CompoundTag();
        nbt.putString("name", this.getClass().getName());
        nbt.putLong("pos", pos.asLong());
        nbt.putInt("dim", dimensionId);
        var list = new ListTag();
        neighbors.forEach(neighbor -> list.add(LongTag.valueOf(neighbor.getPos().asLong())));
        nbt.put("neighbors", list);
        nbt.putDouble("linkScope", linkScope);
        return nbt;
    }
    *///?}

    //? if <1.20 {
    public @NotNull World getWorld() {
        var cachedWorld = world.get();
        if (cachedWorld != null) {
            return cachedWorld;
        }
        var resolvedWorld = resolveWorld(dimensionId);
        if (resolvedWorld != null) {
            return resolvedWorld;
        }
        throw new IllegalStateException("World is null");
    }
    //?} else {
    /*public @NotNull Level getWorld() {
        var cachedWorld = world.get();
        if (cachedWorld != null) {
            return cachedWorld;
        }
        throw new IllegalStateException("World is null");
    }
    *///?}

    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            grid = null;
            clearNeighbors();
        }
    }

    @Override
    public ReferenceSet<INode> getNeighbors() {
        return ReferenceSets.unmodifiable(neighbors);
    }

    @Override
    public void addNeighbor(INode neighbor) {
        if (neighbor == null || !neighbor.isActive()) return;
        neighbors.add(neighbor);
        distanceMap.put(neighbor, distanceSq(neighbor));
    }

    @Override
    public void removeNeighbor(INode neighbor) {
        if (neighbor == null) return;
        neighbors.remove(neighbor);
        distanceMap.remove(neighbor);
    }

    @Override
    public void clearNeighbors() {
        neighbors.clear();
        distanceMap.clear();
    }

    @Override
    public IGrid getGrid() {
        return grid;
    }

    @Override
    public void setGrid(IGrid grid) {
        this.grid = grid;
    }

    //? if <1.20 {
    @Override
    public TileEntity getBlockEntity() {
        var cachedWorld = world.get();
        if (cachedWorld != null) {
            var tileEntity = cachedWorld.getTileEntity(pos);
            if (tileEntity instanceof INodeBlockEntity) {
                return tileEntity;
            }
        }
        return null;
    }
    //?} else {
    /*@Override
    public BlockEntity getBlockEntity() {
        var cachedWorld = world.get();
        if (cachedWorld != null) {
            var blockEntity = cachedWorld.getBlockEntity(pos);
            if (blockEntity instanceof INodeBlockEntity) {
                return blockEntity;
            }
        }
        return null;
    }
    *///?}

    @Override
    public double distanceSq(INode node) {
        if (distanceMap.containsKey(node)) {
            return distanceMap.get(node);
        }
        return this.distanceSq(node.getVec3d());
    }

    @Override
    public double getLinkScope() {
        return linkScope;
    }

    @Override
    public double getLinkScopeSq() {
        return linkScopeSq;
    }

    @Override
    public double distanceSq(BlockPos node) {
        return this.vec3d.squareDistanceTo(node.getX() + 0.5d, node.getY() + 0.5d, node.getZ() + 0.5d);
    }

    @Override
    public double distanceSq(Vec3d pos) {
        return this.vec3d.squareDistanceTo(pos);
    }

    @Override
    public final LinkType linkScopeCheck(INode node) {
        var dist = this.distanceSq(node);
        boolean canConnectAtoB = dist <= this.getLinkScopeSq();
        boolean canConnectBtoA = dist <= node.getLinkScopeSq();

        if (canConnectAtoB && canConnectBtoA) {
            return LinkType.DOUBLY;
        } else if (canConnectAtoB) {
            return LinkType.A_TO_B;
        } else if (canConnectBtoA) {
            return LinkType.B_TO_A;
        }
        return LinkType.DISCONNECT;
    }

    //? if <1.20 {
    private static World resolveWorld(int dimensionId) {
        return DimensionManager.getWorld(dimensionId);
    }

    private static int getDimensionId(INodeBlockEntity blockEntity) {
        return blockEntity.getNodeWorld().provider.getDimension();
    }
    //?} else {
    /*private static Level resolveWorld(int dimensionId) {
        return null;
    }

    private static int getDimensionId(INodeBlockEntity blockEntity) {
        return blockEntity.getNodeWorld().dimension().location().hashCode();
    }
    *///?}
}