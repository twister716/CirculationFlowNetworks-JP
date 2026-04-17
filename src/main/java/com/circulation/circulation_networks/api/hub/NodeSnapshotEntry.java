package com.circulation.circulation_networks.api.hub;

import org.jetbrains.annotations.Nullable;

public record NodeSnapshotEntry(String itemId, int x, int y, int z, String customName) {

    public NodeSnapshotEntry(String itemId, int x, int y, int z, @Nullable String customName) {
        this.itemId = itemId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.customName = customName == null ? "" : customName;
    }
}
