package com.circulation.circulation_networks.handlers;

import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.items.InspectionToolModeModel.ToolFunction;
import com.circulation.circulation_networks.items.InspectionToolState;
import com.circulation.circulation_networks.registry.CFNItems;
import com.circulation.circulation_networks.utils.RenderingUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
//? if <1.20 {
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
//?} else {
/*import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
*///?}

//? if <1.20 {
@SideOnly(Side.CLIENT)
//?} else {
/*@OnlyIn(Dist.CLIENT)
*///?}
public final class ConfigOverrideRenderingHandler {

    public static final ConfigOverrideRenderingHandler INSTANCE = new ConfigOverrideRenderingHandler();

    private static final float INSET = 0.01f;
    private static final double MAX_RENDER_DIST_SQ = 256 * 256;

    private final Long2ObjectMap<IEnergyHandler.EnergyType> overrides = new Long2ObjectLinkedOpenHashMap<>();

    public void addOverride(long pos, IEnergyHandler.EnergyType type) {
        overrides.put(pos, type);
    }

    public void removeOverride(long pos) {
        overrides.remove(pos);
    }

    public void clear() {
        overrides.clear();
    }

    @SubscribeEvent
    //? if <1.20 {
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
    //?} else {
    /*public void renderWorldLastEvent(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
    *///?}
        if (overrides.isEmpty()) return;

        //? if <1.20 {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP p = mc.player;
        //?} else {
        /*Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        *///?}

        //? if <1.20 {
        var stack = p.getHeldItemMainhand();
        //?} else {
        /*var stack = p.getMainHandItem();
        *///?}
        if (!(stack.getItem() == CFNItems.inspectionTool
            && InspectionToolState.getFunction(stack) == ToolFunction.CONFIGURATION))
            return;

        //? if <1.20 {
        double doubleX = RenderingUtils.getPlayerRenderX(event.getPartialTicks());
        double doubleY = RenderingUtils.getPlayerRenderY(event.getPartialTicks());
        double doubleZ = RenderingUtils.getPlayerRenderZ(event.getPartialTicks());
        //?} else {
        /*double doubleX = RenderingUtils.getPlayerRenderX(event.getPartialTick());
        double doubleY = RenderingUtils.getPlayerRenderY(event.getPartialTick());
        double doubleZ = RenderingUtils.getPlayerRenderZ(event.getPartialTick());
        *///?}

        //? if <1.20 {
        GlStateManager.pushMatrix();
        GlStateManager.translate(-doubleX, -doubleY, -doubleZ);
        //?} else {
        /*PoseStack mvStack = RenderSystem.getModelViewStack();
        mvStack.pushPose();
        mvStack.translate(-doubleX, -doubleY, -doubleZ);
        RenderSystem.applyModelViewMatrix();
        *///?}
        RenderingUtils.setupWorldRenderState();
        RenderingUtils.setupAdditiveBlend();

        for (var entry : overrides.long2ObjectEntrySet()) {
            //? if <1.20 {
            BlockPos pos = BlockPos.fromLong(entry.getLongKey());
            //?} else {
            /*BlockPos pos = BlockPos.of(entry.getLongKey());
            *///?}
            IEnergyHandler.EnergyType type = entry.getValue();

            if (!RenderingUtils.isWithinRenderDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                doubleX, doubleY, doubleZ, MAX_RENDER_DIST_SQ)) {
                continue;
            }

            float r, g, b;
            switch (type) {
                case SEND -> { r = 1.0f; g = 0.2f; b = 0.2f; }
                case RECEIVE -> { r = 0.2f; g = 1.0f; b = 0.2f; }
                case STORAGE -> { r = 0.2f; g = 0.4f; b = 1.0f; }
                default -> { r = 1.0f; g = 1.0f; b = 1.0f; }
            }

            double x0 = pos.getX() + INSET;
            double y0 = pos.getY() + INSET;
            double z0 = pos.getZ() + INSET;
            double x1 = pos.getX() + 1.0 - INSET;
            double y1 = pos.getY() + 1.0 - INSET;
            double z1 = pos.getZ() + 1.0 - INSET;

            RenderingUtils.drawFilledBox(x0, y0, z0, x1, y1, z1, r, g, b, 0.15f);
            RenderingUtils.drawBoxEdges(x0, y0, z0, x1, y1, z1, r, g, b, 0.6f, 2.0f);
        }

        RenderingUtils.restoreWorldRenderState();
        //? if <1.20 {
        GlStateManager.popMatrix();
        //?} else {
        /*RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
        *///?}
    }
}
