package com.circulation.circulation_networks.manager;

import com.circulation.circulation_networks.api.IPhaseInterrupterBlockEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
//? if <1.20 {
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
//?} else {
/*import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
*///?}

public final class PhaseInterrupterManager {

    public static final PhaseInterrupterManager INSTANCE = new PhaseInterrupterManager();

    private final Int2ObjectMap<ReferenceSet<IPhaseInterrupterBlockEntity>> dimInterrupters = new Int2ObjectOpenHashMap<>();

    public PhaseInterrupterManager() {
        dimInterrupters.defaultReturnValue(ReferenceSets.emptySet());
    }

    public Int2ObjectMap<ReferenceSet<IPhaseInterrupterBlockEntity>> getDimInterrupters() {
        return dimInterrupters;
    }

    public ReferenceSet<IPhaseInterrupterBlockEntity> getInterruptersForDim(int dimId) {
        return dimInterrupters.get(dimId);
    }

    public void register(IPhaseInterrupterBlockEntity interrupter, int dimId) {
        if (interrupter == null) return;

        ReferenceSet<IPhaseInterrupterBlockEntity> set = dimInterrupters.get(dimId);
        if (set == dimInterrupters.defaultReturnValue()) {
            dimInterrupters.put(dimId, set = new ReferenceOpenHashSet<>());
        }
        set.add(interrupter);
    }

    public void unregister(IPhaseInterrupterBlockEntity interrupter, int dimId) {
        if (interrupter == null) return;

        var interrupters = dimInterrupters.get(dimId);
        if (interrupters == null) return;
        interrupters.remove(interrupter);
    }

    //? if <1.20 {
    public boolean isBlockedByInterrupter(BlockPos tePos, World world) {
    //?} else {
    /*public boolean isBlockedByInterrupter(BlockPos tePos, Level world) {
    *///?}
        if (world == null || isClientWorld(world)) return false;
        int dimId = getDimensionId(world);

        var interrupters = dimInterrupters.get(dimId);
        if (interrupters == null || interrupters.isEmpty()) return false;

        for (IPhaseInterrupterBlockEntity interrupter : interrupters) {
            if (!interrupter.isActive()) continue;
            if (!interrupter.checkScope(tePos)) continue;
            return true;
        }

        return false;
    }

    //? if <1.20 {
    private static boolean isClientWorld(World world) {
        return world.isRemote;
    }

    private static int getDimensionId(World world) {
        return world.provider.getDimension();
    }
    //?} else {
    /*private static boolean isClientWorld(Level world) {
        return world.isClientSide;
    }

    private static int getDimensionId(Level world) {
        return world.dimension().location().hashCode();
    }
    *///?}
}