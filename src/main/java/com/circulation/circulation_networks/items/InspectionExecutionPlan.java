package com.circulation.circulation_networks.items;

import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.items.InspectionToolModeModel.InspectionMode;

public final class InspectionExecutionPlan {

    private final InspectionTargetSnapshot snapshot;
    private final boolean renderSpoce;
    private final boolean renderLink;

    private InspectionExecutionPlan(InspectionTargetSnapshot snapshot, boolean renderSpoce, boolean renderLink) {
        this.snapshot = snapshot;
        this.renderSpoce = renderSpoce;
        this.renderLink = renderLink;
    }

    public static InspectionExecutionPlan fromNode(INode node, int subMode) {
        return fromNode(node, InspectionMode.fromID(subMode));
    }

    public static InspectionExecutionPlan fromNode(INode node, InspectionMode mode) {
        InspectionTargetSnapshot snapshot = InspectionTargetSnapshot.fromNode(node);
        return new InspectionExecutionPlan(
            snapshot,
            mode.isMode(InspectionMode.SPOCE),
            mode.isMode(InspectionMode.LINK)
        );
    }

    public InspectionTargetSnapshot snapshot() {
        return snapshot;
    }

    public boolean renderSpoce() {
        return renderSpoce;
    }

    public boolean renderLink() {
        return renderLink;
    }
}