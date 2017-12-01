
package com.wapitia.common.collection;

import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Function;

/**
 * The common object of the com.wapitia.common.collection
 *
 * @author Corey Morgan
 *
 */
public interface CollectionFunctions {

    // TODO: In Java 1.9, List.of(T...)
    @SafeVarargs
    static <T> List<T> listOf(T ... items) {
        final List<T> result = java.util.Collections.<T> unmodifiableList(
            Arrays.<T> asList(items));
        return result;
    }

    /**
     * Integer increment function
     */
    Function<Integer, Integer> incr = i -> i + 1;

    // Spliterator helper functions

    /**
     * Add the estimated sizes that resulted from calls to two Spliterator's
     * {@link Spliterator#estimateSize() estimateSize} methods.
     * <p>
     * Since the call to {@code estimateSize()} may return Long.MAX_VALUE,
     * then the rule is to return Long.MAX_VALUE if either value
     * is Long.MAX_VALUE, else return the sum of the two.
     */
    static long addEstimateSizes(long a, long b) {

        final long result;
        if (a == Long.MAX_VALUE || b == Long.MAX_VALUE) {
            result = Long.MAX_VALUE;
        } else {
            result = a + b;
        }
        return result;
    }

    /**
     * Return true iff all of the characteristicBits are set in the
     * Spliterator's characteristics.
     */
    static boolean hasCharacteristic(Spliterator<?> spliterator,
        int characteristicBits)
    {

        final int characteristics = spliterator.characteristics();
        return isBitSet(characteristics, characteristicBits);
    }

    /**
     * Return
     * @param fld
     * @param bits
     * @return
     */
    static boolean isBitSet(final int fld, int bits) {

        return (fld & bits) == bits;
    }

}
