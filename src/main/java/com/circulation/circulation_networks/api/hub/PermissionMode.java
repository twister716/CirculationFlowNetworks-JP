package com.circulation.circulation_networks.api.hub;

public enum PermissionMode {
    PUBLIC,
    TEAM,
    PRIVATE;

    public static PermissionMode fromId(int id) {
        return PermissionMode.values()[Math.floorMod(id, PermissionMode.values().length)];
    }

    public int getId() {
        return this.ordinal();
    }
}
