package com.circulation.circulation_networks.items;

import com.circulation.circulation_networks.api.hub.IHubPlugin;
import com.circulation.circulation_networks.client.render.HubRenderLayout;
import com.circulation.circulation_networks.network.hub.HubCapabilitys;
import com.circulation.circulation_networks.network.hub.HubPluginCapability;
//? if <1.20 {
import net.minecraft.util.ResourceLocation;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
*///?}

import org.jetbrains.annotations.Nullable;

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

    @Override
    public @Nullable ResourceLocation getHubModelLocation() {
        //? if <1.21 {
        return new ResourceLocation("circulation_networks", "block/" + HubRenderLayout.DIMENSIONAL_PLUGIN_MODEL);
        //?} else {
        /*return ResourceLocation.fromNamespaceAndPath("circulation_networks", "block/" + HubRenderLayout.DIMENSIONAL_PLUGIN_MODEL);
        *///?}
    }

    @Override
    public int getHubRotationPeriodTicks() {
        return 50;
    }
}
