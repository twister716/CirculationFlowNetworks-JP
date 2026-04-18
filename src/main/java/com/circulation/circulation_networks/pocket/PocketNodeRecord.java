package com.circulation.circulation_networks.pocket;

import com.circulation.circulation_networks.api.node.NodeContext;
import com.circulation.circulation_networks.api.node.NodeType;
import com.circulation.circulation_networks.registry.NodeTypes;
//~ mc_imports
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
//~ if >=1.20 'EnumFacing' -> 'Direction' {
public record PocketNodeRecord(int dimensionId, BlockPos pos, NodeType<?> nodeType, @Nullable EnumFacing attachmentFace,
                               @Nullable String customName, @Nullable String hostBlockId) {

    public int getDimensionId() {
        return dimensionId;
    }

    public BlockPos getPos() {
        return pos;
    }

    public NodeType<?> getNodeType() {
        return nodeType;
    }

    public @Nullable EnumFacing getAttachmentFace() {
        return attachmentFace;
    }

    public @Nullable String getCustomName() {
        return customName;
    }

    public @Nullable String getHostBlockId() {
        return hostBlockId;
    }

    //~ if >=1.20 'NBTTagCompound' -> 'CompoundTag' {
    //~ if >=1.20 '.hasKey(' -> '.contains(' {
    //~ if >=1.20 '.getInteger(' -> '.getInt(' {
    //~ if >=1.20 'BlockPos.fromLong(' -> 'BlockPos.of(' {
    public static @Nullable PocketNodeRecord deserialize(@Nullable NBTTagCompound tag) {
        if (tag == null) {
            return null;
        }
        if (!tag.hasKey("dim") || !tag.hasKey("pos") || !tag.hasKey("type")) {
            return null;
        }
        NodeType<?> nodeType = NodeTypes.getById(tag.getString("type"));
        if (nodeType == null || !nodeType.allowsPocketNode()) {
            return null;
        }
        String customName = tag.hasKey("customName") ? tag.getString("customName") : null;
        return new PocketNodeRecord(
            tag.getInteger("dim"),
            BlockPos.fromLong(tag.getLong("pos")),
            nodeType,
            deserializeFace(tag.hasKey("face") ? tag.getString("face") : null),
            customName == null || customName.isEmpty() ? null : customName,
            tag.hasKey("hostBlockId") ? tag.getString("hostBlockId") : null
        );
    }
    //~}
    //~}
    //~}
    //~}

    private static String toDisplayName(String typeId) {
        String[] parts = typeId.split("_");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        return builder.toString();
    }

    private static String serializeFace(EnumFacing face) {
        //? if <1.20 {
        return face.getName2();
        //?} else {
        /*return face.getName();
         *///?}
    }

    private static @Nullable EnumFacing deserializeFace(@Nullable String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        //? if <1.20 {
        return EnumFacing.byName(name);
        //?} else {
        /*return EnumFacing.byName(name);
         *///?}
    }

    //~ if >=1.20 'World ' -> 'Level ' {
    public NodeContext createNodeContext(World world) {
        return NodeContext.of(world, pos, null, nodeType.fallbackVisualId());
    }
    //~}

    public PocketNodeRecord withHostBlockId(@Nullable String newHostBlockId) {
        return new PocketNodeRecord(dimensionId, pos, nodeType, attachmentFace, customName, newHostBlockId);
    }

    //~ if >=1.20 'NBTTagCompound' -> 'CompoundTag' {
    //~ if >=1.20 'BlockPos.fromLong(' -> 'BlockPos.of(' {
    //~ if >=1.20 '.set' -> '.put' {
    //~ if >=1.20 '.setInteger(' -> '.putInt(' {
    //~ if >=1.20 '.toLong()' -> '.asLong()' {
    public NBTTagCompound serialize() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("dim", dimensionId);
        tag.setLong("pos", pos.toLong());
        tag.setString("type", nodeType.id());
        if (attachmentFace != null) {
            tag.setString("face", serializeFace(attachmentFace));
        }
        if (customName != null && !customName.isEmpty()) {
            tag.setString("customName", customName);
        }
        if (hostBlockId != null && !hostBlockId.isEmpty()) {
            tag.setString("hostBlockId", hostBlockId);
        }
        return tag;
    }
    //~}
    //~}
    //~}
    //~}
    //~}
//~}
}
