package com.circulation.circulation_networks.client.render;

import com.circulation.circulation_networks.CFNConfig;
import com.circulation.circulation_networks.registry.CFNBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.CHARGING_IN_BASE;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.CHARGING_IN_EMISSIVE;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.CHARGING_RING_BASE;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.CHARGING_RING_EMISSIVE;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.CHARGING_STATIC;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.NODE_CRYSTAL;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.PEDESTAL_BASE;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.PEDESTAL_BASE_EMISSIVE;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.PEDESTAL_FRAME_CLOCKWISE;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.PEDESTAL_FRAME_COUNTER_CLOCKWISE;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.PEDESTAL_STATIC;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.PORT_IN_BASE;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.PORT_IN_EMISSIVE;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.PORT_OUT_BASE;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.PORT_OUT_EMISSIVE;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.PORT_STATIC;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.RELAY_BOTTOM_SPIRAL_BASE;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.RELAY_BOTTOM_SPIRAL_EMISSIVE;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.RELAY_STATIC;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.RELAY_TOP_SPIRAL_BASE;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.RELAY_TOP_SPIRAL_EMISSIVE;

@OnlyIn(Dist.CLIENT)
public final class AnimatedNodeItemStackRenderer extends BlockEntityWithoutLevelRenderer {

    private static final float CENTER = 0.5F;
    private static final float FRAME_PIVOT_X = 8.0F / 16.0F;
    private static final float FRAME_PIVOT_Y = 5.0F / 16.0F;
    private static final float FRAME_PIVOT_Z = 8.0F / 16.0F;
    private static AnimatedNodeItemStackRenderer instance;

    private AnimatedNodeItemStackRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    public static AnimatedNodeItemStackRenderer getInstance() {
        if (instance == null) {
            Minecraft mc = Minecraft.getInstance();
            instance = new AnimatedNodeItemStackRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
        }
        return instance;
    }

    private static void renderRelayNode(PoseStack poseStack, MultiBufferSource bufferSource,
                                        long worldTime, float partialTicks, int packedLight, int packedOverlay) {
        if (!CFNConfig.NODE.rendering.animatedSpecialModels) {
            renderModel(poseStack, bufferSource, RELAY_STATIC, packedLight, packedOverlay);
            return;
        }

        float topAngle = NodeRotationAnimation.relayBottomSpiralAngle(worldTime, partialTicks);
        float crystalAngle = NodeRotationAnimation.relayCrystalAngle(worldTime, partialTicks);
        float bottomAngle = NodeRotationAnimation.relayTopSpiralAngle(worldTime, partialTicks);

        renderAroundYAxis(poseStack, bufferSource, RELAY_TOP_SPIRAL_BASE, topAngle, CENTER, CENTER, CENTER, packedLight, packedOverlay);
        renderAroundYAxisFullBright(poseStack, bufferSource, RELAY_TOP_SPIRAL_EMISSIVE, topAngle, CENTER, CENTER, CENTER, packedOverlay);
        renderAroundYAxisFullBright(poseStack, bufferSource, NODE_CRYSTAL, crystalAngle, CENTER, CENTER, CENTER, packedOverlay);
        renderAroundYAxis(poseStack, bufferSource, RELAY_BOTTOM_SPIRAL_BASE, bottomAngle, CENTER, CENTER, CENTER, packedLight, packedOverlay);
        renderAroundYAxisFullBright(poseStack, bufferSource, RELAY_BOTTOM_SPIRAL_EMISSIVE, bottomAngle, CENTER, CENTER, CENTER, packedOverlay);
    }

    private static void renderChargingNode(PoseStack poseStack, MultiBufferSource bufferSource,
                                           long worldTime, float partialTicks, int packedLight, int packedOverlay) {
        if (!CFNConfig.NODE.rendering.animatedSpecialModels) {
            renderModel(poseStack, bufferSource, CHARGING_STATIC, packedLight, packedOverlay);
            return;
        }

        float topAngle = NodeRotationAnimation.relayBottomSpiralAngle(worldTime, partialTicks);
        float crystalAngle = NodeRotationAnimation.relayCrystalAngle(worldTime, partialTicks);
        float bottomAngle = NodeRotationAnimation.relayBottomSpiralAngle(worldTime, partialTicks);

        renderAroundYAxis(poseStack, bufferSource, CHARGING_IN_BASE, topAngle, CENTER, CENTER, CENTER, packedLight, packedOverlay);
        renderAroundYAxisFullBright(poseStack, bufferSource, CHARGING_IN_EMISSIVE, topAngle, CENTER, CENTER, CENTER, packedOverlay);
        renderAroundYAxisFullBright(poseStack, bufferSource, NODE_CRYSTAL, crystalAngle, CENTER, CENTER, CENTER, packedOverlay);
        renderAroundYAxis(poseStack, bufferSource, CHARGING_RING_BASE, bottomAngle, CENTER, CENTER, CENTER, packedLight, packedOverlay);
        renderAroundYAxisFullBright(poseStack, bufferSource, CHARGING_RING_EMISSIVE, bottomAngle, CENTER, CENTER, CENTER, packedOverlay);
    }

    private static void renderPortNode(PoseStack poseStack, MultiBufferSource bufferSource,
                                       long worldTime, float partialTicks, int packedLight, int packedOverlay) {
        if (!CFNConfig.NODE.rendering.animatedSpecialModels) {
            renderModel(poseStack, bufferSource, PORT_STATIC, packedLight, packedOverlay);
            return;
        }

        float topAngle = NodeRotationAnimation.relayBottomSpiralAngle(worldTime, partialTicks);
        float crystalAngle = NodeRotationAnimation.relayCrystalAngle(worldTime, partialTicks);
        float bottomAngle = NodeRotationAnimation.relayTopSpiralAngle(worldTime, partialTicks);

        renderAroundYAxis(poseStack, bufferSource, PORT_IN_BASE, topAngle, CENTER, CENTER, CENTER, packedLight, packedOverlay);
        renderAroundYAxisFullBright(poseStack, bufferSource, PORT_IN_EMISSIVE, topAngle, CENTER, CENTER, CENTER, packedOverlay);
        renderAroundYAxisFullBright(poseStack, bufferSource, NODE_CRYSTAL, crystalAngle, CENTER, CENTER, CENTER, packedOverlay);
        renderAroundYAxis(poseStack, bufferSource, PORT_OUT_BASE, bottomAngle, CENTER, CENTER, CENTER, packedLight, packedOverlay);
        renderAroundYAxisFullBright(poseStack, bufferSource, PORT_OUT_EMISSIVE, bottomAngle, CENTER, CENTER, CENTER, packedOverlay);
    }

    private static void renderNodePedestal(PoseStack poseStack, MultiBufferSource bufferSource,
                                           long worldTime, float partialTicks, int packedLight, int packedOverlay) {
        if (!CFNConfig.NODE.rendering.animatedSpecialModels) {
            renderModel(poseStack, bufferSource, PEDESTAL_STATIC, packedLight, packedOverlay);
            return;
        }

        renderModel(poseStack, bufferSource, PEDESTAL_BASE, packedLight, packedOverlay);
        renderModelFullBright(poseStack, bufferSource, PEDESTAL_BASE_EMISSIVE, packedOverlay);
        renderAroundAxis(poseStack, bufferSource, PEDESTAL_FRAME_CLOCKWISE,
            NodeRotationAnimation.pedestalClockwiseFrameAngle(worldTime, partialTicks),
            FRAME_PIVOT_X, FRAME_PIVOT_Y, FRAME_PIVOT_Z,
            NodeRotationAnimation.tiltedAxisXForZRotation(-22.5F),
            NodeRotationAnimation.tiltedAxisY(),
            NodeRotationAnimation.tiltedAxisZ(),
            LightTexture.FULL_BRIGHT, packedOverlay);
        renderAroundAxis(poseStack, bufferSource, PEDESTAL_FRAME_COUNTER_CLOCKWISE,
            NodeRotationAnimation.pedestalCounterClockwiseFrameAngle(worldTime, partialTicks),
            FRAME_PIVOT_X, FRAME_PIVOT_Y, FRAME_PIVOT_Z,
            NodeRotationAnimation.tiltedAxisXForZRotation(22.5F),
            NodeRotationAnimation.tiltedAxisY(),
            NodeRotationAnimation.tiltedAxisZ(),
            LightTexture.FULL_BRIGHT, packedOverlay);
    }

    private static void renderModel(PoseStack poseStack, MultiBufferSource bufferSource,
                                    ResourceLocation modelLocation, int packedLight, int packedOverlay) {
        BakedModel model = RotatingBlockModelCache.get(modelLocation);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.cutout());
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
            poseStack.last(), consumer, null, model, 1.0F, 1.0F, 1.0F, packedLight, packedOverlay, ModelData.EMPTY, null
        );
    }

    private static void renderModelFullBright(PoseStack poseStack, MultiBufferSource bufferSource,
                                              ResourceLocation modelLocation, int packedOverlay) {
        renderModel(poseStack, bufferSource, modelLocation, LightTexture.FULL_BRIGHT, packedOverlay);
    }

    private static void renderAroundYAxis(PoseStack poseStack, MultiBufferSource bufferSource,
                                          ResourceLocation modelLocation, float angle,
                                          float pivotX, float pivotY, float pivotZ,
                                          int packedLight, int packedOverlay) {
        renderAroundAxis(poseStack, bufferSource, modelLocation, angle,
            pivotX, pivotY, pivotZ, 0.0F, 1.0F, 0.0F, packedLight, packedOverlay);
    }

    private static void renderAroundYAxisFullBright(PoseStack poseStack, MultiBufferSource bufferSource,
                                                    ResourceLocation modelLocation, float angle,
                                                    float pivotX, float pivotY, float pivotZ,
                                                    int packedOverlay) {
        renderAroundAxis(poseStack, bufferSource, modelLocation, angle,
            pivotX, pivotY, pivotZ, 0.0F, 1.0F, 0.0F, LightTexture.FULL_BRIGHT, packedOverlay);
    }

    private static void renderAroundAxis(PoseStack poseStack, MultiBufferSource bufferSource,
                                         ResourceLocation modelLocation, float angle,
                                         float pivotX, float pivotY, float pivotZ,
                                         float axisX, float axisY, float axisZ,
                                         int packedLight, int packedOverlay) {
        BakedModel model = RotatingBlockModelCache.get(modelLocation);

        poseStack.pushPose();
        poseStack.translate(pivotX, pivotY, pivotZ);
        poseStack.mulPose(new Quaternionf().rotationAxis(
            (float) Math.toRadians(angle), axisX, axisY, axisZ));
        poseStack.translate(-pivotX, -pivotY, -pivotZ);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.cutout());
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
            poseStack.last(), consumer, null, model, 1.0F, 1.0F, 1.0F, packedLight, packedOverlay, ModelData.EMPTY, null
        );

        poseStack.popPose();
    }

    private static AnimationTick resolveAnimationTick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            return new AnimationTick(minecraft.level.getGameTime(),
                minecraft.getTimer().getGameTimeDeltaPartialTick(false));
        }
        double tickTime = System.currentTimeMillis() / 50.0D;
        long wholeTicks = (long) tickTime;
        return new AnimationTick(wholeTicks, (float) (tickTime - wholeTicks));
    }

    @Override
    public void renderByItem(ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (stack.isEmpty()) return;

        Block block = stack.getItem() instanceof BlockItem blockItem ? blockItem.getBlock() : null;
        if (block == null) return;

        AnimationTick tick = resolveAnimationTick();

        if (block == CFNBlocks.blockRelayNode) {
            renderRelayNode(poseStack, bufferSource, tick.worldTime, tick.partialTicks, packedLight, packedOverlay);
        } else if (block == CFNBlocks.blockChargingNode) {
            renderChargingNode(poseStack, bufferSource, tick.worldTime, tick.partialTicks, packedLight, packedOverlay);
        } else if (block == CFNBlocks.blockPortNode) {
            renderPortNode(poseStack, bufferSource, tick.worldTime, tick.partialTicks, packedLight, packedOverlay);
        } else if (block == CFNBlocks.blockNodePedestal) {
            renderNodePedestal(poseStack, bufferSource, tick.worldTime, tick.partialTicks, packedLight, packedOverlay);
        }
    }

    private record AnimationTick(long worldTime, float partialTicks) {
    }
}
