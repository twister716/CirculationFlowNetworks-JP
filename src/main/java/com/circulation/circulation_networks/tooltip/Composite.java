package com.circulation.circulation_networks.tooltip;

import com.circulation.circulation_networks.utils.CI18n;

import java.util.function.Supplier;

public record Composite(String key, Supplier<Object[]> supplier) implements LocalizedComponent {
    @Override
    public String get() {
        return CI18n.format(key, supplier.get());
    }
}
