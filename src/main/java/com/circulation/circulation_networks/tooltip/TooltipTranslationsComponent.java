package com.circulation.circulation_networks.tooltip;

import com.circulation.circulation_networks.items.BaseItemTooltipModel;
import com.circulation.circulation_networks.utils.CI18n;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public record TooltipTranslationsComponent(List<String> translationKeys) implements TooltipProvider {

    public static final Codec<TooltipTranslationsComponent> CODEC = Codec.STRING.listOf()
                                                                                .xmap(TooltipTranslationsComponent::new, TooltipTranslationsComponent::translationKeys);

    public TooltipTranslationsComponent {
        translationKeys = ImmutableList.copyOf(translationKeys);
    }

    public static @Nullable TooltipTranslationsComponent fromTranslationKey(String translationKey) {
        return fromTranslationKey(translationKey, UnaryOperator.identity());
    }

    public static @Nullable TooltipTranslationsComponent fromTranslationKey(
        String translationKey,
        UnaryOperator<String[]> keyTransformer
    ) {
        String[] keys = keyTransformer.apply(BaseItemTooltipModel.resolveTooltipKeys(translationKey, CI18n::hasKey));
        if (keys.length == 0) {
            return null;
        }
        return new TooltipTranslationsComponent(List.of(keys));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        for (String translationKey : translationKeys) {
            consumer.accept(Component.translatable(translationKey));
        }
    }
}
