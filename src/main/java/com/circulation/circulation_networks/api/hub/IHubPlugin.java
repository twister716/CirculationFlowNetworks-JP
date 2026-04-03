package com.circulation.circulation_networks.api.hub;

import com.circulation.circulation_networks.network.hub.HubPluginCapability;
//~ mc_imports

/**
 * 中枢插件接口，物品实现此接口以成为可插入中枢的插件
 * <p>
 * Hub plugin interface. Items implementing this interface can be inserted into Hub plugin slots.
 */
public interface IHubPlugin {

    HubPluginCapability<?> getCapability();
}
