package com.circulation.circulation_networks.utils;

import net.minecraft.client.Minecraft;
//? if <1.20 {
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
//?} else {
/*import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.player.LocalPlayer;
*///?}
//? if <1.20 {
//?} else if <1.21 {
/*import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
*///?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
*///?}
import org.lwjgl.opengl.GL11;

@SuppressWarnings("unused")
//? if <1.20 {
@SideOnly(Side.CLIENT)
//?} else {
/*@OnlyIn(Dist.CLIENT)
*///?}
public final class RenderingUtils {

    private static final int CYLINDER_SIDES = 8;
    private static final double CYLINDER_ANGLE_STEP = 2.0 * Math.PI / CYLINDER_SIDES;

    private RenderingUtils() {
    }

    public static void setupWorldRenderState() {
        //? if <1.20 {
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        //?} else {
        /*RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        *///?}
    }

    public static void restoreWorldRenderState() {
        //? if <1.20 {
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        //?} else {
        /*RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        *///?}
    }

    public static void setupAdditiveBlend() {
        //? if <1.20 {
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        //?} else {
        /*RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        *///?}
    }

    public static void drawFilledBox(double x0, double y0, double z0,
                                     double x1, double y1, double z1,
                                     float r, float g, float b, float a) {
        //? if <1.20 {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        GlStateManager.color(r, g, b, a);
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        //?} else if <1.21 {
        /*Tesselator tess = Tesselator.getInstance();
        BufferBuilder buf = tess.getBuilder();

        RenderSystem.setShaderColor(r, g, b, a);
        buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        *///?} else {
        /*Tesselator tess = Tesselator.getInstance();

        RenderSystem.setShaderColor(r, g, b, a);
        BufferBuilder buf = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        *///?}

        //? if <1.20 {
        buf.pos(x0, y0, z0).endVertex();
        buf.pos(x1, y0, z0).endVertex();
        buf.pos(x1, y0, z1).endVertex();
        buf.pos(x0, y0, z1).endVertex();
        buf.pos(x0, y1, z0).endVertex();
        buf.pos(x0, y1, z1).endVertex();
        buf.pos(x1, y1, z1).endVertex();
        buf.pos(x1, y1, z0).endVertex();
        buf.pos(x0, y0, z0).endVertex();
        buf.pos(x0, y1, z0).endVertex();
        buf.pos(x1, y1, z0).endVertex();
        buf.pos(x1, y0, z0).endVertex();
        buf.pos(x0, y0, z1).endVertex();
        buf.pos(x1, y0, z1).endVertex();
        buf.pos(x1, y1, z1).endVertex();
        buf.pos(x0, y1, z1).endVertex();
        buf.pos(x0, y0, z0).endVertex();
        buf.pos(x0, y0, z1).endVertex();
        buf.pos(x0, y1, z1).endVertex();
        buf.pos(x0, y1, z0).endVertex();
        buf.pos(x1, y0, z0).endVertex();
        buf.pos(x1, y1, z0).endVertex();
        buf.pos(x1, y1, z1).endVertex();
        buf.pos(x1, y0, z1).endVertex();
        tess.draw();
        //?} else if <1.21 {
        /*buf.vertex(x0, y0, z0).endVertex();
        buf.vertex(x1, y0, z0).endVertex();
        buf.vertex(x1, y0, z1).endVertex();
        buf.vertex(x0, y0, z1).endVertex();
        buf.vertex(x0, y1, z0).endVertex();
        buf.vertex(x0, y1, z1).endVertex();
        buf.vertex(x1, y1, z1).endVertex();
        buf.vertex(x1, y1, z0).endVertex();
        buf.vertex(x0, y0, z0).endVertex();
        buf.vertex(x0, y1, z0).endVertex();
        buf.vertex(x1, y1, z0).endVertex();
        buf.vertex(x1, y0, z0).endVertex();
        buf.vertex(x0, y0, z1).endVertex();
        buf.vertex(x1, y0, z1).endVertex();
        buf.vertex(x1, y1, z1).endVertex();
        buf.vertex(x0, y1, z1).endVertex();
        buf.vertex(x0, y0, z0).endVertex();
        buf.vertex(x0, y0, z1).endVertex();
        buf.vertex(x0, y1, z1).endVertex();
        buf.vertex(x0, y1, z0).endVertex();
        buf.vertex(x1, y0, z0).endVertex();
        buf.vertex(x1, y1, z0).endVertex();
        buf.vertex(x1, y1, z1).endVertex();
        buf.vertex(x1, y0, z1).endVertex();
        tess.end();
        *///?} else {
        /*buf.addVertex((float) x0, (float) y0, (float) z0);
        buf.addVertex((float) x1, (float) y0, (float) z0);
        buf.addVertex((float) x1, (float) y0, (float) z1);
        buf.addVertex((float) x0, (float) y0, (float) z1);
        buf.addVertex((float) x0, (float) y1, (float) z0);
        buf.addVertex((float) x0, (float) y1, (float) z1);
        buf.addVertex((float) x1, (float) y1, (float) z1);
        buf.addVertex((float) x1, (float) y1, (float) z0);
        buf.addVertex((float) x0, (float) y0, (float) z0);
        buf.addVertex((float) x0, (float) y1, (float) z0);
        buf.addVertex((float) x1, (float) y1, (float) z0);
        buf.addVertex((float) x1, (float) y0, (float) z0);
        buf.addVertex((float) x0, (float) y0, (float) z1);
        buf.addVertex((float) x1, (float) y0, (float) z1);
        buf.addVertex((float) x1, (float) y1, (float) z1);
        buf.addVertex((float) x0, (float) y1, (float) z1);
        buf.addVertex((float) x0, (float) y0, (float) z0);
        buf.addVertex((float) x0, (float) y0, (float) z1);
        buf.addVertex((float) x0, (float) y1, (float) z1);
        buf.addVertex((float) x0, (float) y1, (float) z0);
        buf.addVertex((float) x1, (float) y0, (float) z0);
        buf.addVertex((float) x1, (float) y1, (float) z0);
        buf.addVertex((float) x1, (float) y1, (float) z1);
        buf.addVertex((float) x1, (float) y0, (float) z1);
        BufferUploader.drawWithShader(buf.buildOrThrow());
        *///?}
    }

    public static void drawBoxEdges(double x0, double y0, double z0,
                                    double x1, double y1, double z1,
                                    float r, float g, float b, float a,
                                    float lineWidth) {
        //? if <1.20 {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        GlStateManager.color(r, g, b, a);
        GlStateManager.glLineWidth(lineWidth);
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        //?} else if <1.21 {
        /*Tesselator tess = Tesselator.getInstance();
        BufferBuilder buf = tess.getBuilder();

        RenderSystem.setShaderColor(r, g, b, a);
        RenderSystem.lineWidth(lineWidth);
        buf.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION);
        *///?} else {
        /*Tesselator tess = Tesselator.getInstance();

        RenderSystem.setShaderColor(r, g, b, a);
        RenderSystem.lineWidth(lineWidth);
        BufferBuilder buf = tess.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION);
        *///?}

        //? if <1.20 {
        buf.pos(x0, y0, z0).endVertex();
        buf.pos(x1, y0, z0).endVertex();
        buf.pos(x1, y0, z0).endVertex();
        buf.pos(x1, y0, z1).endVertex();
        buf.pos(x1, y0, z1).endVertex();
        buf.pos(x0, y0, z1).endVertex();
        buf.pos(x0, y0, z1).endVertex();
        buf.pos(x0, y0, z0).endVertex();
        buf.pos(x0, y1, z0).endVertex();
        buf.pos(x1, y1, z0).endVertex();
        buf.pos(x1, y1, z0).endVertex();
        buf.pos(x1, y1, z1).endVertex();
        buf.pos(x1, y1, z1).endVertex();
        buf.pos(x0, y1, z1).endVertex();
        buf.pos(x0, y1, z1).endVertex();
        buf.pos(x0, y1, z0).endVertex();
        buf.pos(x0, y0, z0).endVertex();
        buf.pos(x0, y1, z0).endVertex();
        buf.pos(x1, y0, z0).endVertex();
        buf.pos(x1, y1, z0).endVertex();
        buf.pos(x1, y0, z1).endVertex();
        buf.pos(x1, y1, z1).endVertex();
        buf.pos(x0, y0, z1).endVertex();
        buf.pos(x0, y1, z1).endVertex();
        tess.draw();
        //?} else if <1.21 {
        /*buf.vertex(x0, y0, z0).endVertex();
        buf.vertex(x1, y0, z0).endVertex();
        buf.vertex(x1, y0, z0).endVertex();
        buf.vertex(x1, y0, z1).endVertex();
        buf.vertex(x1, y0, z1).endVertex();
        buf.vertex(x0, y0, z1).endVertex();
        buf.vertex(x0, y0, z1).endVertex();
        buf.vertex(x0, y0, z0).endVertex();
        buf.vertex(x0, y1, z0).endVertex();
        buf.vertex(x1, y1, z0).endVertex();
        buf.vertex(x1, y1, z0).endVertex();
        buf.vertex(x1, y1, z1).endVertex();
        buf.vertex(x1, y1, z1).endVertex();
        buf.vertex(x0, y1, z1).endVertex();
        buf.vertex(x0, y1, z1).endVertex();
        buf.vertex(x0, y1, z0).endVertex();
        buf.vertex(x0, y0, z0).endVertex();
        buf.vertex(x0, y1, z0).endVertex();
        buf.vertex(x1, y0, z0).endVertex();
        buf.vertex(x1, y1, z0).endVertex();
        buf.vertex(x1, y0, z1).endVertex();
        buf.vertex(x1, y1, z1).endVertex();
        buf.vertex(x0, y0, z1).endVertex();
        buf.vertex(x0, y1, z1).endVertex();
        tess.end();
        *///?} else {
        /*buf.addVertex((float) x0, (float) y0, (float) z0);
        buf.addVertex((float) x1, (float) y0, (float) z0);
        buf.addVertex((float) x1, (float) y0, (float) z0);
        buf.addVertex((float) x1, (float) y0, (float) z1);
        buf.addVertex((float) x1, (float) y0, (float) z1);
        buf.addVertex((float) x0, (float) y0, (float) z1);
        buf.addVertex((float) x0, (float) y0, (float) z1);
        buf.addVertex((float) x0, (float) y0, (float) z0);
        buf.addVertex((float) x0, (float) y1, (float) z0);
        buf.addVertex((float) x1, (float) y1, (float) z0);
        buf.addVertex((float) x1, (float) y1, (float) z0);
        buf.addVertex((float) x1, (float) y1, (float) z1);
        buf.addVertex((float) x1, (float) y1, (float) z1);
        buf.addVertex((float) x0, (float) y1, (float) z1);
        buf.addVertex((float) x0, (float) y1, (float) z1);
        buf.addVertex((float) x0, (float) y1, (float) z0);
        buf.addVertex((float) x0, (float) y0, (float) z0);
        buf.addVertex((float) x0, (float) y1, (float) z0);
        buf.addVertex((float) x1, (float) y0, (float) z0);
        buf.addVertex((float) x1, (float) y1, (float) z0);
        buf.addVertex((float) x1, (float) y0, (float) z1);
        buf.addVertex((float) x1, (float) y1, (float) z1);
        buf.addVertex((float) x0, (float) y0, (float) z1);
        buf.addVertex((float) x0, (float) y1, (float) z1);
        BufferUploader.drawWithShader(buf.buildOrThrow());
        *///?}
    }

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

        //? if <1.20 {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        GlStateManager.color(r, g, b, alpha);
        buf.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION);
        //?} else if <1.21 {
        /*Tesselator tess = Tesselator.getInstance();
        BufferBuilder buf = tess.getBuilder();

        RenderSystem.setShaderColor(r, g, b, alpha);
        buf.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION);
        *///?} else {
        /*Tesselator tess = Tesselator.getInstance();

        RenderSystem.setShaderColor(r, g, b, alpha);
        BufferBuilder buf = tess.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION);
        *///?}
        for (int i = 0; i <= CYLINDER_SIDES; i++) {
            double angle = CYLINDER_ANGLE_STEP * i;
            double cos = Math.cos(angle), sin = Math.sin(angle);
            double nx = radius * (cos * bx + sin * cx);
            double ny = radius * (cos * by + sin * cy);
            double nz = radius * (cos * bz + sin * cz);
            //? if <1.20 {
            buf.pos(fromX + nx, fromY + ny, fromZ + nz).endVertex();
            buf.pos(toX + nx, toY + ny, toZ + nz).endVertex();
            //?} else if <1.21 {
            /*buf.vertex(fromX + nx, fromY + ny, fromZ + nz).endVertex();
            buf.vertex(toX + nx, toY + ny, toZ + nz).endVertex();
            *///?} else {
            /*buf.addVertex((float)(fromX + nx), (float)(fromY + ny), (float)(fromZ + nz));
            buf.addVertex((float)(toX + nx), (float)(toY + ny), (float)(toZ + nz));
            *///?}
        }
        //? if <1.20 {
        tess.draw();
        //?} else if <1.21 {
        /*tess.end();
        *///?} else {
        /*BufferUploader.drawWithShader(buf.buildOrThrow());
        *///?}
    }

    public static void drawSphere(float r, float g, float b, float radius, float alpha) {
        drawSphere(r, g, b, radius, alpha, 32, 32);
    }

    public static void drawSphere(float r, float g, float b, float radius, float alpha, int slices, int stacks) {
        //? if <1.20 {
        GlStateManager.color(r, g, b, alpha);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        //?} else if <1.21 {
        /*RenderSystem.setShaderColor(r, g, b, alpha);
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        int ri = (int)(r * 255), gi = (int)(g * 255), bi = (int)(b * 255), ai = (int)(alpha * 255);
        *///?} else {
        /*RenderSystem.setShaderColor(r, g, b, alpha);
        Tesselator tess = Tesselator.getInstance();
        int ri = (int)(r * 255), gi = (int)(g * 255), bi = (int)(b * 255), ai = (int)(alpha * 255);
        *///?}

        double phiStep = Math.PI / slices;
        double thetaStep = 2.0 * Math.PI / stacks;

        for (int i = 0; i < slices; i++) {
            double phi1 = phiStep * i;
            double phi2 = phiStep * (i + 1);
            double sinPhi1 = Math.sin(phi1), cosPhi1 = Math.cos(phi1);
            double sinPhi2 = Math.sin(phi2), cosPhi2 = Math.cos(phi2);
            //? if <1.20 {
            buf.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_NORMAL);
            //?} else if <1.21 {
            /*buf.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR_NORMAL);
            *///?} else {
            /*BufferBuilder buf = tess.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR_NORMAL);
            *///?}
            for (int j = 0; j <= stacks; j++) {
                double theta = thetaStep * j;
                double cosTheta = Math.cos(theta), sinTheta = Math.sin(theta);
                float x1 = (float) (radius * sinPhi1 * cosTheta);
                float y1 = (float) (radius * cosPhi1);
                float z1 = (float) (radius * sinPhi1 * sinTheta);
                //? if <1.20 {
                buf.pos(x1, y1, z1).normal(x1 / radius, y1 / radius, z1 / radius).endVertex();
                //?} else if <1.21 {
                /*buf.vertex(x1, y1, z1).color(ri, gi, bi, ai).normal(x1 / radius, y1 / radius, z1 / radius).endVertex();
                *///?} else {
                /*buf.addVertex(x1, y1, z1).setColor(ri, gi, bi, ai).setNormal(x1 / radius, y1 / radius, z1 / radius);
                *///?}
                float x2 = (float) (radius * sinPhi2 * cosTheta);
                float y2 = (float) (radius * cosPhi2);
                float z2 = (float) (radius * sinPhi2 * sinTheta);
                //? if <1.20 {
                buf.pos(x2, y2, z2).normal(x2 / radius, y2 / radius, z2 / radius).endVertex();
                //?} else if <1.21 {
                /*buf.vertex(x2, y2, z2).color(ri, gi, bi, ai).normal(x2 / radius, y2 / radius, z2 / radius).endVertex();
                *///?} else {
                /*buf.addVertex(x2, y2, z2).setColor(ri, gi, bi, ai).setNormal(x2 / radius, y2 / radius, z2 / radius);
                *///?}
            }
            //? if <1.20 {
            tess.draw();
            //?} else if <1.21 {
            /*tess.end();
            *///?} else {
            /*BufferUploader.drawWithShader(buf.buildOrThrow());
            *///?}
        }
    }

    public static double getPlayerRenderX(float partialTicks) {
        //? if <1.20 {
        EntityPlayerSP p = Minecraft.getMinecraft().player;
        return p.lastTickPosX + (p.posX - p.lastTickPosX) * partialTicks;
        //?} else {
        /*LocalPlayer p = Minecraft.getInstance().player;
        return p.xOld + (p.getX() - p.xOld) * partialTicks;
        *///?}
    }

    public static double getPlayerRenderY(float partialTicks) {
        //? if <1.20 {
        EntityPlayerSP p = Minecraft.getMinecraft().player;
        return p.lastTickPosY + (p.posY - p.lastTickPosY) * partialTicks;
        //?} else {
        /*LocalPlayer p = Minecraft.getInstance().player;
        return p.yOld + (p.getY() - p.yOld) * partialTicks;
        *///?}
    }

    public static double getPlayerRenderZ(float partialTicks) {
        //? if <1.20 {
        EntityPlayerSP p = Minecraft.getMinecraft().player;
        return p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * partialTicks;
        //?} else {
        /*LocalPlayer p = Minecraft.getInstance().player;
        return p.zOld + (p.getZ() - p.zOld) * partialTicks;
        *///?}
    }

    public static boolean isWithinRenderDistance(double posX, double posY, double posZ,
                                                 double playerX, double playerY, double playerZ,
                                                 double maxDistSq) {
        double dx = posX - playerX;
        double dy = posY - playerY;
        double dz = posZ - playerZ;
        return dx * dx + dy * dy + dz * dz <= maxDistSq;
    }

    public static void drawCachedIntersection(float[] verts, float r, float g, float b, float lineWidth) {
        if (verts.length == 0) return;
        //? if <1.20 {
        GlStateManager.color(r, g, b, 1.0f);
        GlStateManager.glLineWidth(lineWidth);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        for (int i = 0; i < verts.length; i += 3) {
            buf.pos(verts[i], verts[i + 1], verts[i + 2]).endVertex();
        }
        tess.draw();
        //?} else if <1.21 {
        /*RenderSystem.setShaderColor(r, g, b, 1.0f);
        RenderSystem.lineWidth(lineWidth);
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION);
        for (int i = 0; i < verts.length; i += 3) {
            buf.vertex(verts[i], verts[i + 1], verts[i + 2]).endVertex();
        }
        tess.end();
        *///?} else {
        /*RenderSystem.setShaderColor(r, g, b, 1.0f);
        RenderSystem.lineWidth(lineWidth);
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION);
        for (int i = 0; i < verts.length; i += 3) {
            buf.addVertex(verts[i], verts[i + 1], verts[i + 2]);
        }
        BufferUploader.drawWithShader(buf.buildOrThrow());
        *///?}
    }

    public static void drawCachedIntersection(float[] verts, float r, float g, float b) {
        drawCachedIntersection(verts, r, g, b, 4.0f);
    }
}
