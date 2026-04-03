package com.circulation.circulation_networks.api.hub;

import java.util.UUID;

//? if <1.20 {
import com.github.bsideup.jabel.Desugar;

@Desugar
//?}
public record PermissionSnapshotEntry(UUID id, String name, HubPermissionLevel permission) {

}