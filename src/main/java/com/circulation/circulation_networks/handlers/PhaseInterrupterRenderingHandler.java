package com.circulation.circulation_networks.handlers;

import com.circulation.circulation_networks.api.IPhaseInterrupterBlockEntity;
import com.circulation.circulation_networks.manager.PhaseInterrupterManager;
import com.circulation.circulation_networks.utils.AnimationUtils;
import com.circulation.circulation_networks.utils.RenderingUtils;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
//? if <1.20 {
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
//?} else {
/*import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
*///?}
//? if <1.20 {
//?} else if <1.21 {
/*import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
*///?} else {
/*import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
*///?}

import java.util.Map;
import java.util.WeakHashMap;

//? if <1.20 {
@SideOnly(Side.CLIENT)
//?} else {
/*@OnlyIn(Dist.CLIENT)
*///?}
public final class PhaseInterrupterRenderingHandler {

    public static final PhaseInterrupterRenderingHandler INSTANCE = new PhaseInterrupterRenderingHandler();

    private static final float ORANGE_R = 1.0f;
    private static final float ORANGE_G = 0.647f;
    private static final float ORANGE_B = 0.0f;
    private static final float ALPHA = 0.5f;
    private static final float RANGE_EXPANSION = 0.01f;
    private static final float ANIMATION_DURATION = 2.0f;

    private final Map<IPhaseInterrupterBlockEntity, Float> animProgress = new WeakHashMap<>();

    @SubscribeEvent
    //? if <1.21 {
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
    //?} else {
    /*public void onClientTick(ClientTickEvent.Pre event) {
    *///?}

        animProgress.replaceAll((tile, progress) -> {
            if (tile.isShowingRange()) {
                return AnimationUtils.advanceTowardsOne(progress, 1.0f / (ANIMATION_DURATION * 20.0f));
            } else {
                return 0.0f;
            }
        });
    }

    private void renderInterrupterRange(IPhaseInterrupterBlockEntity interrupter, double playerX, double playerY, double playerZ) {
        //? if <1.20 {
        GlStateManager.pushMatrix();
        GlStateManager.translate(-playerX, -playerY, -playerZ);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(false);
        //?} else if <1.21 {
        /*PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.translate(-playerX, -playerY, -playerZ);
        RenderSystem.applyModelViewMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        *///?} else {
        /*var modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.translate((float) -playerX, (float) -playerY, (float) -playerZ);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        *///?}

        int scope = interrupter.getScope();
        double x = interrupter.getPos().getX();
        double y = interrupter.getPos().getY();
        double z = interrupter.getPos().getZ();

        float progress = animProgress.getOrDefault(interrupter, 0.0f);
        float easedProgress = AnimationUtils.easeOutCubic(progress);
        float expandedScope = scope * easedProgress + RANGE_EXPANSION;

        RenderingUtils.drawFilledBox(
            x - expandedScope, y - expandedScope, z - expandedScope,
            x + expandedScope + 1, y + expandedScope + 1, z + expandedScope + 1,
            ORANGE_R, ORANGE_G, ORANGE_B, ALPHA
        );

        //? if <1.20 {
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
        //?} else if <1.21 {
        /*RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
        *///?} else {
        /*RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        modelViewStack.popMatrix();
        *///?}
    }

    @SubscribeEvent
    //? if <1.20 {
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.player == null) return;

        double playerX = RenderingUtils.getPlayerRenderX(event.getPartialTicks());
        double playerY = RenderingUtils.getPlayerRenderY(event.getPartialTicks());
        double playerZ = RenderingUtils.getPlayerRenderZ(event.getPartialTicks());

        int dimId = mc.player.dimension;
    //?} else if <1.21 {
    /*public void onRenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        double playerX = RenderingUtils.getPlayerRenderX(event.getPartialTick());
        double playerY = RenderingUtils.getPlayerRenderY(event.getPartialTick());
        double playerZ = RenderingUtils.getPlayerRenderZ(event.getPartialTick());

        int dimId = mc.level.dimension().location().hashCode();
    *///?} else {
    /*public void onRenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        float _partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        double playerX = RenderingUtils.getPlayerRenderX(_partialTick);
        double playerY = RenderingUtils.getPlayerRenderY(_partialTick);
        double playerZ = RenderingUtils.getPlayerRenderZ(_partialTick);

        int dimId = mc.level.dimension().location().hashCode();
    *///?}

        ReferenceSet<IPhaseInterrupterBlockEntity> interrupters =
            PhaseInterrupterManager.INSTANCE.getInterruptersForDim(dimId);
        if (interrupters == null || interrupters.isEmpty()) return;

        for (IPhaseInterrupterBlockEntity interrupter : interrupters) {
            if (interrupter.isShowingRange()) {
                animProgress.putIfAbsent(interrupter, 0.0f);
                renderInterrupterRange(interrupter, playerX, playerY, playerZ);
            }
        }
    }
}
