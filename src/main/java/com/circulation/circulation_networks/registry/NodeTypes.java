package com.circulation.circulation_networks.registry;

import com.circulation.circulation_networks.api.NodeDeserializer;
import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.api.node.NodeType;
import com.circulation.circulation_networks.network.nodes.ChargingNode;
import com.circulation.circulation_networks.network.nodes.HubNode;
import com.circulation.circulation_networks.network.nodes.Node;
import com.circulation.circulation_networks.network.nodes.PortNode;
import com.circulation.circulation_networks.network.nodes.machine_node.ConsumerNode;
import com.circulation.circulation_networks.network.nodes.machine_node.GeneratorNode;
import com.circulation.circulation_networks.network.nodes.machine_node.StorageNode;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
//~ mc_imports
import net.minecraft.nbt.NBTTagCompound;
//? if <1.20 {
import net.minecraftforge.common.DimensionManager;
//?} else {
/*import net.minecraft.nbt.NBTTagCompound;
 *///?}
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public final class NodeTypes {

    private static final Object2ReferenceMap<String, NodeType<?>> TYPES_BY_ID = new Object2ReferenceOpenHashMap<>();
    private static final Object2ReferenceMap<String, NodeDeserializer> DESERIALIZERS_BY_TYPE_ID = new Object2ReferenceOpenHashMap<>();

    private NodeTypes() {
    }

    private static <N extends INode> NodeType<N> type(String id, Class<N> nodeClass, boolean allowsPocketNode, String fallbackVisualId, NodeDeserializer deserializer) {
        var nodeType = new SimpleNodeType<>(id, nodeClass, allowsPocketNode, fallbackVisualId);
        register(nodeType, deserializer);
        return nodeType;
    }

    public static final NodeType<HubNode> HUB = type("hub", HubNode.class, false, "circulation_networks:hub", HubNode::new);

    public static final NodeType<PortNode> PORT_NODE = type("port_node", PortNode.class, false, "circulation_networks:port_node", PortNode::new);

    public static final NodeType<ChargingNode> CHARGING_NODE = type("charging_node", ChargingNode.class, false, "circulation_networks:charging_node", ChargingNode::new);

    public static final NodeType<Node> RELAY_NODE = type("relay_node", Node.class, false, "circulation_networks:relay_node", Node::new);

    public static @Nullable NodeType<?> getById(@Nullable String id) {
        return id == null ? null : TYPES_BY_ID.get(id);
    }

    public static <N extends INode> @NotNull NodeType<N> register(@NotNull NodeType<N> nodeType, @NotNull NodeDeserializer deserializer) {
        String typeId = nodeType.id();
        NodeType<?> existingType = TYPES_BY_ID.get(typeId);
        if (existingType != null && existingType != nodeType) {
            throw new IllegalStateException("Duplicate node type id registration: " + typeId);
        }
        TYPES_BY_ID.put(typeId, nodeType);
        registerTypeDeserializer(typeId, deserializer);
        return nodeType;
    }

    public static final NodeType<GeneratorNode> GENERATOR = type("generator", GeneratorNode.class, false, "", GeneratorNode::new);

    //~ if >=1.20 'NBTTagCompound' -> 'CompoundTag' {
    //~ if >=1.20 '.hasKey(' -> '.contains(' {
    public static @Nullable INode deserialize(@Nullable NBTTagCompound tag) {
        if (tag == null) {
            return null;
        }
        if (!tag.hasKey("type")) {
            return null;
        }
        //? if <1.20 {
        if (!tag.hasKey("dim") || !isRegisteredDimension(tag.getInteger("dim"))) {
            return null;
        }
        //?} else {
        /*if (!tag.hasKey("dim") || !isRegisteredDimension(tag.getString("dim"))) {
            return null;
        }
        *///?}

        NodeDeserializer deserializer = DESERIALIZERS_BY_TYPE_ID.get(tag.getString("type"));
        return deserializer == null ? null : deserializer.apply(tag);
    }
    //~}
    //~}

    public static final NodeType<StorageNode> STORAGE = type("storage", StorageNode.class, false, "", StorageNode::new);

    private static void registerTypeDeserializer(String typeId, NodeDeserializer deserializer) {
        NodeDeserializer existing = DESERIALIZERS_BY_TYPE_ID.get(typeId);
        if (existing != null && existing != deserializer) {
            throw new IllegalStateException("Duplicate node type deserializer registration: " + typeId);
        }
        DESERIALIZERS_BY_TYPE_ID.put(typeId, deserializer);
    }

    public static final NodeType<ConsumerNode> CONSUMER = type("consumer", ConsumerNode.class, false, "", ConsumerNode::new);

    //? if <1.20 {
    private static boolean isRegisteredDimension(int dimId) {
        return DimensionManager.isDimensionRegistered(dimId);
    }
    //?} else if <1.21 {
    /*private static boolean isRegisteredDimension(String dimKey) {
        var server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return false;
        return server.getLevel(
            net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.DIMENSION,
                new net.minecraft.resources.ResourceLocation(dimKey)
            )
        ) != null;
    }
    *///?} else {
    /*private static boolean isRegisteredDimension(String dimKey) {
        var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return false;
        return server.getLevel(
            net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.DIMENSION,
                net.minecraft.resources.ResourceLocation.parse(dimKey)
            )
        ) != null;
    }
    *///?}

    //? if <1.20
    @com.github.bsideup.jabel.Desugar
    private record SimpleNodeType<N extends INode>(String id, Class<N> nodeClass, boolean allowsPocketNode,
                                                   String fallbackVisualId) implements NodeType<N> {

    }

}