package com.circulation.circulation_networks.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

public final class CFNRenderTypes extends RenderType {

    private CFNRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType nodePedestalTranslucentNoCull() {
        return RenderType.create(
            "circulation_networks:node_pedestal_translucent",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            262144,
            true,
            true,
            RenderType.CompositeState.builder()
                .setLightmapState(LIGHTMAP)
                .setShaderState(RENDERTYPE_TRANSLUCENT_SHADER)
                .setTextureState(BLOCK_SHEET_MIPPED)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setOutputState(TRANSLUCENT_TARGET)
                .setCullState(NO_CULL)
                .createCompositeState(true)
        );
    }
}
