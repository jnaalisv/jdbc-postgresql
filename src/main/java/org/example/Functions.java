package org.example;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.example.Exceptions.sneakyThrow;

public class Functions {

    public interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }

    public interface ThrowingBiFunction<A, B, R> {
        R apply(A a, B b) throws Exception;
    }

    public static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R> f) {
        return t -> {
            try {
                return f.apply(t);
            } catch (Exception ex) {
                return sneakyThrow(ex);
            }
        };
    }

    public static <A, B, R> BiFunction<A, B, R> unchecked(ThrowingBiFunction<A, B, R> f) {
        return (a, b) -> {
            try {
                return f.apply(a, b);
            } catch (Exception ex) {
                return sneakyThrow(ex);
            }
        };
    }
}
