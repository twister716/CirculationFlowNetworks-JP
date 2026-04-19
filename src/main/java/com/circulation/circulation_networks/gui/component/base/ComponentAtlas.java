package com.circulation.circulation_networks.gui.component.base;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.NeoForge;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("resource")
public final class ComponentAtlas extends ComponentAtlasBase {

    public static final ComponentAtlas INSTANCE = new ComponentAtlas();
    private static final Identifier ATLAS_TEXTURE_LOCATION = Identifier.fromNamespaceAndPath(DOMAIN, "generated/component_atlas");
    private RenderType guiRenderType;
    private DynamicTexture texture;

    private ComponentAtlas() {
    }

    private static NativeImage toNativeImage(BufferedImage image) {
        NativeImage nativeImage = new NativeImage(image.getWidth(), image.getHeight(), false);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                nativeImage.setPixel(x, y, image.getRGB(x, y));
            }
        }
        return nativeImage;
    }

    @Override
    protected Minecraft minecraft() {
        return Minecraft.getInstance();
    }

    @Override
    protected void postRegisterComponentSprites(RegisterComponentSpritesEvent event) {
        NeoForge.EVENT_BUS.post(event);
    }

    @Override
    protected InputStream openSpriteStream(Minecraft minecraft, String spriteName) throws IOException {
        Identifier location = Identifier.fromNamespaceAndPath(DOMAIN, COMPONENT_DIR + spriteName + ".png");
        return minecraft.getResourceManager().getResourceOrThrow(location).open();
    }

    @Override
    protected void uploadAtlasImage(BufferedImage image) {
        DynamicTexture dynamicTexture = new DynamicTexture(() -> "CFN component atlas", toNativeImage(image));
        minecraft().getTextureManager().register(ATLAS_TEXTURE_LOCATION, dynamicTexture);
        texture = dynamicTexture;
    }

    @Override
    protected boolean hasUploadedTexture() {
        return texture != null;
    }

    @Override
    protected void bindUploadedTexture() {
    }

    @Override
    protected void releaseUploadedTexture() {
        if (texture != null) {
            minecraft().getTextureManager().release(ATLAS_TEXTURE_LOCATION);
            texture = null;
            guiRenderType = null;
        }
    }

    public Identifier textureLocation() {
        return ATLAS_TEXTURE_LOCATION;
    }

    public void ensureTextureRegistered() {
        if (texture != null) {
            minecraft().getTextureManager().register(ATLAS_TEXTURE_LOCATION, texture);
        }
    }

    public RenderType guiRenderType() {
        ensureTextureRegistered();
        if (guiRenderType == null) {
            guiRenderType = RenderType.create(
                "cfn_component_atlas_gui",
                RenderSetup.builder(RenderPipelines.GUI_TEXTURED).withTexture("Sampler0", ATLAS_TEXTURE_LOCATION).createRenderSetup()
            );
        }
        return guiRenderType;
    }
}
