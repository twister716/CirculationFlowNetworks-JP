package com.circulation.circulation_networks.gui.component.base;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.Set;

/**
 * Shared atlas registry state used by version-specific atlas loaders.
 *
 * <p>This keeps the sprite/background registration set and the stitched region
 * lookup map in shared code, while each version remains responsible for actual
 * resource loading, image stitching triggers, and GPU upload lifecycle.</p>
 */
public abstract class ComponentAtlasRegistry {

    private final Object2ObjectMap<String, AtlasRegion> regions = new Object2ObjectOpenHashMap<>();
    private final Set<String> registeredSprites = new ObjectLinkedOpenHashSet<>();

    @Nullable
    public final AtlasRegion getRegion(String name) {
        return regions.get(name);
    }

    final void addSprite(String name) {
        registeredSprites.add(name);
    }

    protected final void replaceRegions(Collection<AtlasRegion> atlasRegions) {
        regions.clear();
        if (atlasRegions.isEmpty()) {
            return;
        }
        for (AtlasRegion region : atlasRegions) {
            regions.put(region.name, region);
        }
    }

    protected final void clearRegisteredRegions() {
        regions.clear();
    }

    protected final String[] registeredSpriteNames() {
        return registeredSprites.toArray(new String[0]);
    }
}
