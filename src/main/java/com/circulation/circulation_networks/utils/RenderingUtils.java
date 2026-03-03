package com.circulation.circulation_networks.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public final class RenderingUtils {

    private static final int CYLINDER_SIDES = 8;

    private RenderingUtils() {
    }

    public static void setupWorldRenderState() {
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
    }

    /**
     * 恢复world render模式的GL状态
     */
    public static void restoreWorldRenderState() {
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
    }

    /**
     * 设置加法混合（用于发光效果）
     */
    public static void setupAdditiveBlend() {
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
    }

    /**
     * 绘制填充方块
     *
     * @param x0 最小X坐标
     * @param y0 最小Y坐标
     * @param z0 最小Z坐标
     * @param x1 最大X坐标
     * @param y1 最大Y坐标
     * @param z1 最大Z坐标
     * @param r  红色分量 (0-1)
     * @param g  绿色分量 (0-1)
     * @param b  蓝色分量 (0-1)
     * @param a  透明度 (0-1)
     */
    public static void drawFilledBox(double x0, double y0, double z0,
                                     double x1, double y1, double z1,
                                     float r, float g, float b, float a) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        GlStateManager.color(r, g, b, a);
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        // Bottom face (Y-)
        buf.pos(x0, y0, z0).endVertex();
        buf.pos(x1, y0, z0).endVertex();
        buf.pos(x1, y0, z1).endVertex();
        buf.pos(x0, y0, z1).endVertex();

        // Top face (Y+)
        buf.pos(x0, y1, z0).endVertex();
        buf.pos(x0, y1, z1).endVertex();
        buf.pos(x1, y1, z1).endVertex();
        buf.pos(x1, y1, z0).endVertex();

        // North face (Z-)
        buf.pos(x0, y0, z0).endVertex();
        buf.pos(x0, y1, z0).endVertex();
        buf.pos(x1, y1, z0).endVertex();
        buf.pos(x1, y0, z0).endVertex();

        // South face (Z+)
        buf.pos(x0, y0, z1).endVertex();
        buf.pos(x1, y0, z1).endVertex();
        buf.pos(x1, y1, z1).endVertex();
        buf.pos(x0, y1, z1).endVertex();

        // West face (X-)
        buf.pos(x0, y0, z0).endVertex();
        buf.pos(x0, y0, z1).endVertex();
        buf.pos(x0, y1, z1).endVertex();
        buf.pos(x0, y1, z0).endVertex();

        // East face (X+)
        buf.pos(x1, y0, z0).endVertex();
        buf.pos(x1, y1, z0).endVertex();
        buf.pos(x1, y1, z1).endVertex();
        buf.pos(x1, y0, z1).endVertex();

        tess.draw();
    }

    /**
     * 绘制方块边框
     *
     * @param x0        最小X坐标
     * @param y0        最小Y坐标
     * @param z0        最小Z坐标
     * @param x1        最大X坐标
     * @param y1        最大Y坐标
     * @param z1        最大Z坐标
     * @param r         红色分量 (0-1)
     * @param g         绿色分量 (0-1)
     * @param b         蓝色分量 (0-1)
     * @param a         透明度 (0-1)
     * @param lineWidth 线宽
     */
    public static void drawBoxEdges(double x0, double y0, double z0,
                                    double x1, double y1, double z1,
                                    float r, float g, float b, float a,
                                    float lineWidth) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        GlStateManager.color(r, g, b, a);
        GlStateManager.glLineWidth(lineWidth);
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        // Bottom face edges
        buf.pos(x0, y0, z0).endVertex();
        buf.pos(x1, y0, z0).endVertex();
        buf.pos(x1, y0, z0).endVertex();
        buf.pos(x1, y0, z1).endVertex();
        buf.pos(x1, y0, z1).endVertex();
        buf.pos(x0, y0, z1).endVertex();
        buf.pos(x0, y0, z1).endVertex();
        buf.pos(x0, y0, z0).endVertex();

        // Top face edges
        buf.pos(x0, y1, z0).endVertex();
        buf.pos(x1, y1, z0).endVertex();
        buf.pos(x1, y1, z0).endVertex();
        buf.pos(x1, y1, z1).endVertex();
        buf.pos(x1, y1, z1).endVertex();
        buf.pos(x0, y1, z1).endVertex();
        buf.pos(x0, y1, z1).endVertex();
        buf.pos(x0, y1, z0).endVertex();

        // Vertical edges
        buf.pos(x0, y0, z0).endVertex();
        buf.pos(x0, y1, z0).endVertex();
        buf.pos(x1, y0, z0).endVertex();
        buf.pos(x1, y1, z0).endVertex();
        buf.pos(x1, y0, z1).endVertex();
        buf.pos(x1, y1, z1).endVertex();
        buf.pos(x0, y0, z1).endVertex();
        buf.pos(x0, y1, z1).endVertex();

        tess.draw();
    }

    /**
     * 绘制激光圆柱体（沿两点连接）
     *
     * @param fromX  起点X坐标
     * @param fromY  起点Y坐标
     * @param fromZ  起点Z坐标
     * @param toX    终点X坐标
     * @param toY    终点Y坐标
     * @param toZ    终点Z坐标
     * @param radius 半径
     * @param r      红色分量 (0-1)
     * @param g      绿色分量 (0-1)
     * @param b      蓝色分量 (0-1)
     * @param alpha  透明度 (0-1)
     */
    public static void drawLaserCylinder(double fromX, double fromY, double fromZ,
                                         double toX, double toY, double toZ,
                                         float radius,
                                         float r, float g, float b, float alpha) {
        double dx = toX - fromX;
        double dy = toY - fromY;
        double dz = toZ - fromZ;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 1e-6) return;

        double ax = dx / len, ay = dy / len, az = dz / len;

        double bx, by, bz;
        if (Math.abs(ax) <= Math.abs(ay) && Math.abs(ax) <= Math.abs(az)) {
            bx = 0;
            by = -az;
            bz = ay;
        } else if (Math.abs(ay) <= Math.abs(az)) {
            bx = -az;
            by = 0;
            bz = ax;
        } else {
            bx = -ay;
            by = ax;
            bz = 0;
        }
        double bLen = Math.sqrt(bx * bx + by * by + bz * bz);
        bx /= bLen;
        by /= bLen;
        bz /= bLen;

        double cx = ay * bz - az * by;
        double cy = az * bx - ax * bz;
        double cz = ax * by - ay * bx;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        GlStateManager.color(r, g, b, alpha);
        buf.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION);
        for (int i = 0; i <= CYLINDER_SIDES; i++) {
            double angle = 2.0 * Math.PI * i / CYLINDER_SIDES;
            double cos = Math.cos(angle), sin = Math.sin(angle);
            double nx = radius * (cos * bx + sin * cx);
            double ny = radius * (cos * by + sin * cy);
            double nz = radius * (cos * bz + sin * cz);
            buf.pos(fromX + nx, fromY + ny, fromZ + nz).endVertex();
            buf.pos(toX + nx, toY + ny, toZ + nz).endVertex();
        }
        tess.draw();
    }

    /**
     * 绘制球体
     *
     * @param r      红色分量 (0-1)
     * @param g      绿色分量 (0-1)
     * @param b      蓝色分量 (0-1)
     * @param radius 球体半径
     * @param alpha  透明度 (0-1)
     */
    public static void drawSphere(float r, float g, float b, float radius, float alpha) {
        drawSphere(r, g, b, radius, alpha, 32, 32);
    }

    /**
     * 绘制球体（自定义分辨率）
     *
     * @param r      红色分量
     * @param g      绿色分量
     * @param b      蓝色分量
     * @param radius 球体半径
     * @param alpha  透明度
     * @param slices 纬度分割数（默认24）
     * @param stacks 经度分割数（默认24）
     */
    public static void drawSphere(float r, float g, float b, float radius, float alpha, int slices, int stacks) {
        GlStateManager.color(r, g, b, alpha);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        for (int i = 0; i < slices; i++) {
            double phi1 = Math.PI * i / slices;
            double phi2 = Math.PI * (i + 1) / slices;
            buf.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_NORMAL);
            for (int j = 0; j <= stacks; j++) {
                double theta = 2.0 * Math.PI * j / stacks;
                float x1 = (float) (radius * Math.sin(phi1) * Math.cos(theta));
                float y1 = (float) (radius * Math.cos(phi1));
                float z1 = (float) (radius * Math.sin(phi1) * Math.sin(theta));
                buf.pos(x1, y1, z1).normal(x1 / radius, y1 / radius, z1 / radius).endVertex();
                float x2 = (float) (radius * Math.sin(phi2) * Math.cos(theta));
                float y2 = (float) (radius * Math.cos(phi2));
                float z2 = (float) (radius * Math.sin(phi2) * Math.sin(theta));
                buf.pos(x2, y2, z2).normal(x2 / radius, y2 / radius, z2 / radius).endVertex();
            }
            tess.draw();
        }
    }

    /**
     * 获取玩家插值后的X坐标（用于渲染偏移）
     */
    public static double getPlayerRenderX(float partialTicks) {
        EntityPlayerSP p = Minecraft.getMinecraft().player;
        return p.lastTickPosX + (p.posX - p.lastTickPosX) * partialTicks;
    }

    /**
     * 获取玩家插值后的Y坐标（用于渲染偏移）
     */
    public static double getPlayerRenderY(float partialTicks) {
        EntityPlayerSP p = Minecraft.getMinecraft().player;
        return p.lastTickPosY + (p.posY - p.lastTickPosY) * partialTicks;
    }

    /**
     * 获取玩家插值后的Z坐标（用于渲染偏移）
     */
    public static double getPlayerRenderZ(float partialTicks) {
        EntityPlayerSP p = Minecraft.getMinecraft().player;
        return p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * partialTicks;
    }

    /**
     * 检查位置是否在玩家的渲染距离内
     *
     * @param posX      目标位置X（注意：应为方块坐标+0.5或世界坐标）
     * @param posY      目标位置Y
     * @param posZ      目标位置Z
     * @param playerX   玩家渲染坐标X
     * @param playerY   玩家渲染坐标Y
     * @param playerZ   玩家渲染坐标Z
     * @param maxDistSq 最大距离的平方
     * @return 是否在范围内
     */
    public static boolean isWithinRenderDistance(double posX, double posY, double posZ,
                                                 double playerX, double playerY, double playerZ,
                                                 double maxDistSq) {
        double dx = posX - playerX;
        double dy = posY - playerY;
        double dz = posZ - playerZ;
        return dx * dx + dy * dy + dz * dz <= maxDistSq;
    }

    /**
     * 绘制缓存的顶点数组（用于射线或相交线）
     *
     * @param verts     顶点数组，每3个浮点数为一个顶点(x, y, z)
     * @param r         红色分量
     * @param g         绿色分量
     * @param b         蓝色分量
     * @param lineWidth 线宽
     */
    public static void drawCachedIntersection(float[] verts, float r, float g, float b, float lineWidth) {
        if (verts.length == 0) return;
        GlStateManager.color(r, g, b, 1.0f);
        GlStateManager.glLineWidth(lineWidth);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        for (int i = 0; i < verts.length; i += 3) {
            buf.pos(verts[i], verts[i + 1], verts[i + 2]).endVertex();
        }
        tess.draw();
    }

    /**
     * 绘制缓存的顶点数组（使用默认线宽4.0f）
     *
     * @param verts 顶点数组，每3个浮点数为一个顶点(x, y, z)
     * @param r     红色分量
     * @param g     绿色分量
     * @param b     蓝色分量
     */
    public static void drawCachedIntersection(float[] verts, float r, float g, float b) {
        drawCachedIntersection(verts, r, g, b, 4.0f);
    }
}
