package com.wapitia.common.collection;

import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Zipperator<L, R, T> implements Spliterator<T> {

    public static <LS, RS, TS> Spliterator<TS> zip(Spliterator<LS> l,
        Spliterator<RS> r, BiFunction<LS, RS, TS> ctor)
    {

        return new Zipperator<>(l, r, ctor);
    }

    private final Spliterator<L>      l;
    private final Spliterator<R>      r;
    private final BiFunction<L, R, T> ctor;
    private boolean                   hasRight;

    private Zipperator(Spliterator<L> l, Spliterator<R> r,
        BiFunction<L, R, T> ctor)
    {
        this.l = l;
        this.r = r;
        this.ctor = ctor;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {

        this.hasRight = false;
        boolean hasLeft = l.tryAdvance(l -> r.tryAdvance(r -> {
            hasRight = true;
            action.accept(ctor.apply(l, r));
        }));
        return hasLeft && hasRight;
    }

    @Override
    public Spliterator<T> trySplit() {

        return null;
    }

    @Override
    public long estimateSize() {

        return Math.min(l.estimateSize(), r.estimateSize());
    }

    @Override
    public int characteristics() {

        return l.characteristics() & r.characteristics()
            & ~(Spliterator.DISTINCT | Spliterator.SORTED);
    }
}
