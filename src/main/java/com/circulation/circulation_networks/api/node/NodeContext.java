package com.circulation.circulation_networks.api.node;

import com.circulation.circulation_networks.utils.WorldResolveCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NodeContext {

    private final Level world;
    private final BlockPos pos;
    private final String defaultName;
    private final String visualId;

    private NodeContext(Level world, BlockPos pos, String defaultName, String visualId) {
        this.world = world;
        this.pos = pos;
        this.defaultName = defaultName;
        this.visualId = visualId;
    }

    public static NodeContext fromWorld(@NotNull Level world, @NotNull BlockPos pos) {
        return new NodeContext(world, pos, resolveDefaultName(world, pos), resolveVisualId(world, pos));
    }

    public static NodeContext of(@NotNull Level world, @NotNull BlockPos pos, @Nullable String defaultName, @NotNull String visualId) {
        return new NodeContext(world, pos, defaultName, visualId);
    }

    private static String resolveDefaultName(Level world, BlockPos pos) {
        return null;
    }

    private static String resolveVisualId(Level world, BlockPos pos) {
        return WorldResolveCompat.getBlockVisualId(world, pos);
    }

    public @NotNull Level getWorld() {
        return world;
    }

    public @NotNull BlockPos getPos() {
        return pos;
    }

    public @NotNull String getDefaultName() {
        return defaultName;
    }

    public @NotNull String getVisualId() {
        return visualId;
    }
}
