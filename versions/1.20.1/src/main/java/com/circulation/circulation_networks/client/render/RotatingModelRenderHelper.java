package com.circulation.circulation_networks.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public final class RotatingModelRenderHelper {

    private static final int FULL_BRIGHT = LightTexture.FULL_BRIGHT;

    private RotatingModelRenderHelper() {
    }

    public static void renderAroundYAxisFullBright(
        PoseStack poseStack, MultiBufferSource bufferSource,
        BlockState state, ResourceLocation modelLocation,
        float angle, float pivotX, float pivotY, float pivotZ,
        int packedOverlay
    ) {
        renderAroundAxisFullBright(poseStack, bufferSource, state, modelLocation,
            angle, pivotX, pivotY, pivotZ, 0.0F, 1.0F, 0.0F, packedOverlay);
    }

    public static void renderAroundAxisFullBright(
        PoseStack poseStack, MultiBufferSource bufferSource,
        BlockState state, ResourceLocation modelLocation,
        float angle, float pivotX, float pivotY, float pivotZ,
        float axisX, float axisY, float axisZ,
        int packedOverlay
    ) {
        BakedModel model = RotatingBlockModelCache.get(modelLocation);

        poseStack.pushPose();
        poseStack.translate(pivotX, pivotY, pivotZ);
        poseStack.mulPose(new Quaternionf().rotationAxis(
            (float) Math.toRadians(angle), axisX, axisY, axisZ));
        poseStack.translate(-pivotX, -pivotY, -pivotZ);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.cutout());
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
            poseStack.last(), consumer, state, model, 1.0F, 1.0F, 1.0F, FULL_BRIGHT, packedOverlay
        );

        poseStack.popPose();
    }

    public static void renderAroundAxis(
        PoseStack poseStack, MultiBufferSource bufferSource,
        BlockAndTintGetter level, BlockPos pos,
        BlockState state, ResourceLocation modelLocation,
        float angle, float pivotX, float pivotY, float pivotZ,
        float axisX, float axisY, float axisZ,
        int packedOverlay
    ) {
        BakedModel model = RotatingBlockModelCache.get(modelLocation);

        poseStack.pushPose();
        poseStack.translate(pivotX, pivotY, pivotZ);
        poseStack.mulPose(new Quaternionf().rotationAxis(
            (float) Math.toRadians(angle), axisX, axisY, axisZ));
        poseStack.translate(-pivotX, -pivotY, -pivotZ);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.cutout());
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(
            level, model, state, pos, poseStack, consumer, false,
            RandomSource.create(), 42L, packedOverlay
        );

        poseStack.popPose();
    }
}
