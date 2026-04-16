package com.circulation.circulation_networks.pocket;

import org.jetbrains.annotations.Nullable;

public final class PocketNodeHostRules {

    private PocketNodeHostRules() {
    }

    public static boolean isHostStateValid(@Nullable String storedHostBlockId,
                                           @Nullable String currentHostBlockId,
                                           boolean isAir,
                                           boolean hasNodeBlockEntity) {
        if (isAir || hasNodeBlockEntity || currentHostBlockId == null || currentHostBlockId.isEmpty()) {
            return false;
        }
        return storedHostBlockId == null || storedHostBlockId.isEmpty() || storedHostBlockId.equals(currentHostBlockId);
    }

    public static @Nullable String resolveHostBlockId(@Nullable String storedHostBlockId,
                                                      @Nullable String currentHostBlockId,
                                                      boolean isAir,
                                                      boolean hasNodeBlockEntity) {
        if (storedHostBlockId != null && !storedHostBlockId.isEmpty()) {
            return storedHostBlockId;
        }
        if (isAir || hasNodeBlockEntity || currentHostBlockId == null || currentHostBlockId.isEmpty()) {
            return null;
        }
        return currentHostBlockId;
    }
}
