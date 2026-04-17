# Developer API

**English** | [中文](developer-api.md)

Circulation Flow Networks provides a complete API for integrating custom energy systems, extending node types, and
querying network state.

This branch is authored directly for `26.1`. This document therefore uses the current source names directly, such as
`BlockEntity`, `Level`, and `CompoundTag`.

**Package path**: `com.circulation.circulation_networks.api`

---

## Contents

- [Quick Start](#quick-start)
- [API Class Overview](#api-class-overview)
    - [Node Queries](#node-queries)
    - [Energy Supply Nodes](#energy-supply-nodes)
    - [Hub Channels](#hub-channels)
    - [Energy Type Checks](#energy-type-checks)
    - [Registration](#registration)
- [Node Interfaces](#node-interfaces)
    - [INode](#inode)
    - [IMachineNode](#imachinenode)
    - [IEnergySupplyNode](#ienergysupplynode)
    - [IHubNode](#ihubnode)
    - [IChargingNode](#ichargingnode)
    - [NodeType<N>](#nodetypen)
    - [NodeContext](#nodecontext)
    - [NodeDeserializer](#nodedeserializer)
    - [NodeCreator](#nodecreator)
- [Block Entity Interfaces](#block-entity-interfaces)
    - [INodeBlockEntity](#inodeblockentity)
    - [IMachineNodeBlockEntity](#imachinenodeblockentity)
    - [IHubNodeBlockEntity](#ihubnodeblockentity)
    - [ICirculationShielderBlockEntity](#icirculationshielderblockentity)
- [Energy System](#energy-system)
    - [IEnergyHandler](#ienergyhandler)
    - [IEnergyHandlerManager](#ienergyhandlermanager)
    - [EnergyAmount](#energyamount)
    - [EnergyAmounts](#energyamounts)
    - [ConstantEnergyAmount](#constantenergyamount)
- [Hub System](#hub-system)
    - [IHubPlugin](#ihubplugin)
    - [IHubChannel](#ihubchannel)
    - [HubPermissionLevel](#hubpermissionlevel)
    - [PermissionMode](#permissionmode)
    - [ChargingDefinition](#chargingdefinition)
    - [ChargingPreference](#chargingpreference)
    - [Snapshot Classes](#snapshot-classes)
- [Tick Interfaces](#tick-interfaces)
    - [ClientTickMachine](#clienttickmachine)
    - [ServerTickMachine](#servertickmachine)
- [Grid Interface](#grid-interface)
    - [IGrid](#igrid)

---

## Quick Start

```
import com.circulation.circulation_networks.api.API;
import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.api.IEnergyHandlerManager;
import com.circulation.circulation_networks.api.node.INode;

// Register a custom energy handler manager
// Registration must finish before RegistryEnergyHandler.lock()
API.registerEnergyHandler(myEnergyHandlerManager);

// Query the energy handler manager for a block entity
IEnergyHandlerManager mgr = API.getEnergyManager(tileEntity);

// Query the energy handler manager for an item stack
IEnergyHandlerManager itemMgr = API.getEnergyManager(itemStack);

// Check whether a block entity is a registered energy container
boolean isEnergy = API.isEnergyTileEntity(tileEntity);

// Get the node at a position
INode node = API.getNodeAt(world, pos);

// Get all active nodes
ReferenceSet<INode> allNodes = API.getAllNodes();
```

---

## API Class Overview

`com.circulation.circulation_networks.api.API` is the single public entry point. All methods are `static`, and their
names should not be renamed.

### Node Queries

| Signature                                                                                          | Description                                                                        |
|----------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------|
| `@Nullable INode getNodeAt(@Nonnull Level world, @Nonnull BlockPos pos)`                           | Returns the node at the given position. Works even if the chunk is not loaded.     |
| `@Nonnull ReferenceSet<INode> getAllNodes()`                                                       | Returns all currently active nodes.                                                |
| `@Nonnull Collection<IGrid> getAllGrids()`                                                         | Returns all currently available grids.                                             |
| `@Nonnull ReferenceSet<INode> getNodesCoveringPos(@Nonnull Level world, @Nonnull BlockPos pos)`    | Returns all nodes whose link range covers the chunk containing the given position. |
| `@Nonnull ReferenceSet<INode> getNodesCoveringChunk(@Nonnull Level world, int chunkX, int chunkZ)` | Returns all nodes whose link range covers the given chunk.                         |
| `@Nonnull ReferenceSet<INode> getNodesInChunk(@Nonnull Level world, int chunkX, int chunkZ)`       | Returns all active nodes located in the given chunk.                               |

### Energy Supply Nodes

| Signature                                                                                                          | Description                                                                                                                    |
|--------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| `@Nonnull ReferenceSet<IEnergySupplyNode> getEnergyNodes(@Nonnull Level world, @Nonnull BlockPos pos)`             | Returns all energy supply nodes whose energy range covers the chunk containing the given position.                             |
| `@Nonnull ReferenceSet<IEnergySupplyNode> getEnergyNodes(@Nonnull Level world, int chunkX, int chunkZ)`            | Returns all energy supply nodes whose energy range covers the given chunk.                                                     |
| `@Deprecated @Nonnull ReferenceSet<IEnergySupplyNode> getEnergyNodes(@Nonnull Level world, @Nonnull ChunkPos pos)` | Deprecated. Use the chunk-coordinate or `BlockPos` overload instead.                                                           |
| `@Nonnull Set<BlockEntity> getMachinesSuppliedBy(@Nonnull IEnergySupplyNode node)`                                 | Returns all machines currently supplied by the given node. The result may include entities that also implement `IMachineNode`. |

### Hub Channels

| Signature                                                               | Description                                                                                                                  |
|-------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------|
| `@Nonnull ReferenceSet<IGrid> getChannelGrids(@Nonnull UUID channelId)` | Returns all grids associated with the given hub channel UUID. Hubs share energy across grids that use the same channel UUID. |

### Energy Type Checks

| Signature                                                                           | Description                                                                                                                                                   |
|-------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `boolean isEnergyBlacklisted(@Nonnull BlockEntity blockEntity)`                     | Checks whether a block entity is on the global energy blacklist. Blacklisted entities are never recognized as energy containers.                              |
| `boolean isSupplyBlacklisted(@Nonnull BlockEntity blockEntity)`                     | Checks whether a block entity is on the supply-node blacklist. Blacklisted entities can only be connected by specialized nodes that override `isBlacklisted`. |
| `boolean isEnergyItem(@Nonnull ItemStack stack)`                                    | Checks whether an item stack is handled as an energy item by a registered manager.                                                                            |
| `boolean isEnergyTileEntity(@Nonnull BlockEntity blockEntity)`                      | Checks whether a block entity is handled as an energy container by a registered manager.                                                                      |
| `@Nullable IEnergyHandlerManager getEnergyManager(@Nonnull BlockEntity blockEntity)` | Returns the matching energy handler manager for a block entity, or `null` if none applies.                                                                    |
| `@Nullable IEnergyHandlerManager getEnergyManager(@Nonnull ItemStack stack)`        | Returns the matching energy handler manager for an item stack, or `null` if none applies.                                                                     |

### Registration

| Signature                                                                                                                               | Description                                                                          |
|-----------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| `void registerEnergyHandler(@Nonnull IEnergyHandlerManager manager)`                                                                    | Registers a custom energy handler manager. Registration must finish before `RegistryEnergyHandler.lock()`. |
| `void registerNodeType(@Nonnull NodeType<? extends INode> nodeType, @Nonnull NodeDeserializer function, @Nullable NodeCreator creator)` | Registers a custom node type together with its NBT deserializer and runtime creator. Node types that can be created in the world should provide a `NodeCreator`. |
| `void registerPocketNodeItem(@Nonnull NodeType<? extends INode> nodeType, @Nonnull Item item)`                                          | Registers the mapping from a node type to its pocket-node item. Only types that allow pocket-node form should register this mapping.                         |

---

## Node Interfaces

### INode

`com.circulation.circulation_networks.api.node.INode`

The base interface for all nodes. It defines node position, world, link range, neighbor management, and grid ownership.

| Method                            | Return Type            | Description                                                   |
|-----------------------------------|------------------------|---------------------------------------------------------------|
| `getPos()`                        | `@Nonnull BlockPos`    | The node's block position.                                    |
| `getVec3d()`                      | `@Nonnull Vec3d`       | The node's precise position vector.                           |
| `getWorld()`                      | `@Nonnull Level`       | The world the node belongs to.                                |
| `getDimensionId()`                | `int`                  | Default implementation: returns the dimension ID.             |
| `getSerializedDimensionKey()`     | `@Nonnull String`      | Default implementation: returns the serialized dimension key. |
| `getNodeType()`                   | `@Nonnull NodeType<?>` | The node type identifier.                                     |
| `getVisualId()`                   | `@Nonnull String`      | The visual identifier, usually a registry ID.                 |
| `serialize()`                     | `CompoundTag`          | Serializes the node to NBT.                                   |
| `isActive()`                      | `boolean`              | Whether the node is active.                                   |
| `setActive(boolean)`              | `void`                 | Sets the active state.                                        |
| `getLinkScope()`                  | `double`               | The node's link range in blocks.                              |
| `getLinkScopeSq()`                | `double`               | The squared link range used for distance checks.              |
| `getNeighbors()`                  | `ReferenceSet<INode>`  | The currently linked neighbor nodes.                          |
| `addNeighbor(INode)`              | `void`                 | Adds a neighbor link.                                         |
| `removeNeighbor(INode)`           | `void`                 | Removes a neighbor link.                                      |
| `clearNeighbors()`                | `void`                 | Clears all neighbor links.                                    |
| `getGrid()`                       | `IGrid`                | The grid this node belongs to.                                |
| `setGrid(IGrid)`                  | `void`                 | Sets the grid ownership.                                      |
| `getCustomName()`                 | `@Nullable String`     | The node's custom name.                                       |
| `setCustomName(@Nullable String)` | `void`                 | Sets the custom name.                                         |
| `distanceSq(INode)`               | `double`               | Squared distance to another node.                             |
| `distanceSq(BlockPos)`            | `double`               | Squared distance to a block position.                         |
| `distanceSq(Vec3d)`               | `double`               | Squared distance to a vector position.                        |
| `linkScopeCheck(INode)`           | `LinkType`             | Evaluates the link relationship between two nodes.            |

**Internal enum `LinkType`**:

| Value        | Description                       |
|--------------|-----------------------------------|
| `DOUBLY`     | Both sides can connect.           |
| `A_TO_B`     | Only A can reach B.               |
| `B_TO_A`     | Only B can reach A.               |
| `DISCONNECT` | Neither side can reach the other. |

---

### IMachineNode

`com.circulation.circulation_networks.api.node.IMachineNode extends IEnergySupplyNode`

A machine node represents a node whose own block position is itself an energy-machine endpoint. It inherits the supply-node contract and expects the node's own block entity to expose the energy handler directly.

| Method      | Return Type                 | Description                                               |
|-------------|-----------------------------|-----------------------------------------------------------|
| `getType()` | `IEnergyHandler.EnergyType` | The node's energy type (`SEND`, `RECEIVE`, or `STORAGE`). |

These types should usually reject pocket-node form.

It inherits all methods from `IEnergySupplyNode` and `INode`.

---

### IEnergySupplyNode

`com.circulation.circulation_networks.api.node.IEnergySupplyNode extends INode`

Marks a node that can interact with nearby energy devices inside its energy range.

| Method                       | Return Type | Description                                                                         |
|------------------------------|-------------|-------------------------------------------------------------------------------------|
| `getEnergyScope()`           | `double`    | The energy detection range in blocks.                                               |
| `getEnergyScopeSq()`         | `double`    | The squared energy detection range.                                                 |
| `supplyScopeCheck(BlockPos)` | `boolean`   | Default implementation: checks whether a position is inside the energy range.       |
| `isBlacklisted(BlockEntity)` | `boolean`   | Default implementation: checks whether the block entity is on the supply blacklist. |

---

### IHubNode

`com.circulation.circulation_networks.api.node.IHubNode extends IEnergySupplyNode, IChargingNode`

The hub node interface. A network can have only one hub. Hubs provide both energy supply and player charging
capabilities.

> **Unique internal implementation**: this interface has only the internal `HubNode` implementation class. External mods
> should not implement it; use it for querying and interaction only.

| Method                                                | Return Type                     | Description                                                                     |
|-------------------------------------------------------|---------------------------------|---------------------------------------------------------------------------------|
| `getPermissionMode()`                                 | `PermissionMode`                | The permission mode (`PUBLIC`, `TEAM`, or `PRIVATE`).                           |
| `setPermissionMode(PermissionMode)`                   | `void`                          | Sets the permission mode.                                                       |
| `getPlugins()`                                        | `IItemHandler`                  | Returns the plugin inventory.                                                   |
| `getHubData()`                                        | `HubMetadata`                   | Returns the hub metadata container.                                             |
| `hasPluginCapability(HubPluginCapability<?>)`         | `boolean`                       | Default implementation: checks whether the hub has the given plugin capability. |
| `getPluginCapabilityData(HubPluginCapability<T>)`     | `T`                             | Default implementation: returns the plugin capability data.                     |
| `getChannelId()`                                      | `@Nonnull UUID`                 | The channel UUID.                                                               |
| `setChannelId(@Nonnull UUID)`                         | `void`                          | Sets the channel UUID.                                                          |
| `getChannelName()`                                    | `@Nonnull String`               | The channel name.                                                               |
| `setChannelName(@Nonnull String)`                     | `void`                          | Sets the channel name.                                                          |
| `getChargingPreference(UUID)`                         | `@Nonnull ChargingPreference`   | Returns the charging preference for a player.                                   |
| `setChargingPreference(UUID, ChargingPreference)`     | `void`                          | Sets the charging preference for a player.                                      |
| `getChargingState(UUID, ChargingDefinition)`          | `boolean`                       | Returns whether a slot category is enabled for charging.                        |
| `setChargingState(UUID, ChargingDefinition, boolean)` | `void`                          | Sets whether a slot category is enabled for charging.                           |
| `getOwner()`                                          | `@Nullable UUID`                | The hub owner.                                                                  |
| `setOwner(@Nullable UUID)`                            | `void`                          | Sets the owner.                                                                 |
| `getExplicitPermission(UUID)`                         | `@Nullable HubPermissionLevel`  | Returns the explicit permission level for a player.                             |
| `getExplicitPermissions()`                            | `Map<UUID, HubPermissionLevel>` | Returns all explicit permissions.                                               |
| `setExplicitPermission(UUID, HubPermissionLevel)`     | `void`                          | Sets an explicit permission for a player.                                       |
| `removeExplicitPermission(UUID)`                      | `void`                          | Removes an explicit permission.                                                 |
| `getPermissionLevel(UUID)`                            | `HubPermissionLevel`            | Returns the final permission level for a player.                                |
| `canEditPermissions(UUID)`                            | `boolean`                       | Whether the player can edit permissions.                                        |
| `getPlayerPreferences()`                              | `Map<UUID, ChargingPreference>` | Returns all local player charging preferences (used when no channel is bound).  |

---

### IChargingNode

`com.circulation.circulation_networks.api.node.IChargingNode extends INode`

Marks a node that can charge player items in range.

| Method                         | Return Type | Description                                                                                  |
|--------------------------------|-------------|----------------------------------------------------------------------------------------------|
| `getChargingScope()`           | `double`    | The charging range in blocks.                                                                |
| `getChargingScopeSq()`         | `double`    | Default implementation: the squared charging range. Implementations should cache this value. |
| `chargingScopeCheck(BlockPos)` | `boolean`   | Default implementation: checks whether a position is inside the charging range.              |

---

### NodeType<N>

`com.circulation.circulation_networks.api.node.NodeType<N extends INode>`

Node type identifier interface used to register and distinguish node types.

| Method                  | Return Type         | Description                                                           |
|-------------------------|---------------------|-----------------------------------------------------------------------|
| `id()`                  | `@NotNull String`   | The unique node type identifier.                                      |
| `nodeClass()`           | `@NotNull Class<N>` | The node class.                                                       |
| `allowsPocketNode()`    | `boolean`           | Whether this type may exist in pocket-node form. Types such as `IMachineNode` should usually return `false`. |
| `fallbackVisualId()`    | `@NotNull String`   | The visual identifier used for pocket-node restore, client fallback display, and item fallback paths.        |
| `getId()`               | `@NotNull String`   | Default implementation: same as `id()`.                               |
| `getNodeClass()`        | `@NotNull Class<N>` | Default implementation: same as `nodeClass()`.                        |
| `getFallbackVisualId()` | `@NotNull String`   | Default implementation: same as `fallbackVisualId()`.                 |
| `matches(INode)`        | `boolean`           | Default implementation: checks whether the node belongs to this type. |
| `cast(INode)`           | `@NotNull N`        | Default implementation: casts the node to this type.                  |

### NodeContext

`com.circulation.circulation_networks.api.node.NodeContext`

Node creation context. Wraps the world, position, default name, and visual identifier needed to create a node.

| Method                                | Return Type          | Description                                                          |
|---------------------------------------|----------------------|----------------------------------------------------------------------|
| `fromWorld(Level, BlockPos)`          | `static NodeContext` | Resolves the default name and visual ID from the world and position. |
| `of(Level, BlockPos, String, String)` | `static NodeContext` | Creates a context with all fields provided manually.                 |
| `getWorld()`                          | `@NotNull Level`     | Returns the world.                                                   |
| `getPos()`                            | `@NotNull BlockPos`  | Returns the position.                                                |
| `getDefaultName()`                    | `@NotNull String`    | Returns the default name, usually the block's localized name.        |
| `getVisualId()`                       | `@NotNull String`    | Returns the visual identifier, usually the block registry ID.        |

---

### NodeDeserializer

`com.circulation.circulation_networks.api.NodeDeserializer`

A functional interface extending `Function<CompoundTag, INode>` used to deserialize nodes from NBT.

Register it through `API.registerNodeType()`.

---

### NodeCreator

`com.circulation.circulation_networks.api.NodeCreator`

A functional interface extending `Function<NodeContext, INode>` used to create new node instances at runtime, such as block placement, block-entity binding, or pocket-node restore.

Register it through `API.registerNodeType()`. Node types that can be created or restored in the world should provide it.

---

## Block Entity Interfaces

### INodeBlockEntity

`com.circulation.circulation_networks.api.INodeBlockEntity`

The base interface for block entities that are linked to a node.

| Method             | Return Type | Description                                                               |
|--------------------|-------------|---------------------------------------------------------------------------|
| `getNode()`        | `INode`     | Returns the associated node.                                              |
| `getNodePos()`     | `BlockPos`  | Returns the node position.                                                |
| `getNodeWorld()`   | `Level`     | Returns the world containing the node.                                    |
| `nodeValidate()`   | `void`      | Lifecycle callback when the node block entity is validated or loaded.     |
| `nodeInvalidate()` | `void`      | Lifecycle callback when the node block entity is invalidated or unloaded. |
| `syncNodeAfterNetworkInit()` | `void` | Synchronizes node state after the network manager finishes initialization. |

---

### IMachineNodeBlockEntity

`com.circulation.circulation_networks.api.IMachineNodeBlockEntity extends INodeBlockEntity`

Block entity interface for machine nodes whose own block entity is the energy-machine endpoint represented by `IMachineNode`.

| Method               | Return Type      | Description                                                                                                 |
|----------------------|------------------|-------------------------------------------------------------------------------------------------------------|
| `getNode()`          | `IMachineNode`   | Returns the associated machine node with a more specific return type.                                       |
| `getEnergyHandler()` | `IEnergyHandler` | Returns the energy handler held by this block entity; implementations must override `recycle()` as a no-op. |

---

### IHubNodeBlockEntity

`com.circulation.circulation_networks.api.IHubNodeBlockEntity extends INodeBlockEntity`

Block entity interface for hub nodes.

| Method         | Return Type    | Description                                                       |
|----------------|----------------|-------------------------------------------------------------------|
| `getNode()`    | `IHubNode`     | Returns the associated hub node with a more specific return type. |
| `getPlugins()` | `IItemHandler` | Returns the hub's plugin inventory.                               |

---

### ICirculationShielderBlockEntity

`com.circulation.circulation_networks.api.ICirculationShielderBlockEntity`

Block entity interface for the Circulation Shielder. The shielder prevents nodes inside its range from linking
automatically.

| Method                 | Return Type | Description                                               |
|------------------------|-------------|-----------------------------------------------------------|
| `checkScope(BlockPos)` | `boolean`   | Checks whether a position is inside the shielder's range. |
| `isActive()`           | `boolean`   | Whether the shielder is active.                           |
| `getScope()`           | `int`       | The shielder's range in blocks.                           |
| `isShowingRange()`     | `boolean`   | Whether range visualization is enabled.                   |
| `getPos()`             | `BlockPos`  | The shielder's position.                                  |

---

## Energy System

### IEnergyHandler

`com.circulation.circulation_networks.api.IEnergyHandler`

An energy handler that wraps a specific energy system such as FE or EU. Instances are pooled to reduce GC pressure.

**Lifecycle methods**:

| Method                             | Return Type                       | Description                                                                         |
|------------------------------------|-----------------------------------|-------------------------------------------------------------------------------------|
| `release(BlockEntity, HubMetadata)` | `static @Nullable IEnergyHandler` | Obtains a pooled handler for a block entity, or creates one if the pool is empty.   |
| `release(ItemStack, HubMetadata)`  | `static @Nullable IEnergyHandler` | Obtains a pooled handler for an item stack, or creates one if the pool is empty.    |
| `init(BlockEntity, HubMetadata)`   | `IEnergyHandler`                  | Initializes the handler from a block entity and returns `this`.                     |
| `init(ItemStack, HubMetadata)`     | `IEnergyHandler`                  | Initializes the handler from an item stack and returns `this`.                      |
| `clear()`                          | `void`                            | Clears internal state only. This lifecycle reset no longer depends on hub metadata. |
| `recycle()`                        | `void`                            | Calls `clear()` and returns the handler to the pool.                                |

**Energy operations**:

| Method                                     | Return Type    | Description                                                                 |
|--------------------------------------------|----------------|-----------------------------------------------------------------------------|
| `receiveEnergy(EnergyAmount, HubMetadata)` | `EnergyAmount` | Injects energy and returns the amount that was actually accepted.           |
| `extractEnergy(EnergyAmount, HubMetadata)` | `EnergyAmount` | Extracts energy and returns the amount that was actually extracted.         |
| `canExtractValue(HubMetadata)`             | `EnergyAmount` | Returns the currently extractable amount.                                   |
| `canReceiveValue(HubMetadata)`             | `EnergyAmount` | Returns the currently receivable amount.                                    |
| `canExtract(IEnergyHandler, HubMetadata)`  | `boolean`      | Whether energy can be extracted from this handler for compatibility checks. |
| `canReceive(IEnergyHandler, HubMetadata)`  | `boolean`      | Whether energy can be injected into this handler for compatibility checks.  |
| `getType(HubMetadata)`                     | `EnergyType`   | The handler's energy type.                                                  |

**Internal enum `EnergyType`**:

| Value     | Description            |
|-----------|------------------------|
| `SEND`    | Output only.           |
| `RECEIVE` | Input only.            |
| `STORAGE` | Bidirectional storage. |
| `INVALID` | Invalid state.         |

---

### IEnergyHandlerManager

`com.circulation.circulation_networks.api.IEnergyHandlerManager implements Comparable<IEnergyHandlerManager>`

Energy handler manager interface. Each energy system must implement this interface and register itself through
`API.registerEnergyHandler()`. Managers are ordered by priority.

| Method                     | Return Type                       | Description                                            |
|----------------------------|-----------------------------------|--------------------------------------------------------|
| `isAvailable(BlockEntity)` | `boolean`                         | Whether a block entity is handled by this manager.     |
| `isAvailable(ItemStack)`   | `boolean`                         | Whether an item stack is handled by this manager.      |
| `getEnergyHandlerClass()`  | `Class<? extends IEnergyHandler>` | Returns the associated handler implementation class.   |
| `getPriority()`            | `int`                             | Manager priority. Lower values are checked first.      |
| `newBlockEntityInstance()` | `IEnergyHandler`                  | Creates a fresh handler instance for block-entity use. |
| `newItemInstance()`        | `IEnergyHandler`                  | Creates a fresh handler instance for item use.         |
| `getUnit()`                | `String`                          | Default: `"FE"`. Returns the energy unit name.         |
| `getMultiplying()`         | `double`                          | Default: `1`. Returns the unit multiplier.             |

**Custom energy system example**:

```
public class MyEnergyManager implements IEnergyHandlerManager {

    @Override
    public boolean isAvailable(BlockEntity blockEntity) {
        return MyEnergyCompat.isSupported(blockEntity);
    }

    @Override
    public boolean isAvailable(ItemStack stack) {
        return stack.hasCapability(MY_ENERGY_CAP, null);
    }

    @Override
    public Class<? extends IEnergyHandler> getEnergyHandlerClass() {
        return MyEnergyHandler.class;
    }

    @Override
    public int getPriority() {
        return 100; // lower number = higher priority
    }

    @Override
    public IEnergyHandler newBlockEntityInstance() {
        return new MyEnergyHandler();
    }

    @Override
    public IEnergyHandler newItemInstance() {
        return new MyEnergyHandler();
    }

    @Override
    public String getUnit() {
        return "MJ";
    }
}

// Register
API.registerEnergyHandler(new MyEnergyManager());
```

---

### EnergyAmount

`com.circulation.circulation_networks.api.EnergyAmount`

A mutable energy value object. Internally it uses `long` and automatically upgrades to `BigInteger` on overflow. It is
pooled to reduce GC pressure and should be returned with `recycle()` after use.

**Obtaining instances**:

| Method                 | Return Type           | Description                                   |
|------------------------|-----------------------|-----------------------------------------------|
| `obtain(long)`         | `static EnergyAmount` | Obtains an instance backed by a `long`.       |
| `obtain(BigInteger)`   | `static EnergyAmount` | Obtains an instance backed by a `BigInteger`. |
| `obtain(String)`       | `static EnergyAmount` | Parses from a string value.                   |
| `obtain(EnergyAmount)` | `static EnergyAmount` | Copies the value from another instance.       |

**Arithmetic operations** (mutate in place and return `this`):

| Method          | Overloads                          | Description    |
|-----------------|------------------------------------|----------------|
| `add(...)`      | `long` / `EnergyAmount` / `double` | Addition       |
| `subtract(...)` | `long` / `EnergyAmount` / `double` | Subtraction    |
| `multiply(...)` | `long` / `EnergyAmount` / `double` | Multiplication |
| `divide(...)`   | `long` / `EnergyAmount` / `double` | Division       |

**Comparison methods**:

| Method                            | Description                              |
|-----------------------------------|------------------------------------------|
| `compareTo(long)`                 | Compares against a `long` value.         |
| `compareTo(EnergyAmount)`         | Compares against another `EnergyAmount`. |
| `min(EnergyAmount, EnergyAmount)` | Returns the smaller value (static).      |
| `max(EnergyAmount, EnergyAmount)` | Returns the larger value (static).       |

**State checks**:

| Method            | Return Type | Description                                         |
|-------------------|-------------|-----------------------------------------------------|
| `isZero()`        | `boolean`   | Whether the value is zero.                          |
| `isPositive()`    | `boolean`   | Whether the value is positive.                      |
| `isNegative()`    | `boolean`   | Whether the value is negative.                      |
| `fitsLong()`      | `boolean`   | Whether the value fits into a `long`.               |
| `isInitialized()` | `boolean`   | Whether the amount is initialized.                  |
| `isBig()`         | `boolean`   | Whether the amount is currently using `BigInteger`. |

**Conversions**:

| Method            | Return Type  | Description                                                                    |
|-------------------|--------------|--------------------------------------------------------------------------------|
| `intValue()`      | `int`        | Converts to `int` and may truncate.                                            |
| `longValue()`     | `long`       | Converts to `long` and may truncate.                                           |
| `floatValue()`    | `float`      | Converts to `float`.                                                           |
| `doubleValue()`   | `double`     | Converts to `double`.                                                          |
| `asBigInteger()`  | `BigInteger` | Converts to `BigInteger`.                                                      |
| `asLongExact()`   | `long`       | Converts exactly to `long`, throwing on overflow.                              |
| `asLongClamped()` | `long`       | Converts to `long` and clamps overflow to `Long.MAX_VALUE` / `Long.MIN_VALUE`. |

**Lifecycle**:

| Method      | Description                       |
|-------------|-----------------------------------|
| `recycle()` | Returns the instance to the pool. |
| `clear()`   | Clears internal state.            |
| `setZero()` | Resets the value to zero.         |

> ⚠️ **Note**: `EnergyAmount` is mutable. Do not use it after `recycle()`. For shared immutable values, use
`EnergyAmounts` or `ConstantEnergyAmount`.

---

### EnergyAmounts

`com.circulation.circulation_networks.api.EnergyAmounts`

Common immutable `EnergyAmount` constants, all backed by `ConstantEnergyAmount`.

| Constant   | Value               | Description        |
|------------|---------------------|--------------------|
| `ZERO`     | `0`                 | Zero               |
| `ONE`      | `1`                 | One                |
| `INT_MIN`  | `Integer.MIN_VALUE` | Minimum int value  |
| `INT_MAX`  | `Integer.MAX_VALUE` | Maximum int value  |
| `LONG_MAX` | `Long.MAX_VALUE`    | Maximum long value |
| `LONG_MIN` | `Long.MIN_VALUE`    | Minimum long value |

---

### ConstantEnergyAmount

`com.circulation.circulation_networks.api.ConstantEnergyAmount extends EnergyAmount`

An immutable `EnergyAmount` subclass. All mutation methods (`add`, `subtract`, `multiply`, `divide`, `clear`,
`setZero`, etc.) throw `UnsupportedOperationException`. Use it for safely shared energy constants.

---

## Hub System

### IHubPlugin

`com.circulation.circulation_networks.api.hub.IHubPlugin`

Hub plugin interface. Items implement this interface to become plugins that can be inserted into hub plugin slots.

| Method            | Return Type              | Description                                               |
|-------------------|--------------------------|-----------------------------------------------------------|
| `getCapability()` | `HubPluginCapability<?>` | Returns the capability identifier provided by the plugin. |

`HubPluginCapability<T>` is an abstract class in `com.circulation.circulation_networks.network.hub` that defines how
plugin data is created and saved:

| Method                                             | Description                                      |
|----------------------------------------------------|--------------------------------------------------|
| `abstract T newPluginData(ItemStack plugin)`       | Creates a new data instance for the plugin item. |
| `abstract void saveData(T data, ItemStack plugin)` | Saves data back into the plugin item.            |

---

### IHubChannel

`com.circulation.circulation_networks.api.hub.IHubChannel`

Hub channel interface for cross-grid channel connections and permission management.

> **Unique internal implementation**: this interface has only the internal `HubChannel` implementation class. External
> mods should not implement it; use it for querying and interaction only.

| Method                                            | Return Type                     | Description                              |
|---------------------------------------------------|---------------------------------|------------------------------------------|
| `getChannelId()`                                  | `UUID`                          | The unique channel identifier.           |
| `getGrids()`                                      | `ReferenceSet<IGrid>`           | All grids in the channel.                |
| `getName()`                                       | `String`                        | The channel name.                        |
| `setName(String)`                                 | `void`                          | Sets the channel name.                   |
| `getPermissionMode()`                             | `PermissionMode`                | The permission mode.                     |
| `setPermissionMode(PermissionMode)`               | `void`                          | Sets the permission mode.                |
| `getOwner()`                                      | `@Nullable UUID`                | The channel owner.                       |
| `setOwner(@Nullable UUID)`                        | `void`                          | Sets the owner.                          |
| `getExplicitPermission(UUID)`                     | `@Nullable HubPermissionLevel`  | Returns a player's explicit permission.  |
| `getExplicitPermissions()`                        | `Map<UUID, HubPermissionLevel>` | Returns all explicit permissions.        |
| `setExplicitPermission(UUID, HubPermissionLevel)` | `void`                          | Sets an explicit permission.             |
| `removeExplicitPermission(UUID)`                  | `void`                          | Removes an explicit permission.          |
| `getPermissionLevel(UUID)`                        | `HubPermissionLevel`            | Returns the final permission level.      |
| `canEditPermissions(UUID)`                        | `boolean`                       | Whether the player can edit permissions. |

---

### HubPermissionLevel

`com.circulation.circulation_networks.api.hub.HubPermissionLevel` (enum)

Hub permission levels.

| Value    | Description   |
|----------|---------------|
| `NONE`   | No permission |
| `MEMBER` | Member        |
| `ADMIN`  | Administrator |
| `OWNER`  | Owner         |

| Method                 | Return Type                 | Description                                      |
|------------------------|-----------------------------|--------------------------------------------------|
| `fromId(int)`          | `static HubPermissionLevel` | Returns the enum value by ordinal.               |
| `getId()`              | `int`                       | Returns the ordinal.                             |
| `canEditPermissions()` | `boolean`                   | Returns whether the level is `OWNER` or `ADMIN`. |

---

### PermissionMode

`com.circulation.circulation_networks.api.hub.PermissionMode` (enum)

Hub permission mode that defines the default access policy.

| Value     | Description                              |
|-----------|------------------------------------------|
| `PUBLIC`  | Public, accessible by everyone           |
| `TEAM`    | Team-based, requires explicit permission |
| `PRIVATE` | Private, owner only                      |

| Method        | Return Type             | Description                        |
|---------------|-------------------------|------------------------------------|
| `fromId(int)` | `static PermissionMode` | Returns the enum value by ordinal. |
| `getId()`     | `int`                   | Returns the ordinal.               |

---

### ChargingDefinition

`com.circulation.circulation_networks.api.hub.ChargingDefinition` (enum)

Defines the player inventory slot categories that hub charging can target.

| Value       | Description                                    |
|-------------|------------------------------------------------|
| `INVENTORY` | Inventory                                      |
| `HOTBAR`    | Hotbar                                         |
| `MAIN_HAND` | Main hand                                      |
| `OFF_HAND`  | Off hand                                       |
| `ARMOR`     | Armor slots                                    |
| `ACCESSORY` | Accessory slots (for example Baubles / Curios) |

---

### ChargingPreference

`com.circulation.circulation_networks.api.hub.ChargingPreference`

Player charging preference stored as a bit mask.

| Method                                                                     | Return Type                 | Description                                                                             |
|----------------------------------------------------------------------------|-----------------------------|-----------------------------------------------------------------------------------------|
| `ChargingPreference(boolean, boolean, boolean, boolean, boolean, boolean)` | Constructor                 | Creates a preference in INVENTORY, HOTBAR, ACCESSORY, MAIN_HAND, OFF_HAND, ARMOR order. |
| `ChargingPreference(byte)`                                                 | Constructor                 | Creates a preference from a raw bit mask.                                               |
| `defaultAll()`                                                             | `static ChargingPreference` | Enables every slot.                                                                     |
| `deserialize(CompoundTag)`                                                 | `static ChargingPreference` | Deserializes from NBT.                                                                  |
| `getPreference(ChargingDefinition)`                                        | `boolean`                   | Checks whether a slot category is enabled.                                              |
| `setPreference(ChargingDefinition, boolean)`                               | `void`                      | Enables or disables a slot category.                                                    |
| `setPrefs(byte)`                                                           | `void`                      | Sets the raw bit mask directly.                                                         |
| `toByte()`                                                                 | `byte`                      | Exports the raw bit mask.                                                               |
| `serialize()`                                                              | `CompoundTag`               | Serializes to NBT.                                                                      |

---

### Snapshot Classes

The following classes are used for GUI synchronization and network transfer. They support JSON and binary serialization.

**`ChannelSnapshotEntry`** — channel snapshot record:

| Field            | Type                 | Description                       |
|------------------|----------------------|-----------------------------------|
| `id`             | `UUID`               | Channel UUID                      |
| `name`           | `String`             | Channel name                      |
| `permissionMode` | `PermissionMode`     | Permission mode                   |
| `permission`     | `HubPermissionLevel` | Current player's permission level |
| `connected`      | `boolean`            | Whether the channel is connected  |

**`ChannelSnapshotList`** — channel snapshot list:

- `getEntries()` → `List<ChannelSnapshotEntry>`
- `toJson()` / `fromJson(String)` — JSON serialization
- `toBytes()` / `fromBytes(byte[])` — binary serialization

**`NodeSnapshotEntry`** — node snapshot record:

| Field         | Type     | Description           |
|---------------|----------|-----------------------|
| `itemId`      | `String` | Node item registry ID |
| `x`, `y`, `z` | `int`    | Node coordinates      |
| `customName`  | `String` | Custom name           |

**`NodeSnapshotList`** — node snapshot list:

- `getEntries()` → `List<NodeSnapshotEntry>`
- `fromGrid(IGrid)` — builds a snapshot from a grid
- `toJson()` / `fromJson(String)` — JSON serialization
- `toBytes()` / `fromBytes(byte[])` — binary serialization with delta-encoded coordinates

**`PermissionSnapshotEntry`** — permission snapshot record:

| Field        | Type                 | Description      |
|--------------|----------------------|------------------|
| `id`         | `UUID`               | Player UUID      |
| `name`       | `String`             | Player name      |
| `permission` | `HubPermissionLevel` | Permission level |

**`PermissionSnapshotList`** — permission snapshot list:

- `getEntries()` → `List<PermissionSnapshotEntry>`
- `toJson()` / `fromJson(String)` — JSON serialization
- `toBytes()` / `fromBytes(byte[])` — binary serialization

---

## Tick Interfaces

### ClientTickMachine

`com.circulation.circulation_networks.api.ClientTickMachine`

Block entities implement this interface to receive updates during client ticks.

```java
public interface ClientTickMachine {
    void clientUpdate();
}
```

### ServerTickMachine

`com.circulation.circulation_networks.api.ServerTickMachine`

Block entities implement this interface to receive updates during server ticks.

```java
public interface ServerTickMachine {
    void serverUpdate();
}
```

---

## Grid Interface

### IGrid

`com.circulation.circulation_networks.api.IGrid`

A grid represents a connected subgraph formed by a set of linked nodes.

> **Unique internal implementation**: this interface has only the internal `Grid` implementation class. External mods
> should not implement it; use it for querying only.

| Method                 | Return Type           | Description                                            |
|------------------------|-----------------------|--------------------------------------------------------|
| `getId()`              | `UUID`                | The grid's unique ID.                                  |
| `getNodes()`           | `ReferenceSet<INode>` | All nodes in the grid.                                 |
| `serialize()`          | `CompoundTag`         | Serializes the grid to NBT.                            |
| `getHubNode()`         | `IHubNode`            | The grid's hub node, or `null` if absent.              |
| `setHubNode(IHubNode)` | `void`                | Sets the hub node.                                     |
| `getSnapshotVersion()` | `long`                | Snapshot version used for incremental synchronization. |
| `markSnapshotDirty()`  | `void`                | Marks the snapshot as needing an update.               |

