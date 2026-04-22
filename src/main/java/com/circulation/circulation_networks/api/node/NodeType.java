package com.circulation.circulation_networks.api.node;

import org.jetbrains.annotations.NotNull;

public interface NodeType<N extends INode> {

    @NotNull String id();

    @NotNull Class<N> nodeClass();

    boolean allowsPocketNode();

    default @NotNull String getId() {
        return id();
    }

    default @NotNull Class<N> getNodeClass() {
        return nodeClass();
    }

    default boolean matches(INode node) {
        return node != null && id().equals(node.getNodeType().id());
    }

    @SuppressWarnings("unchecked")
    default @NotNull N cast(INode node) {
        return (N) node;
    }
}
