package com.circulation.circulation_networks.registry;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.items.ItemDimensionalChargingPlugin;
import com.circulation.circulation_networks.items.ItemHubChannelPlugin;
import com.circulation.circulation_networks.items.ItemInspectionTool;
import com.circulation.circulation_networks.items.ItemWideAreaChargingPlugin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

public final class RegistryItems {

    private RegistryItems() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RegistryItems::onRegisterItems);
    }

    private static void onRegisterItems(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ITEMS, helper -> {
            CFNItems.inspectionTool = register(helper, "inspection_tool", new ItemInspectionTool(new Item.Properties()));
            CFNItems.hubChannelPlugin = register(helper, "hub_channel_plugin", new ItemHubChannelPlugin(new Item.Properties()));
            CFNItems.wideAreaChargingPlugin = register(helper, "wide_area_charging_plugin", new ItemWideAreaChargingPlugin(new Item.Properties()));
            CFNItems.dimensionalChargingPlugin = register(helper, "dimensional_charging_plugin", new ItemDimensionalChargingPlugin(new Item.Properties()));
        });
    }

    private static Item register(RegisterEvent.RegisterHelper<Item> helper, String name, Item item) {
        helper.register(new ResourceLocation(CirculationFlowNetworks.MOD_ID, name), item);
        return item;
    }
}