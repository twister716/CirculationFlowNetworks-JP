package com.circulation.circulation_networks.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public final class RenderingGeometryCore {

    private static final int CYLINDER_SIDES = 8;
    private static final double CYLINDER_ANGLE_STEP = 2.0D * Math.PI / CYLINDER_SIDES;

    private RenderingGeometryCore() {
    }

    public static int toColorComponent(float value) {
        float scaled = value * 255.0F;
        int rounded = Math.round(scaled);
        if (rounded < 0) {
            return 0;
        }
        return Math.min(rounded, 255);
    }

    public static float[] buildFilledBoxVertices(double x0, double y0, double z0,
                                                 double x1, double y1, double z1) {
        return new float[]{
            (float) x0, (float) y0, (float) z0, (float) x1, (float) y0, (float) z0, (float) x1, (float) y0, (float) z1, (float) x0, (float) y0, (float) z1,
            (float) x0, (float) y1, (float) z0, (float) x0, (float) y1, (float) z1, (float) x1, (float) y1, (float) z1, (float) x1, (float) y1, (float) z0,
            (float) x0, (float) y0, (float) z0, (float) x0, (float) y1, (float) z0, (float) x1, (float) y1, (float) z0, (float) x1, (float) y0, (float) z0,
            (float) x0, (float) y0, (float) z1, (float) x1, (float) y0, (float) z1, (float) x1, (float) y1, (float) z1, (float) x0, (float) y1, (float) z1,
            (float) x0, (float) y0, (float) z0, (float) x0, (float) y0, (float) z1, (float) x0, (float) y1, (float) z1, (float) x0, (float) y1, (float) z0,
            (float) x1, (float) y0, (float) z0, (float) x1, (float) y1, (float) z0, (float) x1, (float) y1, (float) z1, (float) x1, (float) y0, (float) z1
        };
    }

    public static float[] buildBoxEdgeVertices(double x0, double y0, double z0,
                                               double x1, double y1, double z1) {
        return new float[]{
            (float) x0, (float) y0, (float) z0, (float) x1, (float) y0, (float) z0,
            (float) x1, (float) y0, (float) z0, (float) x1, (float) y0, (float) z1,
            (float) x1, (float) y0, (float) z1, (float) x0, (float) y0, (float) z1,
            (float) x0, (float) y0, (float) z1, (float) x0, (float) y0, (float) z0,
            (float) x0, (float) y1, (float) z0, (float) x1, (float) y1, (float) z0,
            (float) x1, (float) y1, (float) z0, (float) x1, (float) y1, (float) z1,
            (float) x1, (float) y1, (float) z1, (float) x0, (float) y1, (float) z1,
            (float) x0, (float) y1, (float) z1, (float) x0, (float) y1, (float) z0,
            (float) x0, (float) y0, (float) z0, (float) x0, (float) y1, (float) z0,
            (float) x1, (float) y0, (float) z0, (float) x1, (float) y1, (float) z0,
            (float) x1, (float) y0, (float) z1, (float) x1, (float) y1, (float) z1,
            (float) x0, (float) y0, (float) z1, (float) x0, (float) y1, (float) z1
        };
    }

    public static float[] buildCylinderQuadVertices(double fromX, double fromY, double fromZ,
                                                    double toX, double toY, double toZ,
                                                    float radius) {
        double dx = toX - fromX;
        double dy = toY - fromY;
        double dz = toZ - fromZ;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 1.0E-6D) {
            return new float[0];
        }

        double ax = dx / len;
        double ay = dy / len;
        double az = dz / len;

        double bx;
        double by;
        double bz;
        if (Math.abs(ax) <= Math.abs(ay) && Math.abs(ax) <= Math.abs(az)) {
            bx = 0.0D;
            by = -az;
            bz = ay;
        } else if (Math.abs(ay) <= Math.abs(az)) {
            bx = -az;
            by = 0.0D;
            bz = ax;
        } else {
            bx = -ay;
            by = ax;
            bz = 0.0D;
        }

        double bLen = Math.sqrt(bx * bx + by * by + bz * bz);
        bx /= bLen;
        by /= bLen;
        bz /= bLen;

        double cx = ay * bz - az * by;
        double cy = az * bx - ax * bz;
        double cz = ax * by - ay * bx;

        float[] vertices = new float[CYLINDER_SIDES * 12];
        int cursor = 0;
        for (int i = 0; i < CYLINDER_SIDES; i++) {
            double angle = CYLINDER_ANGLE_STEP * i;
            double nextAngle = CYLINDER_ANGLE_STEP * (i + 1);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double nextCos = Math.cos(nextAngle);
            double nextSin = Math.sin(nextAngle);

            double nx = radius * (cos * bx + sin * cx);
            double ny = radius * (cos * by + sin * cy);
            double nz = radius * (cos * bz + sin * cz);
            double nextNx = radius * (nextCos * bx + nextSin * cx);
            double nextNy = radius * (nextCos * by + nextSin * cy);
            double nextNz = radius * (nextCos * bz + nextSin * cz);

            cursor = put(vertices, cursor, fromX + nx, fromY + ny, fromZ + nz);
            cursor = put(vertices, cursor, toX + nx, toY + ny, toZ + nz);
            cursor = put(vertices, cursor, toX + nextNx, toY + nextNy, toZ + nextNz);
            cursor = put(vertices, cursor, fromX + nextNx, fromY + nextNy, fromZ + nextNz);
        }
        return vertices;
    }

    public static float[] buildUnitSphereVertices(int slices, int stacks) {
        float[] vertices = new float[slices * stacks * 18];
        int cursor = 0;
        for (int i = 0; i < slices; i++) {
            double phi1 = Math.PI * i / slices;
            double phi2 = Math.PI * (i + 1) / slices;
            for (int j = 0; j < stacks; j++) {
                double theta1 = 2.0D * Math.PI * j / stacks;
                double theta2 = 2.0D * Math.PI * (j + 1) / stacks;

                float x00 = (float) (Math.sin(phi1) * Math.cos(theta1));
                float y00 = (float) Math.cos(phi1);
                float z00 = (float) (Math.sin(phi1) * Math.sin(theta1));
                float x10 = (float) (Math.sin(phi2) * Math.cos(theta1));
                float y10 = (float) Math.cos(phi2);
                float z10 = (float) (Math.sin(phi2) * Math.sin(theta1));
                float x01 = (float) (Math.sin(phi1) * Math.cos(theta2));
                float y01 = (float) Math.cos(phi1);
                float z01 = (float) (Math.sin(phi1) * Math.sin(theta2));
                float x11 = (float) (Math.sin(phi2) * Math.cos(theta2));
                float y11 = (float) Math.cos(phi2);
                float z11 = (float) (Math.sin(phi2) * Math.sin(theta2));

                cursor = put(vertices, cursor, x00, y00, z00);
                cursor = put(vertices, cursor, x10, y10, z10);
                cursor = put(vertices, cursor, x01, y01, z01);
                cursor = put(vertices, cursor, x10, y10, z10);
                cursor = put(vertices, cursor, x11, y11, z11);
                cursor = put(vertices, cursor, x01, y01, z01);
            }
        }
        return vertices;
    }

    public static float[] buildUnitSphereQuadVertices(int slices, int stacks) {
        float[] vertices = new float[slices * stacks * 12];
        int cursor = 0;
        for (int i = 0; i < slices; i++) {
            double phi1 = Math.PI * i / slices;
            double phi2 = Math.PI * (i + 1) / slices;
            for (int j = 0; j < stacks; j++) {
                double theta1 = 2.0D * Math.PI * j / stacks;
                double theta2 = 2.0D * Math.PI * (j + 1) / stacks;

                float x00 = (float) (Math.sin(phi1) * Math.cos(theta1));
                float y00 = (float) Math.cos(phi1);
                float z00 = (float) (Math.sin(phi1) * Math.sin(theta1));
                float x10 = (float) (Math.sin(phi2) * Math.cos(theta1));
                float y10 = (float) Math.cos(phi2);
                float z10 = (float) (Math.sin(phi2) * Math.sin(theta1));
                float x11 = (float) (Math.sin(phi2) * Math.cos(theta2));
                float y11 = (float) Math.cos(phi2);
                float z11 = (float) (Math.sin(phi2) * Math.sin(theta2));
                float x01 = (float) (Math.sin(phi1) * Math.cos(theta2));
                float y01 = (float) Math.cos(phi1);
                float z01 = (float) (Math.sin(phi1) * Math.sin(theta2));

                cursor = put(vertices, cursor, x00, y00, z00);
                cursor = put(vertices, cursor, x10, y10, z10);
                cursor = put(vertices, cursor, x11, y11, z11);
                cursor = put(vertices, cursor, x01, y01, z01);
            }
        }
        return vertices;
    }

    public static double getPlayerRenderX(float partialTicks) {
        LocalPlayer player = Minecraft.getInstance().player;
        return player.xOld + (player.getX() - player.xOld) * partialTicks;
    }

    public static double getPlayerRenderY(float partialTicks) {
        LocalPlayer player = Minecraft.getInstance().player;
        return player.yOld + (player.getY() - player.yOld) * partialTicks;
    }

    public static double getPlayerRenderZ(float partialTicks) {
        LocalPlayer player = Minecraft.getInstance().player;
        return player.zOld + (player.getZ() - player.zOld) * partialTicks;
    }

    public static boolean isWithinRenderDistance(double posX, double posY, double posZ,
                                                 double playerX, double playerY, double playerZ,
                                                 double maxDistSq) {
        double dx = posX - playerX;
        double dy = posY - playerY;
        double dz = posZ - playerZ;
        return dx * dx + dy * dy + dz * dz <= maxDistSq;
    }

    private static int put(float[] target, int cursor, double x, double y, double z) {
        target[cursor] = (float) x;
        target[cursor + 1] = (float) y;
        target[cursor + 2] = (float) z;
        return cursor + 3;
    }
}
