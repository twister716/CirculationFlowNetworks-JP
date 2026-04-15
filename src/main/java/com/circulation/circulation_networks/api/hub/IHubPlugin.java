package com.circulation.circulation_networks.api.hub;

import com.circulation.circulation_networks.network.hub.HubPluginCapability;
//? if <1.20 {
import net.minecraft.util.ResourceLocation;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
*///?}

import org.jetbrains.annotations.Nullable;

/**
 * 中枢插件接口，物品实现此接口以成为可插入中枢的插件
 * <p>
 * Hub plugin interface. Items implementing this interface can be inserted into Hub plugin slots.
 */
public interface IHubPlugin {

    int DEFAULT_HUB_ROTATION_PERIOD_TICKS = 200;

    HubPluginCapability<?> getCapability();

    default @Nullable ResourceLocation getHubModelLocation() {
        return null;
    }

    default int getHubRotationPeriodTicks() {
        return DEFAULT_HUB_ROTATION_PERIOD_TICKS;
    }
}
