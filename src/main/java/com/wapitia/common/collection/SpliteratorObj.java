
package com.wapitia.common.collection;

import com.wapitia.common.collection.Link.Chain;

import java.util.Spliterator;

/**
 * Spliterator helper object
 * @author Corey Morgan
 */
public interface SpliteratorObj {

    /**
     * Test that the characteristics of all the spliterators in the given chain
     * all have the given bit set.
     * If all spliterators have the characteristic, then whenAllSet is
     * returned.
     * If there are no spliterators (chain is empty) then whenAllSet is
     * returned.
     * Otherwise when there is at least one spliterator that is missing
     * the characteristic, then whenAnyNotSet is returned.
     *
     * @param ss  subspliter chain
     * @param cbit bit to check
     * @param whenAllSet
     * @param whenAnyNotSet
     * @return
     */
    static <TS,R> R testSubspliterCharacteristicAllSet(
        final Chain<Subspliter<TS>> ss, final int cbit,
        final R whenAllSet, final R whenAnyNotSet)
    {
        final R result = testCharacteristicAllSet(
            ss.map(Subspliter<TS>::getSpliterator),
            cbit, whenAllSet, whenAnyNotSet);
        return result;
    }

    /**
     * Test that the characteristics of all the spliterators in the given chain
     * all have the given bit set.
     * If all spliterators have the characteristic, then whenAllSet is
     * returned.
     * If there are no spliterators (chain is empty) then whenAllSet is
     * returned.
     * Otherwise when there is at least one spliterator that is missing
     * the characteristic, then whenAnyNotSet is returned.
     *
     * @param ss  subspliter chain
     * @param cbit bit to check
     * @param whenAllSet
     * @param whenAnyNotSet
     * @return
     */
    static <TS,R> R testCharacteristicAllSet(
        final Chain<Spliterator<TS>> ss, final int cbit,
        final R whenAllSet, final R whenAnyNotSet)
    {
        final R result = ss
            // for each Spliterator, get its characteristics
            .map(Spliterator::characteristics)
            // for each Spliterator characteristic, see if there's one
            // that is _missing_ the given bit
            .filter( chars -> !CollectionFunctions.isBitSet(chars, cbit) )
            // if a _missing_ one is found ...
            .getFirst()
            // ... return the negative result
            .map(na -> whenAnyNotSet)
            // otherwise return the positive result
            .orElse(whenAllSet);
        return result;
    }
}
