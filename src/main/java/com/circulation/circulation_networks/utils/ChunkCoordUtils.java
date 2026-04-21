package com.circulation.circulation_networks.utils;

//~ mc_imports

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class ChunkCoordUtils {

    private ChunkCoordUtils() {
    }

    public static long mergeChunkCoords(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }

    public static int getChunkX(BlockPos pos) {
        return pos.getX() >> 4;
    }

    public static int getChunkZ(BlockPos pos) {
        return pos.getZ() >> 4;
    }

    //~ if >=1.20 '(World ' -> '(Level ' {
    public static boolean isChunkLoaded(World world, int chunkX, int chunkZ) {
        //? if <1.20 {
        return world.getChunkProvider().getLoadedChunk(chunkX, chunkZ) != null;
        //?} else {
        /*return world.getChunkSource().hasChunk(chunkX, chunkZ);
        *///?}
    }
    //~}

    //~ if >=1.20 '(World ' -> '(Level ' {
    public static boolean isChunkLoaded(World world, BlockPos pos) {
        return isChunkLoaded(world, getChunkX(pos), getChunkZ(pos));
    }
    //~}

    public static long mergeChunkCoords(BlockPos pos) {
        return mergeChunkCoords(getChunkX(pos), getChunkZ(pos));
    }
}
