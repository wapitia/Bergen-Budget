
package com.wapitia.common.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Blenderator is a Spliterator whose source of elements is a managed list of
 * other Spliterators, called sub-spliters.
 *
 * All sub-spliters share the common element type T. T is the return type of
 * the Blenderator.
 *
 * A Comparator of T elements is used to compare the next elements of each
 * sub-spliterator to determine which is this Blenderator's next on-deck element
 * with which to advance. After each sub-spliterator is exhausted from repeated
 * calls to {@link #tryAdvance}, it is discarded from the Blenderator's set.
 * When all sub-spliterator's are exhausted, this Blenderator then also becomes
 * exhausted, and {@link #tryAdvance} will thereafter return false.
 *
 * The sub-spliters and comparator are given in the constructor, and the
 * Blenderator cannot be configured further after construction.
 *
 * <p>Class {@link Blenderator.Builder} provides a step-wise way to construct
 * a Blenderator with an element comparator with convenient methods to add
 * various types of collections: Spliterator, Stream, Iterable,
 * List, Set, Iterator.
 *
 * @author Corey Morgan
 *
 * @param <T>
 *            Spliterator collection item type of this Spliterator, and the
 *            common type of all sub-spliterators given in the constructor.
 */
public class Blenderator<T> implements Spliterator<T> {

    /**
     * Volatile. Changed repeatedly in the constructor as the linked list is
     * being built, and by each good call to tryAdvance().
     */
    private Link.Chain<Subspliter<T>>                 subspliters;

    /**
     * A boolean function wrapping the comparator supplied in the constructor,
     * so that this returns true iff the comparator returns negative.
     */
    private final BiPredicate<Subspliter<T>, Subspliter<T>> insertBefore;

    /**
     * Constructor takes the marshaling comparator and an variable argument
     * list of the sub-spliterators.
     *
     * @param comparator
     *            the marshaling comparator, used to compare the next elements
     *            of each sub-spliterator to determine which is this
     *            Blenderator's next on-deck element for advance.
     * @param spliterators
     *            list of sub-spliterators to source from
     */
    @SafeVarargs
    public Blenderator(final Comparator<T> comparator,
        Spliterator<T>... spliterators)
    {
        this(comparator, Arrays.asList(spliterators));
    }

    /**
     * Constructor takes the marshaling comparator and a list of
     * sub-spliterators.
     *
     * @param comparator
     *            the marshaling comparator, used to compare the next elements
     *            of each sub-spliterator to determine which is this
     *            Blenderator's next on-deck element for advance.
     * @param spliterators
     *            list of sub-spliterators to source from
     */
    public Blenderator(final Comparator<T> comparator,
        List<Spliterator<T>> splitList)
    {
        this(fTrueWhenCompareNeg(comparator));
        splitList.forEach(this::insert);
    }

    public static <T> BiPredicate<Subspliter<T>, Subspliter<T>>
    fTrueWhenCompareNeg(final Comparator<T> comparator) {

        return (q1, q2) -> comparator.compare(q1.topItem(), q2.topItem()) < 0;
    }

    /**
     * Advance to the next element, according to the
     * {@link Spliterator#tryAdvance(Consumer)} process.
     *
     * This returns false when this Blenderator manages no more sub-spliters,
     * meaning that this Blenderator Spliterator is exhausted.
     * No action is taken.
     *
     * If there are any non-exhausted sub-spliters then the top item of
     * the first sub-spliter is popped and applied to the supplied
     * Consumer action. true is then returned.
     *
     */
    @Override
    public boolean tryAdvance(Consumer<? super T> action) {

        // if the QLink structure has anything in it, then we can advance
        final boolean advanceable = !subspliters.isEmpty();
        if (advanceable) {
            // pop the top subspliter. its element is the one on-deck to advance
            final Subspliter<T> firstSpliter = subspliters.pop();
            final T item = firstSpliter.topItem();

            // Insert the first sub-spliter's remaining spliterator back into
            // the mix so that if it is not exhausted, a new sub-spliter is
            // made from it and bubbled into its proper sequence according
            // to the comparator function insertBefore.
            insert(firstSpliter.getSpliterator());

            // perform the action now that the Blenderator is in a stable state
            action.accept(item);
        }
        return advanceable;
    }

    @Override
    public Spliterator<T> trySplit() {

        // Cannot split, as this spliterator is ordered
        return null;
    }

    @Override
    public long estimateSize() {

        Long size = subspliters.collect(Subspliter.SIZE_COLLECTOR);
        return size;
    }

    /**
     * Always ORDERED due to comparator, so cannot then be split
     */
    @Override
    public int characteristics() {

        // return NONNULL only if none of its spliters are MISSING
        // the NONNULL Characteristic
        final int nonNull = subspliters
            .filter(sh -> !sh.nonNullCharacteristic()).getFirst().map(na -> 0)
            .orElse(Spliterator.NONNULL);

        // return IMMUTABLE only if none of its spliters are MISSING
        // the IMMUTABLE Characteristic
        final int immutable = SpliteratorObj.<T,Integer> testSubspliterCharacteristicAllSet(
            subspliters, Spliterator.IMMUTABLE, Spliterator.IMMUTABLE, 0);

        // return SIZED only if none of its spliters are MISSING
        // the SIZED Characteristic
        final int sized = SpliteratorObj.<T,Integer> testSubspliterCharacteristicAllSet(
            subspliters, Spliterator.SIZED, Spliterator.SIZED, 0);

        int chars = Spliterator.ORDERED | nonNull | immutable | sized;
        return chars;
    }


    @Override
    public String toString() {

        return subspliters.isEmpty() ? "[]" : subspliters.head().toString();
    }

    /**
     * Base constructor allows the specification of the insertBefore
     * predicate, although leaves the sub-spliters empty.
     * Subclasses would call insert repeatedly to populate their instances.
     */
    protected Blenderator(
        final BiPredicate<Subspliter<T>, Subspliter<T>> insertBefore)
    {
        this.subspliters = Link.<Subspliter<T>> managedChain();
        this.insertBefore = insertBefore;
    }

    /**
     * Insert a new spliterator into the Blenderator mix if not exhausted,
     * bubbling it down into its proper spot in the subspliter chain.
     * <p>
     * This is called repeatedly by the constructor to prime each
     * incoming spliterator, and then once for each call to tryAdvance,
     * in order to reinsert a "used" tail spliterator back into the mix.
     */
    protected void insert(Spliterator<T> spliterator) {

        final Iterator<T> cracked = Spliterators.iterator(spliterator);
        if (cracked.hasNext()) {
            // not exhausted
            final T item = cracked.next();
            final Subspliter<T> subspliter = new Subspliter<T>(item, spliterator);
            this.subspliters.insert(subspliter, insertBefore,
                Link<Subspliter<T>>::new);
        }
    }

    /**
     * Blenderator.Builder provides a step-wise way to construct a Blenderator
     * starting with the given element comparator, and given convenient
     * methods to add the various collection kinds (Spliterator,
     * Stream, Iterable (List, Set, etc), Iterator), each of which will be
     * wrapped into a Spliterator for the Blenderator constructor.
     *
     * <p>
     * Usage:
     * <pre>{@code ...
     *   Blenderator<String> spliter
     *       = new Blenderator.Builder<>(String::compareTo)
     *           .add(Stream.of("A","B","L"))
     *           .add(Collections.listOf("G","S","Y"))
     *           .add(Stream.of("ER","GR","PR","TR").iterator())
     *           .add(Stream.<String> of().spliterator())
     *           .toBlenderator();
     *
     *   Stream<String> res = StreamSupport.stream(spliter, false);
     *   String act = res.collect(Collectors.joining(";"));
     *   Assert.assertEquals("A;B;ER;G;GR;L;PR;S;TR;Y", act);
     *
     * }</pre>
     *
     * @author Corey Morgan
     *
     * @param <TS>
     */
    public static class Builder<TS> {

        private Optional<Comparator<TS>> comparatorOpt;
        private List<Spliterator<TS>> spliterators;

        @SafeVarargs
        public Builder(Comparator<TS> comparator, Spliterator<TS>... spliters) {
            this.comparatorOpt = Optional.<Comparator<TS>> ofNullable(comparator);
            // copy the spliterator arguments into an expandable local list
            this.spliterators = new ArrayList<>(Arrays.asList(spliters));
        }

        /**
         * Set the comparator for the Blenderator constructor.
         * Overwrites the old comparator
         */
        public Builder<TS> comparator(Comparator<TS> comparator) {

            this.comparatorOpt = Optional.<Comparator<TS>> ofNullable(comparator);
            return this;
        }

        /**
         * Add a spliterator to the list of spliterators for the Blenderator.
         */
        public Builder<TS> add(Spliterator<TS> spliter) {

            spliterators.add(spliter);
            return this;
        }

        /**
         * Consume a stream, whose spliterator shall be added to the list of
         * spliterators for the Blenderator.
         */
        public Builder<TS> add(Stream<TS> stream) {

            return this.add(stream.spliterator());
        }

        /**
         * Take an Iterable (List, Set, Array, etc), whose
         * spliterator shall be added to the list of
         * spliterators for the Blenderator.
         */
        public Builder<TS> add(Iterable<TS> coll) {

            return this.add(coll.spliterator());
        }

        /**
         * Consume an iterator, which will be wrapped in a Spliterator
         * and added to the list of spliterators for the Blenderator.
         * This uses {@link SpliteratorObj#spliteratorUnknownSize}.
         */
        public Builder<TS> add(Iterator<TS> iter) {

            final Spliterator<TS> spliter = Spliterators
                .spliteratorUnknownSize(iter, 0);
            return this.add(spliter);
        }

        /**
         * Create the Blenderator for this accumulator Builder.
         */
        public Blenderator<TS> toBlenderator() {

            final Comparator<TS> comparator = comparatorOpt
                .orElseThrow(() -> new RuntimeException("Missing Comparator"));
            final Blenderator<TS> result = new Blenderator<TS>(comparator,
                spliterators);
            return result;
        }
    }

}
