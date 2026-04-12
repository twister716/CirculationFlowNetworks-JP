package com.circulation.circulation_networks.items;

import com.circulation.circulation_networks.api.hub.IHubPlugin;
import com.circulation.circulation_networks.network.hub.HubCapabilitys;
import com.circulation.circulation_networks.network.hub.HubPluginCapability;
import com.circulation.circulation_networks.tooltip.LocalizedComponent;
//~ mc_imports
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemHubChannelPlugin extends BaseItem implements IHubPlugin {

    //? if <1.20 {
    public ItemHubChannelPlugin() {
        super("hub_channel_plugin");
        this.setMaxStackSize(1);
    }
    //?} else {
    /*public ItemHubChannelPlugin(Properties properties) {
        super(properties.stacksTo(1));
    }
    *///?}

    public static void setChannelInfo(ItemStack stack, UUID channelId, String name) {
        HubChannelPluginData.setChannelInfo(stack, channelId, name);
    }

    @Nullable
    public UUID getChannelId(ItemStack stack) {
        return HubChannelPluginData.getChannelId(stack);
    }

    @Nullable
    public String getChannelName(ItemStack stack) {
        return HubChannelPluginData.getChannelName(stack);
    }

    @Override
    public HubPluginCapability<?> getCapability() {
        return HubCapabilitys.CHANNEL_CAPABILITY;
    }

    @Override
    protected List<LocalizedComponent> buildTooltips(ItemStack stack) {
        List<LocalizedComponent> tips = super.buildTooltips(stack);
        var channelId = getChannelId(stack);
        var channelName = getChannelName(stack);
        if (HubChannelPluginData.isComplete(channelId, channelName)) {
            tips.add(LocalizedComponent.withArgs("item.circulation_networks.hub_channel_plugin.channel", () -> new Object[]{channelName, channelId.toString()}));
        } else {
            tips.add(LocalizedComponent.of("item.circulation_networks.hub_channel_plugin.no_channel"));
        }
        return tips;
    }
}