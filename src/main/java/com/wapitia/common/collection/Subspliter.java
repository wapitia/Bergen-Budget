
package com.wapitia.common.collection;

import org.apache.commons.lang3.mutable.MutableLong;

import java.util.Spliterator;
import java.util.stream.Collector;

/**
 * Utility class wraps the top-most popped item from some spliterator,
 * and the remaining (possibly empty) spliterator.
 *
 * This is used by Blenderator to iterate through one spliterator while
 * interrogating its top element or item.
 *
 * @author Corey Morgan
 *
 * @param <T>
 */
class Subspliter<T> {

    private final T              item;
    private final Spliterator<T> spliterator;

    public Subspliter(final T item, final Spliterator<T> spliterator) {
        this.item = item;
        this.spliterator = spliterator;
    }

    /**
     * This can be called repeatedly.
     */
    public T topItem() {

        return item;
    }

    public Spliterator<T> getSpliterator() {

        return spliterator;
    }

    /**
     * A reusable Collector instance which sums up the
     * {@link Subspliter#calcSizeEstimate() estimated sizes}
     * from a collection of Subspliters of any generic type.
     * Calls {@link CollectionFunctions#addEstimateSizes(long, long)} to manage
     * the adding of two Subspliter estimates, which may return Long.MAX_VALUE.
     *
     */
    public static final Collector<Subspliter<?>, ?, Long> SIZE_COLLECTOR =
    Collector.<Subspliter<?>, MutableLong, Long> of(
        // supplier gives a MutableLong instance starting at 0L
        MutableLong::new,
        // accumulator increments the Mutable long by the
        // SpliterHead's size estimate
        (MutableLong acc, Subspliter<?> sh) -> acc.setValue(
            CollectionFunctions.addEstimateSizes(acc.getValue(), sh.calcSizeEstimate())),
        // Combiner just adds the accumulated values of 2
        // MutableLongs returning a new MutableLong
        (MutableLong acc, MutableLong acc2) -> new MutableLong(
            CollectionFunctions.addEstimateSizes(acc.getValue(), acc2.getValue())),
        // finisher is the Long value from the MutableLong accumulator
        MutableLong::getValue);

    /**
     * Calculate and return the size estimate of this item and the given
     * spliterator. One is added to the remaining spliterator's estimated size,
     * to account for the item, which has not yet been delivered.
     * {@link CollectionFunctions#addEstimateSizes(long, long)} is called to do the sum
     * because Spliterator.estimateSize() may return Long.MAX_VALUE, which
     * should not be added to.
     */
    public long calcSizeEstimate() {

        return CollectionFunctions.addEstimateSizes(spliterator.estimateSize(), 1L);
    }

    public boolean nonNullCharacteristic() {

        boolean result = (item != null)
            && CollectionFunctions.hasCharacteristic(spliterator, Spliterator.NONNULL);
        return result;
    }

    @Override
    public String toString() {

        return item.toString();
    }
}
