package com.circulation.circulation_networks.utils;

import com.circulation.circulation_networks.client.compat.RenderSystemCompat;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import java.util.Optional;

public final class RenderingBackendImpl extends RenderingBackend {

    private static final RenderPipeline OVERLAY_DEBUG_QUADS_PIPELINE = RenderPipelines.DEBUG_QUADS.toBuilder()
                                                                                                  .withLocation("pipeline/cfn_debug_quads_overlay")
                                                                                                  .withDepthStencilState(Optional.empty())
                                                                                                  .build();
    private static final RenderPipeline INTERSECTION_LINES_PIPELINE = RenderPipelines.LINES_DEPTH_BIAS.toBuilder()
                                                                                                      .withLocation("pipeline/cfn_intersection_lines")
                                                                                                      .build();
    private static final RenderType OVERLAY_DEBUG_QUADS = RenderType.create(
        "cfn_overlay_debug_quads",
        RenderSetup.builder(OVERLAY_DEBUG_QUADS_PIPELINE)
                   .sortOnUpload()
                   .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                   .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                   .createRenderSetup()
    );
    private static final RenderType INTERSECTION_LINES = RenderType.create(
        "cfn_intersection_lines",
        RenderSetup.builder(INTERSECTION_LINES_PIPELINE).createRenderSetup()
    );
    private float[] cachedSphereQuadVertices = new float[0];
    private int cachedSphereSlices = -1;
    private int cachedSphereStacks = -1;

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
        RenderSystemCompat.disableBlend();
    }

    @Override
    public void setupAdditiveBlend() {
        RenderSystemCompat.additiveBlendFunc();
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
        drawLaserCylinderWithRenderType(RenderTypes.debugQuads(), fromX, fromY, fromZ, toX, toY, toZ, radius, r, g, b, alpha);
    }

    @Override
    public void drawOverlayLaserCylinder(double fromX, double fromY, double fromZ,
                                         double toX, double toY, double toZ,
                                         float radius,
                                         float r, float g, float b, float alpha) {
        drawLaserCylinderWithRenderType(OVERLAY_DEBUG_QUADS, fromX, fromY, fromZ, toX, toY, toZ, radius, r, g, b, alpha);
    }

    @Override
    public void drawSphere(float r, float g, float b, float radius, float alpha, int slices, int stacks) {
        drawSphereWithRenderType(RenderTypes.debugQuads(), r, g, b, radius, alpha, slices, stacks);
    }

    @Override
    public void drawOverlaySphere(float r, float g, float b, float radius, float alpha, int slices, int stacks) {
        drawSphereWithRenderType(OVERLAY_DEBUG_QUADS, r, g, b, radius, alpha, slices, stacks);
    }

    private void drawLaserCylinderWithRenderType(RenderType renderType,
                                                 double fromX, double fromY, double fromZ,
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
        renderType.draw(buffer.buildOrThrow());
    }

    private void drawSphereWithRenderType(RenderType renderType,
                                          float r, float g, float b, float radius, float alpha, int slices, int stacks) {
        ensureSphereGeometry(slices, stacks);
        if (cachedSphereQuadVertices.length == 0) {
            return;
        }
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        int ri = RenderingGeometryCore.toColorComponent(r);
        int gi = RenderingGeometryCore.toColorComponent(g);
        int bi = RenderingGeometryCore.toColorComponent(b);
        int ai = RenderingGeometryCore.toColorComponent(alpha);
        for (int i = 0; i < cachedSphereQuadVertices.length; i += 3) {
            addVertex(
                buffer,
                cachedSphereQuadVertices[i] * radius,
                cachedSphereQuadVertices[i + 1] * radius,
                cachedSphereQuadVertices[i + 2] * radius,
                ri,
                gi,
                bi,
                ai
            );
        }
        renderType.draw(buffer.buildOrThrow());
    }

    @Override
    public void drawCachedIntersection(float[] verts, float r, float g, float b, float lineWidth) {
        if (verts.length == 0) {
            return;
        }
        int ri = RenderingGeometryCore.toColorComponent(r);
        int gi = RenderingGeometryCore.toColorComponent(g);
        int bi = RenderingGeometryCore.toColorComponent(b);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH);
        for (int i = 0; i + 5 < verts.length; i += 6) {
            addLine(buffer, verts[i], verts[i + 1], verts[i + 2], verts[i + 3], verts[i + 4], verts[i + 5], ri, gi, bi, 255, lineWidth);
        }
        INTERSECTION_LINES.draw(buffer.buildOrThrow());
    }

    private void ensureSphereGeometry(int slices, int stacks) {
        if (cachedSphereSlices == slices && cachedSphereStacks == stacks) {
            return;
        }
        cachedSphereQuadVertices = RenderingGeometryCore.buildUnitSphereQuadVertices(slices, stacks);
        cachedSphereSlices = slices;
        cachedSphereStacks = stacks;
    }
}
