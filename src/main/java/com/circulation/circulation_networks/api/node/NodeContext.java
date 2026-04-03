package com.circulation.circulation_networks.api.node;

//~ mc_imports

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//? if <1.20 {
import net.minecraft.util.ResourceLocation;
//?} else {
/*import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ResourceLocation;
*///?}

import org.jetbrains.annotations.NotNull;

public final class NodeContext {

    //~ if >=1.20 'World ' -> 'Level ' {
    private final World world;
    //~}
    private final BlockPos pos;
    private final String defaultName;
    private final String visualId;

    //~ if >=1.20 'World ' -> 'Level ' {
    private NodeContext(World world, BlockPos pos, String defaultName, String visualId) {
        this.world = world;
        this.pos = pos;
        this.defaultName = defaultName;
        this.visualId = visualId;
    }
    //~}

    //~ if >=1.20 'World ' -> 'Level ' {
    public static NodeContext fromWorld(@NotNull World world, @NotNull BlockPos pos) {
        return new NodeContext(world, pos, resolveDefaultName(world, pos), resolveVisualId(world, pos));
    }

    public static NodeContext of(@NotNull World world, @NotNull BlockPos pos, @NotNull String defaultName, @NotNull String visualId) {
        return new NodeContext(world, pos, defaultName, visualId);
    }

    //~ if >=1.20 'World ' -> 'Level ' {
    //~ if >=1.20 '.getLocalizedName()' -> '.getName().getString()' {
    private static String resolveDefaultName(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().getLocalizedName();
    }
    //~}

    private static String resolveVisualId(World world, BlockPos pos) {
        //? if <1.20 {
        ResourceLocation registryName = world.getBlockState(pos).getBlock().getRegistryName();
        //?} else {
        /*ResourceLocation registryName = BuiltInRegistries.BLOCK.getKey(world.getBlockState(pos).getBlock());
         *///?}
        return registryName != null ? registryName.toString() : "";
    }

    public @NotNull World getWorld() {
        return world;
    }

    public @NotNull BlockPos getPos() {
        return pos;
    }

    public @NotNull String getDefaultName() {
        return defaultName;
    }
    //~}

    public @NotNull String getVisualId() {
        return visualId;
    }
    //~}
}