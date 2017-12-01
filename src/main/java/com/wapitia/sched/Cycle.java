package com.wapitia.sched;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class Cycle {

    final LocalDate                          firstDateOfCycle;
    final LocalDate                          onOrAfterDate;
    final Function<LocalDate, List<Integer>> daysOffsetFuction;
    final UnaryOperator<LocalDate>           rollCycle;

    /**
     *
     * @param firstDateOfCycle
     *            The first date of the first cycle, NOT to be confused with the
     *            starting date of the sequence, which depend on the
     *            onOrAfterDate, and the other parameters.
     * @param onOrAfterDate
     *            The first date of the cycle will be on or after this date. For
     *            a well-formed cycle, this date will lie within the first
     *            cycle.
     */
    public Cycle(final LocalDate firstDateOfCycle,
        final LocalDate onOrAfterDate,
        final Function<LocalDate, List<Integer>> daysOffsetFuction,
        final UnaryOperator<LocalDate> rollCycle)
    {

        this.firstDateOfCycle = firstDateOfCycle;
        this.onOrAfterDate = onOrAfterDate;
        this.daysOffsetFuction = daysOffsetFuction;
        this.rollCycle = rollCycle;
    }

    public Stream<LocalDate> stream() {

        final Iterator<LocalDate> iter = new CIter();
        return Stream.generate(iter::next);
    }

    public class CIter implements Iterator<LocalDate> {

        LocalDate   firstDateOfCycle;
        LocalDate   onOrAfterDate;

        List<Integer> daysOffset;
        int           dayOffsetIx;

        public CIter() {
            this.firstDateOfCycle = Cycle.this.firstDateOfCycle;
            this.onOrAfterDate = Cycle.this.onOrAfterDate;
            this.daysOffset = Cycle.this.daysOffsetFuction
                .apply(firstDateOfCycle);
            this.dayOffsetIx = 0;
        }

        @Override
        public boolean hasNext() {

            return true; // endless dates in the cycle
        }

        @Override
        public LocalDate next() {

            if (dayOffsetIx >= daysOffset.size()) {
                rollToNextCycle();
                // if (daysOffset.length == 0) return null; // shouldn't happen
                return next();
            }
            int dayOff = daysOffset.get(this.dayOffsetIx++);
            LocalDate res = firstDateOfCycle.plusDays(dayOff);
            if (onOrAfterDate != null && res.isBefore(onOrAfterDate)) {
                return next();
            }
            return res;
        }

        private void rollToNextCycle() {

            this.firstDateOfCycle = Cycle.this.rollCycle
                .apply(firstDateOfCycle);
            this.onOrAfterDate = null;
            this.daysOffset = Cycle.this.daysOffsetFuction
                .apply(firstDateOfCycle);
            this.dayOffsetIx = 0;
        }

    }
}
