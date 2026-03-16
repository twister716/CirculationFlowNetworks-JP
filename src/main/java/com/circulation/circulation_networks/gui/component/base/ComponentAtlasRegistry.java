package com.circulation.circulation_networks.gui.component.base;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import javax.annotation.Nullable;
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

    protected static final String BG_PREFIX = "bg/";

    private final Object2ObjectOpenHashMap<String, AtlasRegion> regions = new Object2ObjectOpenHashMap<>();
    private final Set<String> registeredSprites = new ObjectLinkedOpenHashSet<>();
    private final Set<String> registeredBackgrounds = new ObjectLinkedOpenHashSet<>();

    @Nullable
    public final AtlasRegion getRegion(String name) {
        return regions.get(name);
    }

    @Nullable
    public final AtlasRegion getBackground(String name) {
        return regions.get(backgroundName(name));
    }

    final void addSprite(String name) {
        registeredSprites.add(name);
    }

    final void addBackground(String name) {
        registeredBackgrounds.add(name);
    }

    protected final void replaceRegions(Collection<AtlasRegion> atlasRegions) {
        regions.clear();
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

    protected final String[] registeredBackgroundNames() {
        return registeredBackgrounds.toArray(new String[0]);
    }

    protected static String backgroundName(String name) {
        return BG_PREFIX + name;
    }
}