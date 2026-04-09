package com.circulation.circulation_networks.client.render;

import com.circulation.circulation_networks.CFNConfig;
import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.tiles.nodes.TileEntityRelayNode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
public final class RelayNodeRotatingRenderer extends TileEntitySpecialRenderer<TileEntityRelayNode> {

    private static final float CENTER = 0.5F;

    private static final ResourceLocation TOP_SPIRAL_BASE = model("relay_node/relay_node_top_spiral_base");
    private static final ResourceLocation TOP_SPIRAL_EMISSIVE = model("relay_node/relay_node_top_spiral_emissive");
    private static final ResourceLocation CRYSTAL = model("relay_node/relay_node_crystal");
    private static final ResourceLocation BOTTOM_SPIRAL_BASE = model("relay_node/relay_node_bottom_spiral_base");
    private static final ResourceLocation BOTTOM_SPIRAL_EMISSIVE = model("relay_node/relay_node_bottom_spiral_emissive");

    @Override
    public void render(@NotNull TileEntityRelayNode te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (!te.hasWorld() || !CFNConfig.NODE.rendering.animatedSpecialModels) {
            return;
        }

        RotatingModelRenderHelper.RenderBatch batch = RotatingModelRenderHelper.beginBatch(te, x, y, z, destroyStage);
        if (batch == null) return;
        try {
            long worldTime = te.getWorld().getTotalWorldTime();
            float topAngle = NodeRotationAnimation.relayTopSpiralAngle(worldTime, partialTicks);
            float crystalAngle = NodeRotationAnimation.relayCrystalAngle(worldTime, partialTicks);
            float bottomAngle = NodeRotationAnimation.relayBottomSpiralAngle(worldTime, partialTicks);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, NodeRotationAnimation.bobOffset(worldTime, partialTicks), 0.0F);
            batch.renderAroundAxis(TOP_SPIRAL_BASE, topAngle, CENTER, CENTER, CENTER, 0.0F, 1.0F, 0.0F, false, false);
            batch.renderAroundYAxisFullBright(TOP_SPIRAL_EMISSIVE, topAngle, CENTER, CENTER, CENTER);
            batch.renderAroundYAxisFullBright(CRYSTAL, crystalAngle, CENTER, CENTER, CENTER);
            batch.renderAroundAxis(BOTTOM_SPIRAL_BASE, bottomAngle, CENTER, CENTER, CENTER, 0.0F, 1.0F, 0.0F, false, false);
            batch.renderAroundYAxisFullBright(BOTTOM_SPIRAL_EMISSIVE, bottomAngle, CENTER, CENTER, CENTER);
            GlStateManager.popMatrix();
        } finally {
            batch.end();
        }
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
    }

    private static ResourceLocation model(String path) {
        return new ResourceLocation(CirculationFlowNetworks.MOD_ID, "block/" + path);
    }
}
