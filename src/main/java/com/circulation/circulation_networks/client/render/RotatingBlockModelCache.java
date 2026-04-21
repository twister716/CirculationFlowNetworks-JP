package com.circulation.circulation_networks.client.render;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

public final class RotatingBlockModelCache {

    private static final ObjectList<Identifier> REGISTERED_MODELS = new ObjectArrayList<>();
    private static final Object2ObjectMap<Identifier, StandaloneModelKey<BlockStateModel>> MODEL_KEYS = new Object2ObjectOpenHashMap<>();

    public static final Identifier NODE_CRYSTAL = register("node_crystal");
    public static final Identifier RELAY_TOP_SPIRAL_BASE = register("relay_node/relay_node_top_spiral_base");
    public static final Identifier RELAY_TOP_SPIRAL_EMISSIVE = register("relay_node/relay_node_top_spiral_emissive");
    public static final Identifier RELAY_BOTTOM_SPIRAL_BASE = register("relay_node/relay_node_bottom_spiral_base");
    public static final Identifier RELAY_BOTTOM_SPIRAL_EMISSIVE = register("relay_node/relay_node_bottom_spiral_emissive");
    public static final Identifier CHARGING_IN_BASE = register("charging_node/charging_node_in_base");
    public static final Identifier CHARGING_IN_EMISSIVE = register("charging_node/charging_node_in_emissive");
    public static final Identifier CHARGING_RING_BASE = register("charging_node/charging_node_ring_base");
    public static final Identifier CHARGING_RING_EMISSIVE = register("charging_node/charging_node_ring_emissive");
    public static final Identifier PORT_IN_BASE = register("port_node/port_node_in_base");
    public static final Identifier PORT_IN_EMISSIVE = register("port_node/port_node_in_emissive");
    public static final Identifier PORT_OUT_BASE = register("port_node/port_node_out_base");
    public static final Identifier PORT_OUT_EMISSIVE = register("port_node/port_node_out_emissive");
    public static final Identifier PEDESTAL_BASE = register("node_pedestal/node_pedestal_base");
    public static final Identifier PEDESTAL_BASE_EMISSIVE = register("node_pedestal/node_pedestal_base_emissive");
    public static final Identifier PEDESTAL_FRAME_CLOCKWISE = register("node_pedestal/node_pedestal_frame_clockwise");
    public static final Identifier PEDESTAL_FRAME_COUNTER_CLOCKWISE = register("node_pedestal/node_pedestal_frame_counter_clockwise");
    public static final Identifier RELAY_STATIC = register("relay_node/relay_node");
    public static final Identifier CHARGING_STATIC = register("charging_node/charging_node");
    public static final Identifier PORT_STATIC = register("port_node/port_node");
    public static final Identifier PEDESTAL_STATIC = register("node_pedestal/node_pedestal");
    public static final Identifier POCKET_PORT = register("pocket_node/pocket_port_node");
    public static final Identifier POCKET_CHARGING = register("pocket_node/pocket_charging_node");
    public static final Identifier POCKET_RELAY = register("pocket_node/pocket_relay_node");
    public static final Identifier HUB_BASE = register(HubRenderLayout.HUB_BASE_MODEL);
    public static final Identifier HUB_EMISSIVE = register(HubRenderLayout.HUB_EMISSIVE_MODEL);
    public static final Identifier HUB_CRYSTAL = register(HubRenderLayout.HUB_CRYSTAL_MODEL);
    public static final Identifier HUB_RING_UP_BASE = register(HubRenderLayout.RING_UP_BASE_MODEL);
    public static final Identifier HUB_RING_UP_EMISSIVE = register(HubRenderLayout.RING_UP_EMISSIVE_MODEL);
    public static final Identifier HUB_RING_DOWN_BASE = register(HubRenderLayout.RING_DOWN_BASE_MODEL);
    public static final Identifier HUB_RING_DOWN_EMISSIVE = register(HubRenderLayout.RING_DOWN_EMISSIVE_MODEL);
    public static final Identifier HUB_CHANNEL_BEACON_TOP_INSIDE = register(HubRenderLayout.CHANNEL_BEACON_TOP_INSIDE_MODEL);
    public static final Identifier HUB_CHANNEL_BEACON_TOP_OUTSIDE = register(HubRenderLayout.CHANNEL_BEACON_TOP_OUTSIDE_MODEL);
    public static final Identifier HUB_CHANNEL_BEACON_MID_INSIDE = register(HubRenderLayout.CHANNEL_BEACON_MID_INSIDE_MODEL);
    public static final Identifier HUB_CHANNEL_BEACON_MID_OUTSIDE = register(HubRenderLayout.CHANNEL_BEACON_MID_OUTSIDE_MODEL);
    public static final Identifier HUB_CHANNEL_BEACON_DOWN_INSIDE = register(HubRenderLayout.CHANNEL_BEACON_DOWN_INSIDE_MODEL);
    public static final Identifier HUB_CHANNEL_BEACON_DOWN_OUTSIDE = register(HubRenderLayout.CHANNEL_BEACON_DOWN_OUTSIDE_MODEL);
    public static final Identifier HUB_CHANNEL_HOLA_TOP = register(HubRenderLayout.CHANNEL_HOLA_TOP_MODEL);
    public static final Identifier HUB_CHANNEL_HOLA_MIDDLE = register(HubRenderLayout.CHANNEL_HOLA_MIDDLE_MODEL);
    public static final Identifier HUB_CHANNEL_HOLA_BOTTOM = register(HubRenderLayout.CHANNEL_HOLA_BOTTOM_MODEL);
    public static final Identifier HUB_CHANNEL_RING_AERIALS_BASE = register(HubRenderLayout.CHANNEL_RING_AERIALS_BASE_MODEL);
    public static final Identifier HUB_CHANNEL_RING_AERIALS_EMISSIVE = register(HubRenderLayout.CHANNEL_RING_AERIALS_EMISSIVE_MODEL);
    public static final Identifier HUB_EMPTY_PLUGIN = register(HubRenderLayout.EMPTY_PLUGIN_MODEL);
    public static final Identifier HUB_DEFAULT_PLUGIN_0 = register(HubRenderLayout.DEFAULT_PLUGIN_MODEL_PREFIX + "0");
    public static final Identifier HUB_DEFAULT_PLUGIN_1 = register(HubRenderLayout.DEFAULT_PLUGIN_MODEL_PREFIX + "1");
    public static final Identifier HUB_DEFAULT_PLUGIN_2 = register(HubRenderLayout.DEFAULT_PLUGIN_MODEL_PREFIX + "2");
    public static final Identifier HUB_DEFAULT_PLUGIN_3 = register(HubRenderLayout.DEFAULT_PLUGIN_MODEL_PREFIX + "3");
    public static final Identifier HUB_PLUGIN_WIDE_AREA = register(HubRenderLayout.WIDE_AREA_PLUGIN_MODEL);
    public static final Identifier HUB_PLUGIN_DIMENSIONAL = register(HubRenderLayout.DIMENSIONAL_PLUGIN_MODEL);

    private RotatingBlockModelCache() {
    }

    private static Identifier register(String path) {
        Identifier location = Identifier.fromNamespaceAndPath(CirculationFlowNetworks.MOD_ID, "block/" + path);
        REGISTERED_MODELS.add(location);
        MODEL_KEYS.put(location, new StandaloneModelKey<>(location::toString));
        return location;
    }

    public static void onRegisterAdditionalModels(ModelEvent.RegisterStandalone event) {
        for (Identifier location : REGISTERED_MODELS) {
            event.register(MODEL_KEYS.get(location), SimpleUnbakedStandaloneModel.blockStateModel(location));
        }
    }

    public static BlockStateModel get(Identifier location) {
        StandaloneModelKey<BlockStateModel> key = MODEL_KEYS.get(location);
        if (key == null) {
            throw new IllegalArgumentException("Unknown rotating model: " + location);
        }
        return Minecraft.getInstance().getModelManager().getStandaloneModel(key);
    }

    public static void clear() {
        RotatingModelVBORenderer.clearAll();
    }
}
