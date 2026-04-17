package com.circulation.circulation_networks.utils;

import net.minecraft.core.BlockPos;

public final class BlockPosCompat {

    private BlockPosCompat() {
    }

    public static BlockPos fromLong(long packedPos) {
        return BlockPos.of(packedPos);
    }

    public static long toLong(BlockPos pos) {
        return pos.asLong();
    }
}
