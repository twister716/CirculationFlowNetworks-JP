package com.circulation.circulation_networks.utils;

import com.circulation.circulation_networks.client.compat.RenderSystemCompat;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public final class RenderingBackendImpl extends RenderingBackend {

    private final FloatBuffer sphereMvBuf = MemoryUtil.memAllocFloat(16);
    private final FloatBuffer sphereProjBuf = MemoryUtil.memAllocFloat(16);
    private int sphereVAO;
    private int sphereVertexCount;
    private boolean sphereVBOInitialized;
    private int cachedSphereProgId;
    private int locMV = -1;
    private int locProj = -1;
    private int locColor = -1;

    private static void addLine(BufferBuilder buffer,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                int r, int g, int b, int a,
                                float lineWidth) {
        float nx = x2 - x1;
        float ny = y2 - y1;
        float nz = z2 - z1;
        buffer.addVertex(x1, y1, z1).setColor(r, g, b, a).setNormal(nx, ny, nz).setLineWidth(lineWidth);
        buffer.addVertex(x2, y2, z2).setColor(r, g, b, a).setNormal(nx, ny, nz).setLineWidth(lineWidth);
    }

    private static void addVertex(BufferBuilder buffer, float x, float y, float z, int r, int g, int b, int a) {
        buffer.addVertex(x, y, z).setColor(r, g, b, a);
    }

    @Override
    public void setupWorldRenderState() {
        RenderSystemCompat.enableBlend();
        RenderSystemCompat.disableDepthTest();
        RenderSystemCompat.disableCull();
        RenderSystemCompat.depthMask(false);
    }

    @Override
    public void restoreWorldRenderState() {
        RenderSystemCompat.depthMask(true);
        RenderSystemCompat.enableCull();
        RenderSystemCompat.enableDepthTest();
        RenderSystemCompat.defaultBlendFunc();
        RenderSystemCompat.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystemCompat.disableBlend();
    }

    @Override
    public void setupAdditiveBlend() {
        RenderSystemCompat.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
    }

    @Override
    public void seedModelViewFromPoseStack(Object poseStack) {
        PoseStack actualPoseStack = (PoseStack) poseStack;
        if (poseStack == null) {
            return;
        }
        RenderSystem.getModelViewStack().set(actualPoseStack.last().pose());
        RenderSystemCompat.applyModelViewMatrix();
    }

    @Override
    public void drawFilledBox(double x0, double y0, double z0,
                              double x1, double y1, double z1,
                              float r, float g, float b, float a) {
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        int ri = RenderingGeometryCore.toColorComponent(r);
        int gi = RenderingGeometryCore.toColorComponent(g);
        int bi = RenderingGeometryCore.toColorComponent(b);
        int ai = RenderingGeometryCore.toColorComponent(a);
        float[] vertices = RenderingGeometryCore.buildFilledBoxVertices(x0, y0, z0, x1, y1, z1);
        for (int i = 0; i < vertices.length; i += 3) {
            addVertex(buffer, vertices[i], vertices[i + 1], vertices[i + 2], ri, gi, bi, ai);
        }
        RenderTypes.debugFilledBox().draw(buffer.buildOrThrow());
    }

    @Override
    public void drawBoxEdges(double x0, double y0, double z0,
                             double x1, double y1, double z1,
                             float r, float g, float b, float a,
                             float lineWidth) {
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH);
        int ri = RenderingGeometryCore.toColorComponent(r);
        int gi = RenderingGeometryCore.toColorComponent(g);
        int bi = RenderingGeometryCore.toColorComponent(b);
        int ai = RenderingGeometryCore.toColorComponent(a);
        float[] vertices = RenderingGeometryCore.buildBoxEdgeVertices(x0, y0, z0, x1, y1, z1);
        for (int i = 0; i < vertices.length; i += 6) {
            addLine(buffer, vertices[i], vertices[i + 1], vertices[i + 2], vertices[i + 3], vertices[i + 4], vertices[i + 5], ri, gi, bi, ai, lineWidth);
        }
        RenderTypes.linesTranslucent().draw(buffer.buildOrThrow());
    }

    @Override
    public void drawLaserCylinder(double fromX, double fromY, double fromZ,
                                  double toX, double toY, double toZ,
                                  float radius,
                                  float r, float g, float b, float alpha) {
        float[] vertices = RenderingGeometryCore.buildCylinderQuadVertices(fromX, fromY, fromZ, toX, toY, toZ, radius);
        if (vertices.length == 0) {
            return;
        }
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        int ri = RenderingGeometryCore.toColorComponent(r);
        int gi = RenderingGeometryCore.toColorComponent(g);
        int bi = RenderingGeometryCore.toColorComponent(b);
        int ai = RenderingGeometryCore.toColorComponent(alpha);
        for (int i = 0; i < vertices.length; i += 3) {
            addVertex(buffer, vertices[i], vertices[i + 1], vertices[i + 2], ri, gi, bi, ai);
        }
        RenderTypes.debugQuads().draw(buffer.buildOrThrow());
    }

    @Override
    public void drawSphere(float r, float g, float b, float radius, float alpha, int slices, int stacks) {
        drawSphereVBO(r, g, b, radius, alpha, slices, stacks);
    }

    @Override
    public void drawCachedIntersection(float[] verts, float r, float g, float b, float lineWidth) {
        if (verts.length == 0) {
            return;
        }
        GL11.glDepthRange(0.0D, 0.9998D);
        int ri = RenderingGeometryCore.toColorComponent(r);
        int gi = RenderingGeometryCore.toColorComponent(g);
        int bi = RenderingGeometryCore.toColorComponent(b);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH);
        for (int i = 0; i + 5 < verts.length; i += 6) {
            addLine(buffer, verts[i], verts[i + 1], verts[i + 2], verts[i + 3], verts[i + 4], verts[i + 5], ri, gi, bi, 255, lineWidth);
        }
        RenderTypes.lines().draw(buffer.buildOrThrow());
        GL11.glDepthRange(0.0D, 1.0D);
    }

    private void ensureSphereVBO(int slices, int stacks) {
        if (sphereVBOInitialized) {
            return;
        }
        float[] sphereVertices = RenderingGeometryCore.buildUnitSphereVertices(slices, stacks);
        FloatBuffer data = MemoryUtil.memAllocFloat(sphereVertices.length);
        data.put(sphereVertices).flip();
        try {
            sphereVertexCount = data.remaining() / 3;
            int sphereVBO = GlStateManager._glGenBuffers();
            sphereVAO = GL30.glGenVertexArrays();
            GlStateManager._glBindVertexArray(sphereVAO);
            GlStateManager._glBindBuffer(GL15.GL_ARRAY_BUFFER, sphereVBO);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
            GL20.glEnableVertexAttribArray(0);
            GlStateManager._glBindVertexArray(0);
            GlStateManager._glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        } finally {
            MemoryUtil.memFree(data);
        }
        sphereVBOInitialized = true;
    }

    private void drawSphereVBO(float r, float g, float b, float radius, float alpha, int slices, int stacks) {
        ensureSphereVBO(slices, stacks);
        int progId = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        if (progId <= 0) {
            return;
        }
        if (progId != cachedSphereProgId) {
            cachedSphereProgId = progId;
            locMV = GL20.glGetUniformLocation(progId, "ModelViewMat");
            locProj = GL20.glGetUniformLocation(progId, "ProjMat");
            locColor = GL20.glGetUniformLocation(progId, "ColorModulator");
        }

        Matrix4fStack mvStack = RenderSystem.getModelViewStack();
        mvStack.pushMatrix();
        mvStack.scale(radius, radius, radius);
        sphereMvBuf.clear();
        mvStack.get(sphereMvBuf);
        sphereMvBuf.rewind();

        sphereProjBuf.clear();
        RenderSystemCompat.getProjectionMatrix().get(sphereProjBuf);
        sphereProjBuf.rewind();

        GlStateManager._glUseProgram(progId);
        GL20.glUniformMatrix4fv(locMV, false, sphereMvBuf);
        GL20.glUniformMatrix4fv(locProj, false, sphereProjBuf);
        GL20.glUniform4f(locColor, r, g, b, alpha);

        GlStateManager._glBindVertexArray(sphereVAO);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, sphereVertexCount);
        GlStateManager._glBindVertexArray(0);
        GlStateManager._glUseProgram(0);

        mvStack.popMatrix();
    }
}
