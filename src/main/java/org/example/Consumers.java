package org.example;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.example.Exceptions.sneakyThrow;

public class Consumers {

    public interface ThrowingConsumer<T> {
        void consume(T t) throws Exception;
    }

    public interface ThrowingBiConsumer<A, B> {
        void consume(A a, B b) throws Exception;
    }


    static <T> Consumer<T> unchecked(ThrowingConsumer<T> f) {
        return t -> {
            try {
                f.consume(t);
            } catch (Exception ex) {
                sneakyThrow(ex);
            }
        };
    }

    public static <A, B> BiConsumer<A, B> unchecked(ThrowingBiConsumer<A, B> f) {
        return (a, b) -> {
            try {
                f.consume(a, b);
            } catch (Exception ex) {
                sneakyThrow(ex);
            }
        };
    }
}
