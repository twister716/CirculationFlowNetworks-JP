package com.circulation.circulation_networks.handlers;

import com.circulation.circulation_networks.tiles.TileEntityPhaseInterrupter;
import com.circulation.circulation_networks.utils.RenderingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.WeakHashMap;

@SideOnly(Side.CLIENT)
public final class PhaseInterrupterRenderingHandler {

    public static final PhaseInterrupterRenderingHandler INSTANCE = new PhaseInterrupterRenderingHandler();

    private static final float ORANGE_R = 1.0f;
    private static final float ORANGE_G = 0.647f;
    private static final float ORANGE_B = 0.0f;
    private static final float ALPHA = 0.5f;
    private static final float RANGE_EXPANSION = 0.01f;
    private static final float ANIMATION_DURATION = 2.0f;

    private final Map<TileEntityPhaseInterrupter, Float> animProgress = new WeakHashMap<>();

    private static float easeOutCubic(float t) {
        t = Math.min(t, 1.0f);
        return (float) (1.0 - Math.pow(1.0 - t, 3.0));
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        animProgress.forEach((tile, progress) -> {
            if (tile.isShowingRange()) {
                float newProgress = Math.min(progress + 1.0f / (ANIMATION_DURATION * 20.0f), 1.0f);
                animProgress.put(tile, newProgress);
            } else {
                animProgress.put(tile, 0.0f);
            }
        });
    }

    private void renderInterrupterRange(TileEntityPhaseInterrupter interrupter, double playerX, double playerY, double playerZ) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(-playerX, -playerY, -playerZ);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(false);

        int scope = interrupter.getScope();
        double x = interrupter.getPos().getX();
        double y = interrupter.getPos().getY();
        double z = interrupter.getPos().getZ();

        float progress = animProgress.getOrDefault(interrupter, 0.0f);
        float easedProgress = easeOutCubic(progress);
        float expandedScope = scope * easedProgress + RANGE_EXPANSION;

        RenderingUtils.drawFilledBox(
            x - expandedScope, y - expandedScope, z - expandedScope,
            x + expandedScope + 1, y + expandedScope + 1, z + expandedScope + 1,
            ORANGE_R, ORANGE_G, ORANGE_B, ALPHA
        );

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.player == null) return;

        double playerX = RenderingUtils.getPlayerRenderX(event.getPartialTicks());
        double playerY = RenderingUtils.getPlayerRenderY(event.getPartialTicks());
        double playerZ = RenderingUtils.getPlayerRenderZ(event.getPartialTicks());

        for (TileEntity te : mc.world.loadedTileEntityList) {
            if (te instanceof TileEntityPhaseInterrupter interrupter) {
                if (interrupter.isShowingRange()) {
                    animProgress.putIfAbsent(interrupter, 0.0f);
                    renderInterrupterRange(interrupter, playerX, playerY, playerZ);
                }
            }
        }
    }
}
