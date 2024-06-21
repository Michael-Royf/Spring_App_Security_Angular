package com.michael.document.function;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface TriConsumer<T, U, V>  {
    void accept(T t, U u , V v);
}
