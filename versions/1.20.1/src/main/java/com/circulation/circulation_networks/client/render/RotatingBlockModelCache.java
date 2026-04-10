package com.circulation.circulation_networks.client.render;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class RotatingBlockModelCache {

    private static final List<ResourceLocation> REGISTERED_MODELS = new ArrayList<>();
    // Shared crystal model
    public static final ResourceLocation NODE_CRYSTAL = register("node_crystal");
    // Relay node sub-part models
    public static final ResourceLocation RELAY_TOP_SPIRAL_BASE = register("relay_node/relay_node_top_spiral_base");
    public static final ResourceLocation RELAY_TOP_SPIRAL_EMISSIVE = register("relay_node/relay_node_top_spiral_emissive");
    public static final ResourceLocation RELAY_BOTTOM_SPIRAL_BASE = register("relay_node/relay_node_bottom_spiral_base");
    public static final ResourceLocation RELAY_BOTTOM_SPIRAL_EMISSIVE = register("relay_node/relay_node_bottom_spiral_emissive");
    // Charging node sub-part models
    public static final ResourceLocation CHARGING_IN_BASE = register("charging_node/charging_node_in_base");
    public static final ResourceLocation CHARGING_IN_EMISSIVE = register("charging_node/charging_node_in_emissive");
    public static final ResourceLocation CHARGING_RING_BASE = register("charging_node/charging_node_ring_base");
    public static final ResourceLocation CHARGING_RING_EMISSIVE = register("charging_node/charging_node_ring_emissive");
    // Port node sub-part models
    public static final ResourceLocation PORT_IN_BASE = register("port_node/port_node_in_base");
    public static final ResourceLocation PORT_IN_EMISSIVE = register("port_node/port_node_in_emissive");
    public static final ResourceLocation PORT_OUT_BASE = register("port_node/port_node_out_base");
    public static final ResourceLocation PORT_OUT_EMISSIVE = register("port_node/port_node_out_emissive");
    // Node pedestal sub-part models
    public static final ResourceLocation PEDESTAL_BASE = register("node_pedestal/node_pedestal_base");
    public static final ResourceLocation PEDESTAL_BASE_EMISSIVE = register("node_pedestal/node_pedestal_base_emissive");
    public static final ResourceLocation PEDESTAL_FRAME_CLOCKWISE = register("node_pedestal/node_pedestal_frame_clockwise");
    public static final ResourceLocation PEDESTAL_FRAME_COUNTER_CLOCKWISE = register("node_pedestal/node_pedestal_frame_counter_clockwise");
    // Full static models (for item rendering fallback)
    public static final ResourceLocation RELAY_STATIC = register("relay_node/relay_node");
    public static final ResourceLocation CHARGING_STATIC = register("charging_node/charging_node");
    public static final ResourceLocation PORT_STATIC = register("port_node/port_node");
    public static final ResourceLocation PEDESTAL_STATIC = register("node_pedestal/node_pedestal");
    private static final Map<ResourceLocation, BakedModel> BAKED_MODELS = new Object2ObjectOpenHashMap<>();

    private RotatingBlockModelCache() {
    }

    private static ResourceLocation register(String path) {
        ResourceLocation location = new ResourceLocation(CirculationFlowNetworks.MOD_ID, "block/" + path);
        REGISTERED_MODELS.add(location);
        return location;
    }

    public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
        for (ResourceLocation location : REGISTERED_MODELS) {
            event.register(location);
        }
    }

    public static BakedModel get(ResourceLocation location) {
        return BAKED_MODELS.computeIfAbsent(location, RotatingBlockModelCache::loadModel);
    }

    public static void clear() {
        BAKED_MODELS.clear();
        RotatingModelVBORenderer.clearAll();
    }

    private static BakedModel loadModel(ResourceLocation location) {
        return Minecraft.getInstance().getModelManager().getModel(location);
    }
}
