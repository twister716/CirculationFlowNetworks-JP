package com.circulation.circulation_networks.jei;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.gui.CFNBaseGui;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class CFNJEIPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(CirculationFlowNetworks.MOD_ID, "jei_plugin");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
        CFNGuiHandler guiHandler = new CFNGuiHandler<>();
        registration.addGuiContainerHandler(CFNBaseGui.class, guiHandler);
        registration.addGhostIngredientHandler(CFNBaseGui.class, guiHandler);
    }
}
