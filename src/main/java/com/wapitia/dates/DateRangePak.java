
package com.wapitia.dates;

import com.wapitia.dates.RangePak.ARange;
import com.wapitia.dates.RangePak.ARangeFactory;
import com.wapitia.dates.RangePak.ARangeList;
import com.wapitia.dates.RangePak.ARangeMath;
import com.wapitia.dates.RangePak.AllLeftRange;
import com.wapitia.dates.RangePak.ILeftRange;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface DateRangePak {

    public static final DateRange ALL = new DateRange(
        Optional.<LocalDate> empty(), Optional.<LocalDate> empty()) {

        AllLeftRange<LocalDate, DateRange, DateRangeList> leftInst = new AllLeftRange<LocalDate, DateRange, DateRangeList>(FACT);

        @Override
        protected ILeftRange<LocalDate> leftRange() {

            return leftInst;
        }

        @Override
        protected String rangeName() {

            return "ALL";
        }
    };

    public static final DateRangeList ALLLIST = new DateRangeList(
        Collections.singletonList(ALL))
    {
                                              };

    public static final DateRangeList NONE    = new DateRangeList(
        Collections.<DateRange> emptyList())
    {
                                              };

    static class DateRangeMath
        extends RangePak.ARangeMath<LocalDate>
    {

        @Override
        public long diff(LocalDate a, LocalDate b) {

            return b.until(a, ChronoUnit.DAYS);
        }

        @Override
        public LocalDate incr(LocalDate a) {

            return a.plusDays(1L);
        }

        @Override
        public LocalDate decr(LocalDate a) {

            return a.minusDays(1L);
        }

    }

    public static final DateRangeFactory FACT = new DateRangeFactory(
        new DateRangeMath());

    public final class DateRangeFactory
        extends ARangeFactory<LocalDate, DateRange, DateRangeList>
    {

        public DateRangeFactory(
           ARangeMath<LocalDate> rangeMath)
        {
            super(rangeMath);
        }

        @Override
        public DateRange all() {

            return ALL;
        }

        @Override
        public DateRangeList allList() {

            return ALLLIST;
        }

        @Override
        public DateRangeList none() {

            return NONE;
        }

        @Override
        public DateRange makeRange(Optional<LocalDate> startOpt,
            Optional<LocalDate> finishOpt)
        {

            return new DateRange(startOpt, finishOpt);
        }

        @Override
        public DateRangeList makeRangeList(List<ARange<LocalDate>> rangeList) {

            return new DateRangeList(rangeList);
        }

    }

    public class DateRangeList extends ARangeList<LocalDate> {

        @Override
        protected DateRangeFactory fact() {

            return FACT;
        }

        /**
         * The incoming ranges may be in any order, so must be collated
         * correctly in the reduce phase.
         */
        @SafeVarargs
        public static DateRangeList of(final ARange<LocalDate>... ranges) {

            final DateRangeList result = ARangeList
                .<LocalDate, DateRangeList> of(FACT, ranges);
            return result;
        }

        /**
         * ONLY USED BY THE COLLATING FUNCTIONALITY BEING PASSED A PRE-COLLATED
         *
         * @param collatedList
         * @see ARangeList#collate(List, ARangeFactory)
         */
        protected DateRangeList(
            final List<? extends ARange<LocalDate>> collatedList)
        {
            super(collatedList);
        }

    }

    public class DateRange extends RangePak.ARange<LocalDate> {

        @Override
        protected DateRangeFactory fact() {

            return FACT;
        }

        /**
         * Create a new DateRange from start to finish inclusive.
         *
         * @param start
         *            Start date inclusive, must not be null
         * @param finish
         *            Finish date inclusive, must not be null
         * @return a new DateRange from start to finish inclusive.
         * @throws IllegalArgumentException
         *             when start is later than finish
         */
        public static DateRange of(LocalDate start, LocalDate finish) {

            final DateRange result = ARange.<LocalDate, DateRange> of(FACT,
                start, finish);
            return result;
        }

        /**
         * Create a new DateRange with an open start date and an inclusive
         * finish date.
         *
         * @param finish
         *            Finish date inclusive, must not be null
         * @return a new DateRange open to finish inclusive.
         */
        public static DateRange ofOpenStart(LocalDate finish) {

            final DateRange result = ARange
                .<LocalDate, DateRange> ofOpenStart(FACT, finish);
            return result;
        }

        /**
         * Create a new DateRange with an inclusive start and an open finish
         * date.
         *
         * @param start
         *            Start date inclusive, must not be null
         * @return a new DateRange start inclusive to an open finish.
         */
        public static DateRange ofOpenFinish(LocalDate start) {

            final DateRange result = ARange
                .<LocalDate, DateRange> ofOpenFinish(FACT, start);
            return result;
        }

        /**
         * Convenience function to create a single-date date range. Same as
         * {@code make(date, date)
         */
        public static DateRange ofSingle(LocalDate date) {

            final DateRange result = ARange
                .<LocalDate, DateRange> ofSingle(FACT, date);
            return result;
        }

        @Override
        protected String rangeName() {

            return "DateRange";
        }

        /**
         * MUST ONLY BE CALLED WITH A COLLATED PAIR OF VALUES. Used by
         * {@link ARangeList#collate(List, ARangeFactory)}
         *
         * @param collatedList
         * @see DateRangeFactory
         * @see ARangeList#collate(List, ARangeFactory)
         */
        protected DateRange(Optional<LocalDate> bOpt,
            Optional<LocalDate> eOpt)
        {
            super(bOpt, eOpt);
        }

        @Override
        protected ILeftRange<LocalDate> leftRange() {

            // TODO Auto-generated method stub
            return new RangePak.NormLeftRange<LocalDate, DateRange, DateRangeList>(this, fact().math(), fact());
        }

    }
}
