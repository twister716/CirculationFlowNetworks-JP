package com.circulation.circulation_networks.api;

import com.circulation.circulation_networks.api.node.IHubNode;
import com.circulation.circulation_networks.api.node.INode;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public interface IGrid {

    int getId();

    ReferenceSet<INode> getNodes();

    NBTTagCompound serialize();

    /**
     * 获取此网络的中枢节点 / Get the hub node of this network
     *
     * @return 中枢节点，不存在时返回 null
     */
    @Nullable
    default IHubNode getHubNode() {
        return null;
    }

    /**
     * 设置此网络的中枢节点 / Set the hub node of this network
     */
    default void setHubNode(@Nullable IHubNode hub) {
    }
}
