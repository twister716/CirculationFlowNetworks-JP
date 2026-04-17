package com.circulation.circulation_networks.items;

import com.circulation.circulation_networks.api.hub.IHubPlugin;
import com.circulation.circulation_networks.client.render.HubRenderLayout;
import com.circulation.circulation_networks.network.hub.HubCapabilitys;
import com.circulation.circulation_networks.network.hub.HubPluginCapability;
import net.minecraft.resources.Identifier;

import org.jetbrains.annotations.Nullable;

public class ItemDimensionalChargingPlugin extends BaseItem implements IHubPlugin {

    public ItemDimensionalChargingPlugin(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public HubPluginCapability<?> getCapability() {
        return HubCapabilitys.CHARGE_CAPABILITY;
    }

    @Override
    public @Nullable Identifier getHubModelLocation() {
        return Identifier.parse("circulation_networks:block/" + HubRenderLayout.DIMENSIONAL_PLUGIN_MODEL);
    }

    @Override
    public int getHubRotationPeriodTicks() {
        return 50;
    }
}
