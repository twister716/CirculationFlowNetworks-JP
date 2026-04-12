package com.circulation.circulation_networks.pocket;

import com.circulation.circulation_networks.api.node.INode;
//~ mc_imports
//? if <1.20 {
import net.minecraft.util.EnumFacing;
//?} else {
/*import net.minecraft.core.Direction;
 *///?}

import org.jetbrains.annotations.Nullable;

//? if <1.20 {
import com.github.bsideup.jabel.Desugar;

@Desugar
//?}
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
            node.getCustomName()
        );
    }

    //~ if >=1.20 'EnumFacing' -> 'Direction' {
    public @Nullable EnumFacing getAttachmentFace() {
        return record.attachmentFace();
    }
    //~}
}