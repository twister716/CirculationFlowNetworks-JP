package com.circulation.circulation_networks.handlers;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.items.CirculationConfiguratorModeModel;
import com.circulation.circulation_networks.items.CirculationConfiguratorSelection;
import com.circulation.circulation_networks.items.CirculationConfiguratorState;
import com.circulation.circulation_networks.packets.UpdateItemModeMessage;
import com.circulation.circulation_networks.registry.CFNItems;
import com.circulation.circulation_networks.utils.CI18n;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;

public class ItemToolHandler {
    public static final ItemToolHandler INSTANCE = new ItemToolHandler();

    private final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public void onMouseEvent(InputEvent.MouseScrollingEvent event) {
        if (mc.player != null && mc.player.isShiftKeyDown()) {
            ItemStack stack = mc.player.getMainHandItem();
            int delta = CirculationConfiguratorModeModel.normalizeScrollDelta((int) event.getScrollDeltaY());
            if (delta != 0 && stack.getItem() == CFNItems.circulationConfigurator) {
                int mode = CirculationConfiguratorState.getSubMode(stack) + delta;
                CirculationConfiguratorState.setSubMode(stack, mode);

                CirculationFlowNetworks.sendToServer(new UpdateItemModeMessage(mode));

                CirculationConfiguratorSelection selection = CirculationConfiguratorSelection.fromStack(stack);
                String modeName = CI18n.format(selection.modeLangKey());
                String subModeName = CI18n.format(selection.subModeLangKey());


                Component message = Component.literal(
                    ChatFormatting.GOLD + modeName + ChatFormatting.GRAY + "[" + ChatFormatting.BLUE + subModeName + ChatFormatting.GRAY + "]"
                );
                mc.player.sendOverlayMessage(message);

                event.setCanceled(true);
            }
        }
    }
}
