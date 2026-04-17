package com.circulation.circulation_networks.handlers;

import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.client.compat.RenderSystemCompat;
import com.circulation.circulation_networks.items.CirculationConfiguratorModeModel.ToolFunction;
import com.circulation.circulation_networks.items.CirculationConfiguratorState;
import com.circulation.circulation_networks.registry.CFNItems;
import com.circulation.circulation_networks.utils.RenderingUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

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
    public void renderWorldLastEvent(RenderLevelStageEvent.AfterTranslucentBlocks event) {
        if (overrides.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;

        var stack = p.getMainHandItem();
        if (!(stack.getItem() == CFNItems.circulationConfigurator
            && CirculationConfiguratorState.getFunction(stack) == ToolFunction.CONFIGURATION))
            return;

        var cameraPos = mc.gameRenderer.getMainCamera().position();
        double doubleX = cameraPos.x;
        double doubleY = cameraPos.y;
        double doubleZ = cameraPos.z;

        var mvStack = RenderSystem.getModelViewStack();
        mvStack.pushMatrix();
        mvStack.set(event.getModelViewMatrix());
        mvStack.translate((float) -doubleX, (float) -doubleY, (float) -doubleZ);
        RenderSystemCompat.applyModelViewMatrix();
        try {
            RenderingUtils.setupWorldRenderState();
            RenderingUtils.setupAdditiveBlend();

            for (var entry : overrides.long2ObjectEntrySet()) {
                BlockPos pos = BlockPos.of(entry.getLongKey());
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
                    }
                    case RECEIVE -> {
                        r = 0.2f;
                        g = 1.0f;
                        b = 0.2f;
                    }
                    case STORAGE -> {
                        r = 0.2f;
                        g = 0.4f;
                        b = 1.0f;
                    }
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

                RenderingUtils.drawFilledBox(x0, y0, z0, x1, y1, z1, r, g, b, 0.15f);
                RenderingUtils.drawBoxEdges(x0, y0, z0, x1, y1, z1, r, g, b, 0.6f, 2.0f);
            }
        } finally {
            RenderingUtils.restoreWorldRenderState();
            RenderSystem.getModelViewStack().popMatrix();
            RenderSystemCompat.applyModelViewMatrix();
        }
    }
}
