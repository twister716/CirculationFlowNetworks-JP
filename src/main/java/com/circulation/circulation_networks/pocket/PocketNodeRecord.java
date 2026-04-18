package com.circulation.circulation_networks.pocket;

import com.circulation.circulation_networks.api.node.NodeContext;
import com.circulation.circulation_networks.api.node.NodeType;
import com.circulation.circulation_networks.registry.NodeTypes;
import com.circulation.circulation_networks.utils.BlockPosCompat;
import com.circulation.circulation_networks.utils.NbtCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings("unused")
public record PocketNodeRecord(String dimensionId, BlockPos pos, NodeType<?> nodeType, @Nullable Direction attachmentFace,
                               @Nullable String customName, @Nullable String hostBlockId) {

    public static @Nullable PocketNodeRecord deserialize(@Nullable CompoundTag tag) {
        if (tag == null) {
            return null;
        }
        if (!NbtCompat.contains(tag, "dim") || !NbtCompat.contains(tag, "pos") || !NbtCompat.contains(tag, "type")) {
            return null;
        }
        NodeType<?> nodeType = NodeTypes.getById(NbtCompat.getStringOr(tag, "type", ""));
        if (nodeType == null || !nodeType.allowsPocketNode()) {
            return null;
        }
        String customName = NbtCompat.contains(tag, "customName") ? NbtCompat.getStringOr(tag, "customName", "") : null;
        return new PocketNodeRecord(
            NbtCompat.getStringOr(tag, "dim", "minecraft:overworld"),
            BlockPosCompat.fromLong(NbtCompat.getLongOr(tag, "pos", 0L)),
            nodeType,
            deserializeFace(NbtCompat.contains(tag, "face") ? NbtCompat.getStringOr(tag, "face", "") : null),
            customName == null || customName.isEmpty() ? null : customName,
            NbtCompat.contains(tag, "hostBlockId") ? NbtCompat.getStringOr(tag, "hostBlockId", "") : null
        );
    }

    private static String serializeFace(Direction face) {
        return face.getName();
    }

    private static @Nullable Direction deserializeFace(@Nullable String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return Direction.byName(name);
    }

    public String getDimensionId() {
        return dimensionId;
    }

    public BlockPos getPos() {
        return pos;
    }

    public NodeType<?> getNodeType() {
        return nodeType;
    }

    public @Nullable Direction getAttachmentFace() {
        return attachmentFace;
    }

    public @Nullable String getCustomName() {
        return customName;
    }

    public @Nullable String getHostBlockId() {
        return hostBlockId;
    }

    public NodeContext createNodeContext(Level world) {
        return NodeContext.of(world, pos, null, nodeType.fallbackVisualId());
    }

    public PocketNodeRecord withHostBlockId(@Nullable String newHostBlockId) {
        return new PocketNodeRecord(dimensionId, pos, nodeType, attachmentFace, customName, newHostBlockId);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        NbtCompat.putString(tag, "dim", dimensionId);
        NbtCompat.putLong(tag, "pos", BlockPosCompat.toLong(pos));
        NbtCompat.putString(tag, "type", nodeType.id());
        if (attachmentFace != null) {
            NbtCompat.putString(tag, "face", serializeFace(attachmentFace));
        }
        if (customName != null && !customName.isEmpty()) {
            NbtCompat.putString(tag, "customName", customName);
        }
        if (hostBlockId != null && !hostBlockId.isEmpty()) {
            NbtCompat.putString(tag, "hostBlockId", hostBlockId);
        }
        return tag;
    }
}
