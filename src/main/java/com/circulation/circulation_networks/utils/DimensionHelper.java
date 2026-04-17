package com.circulation.circulation_networks.utils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class DimensionHelper {

    private DimensionHelper() {
    }

    public static int getDimensionHash(Level world) {
        return world.dimension().identifier().hashCode();
    }

    public static String getDimensionId(Level world) {
        return world.dimension().identifier().toString();
    }

    public static ResourceKey<Level> createDimensionKey(String dimensionKey) {
        return ResourceKey.create(Registries.DIMENSION, Identifier.parse(dimensionKey));
    }
}
