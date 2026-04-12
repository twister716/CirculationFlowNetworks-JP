package com.circulation.circulation_networks.network.hub;

//~ mc_imports

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class HubPluginCapability<T> {

    public abstract T newPluginData(@NotNull ItemStack plugin);

    public abstract void saveData(@NotNull T data, @NotNull ItemStack plugin);

    @SuppressWarnings("unchecked")
    public final void saveDataRaw(@NotNull Object data, @NotNull ItemStack plugin) {
        saveData((T) data, plugin);
    }
}
