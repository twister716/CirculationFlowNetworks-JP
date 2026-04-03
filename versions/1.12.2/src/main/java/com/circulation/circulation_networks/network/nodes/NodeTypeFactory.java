package com.circulation.circulation_networks.network.nodes;

import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.api.node.NodeContext;
import com.circulation.circulation_networks.api.node.NodeType;
import org.jetbrains.annotations.NotNull;

public final class NodeTypeFactory {

    private NodeTypeFactory() {
    }

    public static <N extends INode> @NotNull N createNode(@NotNull NodeType<? extends N> nodeType, @NotNull NodeContext context) {
        return NodeFactory.createNode(nodeType, context);
    }
}