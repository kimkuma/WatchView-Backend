package com.movie.wathchview.utill;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer <T, E extends Throwable> {
    void accept(T t) throws E;

    static <T, E extends Throwable> Consumer<T> unchecked(ThrowingConsumer<T, E> f) {
        return t -> {
            try {
                f.accept(t);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * 해당 부분은 일단 불필요하여 Deprecated 처리
     * @param f
     * @param c
     * @param <T>
     * @param <E>
     * @return
     */
    @Deprecated
    static <T, E extends Throwable> Consumer<T> unchecked(ThrowingConsumer<T, E> f, Consumer<Throwable> c) {
        return t -> {
            try {
                f.accept(t);
            } catch (Throwable e) {
                c.accept(e);
            }
        };
    }
}
