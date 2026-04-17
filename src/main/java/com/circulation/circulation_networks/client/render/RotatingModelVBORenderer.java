package com.circulation.circulation_networks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

import java.util.List;

public final class RotatingModelVBORenderer {

    private static final Quaternionf ROTATION = new Quaternionf();
    private static final QuadInstance FULL_BRIGHT_QUAD = new QuadInstance();
    private static final RenderType BLOCK_RENDER_TYPE = Sheets.cutoutBlockSheet();
    private static int renderSessionDepth;
    private static SubmitNodeCollector renderSessionCollector;

    static {
        FULL_BRIGHT_QUAD.setColor(-1);
        FULL_BRIGHT_QUAD.setLightCoords(LightCoordsUtil.FULL_BRIGHT);
    }

    private RotatingModelVBORenderer() {
    }

    public static RenderSession beginRenderSession(SubmitNodeCollector submitNodeCollector) {
        if (submitNodeCollector == null) {
            return RenderSession.NOOP;
        }
        if (renderSessionDepth++ == 0) {
            renderSessionCollector = submitNodeCollector;
        }
        return new RenderSession(true);
    }

    public static void renderFullBrightYAxis(PoseStack poseStack, BlockState state, Identifier modelLocation,
                                             float angle, float pivotX, float pivotY, float pivotZ) {
        renderFullBright(poseStack, modelLocation, angle, pivotX, pivotY, pivotZ, 0.0F, 1.0F, 0.0F);
    }

    public static void renderFullBright(PoseStack poseStack, BlockState state, Identifier modelLocation,
                                        float angle, float pivotX, float pivotY, float pivotZ,
                                        float axisX, float axisY, float axisZ) {
        renderFullBright(poseStack, modelLocation, angle, pivotX, pivotY, pivotZ, axisX, axisY, axisZ);
    }

    public static void renderFullBrightYAxis(PoseStack poseStack, Identifier modelLocation,
                                             float angle, float pivotX, float pivotY, float pivotZ) {
        renderFullBright(poseStack, modelLocation, angle, pivotX, pivotY, pivotZ, 0.0F, 1.0F, 0.0F);
    }

    public static void renderFullBright(PoseStack poseStack, Identifier modelLocation,
                                        float angle, float pivotX, float pivotY, float pivotZ,
                                        float axisX, float axisY, float axisZ) {
        SubmitNodeCollector submitNodeCollector = renderSessionCollector;
        if (submitNodeCollector == null) {
            return;
        }
        BlockStateModel model = RotatingBlockModelCache.get(modelLocation);
        submitRotated(
            poseStack,
            submitNodeCollector,
            BLOCK_RENDER_TYPE,
            angle,
            pivotX,
            pivotY,
            pivotZ,
            axisX,
            axisY,
            axisZ,
            (pose, buffer) -> renderFullBrightModel(pose, buffer, model)
        );
    }

    public static void renderAmbientLit(PoseStack poseStack, Level level, BlockPos pos,
                                        BlockState state, Identifier modelLocation,
                                        float angle, float pivotX, float pivotY, float pivotZ,
                                        float axisX, float axisY, float axisZ) {
        renderAmbientLit(poseStack, level, pos, pos, state, modelLocation, angle, pivotX, pivotY, pivotZ, axisX, axisY, axisZ);
    }

    public static void renderAmbientLit(PoseStack poseStack, Level level, BlockPos originPos, BlockPos lightSamplePos,
                                        BlockState state, Identifier modelLocation,
                                        float angle, float pivotX, float pivotY, float pivotZ,
                                        float axisX, float axisY, float axisZ) {
        SubmitNodeCollector submitNodeCollector = renderSessionCollector;
        if (submitNodeCollector == null || !(level instanceof net.minecraft.client.multiplayer.ClientLevel clientLevel)) {
            return;
        }
        BlockStateModel model = RotatingBlockModelCache.get(modelLocation);
        submitRotated(
            poseStack,
            submitNodeCollector,
            BLOCK_RENDER_TYPE,
            angle,
            pivotX,
            pivotY,
            pivotZ,
            axisX,
            axisY,
            axisZ,
            (pose, buffer) -> renderAmbientLitModel(buffer, clientLevel, lightSamplePos, state, model)
        );
    }

    public static void removePosition(int worldId, BlockPos pos) {
    }

    public static void clearAll() {
    }

    public static void renderLitYAxis(PoseStack poseStack, int lightCoords, Identifier modelLocation,
                                      float angle, float pivotX, float pivotY, float pivotZ) {
        renderLit(poseStack, lightCoords, modelLocation, angle, pivotX, pivotY, pivotZ, 0.0F, 1.0F, 0.0F);
    }

    public static void renderLit(PoseStack poseStack, int lightCoords, Identifier modelLocation,
                                 float angle, float pivotX, float pivotY, float pivotZ,
                                 float axisX, float axisY, float axisZ) {
        SubmitNodeCollector submitNodeCollector = renderSessionCollector;
        if (submitNodeCollector == null) {
            return;
        }
        BlockStateModel model = RotatingBlockModelCache.get(modelLocation);
        submitRotated(
            poseStack,
            submitNodeCollector,
            BLOCK_RENDER_TYPE,
            angle,
            pivotX,
            pivotY,
            pivotZ,
            axisX,
            axisY,
            axisZ,
            (pose, buffer) -> renderLitModel(pose, buffer, model, lightCoords)
        );
    }

    private static void submitRotated(
        PoseStack poseStack,
        SubmitNodeCollector submitNodeCollector,
        RenderType renderType,
        float angle,
        float pivotX,
        float pivotY,
        float pivotZ,
        float axisX,
        float axisY,
        float axisZ,
        SubmitNodeCollector.CustomGeometryRenderer renderer
    ) {
        poseStack.pushPose();
        poseStack.translate(pivotX, pivotY, pivotZ);
        ROTATION.rotationAxis((float) Math.toRadians(angle), axisX, axisY, axisZ);
        poseStack.mulPose(ROTATION);
        poseStack.translate(-pivotX, -pivotY, -pivotZ);
        submitNodeCollector.submitCustomGeometry(poseStack, renderType, renderer);
        poseStack.popPose();
    }

    private static void renderAmbientLitModel(
        VertexConsumer buffer,
        BlockAndTintGetter level,
        BlockPos lightSamplePos,
        BlockState state,
        BlockStateModel model
    ) {
        ModelBlockRenderer renderer = new ModelBlockRenderer(true, false, Minecraft.getInstance().getBlockColors());
        BlockQuadOutput output = buffer::putBlockBakedQuad;
        renderer.tesselateBlock(output, 0.0F, 0.0F, 0.0F, level, lightSamplePos, state, model, 42L);
    }

    @SuppressWarnings("deprecation")
    private static void renderFullBrightModel(PoseStack.Pose pose, VertexConsumer buffer, BlockStateModel model) {
        List<BlockStateModelPart> parts = new ObjectArrayList<>();
        model.collectParts(RandomSource.create(42L), parts);
        for (BlockStateModelPart part : parts) {
            emitFullBrightQuads(pose, buffer, part.getQuads(null));
            for (Direction direction : Direction.values()) {
                emitFullBrightQuads(pose, buffer, part.getQuads(direction));
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static void renderLitModel(PoseStack.Pose pose, VertexConsumer buffer, BlockStateModel model, int lightCoords) {
        List<BlockStateModelPart> parts = new ObjectArrayList<>();
        model.collectParts(RandomSource.create(42L), parts);
        QuadInstance litQuad = new QuadInstance();
        litQuad.setColor(-1);
        litQuad.setLightCoords(lightCoords);
        for (BlockStateModelPart part : parts) {
            emitLitQuads(pose, buffer, part.getQuads(null), litQuad);
            for (Direction direction : Direction.values()) {
                emitLitQuads(pose, buffer, part.getQuads(direction), litQuad);
            }
        }
    }

    private static void emitFullBrightQuads(PoseStack.Pose pose, VertexConsumer buffer, List<BakedQuad> quads) {
        for (BakedQuad quad : quads) {
            buffer.putBakedQuad(pose, quad, FULL_BRIGHT_QUAD);
        }
    }

    private static void emitLitQuads(PoseStack.Pose pose, VertexConsumer buffer, List<BakedQuad> quads, QuadInstance litQuad) {
        for (BakedQuad quad : quads) {
            buffer.putBakedQuad(pose, quad, litQuad);
        }
    }

    private static void endRenderSession() {
        if (renderSessionDepth <= 0) {
            return;
        }
        if (--renderSessionDepth > 0) {
            return;
        }
        renderSessionCollector = null;
    }

    public static final class RenderSession implements AutoCloseable {

        private static final RenderSession NOOP = new RenderSession(false);

        private final boolean active;
        private boolean closed;

        private RenderSession(boolean active) {
            this.active = active;
        }

        @Override
        public void close() {
            if (closed || !active) {
                return;
            }
            closed = true;
            endRenderSession();
        }
    }
}
