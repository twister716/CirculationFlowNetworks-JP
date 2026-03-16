package com.circulation.circulation_networks.gui.component.base;

/**
 * Immutable record of a single sprite's position and size within the
 * {@link ComponentAtlas} texture sheet.
 */
public final class AtlasRegion {

    public final String name;
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public final int atlasSize;

    AtlasRegion(String name, int x, int y, int width, int height, int atlasSize) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.atlasSize = atlasSize;
    }

    public float u0() {
        return (float) x / atlasSize;
    }

    public float v0() {
        return (float) y / atlasSize;
    }

    public float u1() {
        return (float) (x + width) / atlasSize;
    }

    public float v1() {
        return (float) (y + height) / atlasSize;
    }

    @Override
    public String toString() {
        return "AtlasRegion{" + name + " @(" + x + "," + y + ") " + width + "x" + height
            + " atlas=" + atlasSize + "}";
    }
}