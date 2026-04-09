package com.circulation.circulation_networks.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.IdentityHashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class RotatingModelVBORenderer {

    private static final Map<ResourceLocation, VertexBuffer> FULL_BRIGHT_VBOS = new IdentityHashMap<>();
    private static final Map<AmbientVBOKey, CachedVBO> AMBIENT_VBOS = new Object2ObjectOpenHashMap<>();
    private static final Quaternionf ROTATION = new Quaternionf();

    private RotatingModelVBORenderer() {
    }

    public static void renderFullBrightYAxis(
        PoseStack poseStack, BlockState state, ResourceLocation modelLocation,
        float angle, float pivotX, float pivotY, float pivotZ
    ) {
        renderFullBright(poseStack, state, modelLocation, angle, pivotX, pivotY, pivotZ, 0.0F, 1.0F, 0.0F);
    }

    public static void renderFullBright(
        PoseStack poseStack, BlockState state, ResourceLocation modelLocation,
        float angle, float pivotX, float pivotY, float pivotZ,
        float axisX, float axisY, float axisZ
    ) {
        VertexBuffer vbo = FULL_BRIGHT_VBOS.get(modelLocation);
        if (vbo == null) {
            vbo = buildFullBrightVBO(state, modelLocation);
            FULL_BRIGHT_VBOS.put(modelLocation, vbo);
        }
        drawVBO(vbo, poseStack, angle, pivotX, pivotY, pivotZ, axisX, axisY, axisZ);
    }

    public static void renderAmbientLit(
        PoseStack poseStack, BlockAndTintGetter level, BlockPos pos,
        BlockState state, ResourceLocation modelLocation,
        float angle, float pivotX, float pivotY, float pivotZ,
        float axisX, float axisY, float axisZ
    ) {
        AmbientVBOKey key = new AmbientVBOKey(System.identityHashCode(level), pos.immutable(), modelLocation);
        int lightSig = computeLightSignature(level, pos, state);
        CachedVBO cached = AMBIENT_VBOS.get(key);
        if (cached == null || cached.lightSignature != lightSig) {
            if (cached != null) {
                cached.vbo.close();
            }
            VertexBuffer vbo = buildAmbientVBO(level, pos, state, modelLocation);
            cached = new CachedVBO(vbo, lightSig);
            AMBIENT_VBOS.put(key, cached);
        }
        drawVBO(cached.vbo, poseStack, angle, pivotX, pivotY, pivotZ, axisX, axisY, axisZ);
    }

    public static void removePosition(int worldId, BlockPos pos) {
        var iter = AMBIENT_VBOS.entrySet().iterator();
        while (iter.hasNext()) {
            var entry = iter.next();
            AmbientVBOKey k = entry.getKey();
            if (k.worldId == worldId && k.pos.equals(pos)) {
                entry.getValue().vbo.close();
                iter.remove();
            }
        }
    }

    public static void clearAll() {
        for (VertexBuffer vbo : FULL_BRIGHT_VBOS.values()) {
            vbo.close();
        }
        FULL_BRIGHT_VBOS.clear();
        for (CachedVBO cached : AMBIENT_VBOS.values()) {
            cached.vbo.close();
        }
        AMBIENT_VBOS.clear();
    }

    private static void drawVBO(
        VertexBuffer vbo, PoseStack poseStack,
        float angle, float pivotX, float pivotY, float pivotZ,
        float axisX, float axisY, float axisZ
    ) {
        ShaderInstance shader = GameRenderer.getRendertypeCutoutShader();
        if (shader == null) return;

        poseStack.pushPose();
        poseStack.translate(pivotX, pivotY, pivotZ);
        ROTATION.rotationAxis((float) Math.toRadians(angle), axisX, axisY, axisZ);
        poseStack.mulPose(ROTATION);
        poseStack.translate(-pivotX, -pivotY, -pivotZ);

        RenderSystem.setShader(GameRenderer::getRendertypeCutoutShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);
        RenderSystem.depthMask(true);
        RenderSystem.disableCull();

        Matrix4f modelView = new Matrix4f(RenderSystem.getModelViewMatrix());
        modelView.mul(poseStack.last().pose());

        vbo.bind();
        vbo.drawWithShader(modelView, RenderSystem.getProjectionMatrix(), shader);
        VertexBuffer.unbind();

        RenderSystem.enableCull();
        poseStack.popPose();
    }

    private static VertexBuffer buildFullBrightVBO(BlockState state, ResourceLocation modelLocation) {
        BakedModel model = RotatingBlockModelCache.get(modelLocation);
        ByteBufferBuilder byteBuffer = new ByteBufferBuilder(262144);
        BufferBuilder builder = new BufferBuilder(byteBuffer, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);

        PoseStack identity = new PoseStack();
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
            identity.last(), builder, state, model,
            1.0F, 1.0F, 1.0F, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null
        );

        VertexBuffer vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
        MeshData mesh = builder.buildOrThrow();
        vbo.bind();
        vbo.upload(mesh);
        VertexBuffer.unbind();
        byteBuffer.close();
        return vbo;
    }

    private static VertexBuffer buildAmbientVBO(
        BlockAndTintGetter level, BlockPos pos, BlockState state, ResourceLocation modelLocation
    ) {
        BakedModel model = RotatingBlockModelCache.get(modelLocation);
        ByteBufferBuilder byteBuffer = new ByteBufferBuilder(262144);
        BufferBuilder builder = new BufferBuilder(byteBuffer, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);

        PoseStack identity = new PoseStack();
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(
            level, model, state, pos, identity, builder, false,
            RandomSource.create(), 42L, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null
        );

        VertexBuffer vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
        MeshData mesh = builder.buildOrThrow();
        vbo.bind();
        vbo.upload(mesh);
        VertexBuffer.unbind();
        byteBuffer.close();
        return vbo;
    }

    private static int computeLightSignature(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        int signature = state.hashCode();
        signature = 31 * signature + level.getRawBrightness(pos, 0);
        for (Direction dir : Direction.values()) {
            BlockPos sidePos = pos.relative(dir);
            BlockState sideState = level.getBlockState(sidePos);
            signature = 31 * signature + level.getRawBrightness(sidePos, 0);
            signature = 31 * signature + (sideState.canOcclude() ? 1 : 0);
        }
        return signature;
    }

    private record AmbientVBOKey(int worldId, BlockPos pos, ResourceLocation modelLocation) {
    }

    private record CachedVBO(VertexBuffer vbo, int lightSignature) {
    }
}
