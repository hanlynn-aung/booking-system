package com.app.booking.util;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class Builder<T> {
    private final T instance;

    private Builder(T instance) {
        this.instance = instance;
    }

    public static <T> Builder<T> of(Supplier<T> supplier) {
        return new Builder<>(supplier.get());
    }

    public <U> Builder<T> add(BiConsumer<T, U> consumer, U value) {
        consumer.accept(instance, value);
        return this;
    }

    public T build() {
        return instance;
    }
}