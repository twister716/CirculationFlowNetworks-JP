package com.circulation.circulation_networks.client.compat;

import net.minecraft.client.Minecraft;

public final class FontCompat {

    private FontCompat() {
    }

    public static String trimToWidth(String text, int width) {
        return Minecraft.getInstance().font.plainSubstrByWidth(text == null ? "" : text, width);
    }
}
