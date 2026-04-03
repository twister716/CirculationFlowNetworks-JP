package com.circulation.circulation_networks.network.hub;

import com.circulation.circulation_networks.items.HubChannelPluginData;
import org.jetbrains.annotations.NotNull;
//~ mc_imports
import net.minecraft.item.ItemStack;

public final class ChannelCapability extends HubPluginCapability<HubChannelPluginData.ChannelInfo> {

    @Override
    public HubChannelPluginData.ChannelInfo newPluginData(@NotNull ItemStack plugin) {
        return HubChannelPluginData.getChannelInfo(plugin);
    }

    @Override
    public void saveData(@NotNull HubChannelPluginData.ChannelInfo data, @NotNull ItemStack plugin) {
        HubChannelPluginData.setChannelInfo(plugin, data);
    }
}