package com.circulation.circulation_networks.handlers;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.items.ItemInspectionTool;
import com.circulation.circulation_networks.packets.UpdateItemModeMessage;
import com.circulation.circulation_networks.registry.RegistryItems;
import com.circulation.circulation_networks.utils.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class ItemToolHandler {
    public static final ItemToolHandler INSTANCE = new ItemToolHandler();

    private final Minecraft mc = FMLClientHandler.instance().getClient();

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        if (mc.player != null && mc.player.isSneaking()) {
            ItemStack stack = mc.player.getHeldItemMainhand();
            int delta = -Mouse.getEventDWheel();
            if (delta % 120 == 0) {
                delta = delta / 120;
            }
            if (delta % 80 == 0) {
                delta = delta / 80;
            }
            if (delta != 0 && stack.getItem() == RegistryItems.inspectionTool) {
                final var tag = Functions.getOrCreateTagCompound(stack);
                int mode = tag.getInteger("mode") + delta;
                tag.setInteger("mode", mode);

                CirculationFlowNetworks.NET_CHANNEL.sendToServer(new UpdateItemModeMessage(mode));

                ItemInspectionTool.ToolFunction function = RegistryItems.inspectionTool.getFunction(stack);
                int subMode = Math.floorMod(mode, function.getSubModeCount());

                String modeName = I18n.format(function.getLangKey());
                String subModeName;
                if (function == ItemInspectionTool.ToolFunction.INSPECTION) {
                    ItemInspectionTool.InspectionMode inspMode = ItemInspectionTool.InspectionMode.fromID(subMode);
                    subModeName = I18n.format(inspMode.getLangKey());
                } else {
                    ItemInspectionTool.ConfigurationMode confMode = ItemInspectionTool.ConfigurationMode.fromID(subMode);
                    subModeName = I18n.format(confMode.getLangKey());
                }

                TextComponentString message = new TextComponentString(
                    TextFormatting.GOLD + modeName + TextFormatting.GRAY + "[" + TextFormatting.BLUE + subModeName + TextFormatting.GRAY + "]"
                );
                mc.player.sendStatusMessage(message, true);

                event.setCanceled(true);
            }
        }
    }
}