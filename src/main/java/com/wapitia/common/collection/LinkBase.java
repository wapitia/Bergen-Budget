package com.wapitia.common.collection;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This is Necessarily abstract by having type S extend LinkBase
 * 
 * @author Corey Morgan
 *
 * @param <T>
 * @param <S>
 */
public abstract class LinkBase<T, S extends LinkBase<T, S>>
    implements ILink<T, S>
{

    final T           elt;
    final Optional<S> nextOpt;

    protected LinkBase(final T elt, final Optional<S> nextOpt) {
        this.elt = elt;
        this.nextOpt = nextOpt;
    }

    @Override
    public T element() {

        return elt;
    }

    @Override
    public Optional<S> next() {

        return nextOpt;
    }

    @Override
    public <A, R> R collect(final Supplier<A> supplier,
        final BiConsumer<A, ? super T> accumulator,
        final Function<A, R> finisher)
    {

        Objects.requireNonNull(supplier);
        Objects.requireNonNull(accumulator);

        final A container = evaluate(
            new ReduceOps<T, R, A>(supplier, accumulator));
        @SuppressWarnings("unchecked")
        final R result = ((finisher == null) || finisher == Function.identity())
            ? (R) container : finisher.apply(container);
        return result;
    }

    @Override
    public String toString() {

        StringBuilder bldr = new StringBuilder();
        bldr.append(element().toString());
        if (next().isPresent()) {
            bldr.append("->");
            bldr.append(next().get().toString());
        }
        return bldr.toString();
    }

    protected <R, A> A evaluate(ReduceOps<T, R, A> reduceOps) {

        reduceOps.addItem(elt);
        nextOpt.ifPresent(link -> link.evaluate(reduceOps));
        return reduceOps.getContainer();
    }

    static class ReduceOps<T, R, A> {

        private final A                        container;
        private final BiConsumer<A, ? super T> accumulator;

        public ReduceOps(final Supplier<A> supplier,
            final BiConsumer<A, ? super T> accumulator)
        {
            this.container = supplier.get();
            this.accumulator = accumulator;
        }

        public void addItem(T item) {

            accumulator.accept(container, item);
        }

        public A getContainer() {

            return container;
        }

    }

}
