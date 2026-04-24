package com.circulation.circulation_networks.handlers;

import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.items.CirculationConfiguratorModeModel.ToolFunction;
import com.circulation.circulation_networks.items.CirculationConfiguratorState;
import com.circulation.circulation_networks.registry.CFNItems;
import com.circulation.circulation_networks.utils.RenderingUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Minecraft;
//~ mc_imports
import net.minecraft.util.math.BlockPos;
//? if <1.20 {
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
//?} else if <1.21 {
/*import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
*///?} else {
/*import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
*///?}
//? if <1.20 {
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderWorldLastEvent;
//?} else {
/*import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
//~ neo_imports
import net.minecraftforge.client.event.RenderLevelStageEvent;
*///?}

//~ if >=1.20 '@SideOnly(Side.CLIENT)' -> '@OnlyIn(Dist.CLIENT)' {
@SideOnly(Side.CLIENT)
//~}
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
        if (!(stack.getItem() == CFNItems.circulationConfigurator
            && CirculationConfiguratorState.getFunction(stack) == ToolFunction.CONFIGURATION))
            return;

        //? if <1.20 {
        double doubleX = RenderingUtils.getPlayerRenderX(event.getPartialTicks());
        double doubleY = RenderingUtils.getPlayerRenderY(event.getPartialTicks());
        double doubleZ = RenderingUtils.getPlayerRenderZ(event.getPartialTicks());
        //?} else {
        /*var cameraPos = event.getCamera().getPosition();
        double doubleX = cameraPos.x;
        double doubleY = cameraPos.y;
        double doubleZ = cameraPos.z;
        *///?}

        //? if <1.20 {
        GlStateManager.pushMatrix();
        GlStateManager.translate(-doubleX, -doubleY, -doubleZ);
        //?} else if <1.21 {
        /*PoseStack mvStack = RenderSystem.getModelViewStack();
        mvStack.pushPose();
        mvStack.last().pose().set(event.getPoseStack().last().pose());
        mvStack.last().normal().set(event.getPoseStack().last().normal());
        mvStack.translate(-doubleX, -doubleY, -doubleZ);
        RenderSystem.applyModelViewMatrix();
        *///?} else {
        /*var mvStack = RenderSystem.getModelViewStack();
        mvStack.pushMatrix();
        mvStack.set(event.getModelViewMatrix());
        mvStack.translate((float) -doubleX, (float) -doubleY, (float) -doubleZ);
        RenderSystem.applyModelViewMatrix();
        *///?}
        try {
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

                float fillR, fillG, fillB;
                float edgeR, edgeG, edgeB;
                switch (type) {
                    case SEND -> {
                        fillR = 1.0f;
                        fillG = 0.2f;
                        fillB = 0.2f;
                        edgeR = fillR;
                        edgeG = fillG;
                        edgeB = fillB;
                    }
                    case RECEIVE -> {
                        fillR = 0.2f;
                        fillG = 1.0f;
                        fillB = 0.2f;
                        edgeR = fillR;
                        edgeG = fillG;
                        edgeB = fillB;
                    }
                    case STORAGE -> {
                        fillR = 0.2f;
                        fillG = 0.4f;
                        fillB = 1.0f;
                        edgeR = fillR;
                        edgeG = fillG;
                        edgeB = fillB;
                    }
                    case INVALID -> {
                        fillR = 0.08f;
                        fillG = 0.08f;
                        fillB = 0.08f;
                        edgeR = 1.0f;
                        edgeG = 0.15f;
                        edgeB = 0.15f;
                    }
                    default -> {
                        fillR = 1.0f;
                        fillG = 1.0f;
                        fillB = 1.0f;
                        edgeR = fillR;
                        edgeG = fillG;
                        edgeB = fillB;
                    }
                }

                double x0 = pos.getX() + INSET;
                double y0 = pos.getY() + INSET;
                double z0 = pos.getZ() + INSET;
                double x1 = pos.getX() + 1.0 - INSET;
                double y1 = pos.getY() + 1.0 - INSET;
                double z1 = pos.getZ() + 1.0 - INSET;

                RenderingUtils.drawFilledBox(x0, y0, z0, x1, y1, z1, fillR, fillG, fillB, 0.15f);
                RenderingUtils.drawBoxEdges(x0, y0, z0, x1, y1, z1, edgeR, edgeG, edgeB, 0.6f, 2.0f);
            }
        } finally {
            RenderingUtils.restoreWorldRenderState();
            //? if <1.20 {
            GlStateManager.popMatrix();
            //?} else if <1.21 {
            /*RenderSystem.getModelViewStack().popPose();
            RenderSystem.applyModelViewMatrix();
            *///?} else {
            /*RenderSystem.getModelViewStack().popMatrix();
            RenderSystem.applyModelViewMatrix();
             *///?}
        }
    }
}
