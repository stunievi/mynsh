package com.beeasy.hzback.core.exception;

import java.util.Objects;
import java.util.function.Function;

public class Try {

    public static <T, R> Function<T, R> of(UncheckedFunction<T, R> mapper) {
        Objects.requireNonNull(mapper);
        return t -> {
            try {
                return mapper.apply(t);
            } catch (RestException ex) {
                throw new RuntimeException();
            }
        };
    }

    @FunctionalInterface
    public static interface UncheckedFunction<T, R> {

        R apply(T t) throws RestException;
    }
}
