package com.circulation.circulation_networks.api.hub;

import org.jetbrains.annotations.Nullable;

//? if <1.20 {
import com.github.bsideup.jabel.Desugar;

@Desugar
//?}
public record NodeSnapshotEntry(String itemId, int x, int y, int z, String customName) {

    public NodeSnapshotEntry(String itemId, int x, int y, int z, @Nullable String customName) {
        this.itemId = itemId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.customName = customName == null ? "" : customName;
    }
}