
package com.wapitia.common.collection;

import static com.wapitia.common.collection.CollectionFunctions.incr;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

public interface ILink<T, S extends ILink<T, S>> {

    @FunctionalInterface
    // <TS,SS extends ILink<TS,SS>>(TS, Optional<SS>) -> SS
    public static interface ILINK_CTOR<TS, SS extends ILink<? super TS, ? super SS>> {

        SS ctor(final TS item, final Optional<SS> nextOpt);
    }

    T element();

    Optional<S> next();

    <A, R> R collect(final Supplier<A> supplier,
        final BiConsumer<A, ? super T> accumulator,
        final Function<A, R> finisher);

    default <A, R> R collect(Collector<? super T, A, R> collector) {

        Objects.requireNonNull(collector);

        final Supplier<A> supplier = collector.supplier();
        final BiConsumer<A, ? super T> accumulator = collector.accumulator();
        final boolean isIdentFinish = collector.characteristics()
            .contains(Characteristics.IDENTITY_FINISH);
        @SuppressWarnings("unchecked")
        final Function<A, R> finisher = isIdentFinish
            ? (Function<A, R>) Function.<R> identity() : collector.finisher();
        return this.collect(supplier, accumulator, finisher);
    }

    default T itemAt(int ix) {

        T resOpt = ILink.itemAt(ix, Optional.of(ofS()));
        return resOpt;
    }

    default void forEach(Consumer<T> consumer) {

        consumer.accept(this.element());
        next().ifPresent(n -> n.forEach(consumer));
    }

    default <U, V extends ILink<U, V>> V map(final Function<T, U> mapItem,
        final ILINK_CTOR<U, V> vctor)
    {
        Optional<V> resOpt = ILink.map(Optional.of(ofS()), mapItem, vctor);
        return resOpt.get();
    }

    default S filter(final Predicate<T> filterBy,
        final ILINK_CTOR<T, S> sctor)
    {
        Optional<S> resOpt = ILink.filter(Optional.of(ofS()), filterBy, sctor);
        return resOpt.get();
    }

    default int size() {

        return ILink.size(Optional.of(ofS()));
    }

    default S ofS() {

        @SuppressWarnings("unchecked")
        final S self = (S) this;
        return self;
    }

    // Static chain methods: those that start with an Optional<ILink> head

    public static <SS extends ILink<?, SS>> int size(Optional<SS> linkOpt) {

        final Integer result = linkOpt.map(SS::next).map(ILink::size).map(incr)
            .orElse(0);
        return result;
    }

    public static <TS, A, R, SS extends ILink<TS, SS>> R collect(
        Optional<SS> linkOpt, Collector<? super TS, A, R> collector)
    {

        @SuppressWarnings("unchecked")
        final R result = linkOpt.map(l -> l.collect(collector))
            .orElseGet(() -> (R) collector.supplier().get());
        return result;
    }

    public static <TS, A, R, SS extends ILink<TS, SS>> void forEach(
        Optional<SS> linkOpt, Consumer<TS> consumer)
    {

        linkOpt.ifPresent(l -> l.forEach(consumer));
    }

    public static <TS, SS extends ILink<TS, SS>, US, VS extends ILink<US, VS>> Optional<VS> map(
        final Optional<SS> linkOpt, final Function<TS, US> mapItem,
        final ILINK_CTOR<US, VS> vctor)
    {

        if (!linkOpt.isPresent()) {
            return Optional.<VS> empty();
        }

        final SS sourceLink = linkOpt.get();
        Optional<VS> tail = map(sourceLink.next(), mapItem, vctor);
        final US newElt = mapItem.apply(sourceLink.element());
        VS newLink = vctor.ctor(newElt, tail);
        Optional<VS> result = Optional.of(newLink);
        return result;
    }

    public static <TS, SS extends ILink<TS, SS>> Optional<SS> filter(
        final Optional<SS> linkOpt, final Predicate<TS> filterBy,
        final ILINK_CTOR<TS, SS> sctor)
    {

        if (!linkOpt.isPresent()) {
            return Optional.<SS> empty();
        }

        final SS sourceLink = linkOpt.get();
        Optional<SS> nextTail = filter(sourceLink.next(), filterBy, sctor);
        final Optional<SS> result;
        final TS elt = sourceLink.element();
        if (filterBy.test(elt)) {
            result = Optional.of(sctor.ctor(elt, nextTail));
        } else {
            result = nextTail;
        }
        return result;
    }

    /**
     * Insert item t into some link chain provided by an optional SS head. This
     * returns an entirely newly created chain, leaving the incoming chain
     * alone, as the link is immutable.
     *
     * @param insertBefore
     *            test interrogates each item in the chain, and will insert the
     *            incoming item before the first test that returns true, or else
     *            at the end of the chain. use () -> true to prepend at head of
     *            the chain, use () -> false to append at the end of the chain.
     */
    public static <TS, SS extends ILink<TS, SS>> SS insert(final TS t,
        final Optional<SS> headOpt, final BiPredicate<TS, TS> insertBefore,
        final ILINK_CTOR<TS, SS> linkCtor)
    {

        final SS result;
        if (headOpt.isPresent()
            && insertBefore.test(headOpt.get().element(), t)) {
            final SS head = headOpt.get();
            final Optional<SS> next = head.next();
            final SS newtail = insert(t, next, insertBefore, linkCtor);
            result = linkCtor.ctor(head.element(), Optional.<SS> of(newtail));
        } else {
            result = linkCtor.ctor(t, headOpt);
        }
        return result;
    }

    public static <SS extends ILink<?, SS>> SS linkAt(int ix,
        final Optional<SS> headOpt)
    {

        if (!headOpt.isPresent())
            throw new IndexOutOfBoundsException();
        final SS result;
        if (ix == 0) {
            result = headOpt.get();
        } else {
            // kick it down the chain
            result = linkAt(ix - 1, headOpt.get().next());
        }
        return result;
    }

    public static <TS, SS extends ILink<TS, SS>> TS itemAt(int ix,
        final Optional<SS> headOpt)
    {

        return linkAt(ix, headOpt).element();
    }

    /**
     * Create a new chain from existing chain, trimmed down to a set size. It is
     * an IndexOutOfBoundsException if the size exceeds the size of the incoming
     * chain.
     */

    public static <TS, SS extends ILink<TS, SS>> Optional<SS> trim(int size,
        final Optional<SS> headOpt, final ILINK_CTOR<TS, SS> linkCtor)
    {

        if (size == 0) {
            return Optional.<SS> empty();
        }
        if (!headOpt.isPresent())
            throw new IndexOutOfBoundsException();

        final SS headLink = headOpt.get();
        final Optional<SS> tail;
        if (size == 1) {
            tail = Optional.<SS> empty();
        } else {
            tail = trim(size - 1, headLink.next(), linkCtor);
        }
        final SS res = linkCtor.ctor(headLink.element(), tail);
        return Optional.of(res);
    }

    /**
     * Create a chain, which manages the head of a possibly empty link of links.
     * This allows top-level functions (filter, map, collect) to be performed
     * from within a non-static call to a chain
     *
     * @return
     */
    static <TS, SS extends ILink<TS, SS>> ChainBase<TS, SS> chain() {

        return new ChainBase<TS, SS>(Optional.empty());
    }

    /**
     * Class to manage a possibly empty Link of Links, providing the convenience
     * methods to fiddle with the optional head. Chain is a mutable object, and
     * the {@link #setHead(Optional)} is used to change it.
     */
    public static class ChainBase<TS, SS extends ILink<TS, SS>> {

        private Optional<SS> headOpt;

        public ChainBase() {
            this.headOpt = Optional.<SS> empty();
        }

        public ChainBase(Optional<SS> head) {
            this.headOpt = head;
        }

        public void setHead(Optional<SS> newHead) {

            this.headOpt = newHead;
        }

        public boolean isEmpty() {

            return !headOpt.isPresent();
        }

        /**
         * Will throw an exception if isEmpty() is true
         */
        public SS head() {

            return headOpt.get();
        }

        public Optional<SS> getFirst() {

            return headOpt;
        }

        /**
         * Will throw an exception if isEmpty() is true
         */
        public TS pop() {

            SS head = headOpt.get();
            final TS result = head.element();
            this.headOpt = head.next();
            return result;
        }

        /**
         * Will throw an exception if isEmpty() is true
         */
        public TS peek() {

            SS head = headOpt.get();
            final TS result = head.element();
            return result;
        }

        public <A, R> R collect(Collector<? super TS, A, R> collector) {

            return ILink.collect(headOpt, collector);
        }

        public void forEach(Consumer<TS> consumer) {

            ILink.forEach(headOpt, consumer);
        }

        public void push(TS t, final ILINK_CTOR<TS, SS> sctor)
        {
            insert(t, (a,b) -> true, sctor);
        }

        public <US, VS extends ILink<US, VS>> ChainBase<US, VS> map(
            final Function<TS, US> mapItem, final ILINK_CTOR<US, VS> vctor)
        {

            return new ChainBase<US, VS>(ILink.map(headOpt, mapItem, vctor));
        }

        public void insert(final TS t, final BiPredicate<TS, TS> insertBefore,
            final ILINK_CTOR<TS, SS> linkCtor)
        {

            final SS newHead = ILink.insert(t, headOpt, insertBefore, linkCtor);
            this.headOpt = Optional.<SS> of(newHead);
        }

        public int size() {

            return headOpt.map(l -> l.size()).orElse(0);
        }

        public ChainBase<TS, SS> filter(final Predicate<TS> filterBy,
            final ILINK_CTOR<TS, SS> sctor)
        {

            final Optional<SS> filt = ILink.filter(headOpt, filterBy, sctor);
            return new ChainBase<TS, SS>(filt);
        }

    }

}
