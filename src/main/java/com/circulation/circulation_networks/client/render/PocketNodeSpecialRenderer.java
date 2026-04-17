package com.circulation.circulation_networks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.POCKET_CHARGING;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.POCKET_PORT;
import static com.circulation.circulation_networks.client.render.RotatingBlockModelCache.POCKET_RELAY;

public final class PocketNodeSpecialRenderer implements NoDataSpecialModelRenderer {

    public static final Identifier TYPE_ID = Identifier.fromNamespaceAndPath("circulation_networks", "pocket_node");
    private static final float CENTER = 0.5F;
    private static final Vector3fc[] UNIT_EXTENTS = {
        new Vector3f(0.0F, 0.0F, 0.0F),
        new Vector3f(1.0F, 0.0F, 0.0F),
        new Vector3f(0.0F, 1.0F, 0.0F),
        new Vector3f(0.0F, 0.0F, 1.0F),
        new Vector3f(1.0F, 1.0F, 0.0F),
        new Vector3f(1.0F, 0.0F, 1.0F),
        new Vector3f(0.0F, 1.0F, 1.0F),
        new Vector3f(1.0F, 1.0F, 1.0F)
    };

    private final Kind kind;

    public PocketNodeSpecialRenderer(Kind kind) {
        this.kind = kind;
    }

    @Override
    public void submit(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        try (RotatingModelVBORenderer.RenderSession ignored = RotatingModelVBORenderer.beginRenderSession(submitNodeCollector)) {
            RotatingModelVBORenderer.renderLitYAxis(poseStack, lightCoords, kind.modelLocation, 0.0F, CENTER, CENTER, CENTER);
            RotatingModelVBORenderer.renderFullBrightYAxis(poseStack, kind.modelLocation, 0.0F, CENTER, CENTER, CENTER);
        }
    }

    @Override
    public void getExtents(@NotNull Consumer<Vector3fc> output) {
        for (Vector3fc extent : UNIT_EXTENTS) {
            output.accept(extent);
        }
    }

    public enum Kind {
        RELAY("relay", POCKET_RELAY),
        CHARGING("charging", POCKET_CHARGING),
        PORT("port", POCKET_PORT);

        static final Codec<Kind> CODEC = Codec.STRING.xmap(Kind::bySerializedName, Kind::serializedName);

        private final String serializedName;
        private final Identifier modelLocation;

        Kind(String serializedName, Identifier modelLocation) {
            this.serializedName = serializedName;
            this.modelLocation = modelLocation;
        }

        private static Kind bySerializedName(String value) {
            for (Kind kind : values()) {
                if (kind.serializedName.equals(value)) {
                    return kind;
                }
            }
            throw new IllegalArgumentException("Unknown pocket node renderer kind: " + value);
        }

        public String serializedName() {
            return serializedName;
        }
    }

    public record Unbaked(Kind kind) implements NoDataSpecialModelRenderer.Unbaked {

        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Kind.CODEC.fieldOf("kind").forGetter(Unbaked::kind)
        ).apply(instance, Unbaked::new));

        @Override
        public @NotNull MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public @Nullable SpecialModelRenderer<Void> bake(@NotNull SpecialModelRenderer.BakingContext context) {
            return new PocketNodeSpecialRenderer(kind);
        }
    }
}
