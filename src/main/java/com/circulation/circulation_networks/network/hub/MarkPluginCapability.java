package com.circulation.circulation_networks.network.hub;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class MarkPluginCapability extends HubPluginCapability<Object> {

    private static final Object MARK = new Object();

    @Override
    public Object newPluginData(@NotNull ItemStack plugin) {
        return MARK;
    }

    @Override
    public void saveData(@NotNull Object data, @NotNull ItemStack plugin) {

    }
}
