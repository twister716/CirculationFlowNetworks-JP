package com.circulation.circulation_networks.pocket;

import com.circulation.circulation_networks.api.node.INode;
import net.minecraft.core.Direction;

import org.jetbrains.annotations.Nullable;

public record PocketNodeHost(PocketNodeRecord record, INode node) {

    public PocketNodeRecord getRecord() {
        return record;
    }

    public INode getNode() {
        return node;
    }

    public PocketNodeRecord toRecord() {
        return new PocketNodeRecord(
            record.dimensionId(),
            record.pos(),
            record.nodeType(),
            record.attachmentFace(),
            node.getCustomName(),
            record.hostBlockId()
        );
    }

    public @Nullable Direction getAttachmentFace() {
        return record.attachmentFace();
    }
}
