package com.circulation.circulation_networks.manager;

import baubles.api.BaublesApi;
import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.api.IGrid;
import com.circulation.circulation_networks.api.hub.ChargingDefinition;
import com.circulation.circulation_networks.api.node.IChargingNode;
import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.utils.Functions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static com.circulation.circulation_networks.CirculationFlowNetworks.server;
import static com.circulation.circulation_networks.manager.EnergyMachineManager.transferEnergy;

public final class ChargingManager {

    public static final ChargingManager INSTANCE = new ChargingManager();
    private static final boolean loadBaubles = Loader.isModLoaded("baubles");

    private final Int2ObjectMap<Long2ObjectMap<ReferenceSet<IChargingNode>>> scopeNode = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<Object2ObjectMap<IChargingNode, LongSet>> nodeScope = new Int2ObjectOpenHashMap<>();

    @Optional.Method(modid = "baubles")
    private static void checkBaubles(Collection<IEnergyHandler> invs, EntityPlayer player) {
        var h = BaublesApi.getBaublesHandler(player);
        for (var i = 0; i < h.getSlots(); i++) {
            var stack = h.getStackInSlot(i);
            var handler = IEnergyHandler.release(stack);
            if (handler == null) continue;
            if (handler.canReceive()) {
                invs.add(handler);
                continue;
            }
            handler.recycle();
        }
    }

    public void onServerTick(Reference2ObjectOpenHashMap<IGrid, EnumMap<IEnergyHandler.EnergyType, Set<IEnergyHandler>>> machineMap) {
        var list = new ObjectArrayList<Reference2ObjectOpenHashMap<IGrid, List<IEnergyHandler>>>();
        var players = server.getPlayerList().getPlayers();
        for (var player : players) {
            var gridMap = new Reference2ObjectOpenHashMap<IGrid, List<IEnergyHandler>>();
            gridMap.defaultReturnValue(ObjectLists.emptyList());
            var map = scopeNode.get(player.dimension);
            if (map != null && !map.isEmpty()) {
                var pos = player.getPosition();
                var set = map.get(Functions.mergeChunkCoords(pos));
                if (set.isEmpty()) continue;
                var grids = new ObjectArrayList<IGrid>();
                for (var node : set) {
                    if (!node.chargingScopeCheck(pos)) continue;
                    grids.add(node.getGrid());
                }
                if (grids.isEmpty()) continue;
                //TODO:等待维度充能和跨维度充能的实现，当前先简单地只检测范围内节点
                EnumMap<ChargingDefinition, List<IEnergyHandler>> gridMapValue = new EnumMap<>(ChargingDefinition.class);
                var m = player.inventory.mainInventory.toArray(new ItemStack[0]);
                for (var grid : grids) {
                    EnumSet<ChargingDefinition> s;
                    if (grid.getHubNode() == null) {
                        s = EnumSet.allOf(ChargingDefinition.class);
                    } else {
                        s = EnumSet.noneOf(ChargingDefinition.class);
                        var c = grid.getHubNode().getChargingPreference(player.getUniqueID());
                        for (var value : ChargingDefinition.values()) {
                            if (c.getPreference(value)) {
                                s.add(value);
                            }
                        }
                    }
                    if (s.isEmpty()) continue;
                    var invs = new ObjectArrayList<IEnergyHandler>();
                    if (s.contains(ChargingDefinition.INVENTORY)) {
                        var i = gridMapValue.get(ChargingDefinition.INVENTORY);
                        if (i != null) {
                            invs.addAll(i);
                        } else {
                            var iiii = new ObjectArrayList<IEnergyHandler>();
                            for (var i1 = 9; i1 < m.length; i1++) {
                                var stack = m[i1];
                                var handler = IEnergyHandler.release(stack);
                                if (handler == null) continue;
                                if (handler.canReceive()) {
                                    iiii.add(handler);
                                    invs.add(handler);
                                    continue;
                                }
                                handler.recycle();
                            }
                            gridMapValue.put(ChargingDefinition.INVENTORY, iiii);
                        }
                    }
                    if (s.contains(ChargingDefinition.HOTBAR)) {
                        var i = gridMapValue.get(ChargingDefinition.HOTBAR);
                        if (i != null) {
                            invs.addAll(i);
                        } else {
                            var iiii = new ObjectArrayList<IEnergyHandler>();
                            for (var i1 = 0; i1 < 9; i1++) {
                                var stack = m[i1];
                                var handler = IEnergyHandler.release(stack);
                                if (handler == null) continue;
                                if (handler.canReceive()) {
                                    iiii.add(handler);
                                    invs.add(handler);
                                    continue;
                                }
                                handler.recycle();
                            }
                            gridMapValue.put(ChargingDefinition.HOTBAR, iiii);
                        }
                    } else {
                        if (s.contains(ChargingDefinition.MAIN_HAND)) {
                            var stack = player.getHeldItemMainhand();
                            var handler = IEnergyHandler.release(stack);
                            if (handler != null) {
                                if (handler.canReceive()) {
                                    gridMapValue.put(ChargingDefinition.HOTBAR, ObjectLists.singleton(handler));
                                    invs.add(handler);
                                } else {
                                    handler.recycle();
                                }
                            }
                        }
                        if (s.contains(ChargingDefinition.OFF_HAND)) {
                            var stack = player.getHeldItemOffhand();
                            var handler = IEnergyHandler.release(stack);
                            if (handler != null) {
                                if (handler.canReceive()) {
                                    gridMapValue.put(ChargingDefinition.OFF_HAND, ObjectLists.singleton(handler));
                                    invs.add(handler);
                                } else {
                                    handler.recycle();
                                }
                            }
                        }
                    }
                    if (s.contains(ChargingDefinition.ARMOR)) {
                        var i = gridMapValue.get(ChargingDefinition.ARMOR);
                        if (i != null) {
                            invs.addAll(i);
                        } else {
                            var iiii = new ObjectArrayList<IEnergyHandler>();
                            for (var i1 = 0; i1 < player.inventory.armorInventory.size(); i1++) {
                                var stack = player.inventory.armorInventory.get(i1);
                                var handler = IEnergyHandler.release(stack);
                                if (handler == null) continue;
                                if (handler.canReceive()) {
                                    iiii.add(handler);
                                    invs.add(handler);
                                    continue;
                                }
                                handler.recycle();
                            }
                            gridMapValue.put(ChargingDefinition.ARMOR, iiii);
                        }
                    }
                    if (loadBaubles && s.contains(ChargingDefinition.BAUBLES)) checkBaubles(invs, player);
                    if (invs.isEmpty()) continue;

                    gridMap.put(grid, invs);
                }
            }
            list.add(gridMap);
        }

        var chargingProcessed = new ReferenceOpenHashSet<IGrid>();

        for (var gridMap : list) {
            for (var entry : gridMap.entrySet()) {
                var grid = entry.getKey();
                if (chargingProcessed.contains(grid)) continue;
                var receive = entry.getValue();

                var hubNode = grid.getHubNode();
                if (hubNode != null) {
                    var channelId = hubNode.getChannelId();
                    if (channelId != null) {
                        var channelGrids = HubChannelManager.INSTANCE.getChannelGrids(channelId);
                        if (channelGrids != null && channelGrids.size() > 1) {
                            var mergedSend = new ObjectLinkedOpenHashSet<IEnergyHandler>();
                            var mergedStorage = new ObjectLinkedOpenHashSet<IEnergyHandler>();
                            for (var cg : channelGrids) {
                                var handlers = machineMap.get(cg);
                                if (handlers != null) {
                                    mergedSend.addAll(handlers.getOrDefault(IEnergyHandler.EnergyType.SEND, ObjectSets.emptySet()));
                                    mergedStorage.addAll(handlers.getOrDefault(IEnergyHandler.EnergyType.STORAGE, ObjectSets.emptySet()));
                                }
                                chargingProcessed.add(cg);
                            }
                            transferEnergy(mergedSend, receive, EnergyMachineManager.Status.EXTRACT, grid);
                            transferEnergy(mergedStorage, receive, EnergyMachineManager.Status.EXTRACT, grid);
                            continue;
                        }
                    }
                }

                chargingProcessed.add(grid);
                var m = machineMap.get(grid);
                if (m != null) {
                    var send = m.getOrDefault(IEnergyHandler.EnergyType.SEND, ObjectSets.emptySet());
                    transferEnergy(send, receive, EnergyMachineManager.Status.EXTRACT, grid);

                    var storage = m.getOrDefault(IEnergyHandler.EnergyType.STORAGE, ObjectSets.emptySet());
                    transferEnergy(storage, receive, EnergyMachineManager.Status.EXTRACT, grid);
                }
            }

            for (var value : gridMap.values()) {
                for (var handler : value) {
                    handler.recycle();
                }
            }
        }

    }

    public void addNode(INode node) {
        if (node instanceof IChargingNode chargingNode) {
            int nodeX = chargingNode.getPos().getX();
            int nodeZ = chargingNode.getPos().getZ();
            int range = (int) chargingNode.getChargingScope();
            int minChunkX = (nodeX - range) >> 4;
            int maxChunkX = (nodeX + range) >> 4;
            int minChunkZ = (nodeZ - range) >> 4;
            int maxChunkZ = (nodeZ + range) >> 4;
            LongSet chunksCovered = new LongOpenHashSet();

            int dimId = node.getWorld().provider.getDimension();

            Long2ObjectMap<ReferenceSet<IChargingNode>> map = scopeNode.get(dimId);
            if (map == scopeNode.defaultReturnValue()) {
                Long2ObjectMap<ReferenceSet<IChargingNode>> newMap = new Long2ObjectOpenHashMap<>();
                newMap.defaultReturnValue(ReferenceSets.emptySet());
                scopeNode.put(dimId, map = newMap);
            }

            for (int cx = minChunkX; cx <= maxChunkX; ++cx) {
                for (int cz = minChunkZ; cz <= maxChunkZ; ++cz) {
                    long chunkCoord = Functions.mergeChunkCoords(cx, cz);
                    chunksCovered.add(chunkCoord);

                    ReferenceSet<IChargingNode> set = map.get(chunkCoord);
                    if (set == map.defaultReturnValue()) {
                        map.put(chunkCoord, set = new ReferenceOpenHashSet<>());
                    }
                    set.add(chargingNode);
                }
            }

            Object2ObjectMap<IChargingNode, LongSet> nodeScopeMap = nodeScope.get(dimId);
            if (nodeScopeMap == nodeScope.defaultReturnValue()) {
                nodeScope.put(dimId, nodeScopeMap = new Object2ObjectOpenHashMap<>());
            }
            nodeScopeMap.put(chargingNode, LongSets.unmodifiable(chunksCovered));
        }
    }

    public void removeNode(INode node) {
        if (node instanceof IChargingNode chargingNode) {
            var world = chargingNode.getWorld();
            int dimId = world.provider.getDimension();

            var nodeScopeMap = nodeScope.get(dimId);
            if (nodeScopeMap == nodeScope.defaultReturnValue()) return;

            LongSet coveredChunks = nodeScopeMap.remove(chargingNode);
            if (coveredChunks == null || coveredChunks.isEmpty()) return;

            var scopeMap = scopeNode.get(dimId);
            if (scopeMap == scopeNode.defaultReturnValue()) return;

            for (long coveredChunk : coveredChunks) {
                var set = scopeMap.get(coveredChunk);
                if (set == scopeMap.defaultReturnValue()) {
                    continue;
                }
                if (set.size() == 1) scopeMap.remove(coveredChunk);
                else set.remove(chargingNode);
            }
        }
    }

    public void onServerStop() {
        scopeNode.clear();
        nodeScope.clear();
    }
}
