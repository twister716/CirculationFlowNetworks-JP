package com.circulation.circulation_networks.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("resource")
public final class WorldResolveCompat {

    private WorldResolveCompat() {
    }

    private static @Nullable MinecraftServer getCurrentServer() {
        return net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
    }

    public static @Nullable MinecraftServer currentServer() {
        return getCurrentServer();
    }

    public static @Nullable Level resolveWorld(Int2ObjectMap<String> serializedDimensionKeys, int dimId) {
        var server = getCurrentServer();
        if (server == null) {
            return null;
        }

        String dimensionKey = serializedDimensionKeys.get(dimId);
        if (dimensionKey != null && !dimensionKey.isEmpty()) {
            var level = server.getLevel(DimensionHelper.createDimensionKey(dimensionKey));
            if (level != null) {
                return level;
            }
        }

        for (Level level : server.getAllLevels()) {
            if (DimensionHelper.getDimensionHash(level) == dimId) {
                return level;
            }
        }
        return null;
    }

    public static @Nullable Level resolveWorld(String serializedDimensionKey, int dimId) {
        var server = getCurrentServer();
        if (server == null) {
            return null;
        }

        if (serializedDimensionKey != null && !serializedDimensionKey.isEmpty()) {
            var level = server.getLevel(DimensionHelper.createDimensionKey(serializedDimensionKey));
            if (level != null) {
                return level;
            }
        }

        for (Level level : server.getAllLevels()) {
            if (DimensionHelper.getDimensionHash(level) == dimId) {
                return level;
            }
        }
        return null;
    }

    public static boolean isRegisteredDimension(String dimKey) {
        var server = getCurrentServer();
        if (server == null) {
            return false;
        }
        return server.getLevel(DimensionHelper.createDimensionKey(dimKey)) != null;
    }

    public static boolean isClientWorld(Level world) {
        return world.isClientSide();
    }

    public static java.util.List<? extends net.minecraft.world.entity.player.Player> getPlayers(Level world) {
        return world.players();
    }

    public static int getDimensionId(Level world) {
        return DimensionHelper.getDimensionHash(world);
    }

    public static String getSerializedDimensionKey(Level world) {
        return DimensionHelper.getDimensionId(world);
    }

    public static int getPlayerDimensionId(net.minecraft.server.level.ServerPlayer player) {
        return DimensionHelper.getDimensionHash(player.level());
    }

    public static int getPlayerDimensionId(net.minecraft.world.entity.player.Player player) {
        return DimensionHelper.getDimensionHash(player.level());
    }

    public static String getBlockVisualId(Level world, net.minecraft.core.BlockPos pos) {
        return ResourceIdCompat.getBlockId(world.getBlockState(pos).getBlock());
    }

    @Nullable
    public static net.minecraft.world.level.block.entity.BlockEntity getBlockEntity(Level world, net.minecraft.core.BlockPos pos) {
        return world.getBlockEntity(pos);
    }

    public static void destroyBlock(Level world, net.minecraft.core.BlockPos pos) {
        world.destroyBlock(pos, true, null);
    }

    public static double getPlayerDistanceSq(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos) {
        double dx = player.getX() - (pos.getX() + 0.5D);
        double dy = player.getY() - (pos.getY() + 1.25D);
        double dz = player.getZ() - (pos.getZ() + 0.5D);
        return dx * dx + dy * dy + dz * dz;
    }

    public static long getPackedPos(net.minecraft.world.level.block.entity.BlockEntity blockEntity) {
        return BlockPosCompat.toLong(blockEntity.getBlockPos());
    }

    public static List<ServerPlayer> getServerPlayers(MinecraftServer server) {
        return server.getPlayerList().getPlayers();
    }

    public static Collection<BlockEntity> getLoadedChunkBlockEntities(Level world, int chunkX, int chunkZ) {
        var chunk = world.getChunkSource().getChunkNow(chunkX, chunkZ);
        if (chunk == null) {
            return Collections.emptyList();
        }
        return chunk.getBlockEntities().values();
    }

    public static boolean isChunkLoaded(Level world, int chunkX, int chunkZ) {
        return world.getChunkSource().hasChunk(chunkX, chunkZ);
    }

    public static Path getRootSavePath() {
        return currentServer().getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT);
    }
}
