package com.circulation.circulation_networks.items;

import com.circulation.circulation_networks.api.hub.IHubPlugin;
import com.circulation.circulation_networks.client.render.HubRenderLayout;
import com.circulation.circulation_networks.network.hub.HubCapabilitys;
import com.circulation.circulation_networks.network.hub.HubPluginCapability;
import net.minecraft.resources.Identifier;

import org.jetbrains.annotations.Nullable;

public class ItemWideAreaChargingPlugin extends BaseItem implements IHubPlugin {

    public ItemWideAreaChargingPlugin(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public HubPluginCapability<?> getCapability() {
        return HubCapabilitys.CHARGE_CAPABILITY;
    }

    @Override
    public @Nullable Identifier getHubModelLocation() {
        return Identifier.parse("circulation_networks:block/" + HubRenderLayout.WIDE_AREA_PLUGIN_MODEL);
    }
}
