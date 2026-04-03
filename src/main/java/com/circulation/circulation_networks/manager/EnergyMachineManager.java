package com.circulation.circulation_networks.manager;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.api.EnergyAmount;
import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.api.IGrid;
import com.circulation.circulation_networks.api.node.IEnergySupplyNode;
import com.circulation.circulation_networks.api.node.IMachineNode;
import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.api.node.IHubNode;
import com.circulation.circulation_networks.events.BlockEntityLifeCycleEvent;
import com.circulation.circulation_networks.packets.NodeNetworkRendering;
import com.circulation.circulation_networks.packets.EnergyWarningRendering;
import com.circulation.circulation_networks.network.nodes.HubNode;
import com.circulation.circulation_networks.registry.RegistryEnergyHandler;
import com.circulation.circulation_networks.utils.Functions;
//? if <1.20 {
import com.github.bsideup.jabel.Desugar;
//?}
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
//~ mc_imports
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
//? if <1.20 {
import net.minecraft.entity.player.EntityPlayerMP;
//?} else {
/*import net.minecraft.server.level.ServerPlayer;
 *///?}

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.server.MinecraftServer;

public final class EnergyMachineManager {

    public static final EnergyMachineManager INSTANCE = new EnergyMachineManager();
    private static final int WARNING_SEND_INTERVAL_TICKS = 20;
    private static final int WARNING_STALE_TICKS = 200;
    private static final double WARNING_RENDER_DISTANCE_SQ = 48.0D * 48.0D;
    private final Int2ObjectMap<Long2ObjectMap<ReferenceSet<IEnergySupplyNode>>> scopeNode = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<Object2ObjectMap<IEnergySupplyNode, LongSet>> nodeScope = new Int2ObjectOpenHashMap<>();
    //~ if >=1.20 'TileEntity' -> 'BlockEntity' {
    private final Reference2ObjectMap<INode, Set<TileEntity>> gridMachineMap = new Reference2ObjectOpenHashMap<>();
    private final WeakHashMap<TileEntity, ReferenceSet<INode>> machineGridMap = new WeakHashMap<>();
    private final Reference2ObjectMap<IGrid, Interaction> interaction = new Reference2ObjectOpenHashMap<>();
    private final ReferenceSet<TileEntity> cache = new ReferenceOpenHashSet<>();
    private final Int2ObjectMap<Long2LongMap> lastWarningTicks = new Int2ObjectOpenHashMap<>();
    private long warningTickCounter;
    private long lastWarningCleanupTick;

    {
        scopeNode.defaultReturnValue(Long2ObjectMaps.emptyMap());
        nodeScope.defaultReturnValue(Object2ObjectMaps.emptyMap());
        gridMachineMap.defaultReturnValue(ReferenceSets.emptySet());
    }

    static void transferEnergy(ObjectCollection<IEnergyHandler> send, ObjectCollection<IEnergyHandler> receive, Status status, IGrid grid) {
        if (send.isEmpty() || receive.isEmpty()) return;
        HubNode.HubMetadata hubMetadata = getHubMetadata(grid);
        var si = send.iterator();
        while (si.hasNext()) {
            var sender = si.next();
            if (receive.isEmpty()) return;
            var ri = receive.iterator();
            EnergyAmount extractable = sender.canExtractValue(hubMetadata);
            if (extractable.isZero()) {
                si.remove();
                continue;
            }
            while (ri.hasNext()) {
                var receiver = ri.next();
                if (sender.canExtract(receiver, hubMetadata) && receiver.canReceive(sender, hubMetadata)) {
                    extractable = sender.canExtractValue(hubMetadata);
                    EnergyAmount receivable = receiver.canReceiveValue(hubMetadata);
                    if (receivable.isZero()) {
                        ri.remove();
                        continue;
                    }
                    try {
                        int compare = extractable.compareTo(receivable);
                        EnergyAmount transferLimit = EnergyAmount.obtain(extractable).min(receivable);
                        try {
                            EnergyAmount extracted = sender.extractEnergy(transferLimit, hubMetadata);
                            try {
                                EnergyAmount received = receiver.receiveEnergy(extracted, hubMetadata);
                                try {
                                    if (!received.isZero()) {
                                        status.interaction(received, grid);
                                    }
                                } finally {
                                    received.recycle();
                                }
                            } finally {
                                extracted.recycle();
                            }
                        } finally {
                            transferLimit.recycle();
                        }

                        if (compare >= 0 && receiver.getType(hubMetadata) != IEnergyHandler.EnergyType.STORAGE) {
                            receiver.recycle(hubMetadata);
                            ri.remove();
                        }
                        if (compare <= 0) {
                            sender.recycle(hubMetadata);
                            si.remove();
                            break;
                        }
                    } finally {
                        extractable.recycle();
                        receivable.recycle();
                    }
                }
            }
        }
    }
    //~}

    //? if <1.20 {
    private static MinecraftServer getServer() {
        return CirculationFlowNetworks.server;
    }

    private static boolean isClientWorld(World world) {
        return world.isRemote;
    }

    private static int getDimensionId(World world) {
        return world.provider.getDimension();
    }

    private static int getPlayerDimensionId(EntityPlayerMP player) {
        return player.dimension;
    }

    private static double getPlayerDistanceSq(EntityPlayerMP player, BlockPos pos) {
        return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 1.25D, pos.getZ() + 0.5D);
    }

    private static long getPackedPos(TileEntity blockEntity) {
        return blockEntity.getPos().toLong();
    }
    //?} else if <1.21 {
    /*private static MinecraftServer getServer() {
        return net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
    }

    private static boolean isClientWorld(Level world) {
        return world.isClientSide;
    }

    private static int getDimensionId(Level world) {
        return world.dimension().location().hashCode();
    }

    private static int getPlayerDimensionId(ServerPlayer player) {
        return player.level().dimension().location().hashCode();
    }

    private static double getPlayerDistanceSq(ServerPlayer player, BlockPos pos) {
        double dx = player.getX() - (pos.getX() + 0.5D);
        double dy = player.getY() - (pos.getY() + 1.25D);
        double dz = player.getZ() - (pos.getZ() + 0.5D);
        return dx * dx + dy * dy + dz * dz;
    }
    *///?} else {
    /*private static MinecraftServer getServer() {
        return net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
    }

    private static boolean isClientWorld(Level world) {
        return world.isClientSide;
    }

    private static int getDimensionId(Level world) {
        return world.dimension().location().hashCode();
    }

    private static int getPlayerDimensionId(ServerPlayer player) {
        return player.level().dimension().location().hashCode();
    }

    private static double getPlayerDistanceSq(ServerPlayer player, BlockPos pos) {
        double dx = player.getX() - (pos.getX() + 0.5D);
        double dy = player.getY() - (pos.getY() + 1.25D);
        double dz = player.getZ() - (pos.getZ() + 0.5D);
        return dx * dx + dy * dy + dz * dz;
    }
    *///?}

    //~ if >=1.20 '.fromLong(' -> '.of(' {
    private static BlockPos blockPosFromLong(long posLong) {
        return BlockPos.fromLong(posLong);
    }
    //~}

    //~ if >=1.20 'TileEntity' -> 'BlockEntity' {
    public WeakHashMap<TileEntity, ReferenceSet<INode>> getMachineGridMap() {
        return machineGridMap;
    }
    //~}

    public Reference2ObjectMap<IGrid, Interaction> getInteraction() {
        return interaction;
    }

    public void onBlockEntityValidate(BlockEntityLifeCycleEvent.Validate event) {
        if (isClientWorld(event.getWorld())) return;
        if (NetworkManager.INSTANCE.isInit()) {
            addMachine(event.getBlockEntity());
            var node = NetworkManager.INSTANCE.getNodeFromPos(event.getWorld(), event.getPos());
            if (node instanceof IMachineNode im) {
                addMachineNode(im, event.getBlockEntity());
            }
        } else cache.add(event.getBlockEntity());
    }

    public void onBlockEntityInvalidate(BlockEntityLifeCycleEvent.Invalidate event) {
        if (isClientWorld(event.getWorld())) return;
        removeMachine(event.getBlockEntity());
    }

    public void onServerTick() {
        var server = getServer();
        if (server == null || !NetworkManager.INSTANCE.isInit()) return;
        warningTickCounter++;
        interaction.values().forEach(Interaction::reset);
        var overrideManager = EnergyTypeOverrideManager.get();
        var gridMap = new Reference2ObjectOpenHashMap<IGrid, EnumMap<IEnergyHandler.EnergyType, ObjectSet<IEnergyHandler>>>();
        var receiveTargetsByGrid = new Reference2ObjectOpenHashMap<IGrid, Reference2ObjectMap<IEnergyHandler, WarningTarget>>();
        for (var entry : machineGridMap.entrySet()) {
            var te = entry.getKey();
            //~ if >=1.20 '.getWorld()' -> '.getLevel()' {
            //~ if >=1.20 '.isBlockLoaded(' -> '.isLoaded(' {
            //~ if >=1.20 '.getPos()' -> '.getBlockPos()' {
            var world = te.getWorld();
            var pos = te.getPos();
            if (!world.isBlockLoaded(pos)) continue;
            //~}
            //~}
            //~}
            if (CirculationShielderManager.INSTANCE.isBlockedByShielder(pos, world)) continue;
            var handler = IEnergyHandler.release(te, null);

            if (handler == null) {
                continue;
            }
            if (overrideManager != null) {
                var override = overrideManager.getOverride(getDimensionId(world), pos);
                boolean addedToAnyGrid = false;
                for (var node : entry.getValue()) {
                    var grid = node.getGrid();
                    if (grid == null) continue;

                    var type = handler.getType(getHubMetadata(grid));
                    if (override != null) {
                        type = override;
                    }
                    if (type == IEnergyHandler.EnergyType.INVALID) {
                        continue;
                    }

                    addedToAnyGrid = true;
                    gridMap.computeIfAbsent(grid, g -> new EnumMap<>(IEnergyHandler.EnergyType.class))
                           .computeIfAbsent(type, s -> new ObjectLinkedOpenHashSet<>())
                           .add(handler);
                    if (type == IEnergyHandler.EnergyType.RECEIVE) {
                        receiveTargetsByGrid.computeIfAbsent(grid, ignored -> new Reference2ObjectOpenHashMap<>())
                                            .putIfAbsent(handler, new WarningTarget(getDimensionId(world), getPackedPos(te)));
                    }
                }
                if (!addedToAnyGrid) {
                    handler.recycle(null);
                }
                continue;
            }
            boolean addedToAnyGrid = false;
            for (var node : entry.getValue()) {
                var grid = node.getGrid();
                if (grid == null) continue;

                var type = handler.getType(getHubMetadata(grid));
                if (type == IEnergyHandler.EnergyType.INVALID) {
                    continue;
                }

                addedToAnyGrid = true;
                gridMap.computeIfAbsent(grid, g -> new EnumMap<>(IEnergyHandler.EnergyType.class))
                       .computeIfAbsent(type, s -> new ObjectLinkedOpenHashSet<>())
                       .add(handler);
                if (type == IEnergyHandler.EnergyType.RECEIVE) {
                    receiveTargetsByGrid.computeIfAbsent(grid, ignored -> new Reference2ObjectOpenHashMap<>())
                                        .putIfAbsent(handler, new WarningTarget(getDimensionId(world), getPackedPos(te)));
                }
            }
            if (!addedToAnyGrid) {
                handler.recycle(null);
            }
        }

        ReferenceSet<IGrid> processedGrids = new ReferenceOpenHashSet<>();
        Int2ObjectMap<LongSet> warningPositions = new Int2ObjectOpenHashMap<>();

        for (var e : gridMap.entrySet()) {
            var grid = e.getKey();
            if (processedGrids.contains(grid)) continue;
            var hubNode = grid.getHubNode();
            if (hubNode != null && hubNode.isActive()) {
                var channelId = hubNode.getChannelId();
                if (channelId != null) {
                    var channelGrids = HubChannelManager.INSTANCE.getChannelGrids(channelId);
                    if (channelGrids != null && channelGrids.size() > 1) {
                        var mergedSend = new ObjectLinkedOpenHashSet<IEnergyHandler>();
                        var mergedStorage = new ObjectLinkedOpenHashSet<IEnergyHandler>();
                        var mergedReceive = new ObjectLinkedOpenHashSet<IEnergyHandler>();
                        var mergedReceiveTargets = new Reference2ObjectOpenHashMap<IEnergyHandler, WarningTarget>();
                        for (var cg : channelGrids) {
                            var handlers = gridMap.get(cg);
                            if (handlers != null) {
                                mergedSend.addAll(handlers.getOrDefault(IEnergyHandler.EnergyType.SEND, ObjectSets.emptySet()));
                                mergedStorage.addAll(handlers.getOrDefault(IEnergyHandler.EnergyType.STORAGE, ObjectSets.emptySet()));
                                mergedReceive.addAll(handlers.getOrDefault(IEnergyHandler.EnergyType.RECEIVE, ObjectSets.emptySet()));
                            }
                            var receiveTargets = receiveTargetsByGrid.get(cg);
                            if (receiveTargets != null && !receiveTargets.isEmpty()) {
                                mergedReceiveTargets.putAll(receiveTargets);
                            }
                            processedGrids.add(cg);
                        }
                        transferEnergy(mergedSend, mergedReceive, Status.INTERACTION, grid);
                        transferEnergy(mergedStorage, mergedReceive, Status.EXTRACT, grid);
                        collectWarningPositions(mergedReceive, mergedReceiveTargets, warningPositions, getHubMetadata(grid));
                        transferEnergy(mergedSend, mergedStorage, Status.RECEIVE, grid);
                        continue;
                    }
                }
            }

            processedGrids.add(grid);
            var handlers = e.getValue();
            var send = handlers.getOrDefault(IEnergyHandler.EnergyType.SEND, ObjectSets.emptySet());
            var storage = handlers.getOrDefault(IEnergyHandler.EnergyType.STORAGE, ObjectSets.emptySet());
            var receive = handlers.getOrDefault(IEnergyHandler.EnergyType.RECEIVE, ObjectSets.emptySet());
            var receiveTargets = receiveTargetsByGrid.get(grid);

            transferEnergy(send, receive, Status.INTERACTION, grid);
            transferEnergy(storage, receive, Status.EXTRACT, grid);
            collectWarningPositions(receive, receiveTargets, warningPositions, getHubMetadata(grid));
            transferEnergy(send, storage, Status.RECEIVE, grid);
        }

        sendWarningsToNearbyPlayers(server, warningPositions);
        cleanupStaleWarnings();

        ChargingManager.INSTANCE.onServerTick(server, gridMap);

        for (var value : gridMap.values()) {
            for (var handlers : value.values()) {
                for (var handler : handlers) {
                    handler.recycle(null);
                }
            }
        }
    }

    //~ if >=1.20 'TileEntity' -> 'BlockEntity' {
    public void addMachine(TileEntity blockEntity) {
        //~}
        if (!RegistryEnergyHandler.isEnergyTileEntity(blockEntity)) return;
        if (RegistryEnergyHandler.isBlack(blockEntity)) return;
        //~ if >=1.20 '.getPos()' -> '.getBlockPos()' {
        //~ if >=1.20 '.getWorld()' -> '.getLevel()' {
        var pos = blockEntity.getPos();
        long chunkCoord = Functions.mergeChunkCoords(pos);

        var dim = getDimensionId(blockEntity.getWorld());
        //~}
        //~}
        var map = scopeNode.get(dim);
        if (map == scopeNode.defaultReturnValue()) {
            scopeNode.put(dim, map = new Long2ObjectOpenHashMap<>());
            map.defaultReturnValue(ReferenceSets.emptySet());
        }
        ReferenceSet<IEnergySupplyNode> set = map.get(chunkCoord);
        if (!set.isEmpty()) {
            var s = machineGridMap.get(blockEntity);
            if (s == null) s = new ReferenceOpenHashSet<>();
            for (var node : set) {
                if (!node.supplyScopeCheck(pos)) continue;
                if (node.isBlacklisted(blockEntity)) continue;

                var set1 = gridMachineMap.get(node);
                if (set1 == gridMachineMap.defaultReturnValue()) {
                    gridMachineMap.put(node, set1 = Collections.newSetFromMap(new WeakHashMap<>()));
                }
                s.add(node);
                set1.add(blockEntity);

                var players = NodeNetworkRendering.getPlayers(node.getGrid());
                if (players != null && !players.isEmpty()) {
                    for (var player : players) {
                        CirculationFlowNetworks.sendToPlayer(new NodeNetworkRendering(player, blockEntity, node, NodeNetworkRendering.MACHINE_ADD), player);
                    }
                }
            }
            if (s.isEmpty()) return;
            machineGridMap.putIfAbsent(blockEntity, s);
        }
    }

    //~ if >=1.20 'TileEntity' -> 'BlockEntity' {
    public void removeMachine(TileEntity blockEntity) {
        //~}
        var set = machineGridMap.remove(blockEntity);
        if (set == null || set.isEmpty()) return;
        for (var node : set) {
            gridMachineMap.get(node).remove(blockEntity);

            var players = NodeNetworkRendering.getPlayers(node.getGrid());
            if (players != null && !players.isEmpty()) {
                for (var player : players) {
                    CirculationFlowNetworks.sendToPlayer(new NodeNetworkRendering(player, blockEntity, node, NodeNetworkRendering.MACHINE_REMOVE), player);
                }
            }
        }
    }

    //~ if >=1.20 'TileEntity' -> 'BlockEntity' {
    public void addMachineNode(IMachineNode iMachineNode, TileEntity blockEntity) {
        //~}
        var allConnected = new ReferenceOpenHashSet<INode>();
        for (INode candidate : NetworkManager.INSTANCE.getNodesCoveringPosition(iMachineNode.getWorld(), iMachineNode.getPos())) {
            if (candidate.linkScopeCheck(iMachineNode) != INode.LinkType.DISCONNECT) {
                allConnected.add(candidate);
            }
        }

        if (!allConnected.isEmpty()) {
            var s = machineGridMap.get(blockEntity);
            if (s == null) s = new ReferenceOpenHashSet<>();
            for (var node : allConnected) {
                var set1 = gridMachineMap.get(node);
                if (set1 == gridMachineMap.defaultReturnValue()) {
                    gridMachineMap.put(node, set1 = Collections.newSetFromMap(new WeakHashMap<>()));
                }
                s.add(node);
                set1.add(blockEntity);
            }
            if (s.isEmpty()) return;
            machineGridMap.putIfAbsent(blockEntity, s);
        }
    }

    public void addNode(INode node) {
        if (node instanceof IEnergySupplyNode energySupplyNode) {
            int nodeX = energySupplyNode.getPos().getX();
            int nodeZ = energySupplyNode.getPos().getZ();
            int range = (int) energySupplyNode.getEnergyScope();
            int minChunkX = (nodeX - range) >> 4;
            int maxChunkX = (nodeX + range) >> 4;
            int minChunkZ = (nodeZ - range) >> 4;
            int maxChunkZ = (nodeZ + range) >> 4;
            LongSet chunksCovered = new LongOpenHashSet();

            int dimId = getDimensionId(node.getWorld());

            Long2ObjectMap<ReferenceSet<IEnergySupplyNode>> map = scopeNode.get(dimId);
            if (map == scopeNode.defaultReturnValue()) {
                Long2ObjectMap<ReferenceSet<IEnergySupplyNode>> newMap = new Long2ObjectOpenHashMap<>();
                newMap.defaultReturnValue(ReferenceSets.emptySet());
                scopeNode.put(dimId, map = newMap);
            }

            for (int cx = minChunkX; cx <= maxChunkX; ++cx) {
                for (int cz = minChunkZ; cz <= maxChunkZ; ++cz) {
                    long chunkCoord = Functions.mergeChunkCoords(cx, cz);
                    chunksCovered.add(chunkCoord);

                    ReferenceSet<IEnergySupplyNode> set = map.get(chunkCoord);
                    if (set == map.defaultReturnValue()) {
                        map.put(chunkCoord, set = new ReferenceOpenHashSet<>());
                    }
                    set.add(energySupplyNode);

                    //? if <1.20 {
                    var chunk = node.getWorld().getChunkProvider().getLoadedChunk(cx, cz);
                    if (chunk == null || chunk.isEmpty()) {
                        continue;
                    }
                    var set2 = gridMachineMap.get(node);
                    for (var tileEntity : chunk.getTileEntityMap().values()) {
                        if (!energySupplyNode.supplyScopeCheck(tileEntity.getPos())) continue;
                        if (RegistryEnergyHandler.isBlack(tileEntity)) continue;
                        if (energySupplyNode.isBlacklisted(tileEntity)) continue;
                        if (RegistryEnergyHandler.isEnergyTileEntity(tileEntity)) {
                            if (set2 == gridMachineMap.defaultReturnValue()) {
                                gridMachineMap.put(energySupplyNode, set2 = Collections.newSetFromMap(new WeakHashMap<>()));
                            }
                            set2.add(tileEntity);

                            var set3 = machineGridMap.get(tileEntity);
                            if (set3 == null) {
                                machineGridMap.put(tileEntity, set3 = new ReferenceOpenHashSet<>());
                            }
                            set3.add(energySupplyNode);
                        }
                    }
                    //?} else {
                    /*var chunk = node.getWorld().getChunkSource().getChunkNow(cx, cz);
                    if (chunk == null) {
                        continue;
                    }
                    var set2 = gridMachineMap.get(node);
                    for (var blockEntity : chunk.getBlockEntities().values()) {
                        if (!energySupplyNode.supplyScopeCheck(blockEntity.getBlockPos())) continue;
                        if (RegistryEnergyHandler.isBlack(blockEntity)) continue;
                        if (energySupplyNode.isBlacklisted(blockEntity)) continue;
                        if (RegistryEnergyHandler.isEnergyTileEntity(blockEntity)) {
                            if (set2 == gridMachineMap.defaultReturnValue()) {
                                gridMachineMap.put(energySupplyNode, set2 = Collections.newSetFromMap(new WeakHashMap<>()));
                            }
                            set2.add(blockEntity);

                            var set3 = machineGridMap.get(blockEntity);
                            if (set3 == null) {
                                machineGridMap.put(blockEntity, set3 = new ReferenceOpenHashSet<>());
                            }
                            set3.add(energySupplyNode);
                        }
                    }
                    *///?}
                }
            }

            Object2ObjectMap<IEnergySupplyNode, LongSet> nodeScopeMap = nodeScope.get(dimId);
            if (nodeScopeMap == nodeScope.defaultReturnValue()) {
                nodeScope.put(dimId, nodeScopeMap = new Object2ObjectOpenHashMap<>());
            }
            nodeScopeMap.put(energySupplyNode, LongSets.unmodifiable(chunksCovered));
        }
    }

    void initGrid(Collection<NetworkManager.GridEntry> entries) {
        for (var entry : entries) {
            var dim = entry.dimId();
            if (entry.grid().getNodes().isEmpty()) continue;
            for (INode node : entry.grid().getNodes()) {
                if (!(node instanceof IEnergySupplyNode energySupplyNode)) continue;

                int nodeX = energySupplyNode.getPos().getX();
                int nodeZ = energySupplyNode.getPos().getZ();
                int range = (int) energySupplyNode.getEnergyScope();
                int minChunkX = (nodeX - range) >> 4, maxChunkX = (nodeX + range) >> 4;
                int minChunkZ = (nodeZ - range) >> 4, maxChunkZ = (nodeZ + range) >> 4;

                LongSet chunksCovered = new LongOpenHashSet();

                Long2ObjectMap<ReferenceSet<IEnergySupplyNode>> map = scopeNode.get(dim);
                if (map == scopeNode.defaultReturnValue()) {
                    Long2ObjectMap<ReferenceSet<IEnergySupplyNode>> newMap = new Long2ObjectOpenHashMap<>();
                    newMap.defaultReturnValue(ReferenceSets.emptySet());
                    scopeNode.put(dim, map = newMap);
                }

                for (int cx = minChunkX; cx <= maxChunkX; ++cx) {
                    for (int cz = minChunkZ; cz <= maxChunkZ; ++cz) {
                        long chunkCoord = Functions.mergeChunkCoords(cx, cz);
                        chunksCovered.add(chunkCoord);

                        ReferenceSet<IEnergySupplyNode> set = map.get(chunkCoord);
                        if (set == map.defaultReturnValue()) {
                            map.put(chunkCoord, set = new ReferenceOpenHashSet<>());
                        }
                        set.add(energySupplyNode);
                    }
                }

                Object2ObjectMap<IEnergySupplyNode, LongSet> nodeScopeMap = nodeScope.get(dim);
                if (nodeScopeMap == nodeScope.defaultReturnValue()) {
                    nodeScope.put(dim, nodeScopeMap = new Object2ObjectOpenHashMap<>());
                }
                nodeScopeMap.put(energySupplyNode, LongSets.unmodifiable(chunksCovered));
            }
        }

        for (var te : cache) {
            addMachine(te);
            //~ if >=1.20 '.getWorld()' -> '.getLevel()' {
            //~ if >=1.20 '.getPos()' -> '.getBlockPos()' {
            var node = NetworkManager.INSTANCE.getNodeFromPos(te.getWorld(), te.getPos());
            //~}
            //~}
            if (node instanceof IMachineNode im) {
                addMachineNode(im, te);
            }
        }
        cache.clear();
    }

    public void removeNode(INode node) {
        if (node instanceof IEnergySupplyNode removedNode) {
            var world = removedNode.getWorld();
            int dimId = getDimensionId(world);

            var nodeScopeMap = nodeScope.get(dimId);
            if (nodeScopeMap == nodeScope.defaultReturnValue()) return;

            LongSet coveredChunks = nodeScopeMap.remove(removedNode);
            if (coveredChunks == null || coveredChunks.isEmpty()) return;

            var scopeMap = scopeNode.get(dimId);
            if (scopeMap == scopeNode.defaultReturnValue()) return;

            for (long coveredChunk : coveredChunks) {
                var set = scopeMap.get(coveredChunk);
                if (set == scopeMap.defaultReturnValue()) {
                    continue;
                }
                if (set.size() == 1) scopeMap.remove(coveredChunk);
                else set.remove(removedNode);
            }

            var c = gridMachineMap.remove(removedNode);
            if (c == null || c.isEmpty()) return;
            for (var te : c) {
                var set = machineGridMap.get(te);
                if (set == null) {
                    continue;
                }
                if (set.size() == 1) machineGridMap.remove(te);
                else set.remove(removedNode);
            }
        }
    }

    public void onServerStop() {
        scopeNode.clear();
        nodeScope.clear();
        gridMachineMap.clear();
        machineGridMap.clear();
        interaction.clear();
        lastWarningTicks.clear();
        warningTickCounter = 0L;
        lastWarningCleanupTick = 0L;
    }

    //~ if >=1.20 '(World ' -> '(Level ' {
    public @Nonnull ReferenceSet<IEnergySupplyNode> getEnergyNodes(World world, BlockPos pos) {
        return getEnergyNodes(world, pos.getX() >> 4, pos.getZ() >> 4);
    }
    //~}

    //~ if >=1.20 '(World ' -> '(Level ' {
    public @Nonnull ReferenceSet<IEnergySupplyNode> getEnergyNodes(World world, int chunkX, int chunkZ) {
        var map = scopeNode.get(getDimensionId(world));
        return map.get(Functions.mergeChunkCoords(chunkX, chunkZ));
    }
    //~}
    //? if >=1.20 {
    /*private static long getPackedPos(BlockEntity blockEntity) {
        return blockEntity.getBlockPos().asLong();
    }
    *///?}

    //~ if >=1.20 'TileEntity' -> 'BlockEntity' {
    public @Nonnull Set<TileEntity> getMachinesSuppliedBy(IEnergySupplyNode node) {
        return gridMachineMap.getOrDefault(node, Collections.emptySet());
    }

    @Nullable
    private static HubNode.HubMetadata getHubMetadata(@Nullable IGrid grid) {
        if (grid == null) {
            return null;
        }
        IHubNode hubNode = grid.getHubNode();
        return hubNode != null ? hubNode.getHubData() : null;
    }

    private void collectWarningPositions(Set<IEnergyHandler> receiveHandlers,
                                         Reference2ObjectMap<IEnergyHandler, WarningTarget> receiveTargets,
                                         Int2ObjectMap<LongSet> warningPositions,
                                         @Nullable HubNode.HubMetadata hubMetadata) {
        if (receiveHandlers.isEmpty() || receiveTargets == null || receiveTargets.isEmpty()) {
            return;
        }
        for (var handler : receiveHandlers) {
            if (handler.canReceiveValue(hubMetadata).isZero()) {
                continue;
            }
            var target = receiveTargets.get(handler);
            if (target == null || !shouldSendWarning(target)) {
                continue;
            }
            warningPositions.computeIfAbsent(target.dimId, ignored -> new LongOpenHashSet()).add(target.posLong);
        }
    }

    private boolean shouldSendWarning(WarningTarget target) {
        Long2LongMap dimWarnings = lastWarningTicks.get(target.dimId);
        if (dimWarnings == null) {
            dimWarnings = new Long2LongOpenHashMap();
            dimWarnings.defaultReturnValue(Long.MIN_VALUE);
            lastWarningTicks.put(target.dimId, dimWarnings);
        }
        long lastTick = dimWarnings.get(target.posLong);
        if (lastTick != Long.MIN_VALUE && warningTickCounter - lastTick < WARNING_SEND_INTERVAL_TICKS) {
            return false;
        }
        dimWarnings.put(target.posLong, warningTickCounter);
        return true;
    }

    private void sendWarningsToNearbyPlayers(MinecraftServer server, Int2ObjectMap<LongSet> warningPositions) {
        if (warningPositions.isEmpty()) {
            return;
        }
        for (var player : server.getPlayerList().getPlayers()) {
            int dimId = getPlayerDimensionId(player);
            LongSet dimWarnings = warningPositions.get(dimId);
            if (dimWarnings == null || dimWarnings.isEmpty()) {
                continue;
            }
            LongSet visibleWarnings = new LongOpenHashSet();
            for (long posLong : dimWarnings) {
                BlockPos pos = blockPosFromLong(posLong);
                if (getPlayerDistanceSq(player, pos) <= WARNING_RENDER_DISTANCE_SQ) {
                    visibleWarnings.add(posLong);
                }
            }
            if (!visibleWarnings.isEmpty()) {
                CirculationFlowNetworks.sendToPlayer(new EnergyWarningRendering(dimId, visibleWarnings), player);
            }
        }
    }

    private void cleanupStaleWarnings() {
        if (warningTickCounter - lastWarningCleanupTick < WARNING_SEND_INTERVAL_TICKS) {
            return;
        }
        lastWarningCleanupTick = warningTickCounter;
        for (var dimIterator = lastWarningTicks.int2ObjectEntrySet().iterator(); dimIterator.hasNext(); ) {
            var dimEntry = dimIterator.next();
            Long2LongMap dimWarnings = dimEntry.getValue();
            dimWarnings.long2LongEntrySet().removeIf(warningEntry -> warningTickCounter - warningEntry.getLongValue() > WARNING_STALE_TICKS);
            if (dimWarnings.isEmpty()) {
                dimIterator.remove();
            }
        }
    }
    //~}

    enum Status {
        EXTRACT,
        INTERACTION,
        RECEIVE;

        private void interaction(EnergyAmount value, IGrid grid) {
            var i = EnergyMachineManager.INSTANCE.interaction.get(grid);
            if (i == null) {
                return;
            }
            switch (this) {
                case INTERACTION -> {
                    i.input.add(value);
                    i.output.add(value);
                }
                case EXTRACT -> i.output.add(value);
                case RECEIVE -> i.input.add(value);
            }
        }
    }

    //? if <1.20
    @Desugar
    private record WarningTarget(int dimId, long posLong) {
    }

    @SuppressWarnings("unused")
    public static class Interaction {
        private final EnergyAmount input = EnergyAmount.obtain(0L);
        private final EnergyAmount output = EnergyAmount.obtain(0L);

        public EnergyAmount getInput() {
            return input;
        }

        public EnergyAmount getOutput() {
            return output;
        }

        private void reset() {
            input.setZero();
            output.setZero();
        }
    }
}