package com.circulation.circulation_networks.api.hub;

import java.util.UUID;

public record PermissionSnapshotEntry(UUID id, String name, HubPermissionLevel permission) {

}
