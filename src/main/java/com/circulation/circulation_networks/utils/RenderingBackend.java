package com.circulation.circulation_networks.utils;

import java.util.Objects;

public abstract class RenderingBackend {

    private static RenderingBackend INSTANCE;

    public static void setInstance(RenderingBackend backend) {
        INSTANCE = Objects.requireNonNull(backend, "backend");
    }

    protected static RenderingBackend get() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Rendering backend has not been initialized");
        }
        return INSTANCE;
    }

    public abstract void setupWorldRenderState();

    public abstract void restoreWorldRenderState();

    public abstract void setupAdditiveBlend();

    public abstract void seedModelViewFromPoseStack(Object poseStack);

    public abstract void drawFilledBox(double x0, double y0, double z0,
                                       double x1, double y1, double z1,
                                       float r, float g, float b, float a);

    public abstract void drawBoxEdges(double x0, double y0, double z0,
                                      double x1, double y1, double z1,
                                      float r, float g, float b, float a,
                                      float lineWidth);

    public abstract void drawLaserCylinder(double fromX, double fromY, double fromZ,
                                           double toX, double toY, double toZ,
                                           float radius,
                                           float r, float g, float b, float alpha);

    public abstract void drawSphere(float r, float g, float b, float radius, float alpha, int slices, int stacks);

    public abstract void drawCachedIntersection(float[] verts, float r, float g, float b, float lineWidth);
}
