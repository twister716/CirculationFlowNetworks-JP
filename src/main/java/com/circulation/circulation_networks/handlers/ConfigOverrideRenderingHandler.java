package com.circulation.circulation_networks.handlers;

import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.items.ItemInspectionTool;
import com.circulation.circulation_networks.registry.RegistryItems;
import com.circulation.circulation_networks.utils.RenderingUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ConfigOverrideRenderingHandler {

    public static final ConfigOverrideRenderingHandler INSTANCE = new ConfigOverrideRenderingHandler();

    private static final float INSET = 0.01f;
    private static final double MAX_RENDER_DIST_SQ = 256 * 256;

    private final Long2ObjectMap<IEnergyHandler.EnergyType> overrides = new Long2ObjectLinkedOpenHashMap<>();

    public void addOverride(long pos, IEnergyHandler.EnergyType type) {
        overrides.put(pos, type);
    }

    public void removeOverride(long pos) {
        overrides.remove(pos);
    }

    public void clear() {
        overrides.clear();
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        if (overrides.isEmpty()) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP p = mc.player;

        var stack = p.getHeldItemMainhand();
        if (!(stack.getItem() == RegistryItems.inspectionTool
            && RegistryItems.inspectionTool.getFunction(stack) == ItemInspectionTool.ToolFunction.CONFIGURATION))
            return;

        double doubleX = RenderingUtils.getPlayerRenderX(event.getPartialTicks());
        double doubleY = RenderingUtils.getPlayerRenderY(event.getPartialTicks());
        double doubleZ = RenderingUtils.getPlayerRenderZ(event.getPartialTicks());

        GlStateManager.pushMatrix();
        GlStateManager.translate(-doubleX, -doubleY, -doubleZ);
        RenderingUtils.setupWorldRenderState();
        RenderingUtils.setupAdditiveBlend();

        for (var entry : overrides.long2ObjectEntrySet()) {
            BlockPos pos = BlockPos.fromLong(entry.getLongKey());
            IEnergyHandler.EnergyType type = entry.getValue();

            if (!RenderingUtils.isWithinRenderDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                doubleX, doubleY, doubleZ, MAX_RENDER_DIST_SQ)) {
                continue;
            }

            float r, g, b;
            switch (type) {
                case SEND -> {
                    r = 1.0f;
                    g = 0.2f;
                    b = 0.2f;
                }      // Red
                case RECEIVE -> {
                    r = 0.2f;
                    g = 1.0f;
                    b = 0.2f;
                }   // Green
                case STORAGE -> {
                    r = 0.2f;
                    g = 0.4f;
                    b = 1.0f;
                }   // Blue
                default -> {
                    r = 1.0f;
                    g = 1.0f;
                    b = 1.0f;
                }
            }

            double x0 = pos.getX() + INSET;
            double y0 = pos.getY() + INSET;
            double z0 = pos.getZ() + INSET;
            double x1 = pos.getX() + 1.0 - INSET;
            double y1 = pos.getY() + 1.0 - INSET;
            double z1 = pos.getZ() + 1.0 - INSET;

            // Glow layer
            RenderingUtils.drawFilledBox(x0, y0, z0, x1, y1, z1, r, g, b, 0.15f);
            // Core edges
            RenderingUtils.drawBoxEdges(x0, y0, z0, x1, y1, z1, r, g, b, 0.6f, 2.0f);
        }

        RenderingUtils.restoreWorldRenderState();
        GlStateManager.popMatrix();
    }
}
