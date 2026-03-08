package com.circulation.circulation_networks.api.node;

import com.circulation.circulation_networks.api.hub.ChargingDefinition;
import com.circulation.circulation_networks.api.hub.ChargingPreference;
import com.circulation.circulation_networks.api.hub.PermissionMode;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * 中枢节点接口，一个网络中只能存在一个中枢节点。
 * 中枢节点同时具有能量供应和玩家充能能力。
 * <p>
 * Hub node interface. Only one hub node can exist per network.
 * Hub nodes have both energy supply and player charging capabilities.
 */
public interface IHubNode extends IEnergySupplyNode, IChargingNode {

    int PLUGIN_SLOTS = 5;

    /**
     * 获取权限模式 / Get permission mode
     */
    PermissionMode getPermissionMode();

    /**
     * 设置权限模式 / Set permission mode
     */
    void setPermissionMode(PermissionMode mode);

    /**
     * 获取所有插件槽位 / Get all plugin slots
     *
     * @return 长度为 {@link #PLUGIN_SLOTS} 的数组，空槽位为 {@link ItemStack#EMPTY}
     */
    ItemStack[] getPlugins();

    /**
     * 设置指定槽位的插件 / Set plugin in specified slot
     */
    void setPlugin(int slot, ItemStack stack);

    /**
     * 获取所属频道的UUID
     * <p>
     * Get the channel UUID this hub belongs to.
     *
     * @return 频道UUID，无频道时返回 null
     */
    @Nullable
    UUID getChannelId();

    /**
     * 获取指定玩家的充能偏好 / Get charging preference for specified player
     *
     * @return 若未设置则返回null，无中枢时使用默认全部充能
     */
    @Nonnull
    ChargingPreference getChargingPreference(UUID playerId);

    /**
     * 设置指定玩家的充能偏好 / Set charging preference for specified player
     */
    void setChargingPreference(UUID playerId, ChargingPreference preference);

    /**
     * 获取指定玩家的充能偏好 / Get charging preference for specified player
     *
     * @return 若未设置则返回null，无中枢时使用默认全部充能
     */
    boolean getChargingState(UUID playerId, ChargingDefinition preference);

    /**
     * 设置指定玩家的充能偏好 / Set charging preference for specified player
     */
    void setChargingState(UUID playerId, ChargingDefinition preference, boolean value);

    /**
     * 获取中枢所有者 / Get hub owner
     */
    @Nullable
    UUID getOwner();

    /**
     * 设置中枢所有者 / Set hub owner
     */
    void setOwner(@Nullable UUID owner);
}
