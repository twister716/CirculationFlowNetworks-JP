package com.circulation.circulation_networks.client.compat;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

public final class RenderSystemCompat {

    private RenderSystemCompat() {
    }

    private static boolean canMutateRenderState() {
        return RenderSystem.isOnRenderThread();
    }

    public static void enableBlend() {
        if (canMutateRenderState()) {
            GlStateManager._enableBlend();
        }
    }

    public static void defaultBlendFunc() {
        if (canMutateRenderState()) {
            GlStateManager._blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        }
    }

    public static void disableDepthTest() {
        if (canMutateRenderState()) {
            GlStateManager._disableDepthTest();
        }
    }

    public static void enableDepthTest() {
        if (canMutateRenderState()) {
            GlStateManager._enableDepthTest();
        }
    }

    public static void disableCull() {
        if (canMutateRenderState()) {
            GlStateManager._disableCull();
        }
    }

    public static void enableCull() {
        if (canMutateRenderState()) {
            GlStateManager._enableCull();
        }
    }

    public static void disableBlend() {
        if (canMutateRenderState()) {
            GlStateManager._disableBlend();
        }
    }

    public static void setShaderColor(float r, float g, float b, float a) {
        // 26.1 no longer uses the legacy fixed-function color state path.
    }

    public static void lineWidth(float width) {
        // 26.1 line width is carried by POSITION_COLOR_NORMAL_LINE_WIDTH vertex data.
    }

    public static void applyModelViewMatrix() {
    }

    public static void depthMask(boolean mask) {
        if (canMutateRenderState()) {
            GlStateManager._depthMask(mask);
        }
    }

    public static void blendFunc(int srcFactor, int dstFactor) {
        if (canMutateRenderState()) {
            GlStateManager._blendFuncSeparate(srcFactor, dstFactor, srcFactor, dstFactor);
        }
    }

    public static void additiveBlendFunc() {
        blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
    }

    public static Matrix4f getProjectionMatrix() {
        return new Matrix4f(Minecraft.getInstance().gameRenderer.getGameRenderState().levelRenderState.cameraRenderState.projectionMatrix);
    }

    public static Quaternionf getCameraOrientation(Minecraft minecraft) {
        return new Quaternionf(minecraft.gameRenderer.getMainCamera().rotation());
    }
}
