# Migration Architecture Guide

This repository currently tracks the NeoForge 26.1 branch while keeping the shared-layer split rules needed for future ports.

## Shared Layer

The shared layer owns business logic, orchestration, serialization policy, caches, and other stable domain behavior. Shared code should keep a small Minecraft API surface and rely on focused compat or backend helpers for version touchpoints.

Shared and version code should prefer fastutil collections for caches, registries, snapshots, and hot-path temporary aggregation. JDK collection implementations such as `ArrayList`, `HashMap`, and `LinkedList` should stay only where an API contract or required semantics makes them the better choice.

The shared source is now authored directly for `26.1`. Stonecutter remains enabled only as a future compatibility hook, so the shared tree should stay clean and 26.1-native until another real version target returns.

## Stonecutter Usage

Stonecutter is no longer the default tool on this branch. When another live version returns, use it only for light differences such as:

- type names
- method names
- parameter names
- small control-flow differences
- `BlockEntity` and `TileEntity` naming gaps

When a class starts collecting large groups of Minecraft imports, render API differences, container lifecycle differences, input event differences, or server lifecycle differences, move those touchpoints into compat, service, or backend helpers.

## Split Strategy

Use three placement buckets when migrating code:

1. Shared body
2. Shared body plus backend
3. Fully version-specific

`DimensionHelper` is the canonical fully version-specific class. Packet classes remain version-local. 26.1 tooltip work uses data components. 26.1 item rendering uses the special model renderer path.

## Current Compat Targets

The migration baseline uses these helper families:

- Dimension helpers
- NBT helpers
- Compressed NBT IO helpers
- World or server resolution helpers
- Accessory inventory helpers
- Container menu helpers
- Text field platform helpers
- Render system helpers
- Rendering backends
- Rendering geometry helpers
- Shader resource helpers
- Resource ID helpers
- Player feedback helpers

The goal is stable shared logic with narrow, reusable version touchpoints.

That also applies to collection policy: shared abstractions should not silently drift back to default JDK collection implementations when fastutil gives the same behavior with better fit for the project.

## Validation Order

Current branch validation uses the 26.1 project only:

1. `:26.1:classes`
2. `:26.1:runClient`

If another version returns in the future, reintroduce a multi-version validation order only after that version has real source ownership again.
