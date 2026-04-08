package com.circulation.circulation_networks.client.render;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
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

    private static final Map<ResourceLocation, IBakedModel> BAKED_MODELS = new Object2ObjectOpenHashMap<>();

    private RotatingBlockModelCache() {
    }

    public static IBakedModel get(ResourceLocation location) {
        return BAKED_MODELS.computeIfAbsent(location, RotatingBlockModelCache::bakeModel);
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
