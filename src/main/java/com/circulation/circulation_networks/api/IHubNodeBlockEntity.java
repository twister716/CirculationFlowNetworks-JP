package com.circulation.circulation_networks.api;

import com.circulation.circulation_networks.api.hub.ChargingPreference;
import com.circulation.circulation_networks.api.hub.PermissionMode;
//? if <1.20 {
import net.minecraft.item.ItemStack;
//?} else {
/*import net.minecraft.world.item.ItemStack;
*///?}

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public interface IHubNodeBlockEntity extends INodeBlockEntity {

    PermissionMode getPermissionMode();

    void setPermissionMode(PermissionMode mode);

    ItemStack[] getPlugins();

    void setPlugin(int slot, ItemStack stack);

    @Nonnull
    ChargingPreference getChargingPreference(UUID playerId);

    void setChargingPreference(UUID playerId, ChargingPreference preference);

    @Nullable
    UUID getOwner();

    void setOwner(@Nullable UUID owner);

    @Nullable
    UUID getChannelId();

    void setChannelId(@Nullable UUID channelId);

    @Nullable
    String getChannelName();

    void setChannelName(@Nullable String channelName);
}