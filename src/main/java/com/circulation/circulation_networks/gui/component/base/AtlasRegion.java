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
    public final int atlasWidth;
    public final int atlasHeight;

    AtlasRegion(String name, int x, int y, int width, int height, int atlasWidth, int atlasHeight) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.atlasWidth = atlasWidth;
        this.atlasHeight = atlasHeight;
    }

    public float u0() {
        return (float) x / atlasWidth;
    }

    public float v0() {
        return (float) y / atlasHeight;
    }

    public float u1() {
        return (float) (x + width) / atlasWidth;
    }

    public float v1() {
        return (float) (y + height) / atlasHeight;
    }

    @Override
    public String toString() {
        return "AtlasRegion{" + name + " @(" + x + "," + y + ") " + width + "x" + height
            + " atlas=" + atlasWidth + "x" + atlasHeight + "}";
    }
}