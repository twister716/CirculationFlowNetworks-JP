package com.circulation.circulation_networks.items;

import com.circulation.circulation_networks.api.hub.IHubPlugin;
import com.circulation.circulation_networks.network.hub.HubCapabilitys;
import com.circulation.circulation_networks.network.hub.HubPluginCapability;
//~ mc_imports

public class ItemDimensionalChargingPlugin extends BaseItem implements IHubPlugin {

    //? if <1.20 {
    public ItemDimensionalChargingPlugin() {
        super("dimensional_charging_plugin");
        this.setMaxStackSize(1);
    }
    //?} else {
    /*public ItemDimensionalChargingPlugin(Properties properties) {
        super(properties.stacksTo(1));
    }
    *///?}

    @Override
    public HubPluginCapability<?> getCapability() {
        return HubCapabilitys.CHARGE_CAPABILITY;
    }
}
