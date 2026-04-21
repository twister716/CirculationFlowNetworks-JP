package com.circulation.circulation_networks.client.render;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

@SideOnly(Side.CLIENT)
public final class RotatingBlockModelCache {

    public static final ResourceLocation HUB_BASE = model(HubRenderLayout.HUB_BASE_MODEL);
    public static final ResourceLocation HUB_EMISSIVE = model(HubRenderLayout.HUB_EMISSIVE_MODEL);
    public static final ResourceLocation HUB_CRYSTAL = model(HubRenderLayout.HUB_CRYSTAL_MODEL);
    public static final ResourceLocation HUB_RING_UP_BASE = model(HubRenderLayout.RING_UP_BASE_MODEL);
    public static final ResourceLocation HUB_RING_UP_EMISSIVE = model(HubRenderLayout.RING_UP_EMISSIVE_MODEL);
    public static final ResourceLocation HUB_RING_DOWN_BASE = model(HubRenderLayout.RING_DOWN_BASE_MODEL);
    public static final ResourceLocation HUB_RING_DOWN_EMISSIVE = model(HubRenderLayout.RING_DOWN_EMISSIVE_MODEL);
    public static final ResourceLocation HUB_CHANNEL_BEACON_TOP_INSIDE = model(HubRenderLayout.CHANNEL_BEACON_TOP_INSIDE_MODEL);
    public static final ResourceLocation HUB_CHANNEL_BEACON_TOP_OUTSIDE = model(HubRenderLayout.CHANNEL_BEACON_TOP_OUTSIDE_MODEL);
    public static final ResourceLocation HUB_CHANNEL_BEACON_MID_INSIDE = model(HubRenderLayout.CHANNEL_BEACON_MID_INSIDE_MODEL);
    public static final ResourceLocation HUB_CHANNEL_BEACON_MID_OUTSIDE = model(HubRenderLayout.CHANNEL_BEACON_MID_OUTSIDE_MODEL);
    public static final ResourceLocation HUB_CHANNEL_BEACON_DOWN_INSIDE = model(HubRenderLayout.CHANNEL_BEACON_DOWN_INSIDE_MODEL);
    public static final ResourceLocation HUB_CHANNEL_BEACON_DOWN_OUTSIDE = model(HubRenderLayout.CHANNEL_BEACON_DOWN_OUTSIDE_MODEL);
    public static final ResourceLocation HUB_CHANNEL_HOLA_TOP = model(HubRenderLayout.CHANNEL_HOLA_TOP_MODEL);
    public static final ResourceLocation HUB_CHANNEL_HOLA_MIDDLE = model(HubRenderLayout.CHANNEL_HOLA_MIDDLE_MODEL);
    public static final ResourceLocation HUB_CHANNEL_HOLA_BOTTOM = model(HubRenderLayout.CHANNEL_HOLA_BOTTOM_MODEL);
    public static final ResourceLocation HUB_CHANNEL_RING_AERIALS_BASE = model(HubRenderLayout.CHANNEL_RING_AERIALS_BASE_MODEL);
    public static final ResourceLocation HUB_CHANNEL_RING_AERIALS_EMISSIVE = model(HubRenderLayout.CHANNEL_RING_AERIALS_EMISSIVE_MODEL);
    public static final ResourceLocation HUB_EMPTY_PLUGIN = model(HubRenderLayout.EMPTY_PLUGIN_MODEL);
    public static final ResourceLocation HUB_DEFAULT_PLUGIN_0 = model(HubRenderLayout.DEFAULT_PLUGIN_MODEL_PREFIX + "0");
    public static final ResourceLocation HUB_DEFAULT_PLUGIN_1 = model(HubRenderLayout.DEFAULT_PLUGIN_MODEL_PREFIX + "1");
    public static final ResourceLocation HUB_DEFAULT_PLUGIN_2 = model(HubRenderLayout.DEFAULT_PLUGIN_MODEL_PREFIX + "2");
    public static final ResourceLocation HUB_DEFAULT_PLUGIN_3 = model(HubRenderLayout.DEFAULT_PLUGIN_MODEL_PREFIX + "3");
    public static final ResourceLocation HUB_PLUGIN_WIDE_AREA = model(HubRenderLayout.WIDE_AREA_PLUGIN_MODEL);
    public static final ResourceLocation HUB_PLUGIN_DIMENSIONAL = model(HubRenderLayout.DIMENSIONAL_PLUGIN_MODEL);
    private static final Map<ResourceLocation, IBakedModel> BAKED_MODELS = new Object2ObjectOpenHashMap<>();
    private static final ResourceLocation[] HUB_TEXTURES = {
        texture("node/hub/hub_body_primary"),
        texture("node/hub/hub_body_secondary"),
        texture("node/hub/hub_body_tertiary"),
        texture("node/hub/hub_body_emissive"),
        texture("node/hub/hub_body_frame"),
        texture("node/hub/hub_channel_energy"),
        texture("node/hub/hub_crystal_core"),
        texture("node/hub/hub_ring_band"),
        texture("node/hub/plug/hub_plug_shell"),
        texture("node/hub/plug/hub_plug_detail"),
        texture("node/hub/plug/hub_plug_dimensional_link"),
        texture("node/hub/plug/hub_plug_wide_area_link")
    };

    private RotatingBlockModelCache() {
    }

    private static ResourceLocation model(String path) {
        return new ResourceLocation(CirculationFlowNetworks.MOD_ID, "block/" + path);
    }

    private static ResourceLocation texture(String path) {
        return new ResourceLocation(CirculationFlowNetworks.MOD_ID, "block/" + path);
    }

    public static IBakedModel get(ResourceLocation location) {
        return BAKED_MODELS.computeIfAbsent(location, RotatingBlockModelCache::bakeModel);
    }

    public static void registerAdditionalSprites(TextureMap textureMap) {
        for (ResourceLocation location : HUB_TEXTURES) {
            textureMap.registerSprite(location);
        }
    }

    public static void clear() {
        BAKED_MODELS.clear();
    }

    private static IBakedModel bakeModel(ResourceLocation location) {
        Minecraft minecraft = Minecraft.getMinecraft();
        IBakedModel missingModel = minecraft.getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
        try {
            IModel model = ModelLoaderRegistry.getModelOrMissing(location);
            return model.bake(model.getDefaultState(), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
        } catch (Exception e) {
            CirculationFlowNetworks.LOGGER.error("[CFN] Failed to bake model {}", location, e);
            return missingModel;
        }
    }
}
