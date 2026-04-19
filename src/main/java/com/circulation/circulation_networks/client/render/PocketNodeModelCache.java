package com.circulation.circulation_networks.client.render;

import com.circulation.circulation_networks.registry.CFNItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.jetbrains.annotations.Nullable;

public final class PocketNodeModelCache {

    private PocketNodeModelCache() {
    }

    public static void onRegisterAdditionalModels(ModelEvent.RegisterStandalone event) {
    }

    public static ItemStackRenderState get(ItemStack stack) {
        ItemStack resolved = resolveKnownPocketItem(stack);
        ItemStackRenderState renderState = new ItemStackRenderState();
        if (resolved == null) {
            return renderState;
        }

        Minecraft minecraft = Minecraft.getInstance();
        ItemModelResolver resolver = new ItemModelResolver(minecraft.getModelManager());
        resolver.updateForTopItem(renderState, resolved, ItemDisplayContext.FIXED, minecraft.level, null, 0);
        return renderState;
    }

    public static boolean isGui3d(ItemStack stack) {
        return resolveKnownPocketItem(stack) != null;
    }

    public static void clear() {
    }

    private static @Nullable ItemStack resolveKnownPocketItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        if (stack.getItem() == CFNItems.pocketPortNode
            || stack.getItem() == CFNItems.pocketChargingNode
            || stack.getItem() == CFNItems.pocketRelayNode) {
            return stack;
        }
        return null;
    }
}
