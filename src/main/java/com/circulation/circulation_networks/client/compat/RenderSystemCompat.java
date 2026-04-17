package com.circulation.circulation_networks.client.compat;

import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

public final class RenderSystemCompat {

    private RenderSystemCompat() {
    }

    public static void enableBlend() {
        GL11.glEnable(GL11.GL_BLEND);
    }

    public static void defaultBlendFunc() {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void disableDepthTest() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    public static void enableDepthTest() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void disableCull() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public static void enableCull() {
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    public static void disableBlend() {
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void setShaderColor(float r, float g, float b, float a) {
        GL11.glColor4f(r, g, b, a);
    }

    public static void lineWidth(float width) {
        GL11.glLineWidth(width);
    }

    public static void applyModelViewMatrix() {
    }

    public static void depthMask(boolean mask) {
        GL11.glDepthMask(mask);
    }

    public static void blendFunc(int srcFactor, int dstFactor) {
        GL11.glBlendFunc(srcFactor, dstFactor);
    }

    public static Matrix4f getProjectionMatrix() {
        return new Matrix4f(Minecraft.getInstance().gameRenderer.getGameRenderState().levelRenderState.cameraRenderState.projectionMatrix);
    }

    public static Quaternionf getCameraOrientation(Minecraft minecraft) {
        return new Quaternionf(minecraft.gameRenderer.getMainCamera().rotation());
    }
}
