package com.circulation.circulation_networks.tooltip;

import com.circulation.circulation_networks.items.BaseItemTooltipModel;
import com.circulation.circulation_networks.utils.CI18n;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

public record TooltipTranslationsComponent(String baseKey, boolean moveFirstToEnd) implements TooltipProvider {

    public static final Codec<TooltipTranslationsComponent> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("baseKey").forGetter(TooltipTranslationsComponent::baseKey),
            Codec.BOOL.optionalFieldOf("moveFirstToEnd", false).forGetter(TooltipTranslationsComponent::moveFirstToEnd)
        ).apply(instance, TooltipTranslationsComponent::new)
    );

    @Override
    @ParametersAreNonnullByDefault
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        String[] keys = BaseItemTooltipModel.resolveTooltipKeys(baseKey, CI18n::hasKey);
        if (moveFirstToEnd) {
            keys = BaseItemTooltipModel.moveFirstTooltipKeyToEnd(keys);
        }
        for (String key : keys) {
            consumer.accept(Component.translatable(key));
        }
    }
}
