package com.circulation.circulation_networks.client.render;

import com.google.common.base.Suppliers;
import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class AnimatedSpecialItemModel<T> implements ItemModel {

    public static final Identifier TYPE_ID = Identifier.fromNamespaceAndPath("circulation_networks", "animated_special");

    private final SpecialModelRenderer<T> specialRenderer;
    private final ModelRenderProperties properties;
    private final Supplier<Vector3fc[]> extents;
    private final Matrix4fc transformation;

    public AnimatedSpecialItemModel(SpecialModelRenderer<T> specialRenderer, ModelRenderProperties properties, Matrix4fc transformation) {
        this.specialRenderer = specialRenderer;
        this.properties = properties;
        this.extents = Suppliers.memoize(() -> {
            ObjectOpenHashSet<Vector3fc> results = new ObjectOpenHashSet<>();
            specialRenderer.getExtents(results::add);
            return results.toArray(new Vector3fc[0]);
        });
        this.transformation = transformation;
    }

    @Override
    public void update(
        ItemStackRenderState output,
        ItemStack item,
        ItemModelResolver resolver,
        ItemDisplayContext displayContext,
        @Nullable ClientLevel level,
        @Nullable ItemOwner owner,
        int seed
    ) {
        output.appendModelIdentityElement(this);
        output.setAnimated();

        ItemStackRenderState.LayerRenderState layer = output.newLayer();
        if (item.hasFoil()) {
            ItemStackRenderState.FoilType foilType = ItemStackRenderState.FoilType.STANDARD;
            layer.setFoilType(foilType);
            output.appendModelIdentityElement(foilType);
        }

        T argument = this.specialRenderer.extractArgument(item);
        layer.setExtents(this.extents);
        layer.setLocalTransform(this.transformation);
        layer.setupSpecialModel(this.specialRenderer, argument);
        if (argument != null) {
            output.appendModelIdentityElement(argument);
        }

        this.properties.applyToLayer(layer, displayContext);
    }

    public record Unbaked(Identifier base, Optional<Transformation> transformation,
                          SpecialModelRenderer.Unbaked<?> specialModel) implements ItemModel.Unbaked {

        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("base").forGetter(Unbaked::base),
            Transformation.EXTENDED_CODEC.optionalFieldOf("transformation").forGetter(Unbaked::transformation),
            SpecialModelRenderers.CODEC.fieldOf("model").forGetter(Unbaked::specialModel)
        ).apply(instance, Unbaked::new));

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            resolver.markDependency(this.base);
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transformation) {
            Matrix4fc modelTransform = Transformation.compose(transformation, this.transformation);
            SpecialModelRenderer<?> bakedSpecialModel = this.specialModel.bake(context);
            if (bakedSpecialModel == null) {
                return context.missingItemModel(modelTransform);
            }

            return new AnimatedSpecialItemModel<>(bakedSpecialModel, this.getProperties(context), modelTransform);
        }

        private ModelRenderProperties getProperties(ItemModel.BakingContext context) {
            ModelBaker baker = context.blockModelBaker();
            ResolvedModel model = baker.getModel(this.base);
            TextureSlots textureSlots = model.getTopTextureSlots();
            return ModelRenderProperties.fromResolvedModel(baker, model, textureSlots);
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
