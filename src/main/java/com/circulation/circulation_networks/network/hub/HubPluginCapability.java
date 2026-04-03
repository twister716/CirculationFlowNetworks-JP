package com.circulation.circulation_networks.network.hub;

//~ mc_imports

import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class HubPluginCapability<T> {

    public abstract T newPluginData(ItemStack plugin);

    public abstract void saveData(T data, ItemStack plugin);

    @SuppressWarnings("unchecked")
    public final void saveDataRaw(Object data, ItemStack plugin) {
        saveData((T) data, plugin);
    }
}
