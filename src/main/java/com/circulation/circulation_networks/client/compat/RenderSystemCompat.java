package com.circulation.circulation_networks.client.compat;

import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public final class RenderSystemCompat {

    private RenderSystemCompat() {
    }

    private static boolean hasCurrentContext() {
        return GLFW.glfwGetCurrentContext() != 0L;
    }

    public static void enableBlend() {
        if (hasCurrentContext()) {
            GL11.glEnable(GL11.GL_BLEND);
        }
    }

    public static void defaultBlendFunc() {
        if (hasCurrentContext()) {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    public static void disableDepthTest() {
        if (hasCurrentContext()) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
    }

    public static void enableDepthTest() {
        if (hasCurrentContext()) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
    }

    public static void disableCull() {
        if (hasCurrentContext()) {
            GL11.glDisable(GL11.GL_CULL_FACE);
        }
    }

    public static void enableCull() {
        if (hasCurrentContext()) {
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    }

    public static void disableBlend() {
        if (hasCurrentContext()) {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    public static void setShaderColor(float r, float g, float b, float a) {
        if (hasCurrentContext()) {
            GL11.glColor4f(r, g, b, a);
        }
    }

    public static void lineWidth(float width) {
        if (hasCurrentContext()) {
            GL11.glLineWidth(width);
        }
    }

    public static void applyModelViewMatrix() {
    }

    public static void depthMask(boolean mask) {
        if (hasCurrentContext()) {
            GL11.glDepthMask(mask);
        }
    }

    public static void blendFunc(int srcFactor, int dstFactor) {
        if (hasCurrentContext()) {
            GL11.glBlendFunc(srcFactor, dstFactor);
        }
    }

    public static Matrix4f getProjectionMatrix() {
        return new Matrix4f(Minecraft.getInstance().gameRenderer.getGameRenderState().levelRenderState.cameraRenderState.projectionMatrix);
    }

    public static Quaternionf getCameraOrientation(Minecraft minecraft) {
        return new Quaternionf(minecraft.gameRenderer.getMainCamera().rotation());
    }
}
