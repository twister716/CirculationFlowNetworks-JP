package com.circulation.circulation_networks.api.hub;

import java.util.UUID;

public record ChannelSnapshotEntry(UUID id, String name, PermissionMode permissionMode, HubPermissionLevel permission,
                                   boolean connected) {

    public ChannelSnapshotEntry(UUID id, String name, PermissionMode permissionMode, HubPermissionLevel permission, boolean connected) {
        this.id = id;
        this.name = name == null ? "" : name;
        this.permissionMode = permissionMode == null ? PermissionMode.PRIVATE : permissionMode;
        this.permission = permission == null ? HubPermissionLevel.NONE : permission;
        this.connected = connected;
    }
}
