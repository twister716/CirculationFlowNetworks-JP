package com.circulation.circulation_networks.network.hub;

import com.circulation.circulation_networks.registry.CFNItems;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class ChargeCapability extends HubPluginCapability<Boolean> {

    @Override
    public Boolean newPluginData(@NotNull ItemStack plugin) {
        return plugin.getItem() == CFNItems.dimensionalChargingPlugin;
    }

    @Override
    public void saveData(@NotNull Boolean data, @NotNull ItemStack plugin) {

    }

}
