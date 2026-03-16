package com.circulation.circulation_networks.gui.component.base;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
//? if <1.20 {
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
//?} else if <1.21 {
/*import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
*///?} else {
/*import net.neoforged.bus.api.Event;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
*///?}

import java.util.Collections;
import java.util.List;

/**
 * Fired on the event bus before the {@link ComponentAtlas} begins stitching.
 * Subscribe to this event to register custom sprites or backgrounds into the shared atlas.
 *
 * <p>Registered names are collected during event dispatch and applied to the atlas
 * only by the atlas itself after the event completes. Manually constructing this
 * event and calling {@link #register}/{@link #registerBackground} outside of the
 * event bus has no effect on the atlas.
 *
 * <p>Component sprites must be located at
 * {@code assets/circulation_networks/textures/gui/component/<name>.png}.
 * Background sprites must be located at
 * {@code assets/circulation_networks/textures/gui/background/<name>.png}.
 */
//? if <1.20 {
@SideOnly(Side.CLIENT)
//?} else {
/*@OnlyIn(Dist.CLIENT)
*///?}
public final class RegisterComponentSpritesEvent extends Event {

    private final List<String> sprites = new ObjectArrayList<>();
    private final List<String> backgrounds = new ObjectArrayList<>();

    /**
     * Registers a component sprite base-name (without {@code .png} extension) to be
     * included in the atlas.
     */
    public void register(String name) {
        sprites.add(name);
    }

    /**
     * Registers a UI background image base-name (without {@code .png} extension) to be
     * stitched into the same atlas as component sprites.
     */
    public void registerBackground(String name) {
        backgrounds.add(name);
    }

    /** Returns an unmodifiable view of all registered sprite names. */
    public List<String> getSprites() {
        return Collections.unmodifiableList(sprites);
    }

    /** Returns an unmodifiable view of all registered background names. */
    public List<String> getBackgrounds() {
        return Collections.unmodifiableList(backgrounds);
    }
}
