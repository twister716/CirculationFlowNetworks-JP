package com.circulation.circulation_networks.items;

import com.circulation.circulation_networks.api.hub.IHubPlugin;
import com.circulation.circulation_networks.api.node.IHubNode;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemHubChannelPlugin extends BaseItem implements IHubPlugin {

    public ItemHubChannelPlugin() {
        super("hub_channel_plugin");
        this.setMaxStackSize(1);
    }

    /**
     * 设置物品的频道UUID和名称
     */
    public static void setChannelInfo(ItemStack stack, UUID channelId, String name) {
        HubChannelPluginData.setChannelInfo(stack, channelId, name);
    }

    /**
     * 从物品NBT中获取频道UUID
     */
    @Nullable
    public UUID getChannelId(ItemStack stack) {
        return HubChannelPluginData.getChannelId(stack);
    }

    /**
     * 从物品NBT中获取频道名称
     */
    @Nullable
    public String getChannelName(ItemStack stack) {
        return HubChannelPluginData.getChannelName(stack);
    }

    @Override
    public void onInserted(IHubNode hub, int slot, ItemStack stack) {
        HubChannelPluginData.applyToHub(hub, stack);
    }

    @Override
    public void onRemoved(IHubNode hub, int slot, ItemStack stack) {
        HubChannelPluginData.clearHub(hub);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        var channelId = getChannelId(stack);
        var channelName = getChannelName(stack);
        if (HubChannelPluginData.isComplete(channelId, channelName)) {
            tooltip.add(I18n.format("item.circulation_networks.hub_channel_plugin.channel", channelName, channelId.toString()));
        } else {
            tooltip.add(I18n.format("item.circulation_networks.hub_channel_plugin.no_channel"));
        }
    }
}
