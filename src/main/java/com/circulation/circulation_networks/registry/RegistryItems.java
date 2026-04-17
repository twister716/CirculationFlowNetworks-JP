package com.circulation.circulation_networks.registry;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.items.ItemCirculationConfigurator;
import com.circulation.circulation_networks.items.ItemDimensionalChargingPlugin;
import com.circulation.circulation_networks.items.ItemHubChannelPlugin;
import com.circulation.circulation_networks.items.ItemMaterial;
import com.circulation.circulation_networks.items.ItemPocketNode;
import com.circulation.circulation_networks.items.ItemWideAreaChargingPlugin;
import com.circulation.circulation_networks.tooltip.TooltipTranslationsComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Function;

public final class RegistryItems {

    private RegistryItems() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RegistryItems::onRegisterItems);
    }

    private static void onRegisterItems(RegisterEvent event) {
        event.register(net.minecraft.core.registries.Registries.ITEM, helper -> {
            CFNItems.circulationConfigurator = register(helper, "circulation_configurator", properties -> new ItemCirculationConfigurator(withStaticTooltip(properties, "circulation_configurator")));
            CFNItems.pocketPortNode = register(helper, "pocket_port_node", properties -> new ItemPocketNode(NodeTypes.PORT_NODE, withStaticTooltip(properties, "pocket_port_node")));
            CFNItems.pocketChargingNode = register(helper, "pocket_charging_node", properties -> new ItemPocketNode(NodeTypes.CHARGING_NODE, withStaticTooltip(properties, "pocket_charging_node")));
            CFNItems.pocketRelayNode = register(helper, "pocket_relay_node", properties -> new ItemPocketNode(NodeTypes.RELAY_NODE, withStaticTooltip(properties, "pocket_relay_node")));
            PocketNodeItems.register(NodeTypes.PORT_NODE, CFNItems.pocketPortNode);
            PocketNodeItems.register(NodeTypes.CHARGING_NODE, CFNItems.pocketChargingNode);
            PocketNodeItems.register(NodeTypes.RELAY_NODE, CFNItems.pocketRelayNode);
            CFNItems.hubChannelPlugin = register(helper, "hub_channel_plugin", properties -> new ItemHubChannelPlugin(withStaticTooltip(properties, "hub_channel_plugin")));
            CFNItems.wideAreaChargingPlugin = register(helper, "wide_area_charging_plugin", properties -> new ItemWideAreaChargingPlugin(withStaticTooltip(properties, "wide_area_charging_plugin")));
            CFNItems.dimensionalChargingPlugin = register(helper, "dimensional_charging_plugin", properties -> new ItemDimensionalChargingPlugin(withStaticTooltip(properties, "dimensional_charging_plugin")));
            CFNItems.circulationSourceCrystal = register(helper, "circulation_source_crystal", properties -> new ItemMaterial(withStaticTooltip(properties, "circulation_source_crystal")));
            CFNItems.infernalMeltingCrystal = register(helper, "infernal_melting_crystal", properties -> new ItemMaterial(withStaticTooltip(properties, "infernal_melting_crystal")));
            CFNItems.endCoreCrystal = register(helper, "end_core_crystal", properties -> new ItemMaterial(withStaticTooltip(properties, "end_core_crystal")));
        });
    }

    private static Item.Properties withStaticTooltip(Item.Properties properties, String itemName) {
        TooltipTranslationsComponent component = TooltipTranslationsComponent.fromTranslationKey(
            "item." + CirculationFlowNetworks.MOD_ID + "." + itemName
        );
        if (component == null) {
            return properties;
        }
        return properties.component(CFNDataComponents.TOOLTIP_TRANSLATIONS, component);
    }

    private static <T extends Item> T register(RegisterEvent.RegisterHelper<Item> helper, String name, Function<Item.Properties, T> factory) {
        Identifier id = Identifier.parse(CirculationFlowNetworks.MOD_ID + ":" + name);
        T item = factory.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)));
        helper.register(id, item);
        return item;
    }
}
