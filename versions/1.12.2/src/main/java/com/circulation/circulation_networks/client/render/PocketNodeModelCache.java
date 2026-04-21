package com.circulation.circulation_networks.client.render;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.registry.CFNItems;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SideOnly(Side.CLIENT)
public final class PocketNodeModelCache {

    public static final ResourceLocation PORT = model("pocket_node/pocket_port_node");
    public static final ResourceLocation CHARGING = model("pocket_node/pocket_charging_node");
    public static final ResourceLocation RELAY = model("pocket_node/pocket_relay_node");
    private static final Map<ResourceLocation, IBakedModel> BAKED_MODELS = new Object2ObjectOpenHashMap<>();
    private static final ResourceLocation[] TEXTURES = {
        texture("node/node_crystal"),
        texture("node/pocket_node")
    };

    private PocketNodeModelCache() {
    }

    private static ResourceLocation model(String path) {
        return new ResourceLocation(CirculationFlowNetworks.MOD_ID, "block/" + path);
    }

    private static ResourceLocation texture(String path) {
        return new ResourceLocation(CirculationFlowNetworks.MOD_ID, "block/" + path);
    }

    public static IBakedModel get(ItemStack stack) {
        ResourceLocation location = resolveLocation(stack);
        if (location == null) {
            return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
        }
        return BAKED_MODELS.computeIfAbsent(location, PocketNodeModelCache::bakeModel);
    }

    public static boolean isGui3d(ItemStack stack) {
        return get(stack).isGui3d();
    }

    public static void registerAdditionalSprites(TextureMap textureMap) {
        for (ResourceLocation location : TEXTURES) {
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
            CirculationFlowNetworks.LOGGER.error("[CFN] Failed to bake pocket node model {}", location, e);
            return missingModel;
        }
    }

    private static @Nullable ResourceLocation resolveLocation(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        if (stack.getItem() == CFNItems.pocketPortNode) {
            return PORT;
        }
        if (stack.getItem() == CFNItems.pocketChargingNode) {
            return CHARGING;
        }
        if (stack.getItem() == CFNItems.pocketRelayNode) {
            return RELAY;
        }
        return null;
    }
}
