package com.circulation.circulation_networks.handlers;

import com.circulation.circulation_networks.math.Vec3d;
import com.circulation.circulation_networks.utils.BuckyBallGeometry;
import com.mojang.blaze3d.opengl.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class SpoceRenderingHandlerGL46L3 extends SpoceRenderingHandler {

    protected int sphereVAO, sphereVBO, sphereVertexCount;
    protected int buckyVAO, buckyVBO, buckyVertexCount;

    protected static FloatBuffer buildSphereDataDirect() {
        final int slices = 32;
        final int stacks = 32;
        FloatBuffer buf = MemoryUtil.memAllocFloat(slices * stacks * 6 * 3);
        for (int i = 0; i < slices; i++) {
            double p1 = Math.PI * i / slices;
            double p2 = Math.PI * (i + 1) / slices;
            for (int j = 0; j < stacks; j++) {
                double t1 = 2.0 * Math.PI * j / stacks;
                double t2 = 2.0 * Math.PI * (j + 1) / stacks;
                float x00 = (float) (Math.sin(p1) * Math.cos(t1));
                float y00 = (float) Math.cos(p1);
                float z00 = (float) (Math.sin(p1) * Math.sin(t1));
                float x10 = (float) (Math.sin(p2) * Math.cos(t1));
                float y10 = (float) Math.cos(p2);
                float z10 = (float) (Math.sin(p2) * Math.sin(t1));
                float x01 = (float) (Math.sin(p1) * Math.cos(t2));
                float y01 = (float) Math.cos(p1);
                float z01 = (float) (Math.sin(p1) * Math.sin(t2));
                float x11 = (float) (Math.sin(p2) * Math.cos(t2));
                float y11 = (float) Math.cos(p2);
                float z11 = (float) (Math.sin(p2) * Math.sin(t2));
                buf.put(x00).put(y00).put(z00).put(x10).put(y10).put(z10).put(x01).put(y01).put(z01);
                buf.put(x10).put(y10).put(z10).put(x11).put(y11).put(z11).put(x01).put(y01).put(z01);
            }
        }
        buf.flip();
        return buf;
    }

    protected static FloatBuffer buildBuckyDataDirect() {
        FloatBuffer buf = MemoryUtil.memAllocFloat(BuckyBallGeometry.edges.size() * 2 * 3);
        for (int[] edge : BuckyBallGeometry.edges) {
            Vec3d v1 = BuckyBallGeometry.vertices.get(edge[0]);
            Vec3d v2 = BuckyBallGeometry.vertices.get(edge[1]);
            buf.put((float) v1.x).put((float) v1.y).put((float) v1.z);
            buf.put((float) v2.x).put((float) v2.y).put((float) v2.z);
        }
        buf.flip();
        return buf;
    }

    protected void initGL() {
        FloatBuffer sd = buildSphereDataDirect();
        try {
            sphereVertexCount = sd.limit() / 3;
            sphereVBO = GL45.glCreateBuffers();
            GL45.glNamedBufferStorage(sphereVBO, sd, 0);
        } finally {
            MemoryUtil.memFree(sd);
        }
        sphereVAO = GL30.glGenVertexArrays();
        GlStateManager._glBindVertexArray(sphereVAO);
        GlStateManager._glBindBuffer(GL15.GL_ARRAY_BUFFER, sphereVBO);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
        GL20.glEnableVertexAttribArray(0);
        GlStateManager._glBindVertexArray(0);
        GlStateManager._glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        FloatBuffer bd = buildBuckyDataDirect();
        try {
            buckyVertexCount = bd.limit() / 3;
            buckyVBO = GL45.glCreateBuffers();
            GL45.glNamedBufferStorage(buckyVBO, bd, 0);
        } finally {
            MemoryUtil.memFree(bd);
        }
        buckyVAO = GL30.glGenVertexArrays();
        GlStateManager._glBindVertexArray(buckyVAO);
        GlStateManager._glBindBuffer(GL15.GL_ARRAY_BUFFER, buckyVBO);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
        GL20.glEnableVertexAttribArray(0);
        GlStateManager._glBindVertexArray(0);
        GlStateManager._glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }
}
