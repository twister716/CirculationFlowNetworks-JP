package com.circulation.circulation_networks.utils;

public final class RenderingUtils {

    private RenderingUtils() {
    }

    public static void setupWorldRenderState() {
        RenderingBackend.get().setupWorldRenderState();
    }

    public static void restoreWorldRenderState() {
        RenderingBackend.get().restoreWorldRenderState();
    }

    public static void setupAdditiveBlend() {
        RenderingBackend.get().setupAdditiveBlend();
    }

    public static void seedModelViewFromPoseStack(Object poseStack) {
        RenderingBackend.get().seedModelViewFromPoseStack(poseStack);
    }

    public static void drawFilledBox(double x0, double y0, double z0,
                                     double x1, double y1, double z1,
                                     float r, float g, float b, float a) {
        RenderingBackend.get().drawFilledBox(x0, y0, z0, x1, y1, z1, r, g, b, a);
    }

    public static void drawBoxEdges(double x0, double y0, double z0,
                                    double x1, double y1, double z1,
                                    float r, float g, float b, float a,
                                    float lineWidth) {
        RenderingBackend.get().drawBoxEdges(x0, y0, z0, x1, y1, z1, r, g, b, a, lineWidth);
    }

    public static void drawLaserCylinder(double fromX, double fromY, double fromZ,
                                         double toX, double toY, double toZ,
                                         float radius,
                                         float r, float g, float b, float alpha) {
        RenderingBackend.get().drawLaserCylinder(fromX, fromY, fromZ, toX, toY, toZ, radius, r, g, b, alpha);
    }

    public static void drawOverlayLaserCylinder(double fromX, double fromY, double fromZ,
                                                double toX, double toY, double toZ,
                                                float radius,
                                                float r, float g, float b, float alpha) {
        RenderingBackend.get().drawOverlayLaserCylinder(fromX, fromY, fromZ, toX, toY, toZ, radius, r, g, b, alpha);
    }

    public static void drawSphere(float r, float g, float b, float radius, float alpha) {
        drawSphere(r, g, b, radius, alpha, 32, 32);
    }

    public static void drawSphere(float r, float g, float b, float radius, float alpha, int slices, int stacks) {
        RenderingBackend.get().drawSphere(r, g, b, radius, alpha, slices, stacks);
    }

    public static void drawOverlaySphere(float r, float g, float b, float radius, float alpha) {
        drawOverlaySphere(r, g, b, radius, alpha, 32, 32);
    }

    public static void drawOverlaySphere(float r, float g, float b, float radius, float alpha, int slices, int stacks) {
        RenderingBackend.get().drawOverlaySphere(r, g, b, radius, alpha, slices, stacks);
    }

    public static double getPlayerRenderX(float partialTicks) {
        return RenderingGeometryCore.getPlayerRenderX(partialTicks);
    }

    public static double getPlayerRenderY(float partialTicks) {
        return RenderingGeometryCore.getPlayerRenderY(partialTicks);
    }

    public static double getPlayerRenderZ(float partialTicks) {
        return RenderingGeometryCore.getPlayerRenderZ(partialTicks);
    }

    public static boolean isWithinRenderDistance(double posX, double posY, double posZ,
                                                 double playerX, double playerY, double playerZ,
                                                 double maxDistSq) {
        return RenderingGeometryCore.isWithinRenderDistance(posX, posY, posZ, playerX, playerY, playerZ, maxDistSq);
    }

    public static void drawCachedIntersection(float[] verts, float r, float g, float b, float lineWidth) {
        RenderingBackend.get().drawCachedIntersection(verts, r, g, b, lineWidth);
    }

    public static void drawCachedIntersection(float[] verts, float r, float g, float b) {
        drawCachedIntersection(verts, r, g, b, 4.0F);
    }
}
