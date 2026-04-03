package com.circulation.circulation_networks.network.nodes;

import com.circulation.circulation_networks.CFNConfig;
import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.api.node.NodeContext;
import com.circulation.circulation_networks.api.node.NodeType;
import com.circulation.circulation_networks.registry.NodeTypes;
import org.jetbrains.annotations.NotNull;

public final class NodeFactory {

    private NodeFactory() {
    }

    @SuppressWarnings("unchecked")
    public static <N extends INode> @NotNull N createNode(@NotNull NodeType<? extends N> nodeType, @NotNull NodeContext context) {
        if (nodeType == NodeTypes.HUB) {
            return (N) new HubNode(
                context,
                CFNConfig.NODE.hub.energyScope,
                CFNConfig.NODE.hub.chargingScope,
                CFNConfig.NODE.hub.linkScope
            );
        }
        if (nodeType == NodeTypes.PORT_NODE) {
            return (N) new PortNode(
                context,
                CFNConfig.NODE.portNode.energyScope,
                CFNConfig.NODE.portNode.linkScope
            );
        }
        if (nodeType == NodeTypes.CHARGING_NODE) {
            return (N) new ChargingNode(
                context,
                CFNConfig.NODE.chargingNode.chargingScope,
                CFNConfig.NODE.chargingNode.linkScope
            );
        }
        if (nodeType == NodeTypes.RELAY_NODE) {
            return (N) new Node(
                NodeTypes.RELAY_NODE,
                context,
                CFNConfig.NODE.relayNode.linkScope
            );
        }
        throw new IllegalArgumentException("Unsupported node type: " + nodeType.id());
    }
}