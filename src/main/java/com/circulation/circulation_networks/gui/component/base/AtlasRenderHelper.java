package com.circulation.circulation_networks.gui.component.base;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public final class AtlasRenderHelper {

    private static final RenderType COLOR_QUAD_RENDER_TYPE = RenderType.create(
        "cfn_gui_color_quad",
        RenderSetup.builder(RenderPipelines.GUI).createRenderSetup()
    );
    private static int batchDepth;
    private static BufferBuilder batchBuffer;
    private static ComponentAtlas batchAtlas;

    private AtlasRenderHelper() {
    }

    public static void beginBatch(ComponentAtlas atlas) {
        if (batchDepth++ > 0) return;
        batchAtlas = atlas;
        batchBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
    }

    public static void endBatch() {
        if (batchDepth <= 0) return;
        if (--batchDepth > 0) return;
        flushCurrentBatch();
    }

    public static void flushBatch(ComponentAtlas atlas) {
        if (batchDepth <= 0) return;
        int saved = batchDepth;
        batchDepth = 1;
        endBatch();
        batchDepth = 0;
        beginBatch(atlas);
        batchDepth = saved;
    }

    public static boolean isBatching() {
        return batchDepth > 0;
    }

    public static void drawRegion(ComponentAtlas atlas, AtlasRegion region, int screenX, int screenY, int renderW, int renderH) {
        drawRegion(atlas, region, (float) screenX, (float) screenY, (float) renderW, (float) renderH, 255, 255, 255, 255);
    }

    public static void drawRegion(ComponentAtlas atlas, AtlasRegion region, float screenX, float screenY, float renderW, float renderH) {
        drawRegion(atlas, region, screenX, screenY, renderW, renderH, 255, 255, 255, 255);
    }

    public static void drawRegion(ComponentAtlas atlas, AtlasRegion region, float screenX, float screenY, float renderW, float renderH,
                                  int red, int green, int blue, int alpha) {
        if (batchDepth > 0 && alpha == 255 && red == 255 && green == 255 && blue == 255 && atlas == batchAtlas) {
            appendQuad(region.u0(), region.v0(), region.u1(), region.v1(), screenX, screenY, renderW, renderH, red, green, blue, alpha);
            return;
        }

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        appendQuad(buffer, region.u0(), region.v0(), region.u1(), region.v1(), screenX, screenY, renderW, renderH, red, green, blue, alpha);
        drawMesh(atlas.guiRenderType(), buffer.buildOrThrow());
    }

    public static void drawSubRegion(ComponentAtlas atlas,
                                     AtlasRegion region,
                                     int srcX,
                                     int srcY,
                                     int srcW,
                                     int srcH,
                                     int screenX,
                                     int screenY,
                                     int renderW,
                                     int renderH) {
        if (srcW <= 0 || srcH <= 0 || renderW <= 0 || renderH <= 0) {
            return;
        }
        if (srcX < 0 || srcY < 0 || srcX + srcW > region.width || srcY + srcH > region.height) {
            return;
        }

        float u0 = (float) (region.x + srcX) / region.atlasWidth;
        float v0 = (float) (region.y + srcY) / region.atlasHeight;
        float u1 = (float) (region.x + srcX + srcW) / region.atlasWidth;
        float v1 = (float) (region.y + srcY + srcH) / region.atlasHeight;

        if (batchDepth > 0 && atlas == batchAtlas) {
            appendQuad(u0, v0, u1, v1, screenX, screenY, renderW, renderH, 255, 255, 255, 255);
            return;
        }

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        appendQuad(buffer, u0, v0, u1, v1, screenX, screenY, renderW, renderH, 255, 255, 255, 255);
        drawMesh(atlas.guiRenderType(), buffer.buildOrThrow());
    }

    public static void drawColoredQuad(float x1, float y1, float x2, float y2, int argb) {
        int alpha = (argb >>> 24) & 0xFF;
        int red = (argb >>> 16) & 0xFF;
        int green = (argb >>> 8) & 0xFF;
        int blue = argb & 0xFF;
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.addVertex(x1, y2, 0.0F).setColor(red, green, blue, alpha);
        buffer.addVertex(x2, y2, 0.0F).setColor(red, green, blue, alpha);
        buffer.addVertex(x2, y1, 0.0F).setColor(red, green, blue, alpha);
        buffer.addVertex(x1, y1, 0.0F).setColor(red, green, blue, alpha);
        drawMesh(COLOR_QUAD_RENDER_TYPE, buffer.buildOrThrow());
    }

    private static void flushCurrentBatch() {
        if (batchBuffer == null || batchAtlas == null) {
            batchBuffer = null;
            batchAtlas = null;
            return;
        }
        MeshData mesh = batchBuffer.build();
        if (mesh != null) {
            drawMesh(batchAtlas.guiRenderType(), mesh);
        }
        batchBuffer = null;
        batchAtlas = null;
    }

    private static void drawMesh(RenderType renderType, MeshData mesh) {
        renderType.draw(mesh);
    }

    private static void appendQuad(float u0, float v0, float u1, float v1,
                                   float screenX, float screenY, float renderW, float renderH,
                                   int red, int green, int blue, int alpha) {
        if (batchBuffer != null) {
            appendQuad(batchBuffer, u0, v0, u1, v1, screenX, screenY, renderW, renderH, red, green, blue, alpha);
        }
    }

    private static void appendQuad(BufferBuilder buffer, float u0, float v0, float u1, float v1,
                                   float screenX, float screenY, float renderW, float renderH,
                                   int red, int green, int blue, int alpha) {
        buffer.addVertex(screenX, screenY + renderH, 0.0F).setUv(u0, v1).setColor(red, green, blue, alpha);
        buffer.addVertex(screenX + renderW, screenY + renderH, 0.0F).setUv(u1, v1).setColor(red, green, blue, alpha);
        buffer.addVertex(screenX + renderW, screenY, 0.0F).setUv(u1, v0).setColor(red, green, blue, alpha);
        buffer.addVertex(screenX, screenY, 0.0F).setUv(u0, v0).setColor(red, green, blue, alpha);
    }
}
