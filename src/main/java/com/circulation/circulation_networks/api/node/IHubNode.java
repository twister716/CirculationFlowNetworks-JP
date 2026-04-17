package com.circulation.circulation_networks.api.node;

import com.circulation.circulation_networks.api.hub.ChargingDefinition;
import com.circulation.circulation_networks.api.hub.ChargingPreference;
import com.circulation.circulation_networks.api.hub.HubPermissionLevel;
import com.circulation.circulation_networks.api.hub.PermissionMode;
import com.circulation.circulation_networks.inventory.CFNInternalInventory;
import com.circulation.circulation_networks.network.hub.HubPluginCapability;
import com.circulation.circulation_networks.network.nodes.HubNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public interface IHubNode extends IEnergySupplyNode, IChargingNode {

    PermissionMode getPermissionMode();

    void setPermissionMode(PermissionMode mode);

    CFNInternalInventory getPlugins();

    HubNode.HubMetadata getHubData();

    default boolean hasPluginCapability(HubPluginCapability<?> capability) {
        return capability != null && getHubData().hasKey(capability);
    }

    default <T> T getPluginCapabilityData(HubPluginCapability<T> capability) {
        return getHubData().get(capability);
    }

    @NotNull
    UUID getChannelId();

    void setChannelId(@NotNull UUID channelId);

    @NotNull
    String getChannelName();

    void setChannelName(@NotNull String channelName);

    @NotNull
    ChargingPreference getChargingPreference(UUID playerId);

    void setChargingPreference(UUID playerId, ChargingPreference preference);

    boolean getChargingState(UUID playerId, ChargingDefinition preference);

    void setChargingState(UUID playerId, ChargingDefinition preference, boolean value);

    @Nullable
    UUID getOwner();

    void setOwner(@Nullable UUID owner);

    @Nullable
    HubPermissionLevel getExplicitPermission(UUID playerId);

    Map<UUID, HubPermissionLevel> getExplicitPermissions();

    void setExplicitPermission(UUID playerId, HubPermissionLevel permissionLevel);

    void removeExplicitPermission(UUID playerId);

    HubPermissionLevel getPermissionLevel(UUID playerId);

    boolean canEditPermissions(UUID playerId);

    Map<UUID, ChargingPreference> getPlayerPreferences();
}
