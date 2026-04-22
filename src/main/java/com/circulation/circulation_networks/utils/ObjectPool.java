package com.circulation.circulation_networks.utils;

import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ObjectPool<T> {

    private final Supplier<T> factory;
    private final Consumer<T> resetter;
    private final int maxSize;
    private final ReferenceLinkedOpenHashSet<T> storage;

    public ObjectPool(Supplier<T> factory, Consumer<T> resetter, int maxSize) {
        this(factory, resetter, maxSize, new ReferenceLinkedOpenHashSet<>(maxSize));
    }

    public ObjectPool(Supplier<T> factory, Consumer<T> resetter, int maxSize, ReferenceLinkedOpenHashSet<T> storage) {
        if (maxSize < 0) {
            throw new IllegalArgumentException("ObjectPool maxSize cannot be negative");
        }
        this.factory = Objects.requireNonNull(factory, "ObjectPool factory cannot be null");
        this.resetter = Objects.requireNonNull(resetter, "ObjectPool resetter cannot be null");
        this.maxSize = maxSize;
        this.storage = Objects.requireNonNull(storage, "ObjectPool storage cannot be null");
    }

    public T obtain() {
        return storage.isEmpty() ? factory.get() : storage.removeLast();
    }

    public void recycle(T value) {
        if (value == null) {
            throw new IllegalArgumentException("ObjectPool cannot recycle null");
        }
        if (storage.contains(value)) {
            return;
        }
        resetter.accept(value);
        if (storage.size() < maxSize) {
            storage.add(value);
        }
    }

    public void clear() {
        storage.clear();
    }

    public int size() {
        return storage.size();
    }
}
