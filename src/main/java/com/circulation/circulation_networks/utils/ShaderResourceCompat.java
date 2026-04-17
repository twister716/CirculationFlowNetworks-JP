package com.circulation.circulation_networks.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class ShaderResourceCompat {

    private ShaderResourceCompat() {
    }

    public static Identifier cast(Object loc) {
        return (Identifier) loc;
    }

    @Nullable
    public static String readResource(Object loc) {
        try (InputStream is = Minecraft.getInstance().getResourceManager().getResourceOrThrow(cast(loc)).open();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
