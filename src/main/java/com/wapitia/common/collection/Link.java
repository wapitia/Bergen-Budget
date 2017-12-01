
package com.wapitia.common.collection;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * ILink concrete implementation may be used directly.
 *
 * @author Corey Morgan
 *
 * @param <T>
 *            element type
 */
public class Link<T> extends LinkBase<T, Link<T>> {

    public Link(T elt) {
        this(elt, Optional.<Link<T>> empty());
    }

    public Link(T elt, Link<T> tail) {
        this(elt, Optional.<Link<T>> of(tail));
    }

    public Link(T elt, Optional<Link<T>> tail) {
        super(elt, tail);
    }

    public <U> Link<U> map(final Function<T, U> mapItem) {

        final Link<U> result = super.map(mapItem, Link<U>::new);
        return result;

    }

    public Link<T> filter(final Predicate<T> filterBy) {

        final Link<T> result = super.filter(filterBy, Link<T>::new);
        return result;
    }

    public ChainBase<T, Link<T>> asChain() {
        return new Chain<T>(Optional.of(this));
    }

    public static <TS> Chain<TS> managedChain() {

        return new Chain<TS>();
    }

    public static class Chain<TS> extends ChainBase<TS, Link<TS>> {

        public Chain() {
            super();
        }

        public Chain(Optional<Link<TS>> head) {
            super(head);
        }

        protected Chain(ChainBase<TS, Link<TS>> chain) {
            super(chain.getFirst());
        }

        public <US> Chain<US> map(final Function<TS, US> mapItem) {

            ChainBase<US, Link<US>> result = super.map(mapItem, Link<US>::new);
            return new Chain<US>(result);
        }

        public void insert(final TS t, final BiPredicate<TS, TS> insertBefore) {

            super.insert(t, insertBefore, Link<TS>::new);
        }

        public Chain<TS> filter(final Predicate<TS> filterBy) {

            final ChainBase<TS, Link<TS>> result = super.filter(filterBy,
                Link<TS>::new);
            return new Chain<TS>(result);
        }

    }

}
