package com.circulation.circulation_networks.tiles.nodes;

import com.circulation.circulation_networks.api.IHubNodeBlockEntity;
import com.circulation.circulation_networks.api.hub.IHubPlugin;
import com.circulation.circulation_networks.api.node.IHubNode;
import com.circulation.circulation_networks.api.node.NodeType;
import com.circulation.circulation_networks.container.ContainerHub;
import com.circulation.circulation_networks.gui.GuiHub;
import com.circulation.circulation_networks.inventory.CFNInternalInventory;
import com.circulation.circulation_networks.inventory.CFNInternalInventoryHost;
import com.circulation.circulation_networks.inventory.CFNInventoryChangeOperation;
import com.circulation.circulation_networks.items.HubChannelPluginData;
import com.circulation.circulation_networks.network.hub.HubCapabilitys;
import com.circulation.circulation_networks.network.hub.HubPluginCapability;
import com.circulation.circulation_networks.network.nodes.HubNode;
import com.circulation.circulation_networks.registry.NodeTypes;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TileEntityHub extends BaseNodeTileEntity<IHubNode> implements IHubNodeBlockEntity, CFNInternalInventoryHost {

    private final CFNInternalInventory plugins = new CFNInternalInventory(this, 5, 1).setInputFilter((inventory, slot, itemStack) -> {
        if (!(itemStack.getItem() instanceof IHubPlugin plugin)) {
            return false;
        }
        return isUniquePluginCapability(inventory, slot, plugin.getCapability());
    });
    private boolean init;
    private transient NBTTagCompound initNbt;

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public @NotNull ContainerHub getContainer(EntityPlayer player) {
        return new ContainerHub(player, getNode());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer getGui(EntityPlayer player) {
        return new GuiHub(player, getNode());
    }

    @Override
    protected @NotNull NodeType<? extends IHubNode> getNodeType() {
        return NodeTypes.HUB;
    }

    @Override
    protected void onNodeBound(IHubNode node) {
        if (node instanceof HubNode hubNode) {
            hubNode.bindPlugins(getPlugins());
        }
        rebuildHubPluginData();
    }

    public CFNInternalInventory getPlugins() {
        return plugins;
    }

    @Override
    protected void onInvalidate() {
        if (getNode() != null && getNode().getGrid() != null) {
            getNode().getGrid().setHubNode(null);
        }
        super.onInvalidate();
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        plugins.writeToNBT(compound, "plugins");
        return compound;
    }

    @Override
    public final void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        plugins.readFromNBT(nbt, "plugins");

        if (!init) {
            initNbt = nbt;
            init = true;
        } else {
            rebuildHubPluginData();
        }
    }

    protected void onValidate() {
        super.onValidate();
        if (initNbt != null) {
            rebuildHubPluginData();
            initNbt = null;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void onClientValidate() {
        super.onClientValidate();
        if (initNbt != null) {
            rebuildHubPluginData();
            initNbt = null;
        }
    }

    @Override
    public void onChangeInventory(CFNInternalInventory inventory, int slot, CFNInventoryChangeOperation operation, ItemStack oldStack, ItemStack newStack) {
        if (init) {
            saveAllPluginData();
            savePluginData(oldStack);
            rebuildHubPluginData();
        }
        markDirty();
    }

    private void rebuildHubPluginData() {
        IHubNode node = getNode();
        if (!(node instanceof HubNode hubNode)) {
            return;
        }

        clearPluginState(hubNode);

        for (int i = 0; i < plugins.getSlots(); i++) {
            ItemStack stack = plugins.getStackInSlot(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof IHubPlugin plugin)) {
                continue;
            }

            HubPluginCapability<?> capability = plugin.getCapability();
            if (hubNode.getHubData().hasKey(capability)) {
                continue;
            }

            hubNode.putPluginDataIfAbsent(capability, stack);
        }

        applyPluginState(hubNode);
    }

    private void clearPluginState(HubNode node) {
        node.removePluginData(HubCapabilitys.CHANNEL_CAPABILITY);
        node.removePluginData(HubCapabilitys.CHARGE_CAPABILITY);
        HubChannelPluginData.clearHub(node);
    }

    private void applyPluginState(IHubNode node) {
        HubChannelPluginData.ChannelInfo channelInfo = node.getHubData().get(HubCapabilitys.CHANNEL_CAPABILITY);
        if (HubChannelPluginData.isComplete(channelInfo)) {
            HubChannelPluginData.applyToHub(node, channelInfo);
        }
    }

    private void savePluginData(ItemStack stack) {
        IHubNode node = getNode();
        if (node == null || stack.isEmpty() || !(stack.getItem() instanceof IHubPlugin plugin)) {
            return;
        }

        HubPluginCapability<?> capability = plugin.getCapability();
        if (!node.getHubData().hasKey(capability)) {
            return;
        }

        if (capability == HubCapabilitys.CHANNEL_CAPABILITY) {
            capability.saveDataRaw(HubChannelPluginData.getChannelInfo(node), stack);
            return;
        }

        capability.saveDataRaw(node.getHubData().get(capability), stack);
    }

    private void saveAllPluginData() {
        for (int i = 0; i < plugins.getSlots(); i++) {
            savePluginData(plugins.getStackInSlot(i));
        }
    }

    private static boolean isUniquePluginCapability(CFNInternalInventory inventory, int slot, HubPluginCapability<?> capability) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (i == slot) {
                continue;
            }

            ItemStack existing = inventory.getStackInSlot(i);
            if (existing.isEmpty() || !(existing.getItem() instanceof IHubPlugin plugin)) {
                continue;
            }

            if (plugin.getCapability() == capability) {
                return false;
            }
        }
        return true;
    }
}
