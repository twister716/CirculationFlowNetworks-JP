package com.circulation.circulation_networks.handlers;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.items.InspectionToolModeModel;
import com.circulation.circulation_networks.items.InspectionToolSelection;
import com.circulation.circulation_networks.items.InspectionToolState;
//? if <1.20 {
import com.circulation.circulation_networks.packets.UpdateItemModeMessage;
//?}
import com.circulation.circulation_networks.registry.CFNItems;
//? if <1.20 {
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
//?} else {
/*import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
*///?}

//? if <1.20 {
@SideOnly(Side.CLIENT)
//?} else {
/*@OnlyIn(Dist.CLIENT)
*///?}
public class ItemToolHandler {
    public static final ItemToolHandler INSTANCE = new ItemToolHandler();

    //? if <1.20 {
    private final Minecraft mc = FMLClientHandler.instance().getClient();
    //?} else {
    /*private final Minecraft mc = Minecraft.getInstance();
    *///?}

    @SubscribeEvent
    //? if <1.20 {
    public void onMouseEvent(MouseEvent event) {
        if (mc.player != null && mc.player.isSneaking()) {
            ItemStack stack = mc.player.getHeldItemMainhand();
            int delta = InspectionToolModeModel.normalizeScrollDelta(Mouse.getEventDWheel());
    //?} else {
    /*public void onMouseEvent(InputEvent.MouseScrollingEvent event) {
        if (mc.player != null && mc.player.isShiftKeyDown()) {
            ItemStack stack = mc.player.getMainHandItem();
            int delta = InspectionToolModeModel.normalizeScrollDelta((int) event.getScrollDelta());
    *///?}
            if (delta != 0 && stack.getItem() == CFNItems.inspectionTool) {
                int mode = InspectionToolState.getSubMode(stack) + delta;
                InspectionToolState.setSubMode(stack, mode);

                //? if <1.20 {
                CirculationFlowNetworks.NET_CHANNEL.sendToServer(new UpdateItemModeMessage(mode));
                //?}

                InspectionToolSelection selection = InspectionToolSelection.fromStack(stack);

                //? if <1.20 {
                String modeName = I18n.format(selection.modeLangKey());
                String subModeName = I18n.format(selection.subModeLangKey());

                TextComponentString message = new TextComponentString(
                    TextFormatting.GOLD + modeName + TextFormatting.GRAY + "[" + TextFormatting.BLUE + subModeName + TextFormatting.GRAY + "]"
                );
                mc.player.sendStatusMessage(message, true);
                //?} else {
                /*String modeName = I18n.get(selection.modeLangKey());
                String subModeName = I18n.get(selection.subModeLangKey());

                Component message = Component.literal(
                    ChatFormatting.GOLD + modeName + ChatFormatting.GRAY + "[" + ChatFormatting.BLUE + subModeName + ChatFormatting.GRAY + "]"
                );
                mc.player.displayClientMessage(message, true);
                *///?}

                event.setCanceled(true);
            }
        }
    }
}
