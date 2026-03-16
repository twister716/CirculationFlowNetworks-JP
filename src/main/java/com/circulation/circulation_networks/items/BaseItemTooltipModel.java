package com.circulation.circulation_networks.items;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.function.Predicate;

public final class BaseItemTooltipModel {

    private static final String[] EMPTY_TOOLTIPS = new String[0];

    private BaseItemTooltipModel() {
    }

    public static String[] resolveTooltipKeys(String translationKey, Predicate<String> keyExists) {
        List<String> keys = new ObjectArrayList<>();
        int index = 0;
        while (true) {
            String key = translationKey + ".tooltip." + index;
            if (!keyExists.test(key)) {
                break;
            }
            keys.add(key);
            index++;
        }
        return keys.isEmpty() ? EMPTY_TOOLTIPS : keys.toArray(EMPTY_TOOLTIPS);
    }
}