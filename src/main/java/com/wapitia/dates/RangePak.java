package com.wapitia.dates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Incremental Ranges object package.
 *
 * @author Corey Morgan
 *
 */
public interface RangePak {

    public abstract class ARangeMath<T> {

        public abstract long diff(T a, T b);

        public abstract T incr(T a);

        public abstract T decr(T a);

        public boolean isEqual(T a, T b) {

            return diff(a, b) == 0L;
        }

        public boolean isBefore(T a, T b) {

            return diff(a, b) < 0L;
        }

        public boolean isAfter(T a, T b) {

            return diff(a, b) > 0L;
        }

        public T minValue(T a, T c) {

            return isBefore(c, a) ? c : a;
        }

        public T maxValue(T a, T c) {

            return isAfter(c, a) ? c : a;
        }

        public long size(Optional<T> startOpt, Optional<T> endOpt) {

            final long result;
            if (startOpt.isPresent() && endOpt.isPresent()) {
                result = diff(endOpt.get(), startOpt.get());
            } else {
                result = Long.MAX_VALUE;
            }
            return result;
        }

    }

    public interface ILeftRange<T> {

        T start();

        T finish();

        long size();

        boolean isBoundedStart();

        boolean isBoundedFinish();

        boolean isBounded();

        boolean isEqual(ARange<T> other);

        boolean endsAfter(ARange<T> other);

        boolean startsBefore(ARange<T> other);

        boolean isContainedBy(ARange<T> other);

        boolean contains(ARange<T> other);

        boolean adjoins(ARange<T> other);

        boolean intersects(ARange<T> other);

        Optional<T> lowestStart(final ARange<T> other);

        Optional<T> highestFinish(final ARange<T> other);

        boolean startIsBeforeFinishOf(ARange<T> other);

        boolean finishIsBeforeStartOf(ARange<T> other);

        <RC extends ARange<T>>
        ARange<T>
        span(final ARange<T> other, final ARangeFactory<T, RC, ?> fact);

        <RC extends ARange<T>, LC extends ARangeList<T>>
        ARangeList<T>
        union(final ARange<T> other, final ARangeFactory<T, RC, LC> fact);

        <RC extends ARange<T>, LC extends ARangeList<T>>
        ARangeList<T>
        intersect(final ARange<T> other, final ARangeFactory<T, RC, LC> fact);

        <RC extends ARange<T>, LC extends ARangeList<T>>
        ARangeList<T>
        exclude(final ARange<T> other, final ARangeFactory<T, RC, LC> fact);

        <RC extends ARange<T>, LC extends ARangeList<T>>
        ARangeList<T>
        not(final ARangeFactory<T, RC, LC> fact);

    }

    public class NormLeftRange<T, RC extends ARange<T>, LC extends ARangeList<T>> implements ILeftRange<T> {

        private final ARange<T>     range;
        private final ARangeMath<T> math;
        private final ARangeFactory<T, RC, LC> fact;

        public NormLeftRange(final ARange<T> range, final ARangeMath<T> math, final ARangeFactory<T, RC, LC> fact) {
            this.range = range;
            this.math = math;
            this.fact = fact;
        }

        /**
         * @throws NoSuchElementException
         *             if start not bounded
         */
        @Override
        public T start() {

            return range.startOpt.get();
        }

        /**
         * @throws NoSuchElementException
         *             if end not bounded
         */
        @Override
        public T finish() {

            return math.decr(range.endOpt.get());
        }

        @Override
        public long size() {

            final long result = math.size(range.startOpt, range.endOpt);
            return result;
        }

        @Override
        public boolean isBoundedStart() {

            return range.startOpt.isPresent();
        }

        /**
         * is this range bounded on the finish.
         */
        @Override
        public boolean isBoundedFinish() {

            return range.endOpt.isPresent();
        }

        /**
         * True if bounded on start and finish, false if either start or finish
         * is unbounded
         */
        @Override
        public boolean isBounded() {

            return isBoundedStart() && isBoundedFinish();
        }

        @Override
        public boolean isEqual(final ARange<T> other) {

            return range.startOpt.equals(other.startOpt)
                && range.endOpt.equals(other.endOpt);
        }

        @Override
        public boolean endsAfter(final ARange<T> other) {

            // TODO
            return false;
        }

        @Override
        public boolean startsBefore(final ARange<T> other) {

            // TODO
            return false;
        }

        @Override
        public boolean isContainedBy(final ARange<T> other) {

            // TODO
            return false;
        }

        @Override
        public boolean contains(final ARange<T> other) {

            // TODO
            return false;
        }

        /**
         * True when this shares a value with the given range (intesects) or if
         * given range abuts this range, so that either the given range's start
         * is the value after this finish, or else this finish is the value
         * before the given range's start.
         */
        @Override
        public boolean adjoins(final ARange<T> other) {

            if (range.intersects(other))
                return true;
            boolean adj = math.isEqual(range.endOpt.get(), other.startOpt.get())
                || math.isEqual(other.endOpt.get(), range.startOpt.get());
            return adj;
        }

        /**
         * Return true when this range and some other range share at least one
         * integer value.
         */
        @Override
        public boolean intersects(final ARange<T> other) {

            return range.startIsBeforeFinishOf(Objects.requireNonNull(other))
                && other.startIsBeforeFinishOf(range);
        }

        @Override
        public Optional<T> lowestStart(final ARange<T> other) {

            final Optional<T> lowStart;
            if (range.isBoundedStart() && other.isBoundedStart()) {
                lowStart = Optional
                    .<T> of(math.minValue(range.start(), other.start()));
            } else {
                // one or the other starts is unbounded.
                lowStart = Optional.<T> empty();
            }
            return lowStart;
        }

        @Override
        public Optional<T> highestFinish(final ARange<T> other) {

            final Optional<T> fin;
            if (range.isBoundedFinish()
                && Objects.requireNonNull(other).isBoundedFinish()) {
                fin = Optional
                    .<T> of(math.maxValue(range.finish(), other.finish()));
            } else {
                // one or the other finishes is unbounded.
                fin = Optional.<T> empty();
            }
            return fin;
        }

        @Override
        public boolean startIsBeforeFinishOf(ARange<T> other) {

            final boolean result = other.isBoundedFinish()
                && ((!this.isBoundedStart())
                    || math.isBefore(this.start(), other.finish()));
            return result;
        }

        @Override
        public boolean finishIsBeforeStartOf(ARange<T> other) {

            final boolean result = this.isBoundedFinish()
                && other.isBoundedStart()
                && math.isBefore(this.finish(), other.start());
            return result;
        }

        /**
         * Return the smallest range that spans (encompasses, groups) this range
         * and other range.
         *
         * @param other
         */
        @Override
        public <RC extends ARange<T>> ARange<T> span(final ARange<T> other,
            ARangeFactory<T, RC, ?> fact)
        {

            final Optional<T> s = range
                .lowestStart(Objects.requireNonNull(other));
            final Optional<T> f = range.highestFinish(other);
            final ARange<T> result = fact.makeRange(s, f);
            return result;

        }

        /**
         * Returns possibly 1 or 2 ranges Abutting (or intersecting) ranges
         * result in a single range, in which case the single result is the same
         * as {@link #span(range)}.
         * <p>
         * Disjoint (non-abutting, non-intersecting) ranges result in two
         * ranges, whose outside values are given by {@link #span(range)}.
         * <p>
         * If this range falls before the other range (this.end < other.start),
         * then the first result range is (span.start, this.end), and the second
         * result range is (other.start, span.end)
         * <p>
         * If the range falls entirely after the other range (other.end <
         * this.start), then the first result range is (span.start, other.end),
         * and the second result range is (this.start, span.end)
         *
         */
        @Override
        public <RC extends ARange<T>, LC extends ARangeList<T>> ARangeList<T> union(
            final ARange<T> other, ARangeFactory<T, RC, LC> fact)
        {

            final ARangeList<T> result;
            final Optional<T> s = range.lowestStart(other);
            final Optional<T> f = range.highestFinish(other);
            if (range.isBoundedFinish() && other.isBoundedStart()
                && math.isBefore(math.incr(range.finish()), other.start())) {
                final ARange<T> range1 = fact.makeRange(s,
                    Optional.of(range.finish()));
                final ARange<T> range2 = fact
                    .makeRange(Optional.of(other.start()), f);
                result = fact.makeRangeList(Arrays.asList(range1, range2));
            } else if (other.isBoundedFinish() && range.isBoundedStart()
                && math.isBefore(math.incr(other.finish()), range.start())) {
                final ARange<T> range1 = fact.makeRange(s,
                    Optional.of(other.finish()));
                final ARange<T> range2 = fact
                    .makeRange(Optional.of(range.start()), f);
                result = fact.makeRangeList(Arrays.asList(range1, range2));
            } else if (s.isPresent() || f.isPresent()) {
                final ARange<T> rangeA = fact.makeRange(s, f);
                result = fact.makeRangeList(Collections.singletonList(rangeA));
            } else {
                result = fact.allList();
            }

            return result;
        }

        /**
         * Returns possibly 0 or 1 ranges.
         */
        // and()
        @Override
        public <RC extends ARange<T>, LC extends ARangeList<T>> ARangeList<T> intersect(
            final ARange<T> other, ARangeFactory<T, RC, LC> fact)
        {

            // TODO
            return null;
        }

        // andNot()
        @Override
        public <RC extends ARange<T>, LC extends ARangeList<T>> ARangeList<T> exclude(
            final ARange<T> other, ARangeFactory<T, RC, LC> fact)
        {

            // TODO
            return null;
        }

        /**
         * Returns possibly 0, 1 or 2 ranges
         */
        @Override
        public <RC extends ARange<T>, LC extends ARangeList<T>> ARangeList<T> not(
            ARangeFactory<T, RC, LC> fact)
        {

            // TODO
            return null;
        }

    }

    public class AllLeftRange<T, RC extends ARange<T>, LC extends ARangeList<T>> implements ILeftRange<T> {

        private final ARangeFactory<T, RC, LC> fact;

        public AllLeftRange(final ARangeFactory<T, RC, LC> fact) {
            this.fact = fact;
        }

        @Override
        public T start() {

            throw new NoSuchElementException();
        }

        @Override
        public T finish() {

            throw new NoSuchElementException();
        }

        @Override
        public long size() {

            return Long.MAX_VALUE;
        }

        @Override
        public boolean isBoundedStart() {

            return false;
        }

        /**
         * is this range bounded on the finish.
         */
        @Override
        public boolean isBoundedFinish() {

            return false;
        }

        /**
         * True if bounded on start and finish, false if either start or finish
         * is unbounded
         */
        @Override
        public boolean isBounded() {

            return false;
        }

        @Override
        public boolean isEqual(ARange<T> other) {

            return other == fact.all();
        }

        @Override
        public boolean endsAfter(ARange<T> other) {

            return other.isBoundedFinish();
        }

        @Override
        public boolean startsBefore(ARange<T> other) {

            return other.isBoundedStart();
        }

        @Override
        public boolean isContainedBy(ARange<T> other) {

            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean contains(ARange<T> other) {

            Objects.requireNonNull(other);
            return true;
        }

        /**
         * true. ALL adjoins everything
         */
        @Override
        public boolean adjoins(final ARange<T> other) {

            Objects.requireNonNull(other);
            return true; // ALL adjoins everything
        }

        /**
         * true. ALL intersects everything
         */
        @Override
        public boolean intersects(final ARange<T> other) {

            Objects.requireNonNull(other);
            return true; // ALL intersects everything
        }

        @Override
        public Optional<T> lowestStart(final ARange<T> na) {

            return Optional.<T> empty();
        }

        @Override
        public Optional<T> highestFinish(final ARange<T> na) {

            return Optional.<T> empty();
        }

        @Override
        public boolean startIsBeforeFinishOf(final ARange<T> other) {

            return true;
        }

        @Override
        public boolean finishIsBeforeStartOf(final ARange<T> other) {

            return false;
        }

        /**
         * returns ALL
         */
       @Override
        public <RC extends ARange<T>>
        ARange<T> span(final ARange<T> other, final ARangeFactory<T, RC, ?> fact) {

            return fact.all();
        }

        @Override
        public <RC extends ARange<T>, LC extends ARangeList<T>>
        ARangeList<T> union(final ARange<T> other, final ARangeFactory<T, RC, LC> fact) {

            return fact.allList();
        }

        @Override
        public <RC extends ARange<T>, LC extends ARangeList<T>>
        ARangeList<T> intersect(ARange<T> other, ARangeFactory<T, RC, LC> fact)
        {

            final ARangeList<T> makeRangeList = fact
                .makeRangeList(
                    Collections.<ARange<T>> singletonList(other));
            return makeRangeList;
        }

        @Override
        public <RC extends ARange<T>, LC extends ARangeList<T>>
        ARangeList<T> exclude(ARange<T> other, ARangeFactory<T, RC, LC> fact)
        {
            // if (other == ALL) {
            // return RangePak.<LocalDate,ARangeList<LocalDate>>
            // LIST_CAST(ARangeList.NONE);
            if (other == fact.all()) {
                return fact.allList();
            } else {
                // WARNING: Only ALL can do this reflection: all others
                // must guard against other == ALL so that this doesn't
                // turn into an endless loop
                return null;
            }
        }

        /**
         * Returns NONE
         */
        @Override
        public <RC extends ARange<T>, LC extends ARangeList<T>>
        ARangeList<T> not(ARangeFactory<T, RC, LC> fact)
        {

            return fact.none();
        }
    }

    public abstract class ARangeFactory<T, RC extends ARange<T>, LC extends ARangeList<T>> {

        public abstract RC makeRange(Optional<T> startOpt,
            Optional<T> finishOpt);

        public abstract LC makeRangeList(final List<ARange<T>> rangeList);

        public abstract RC all();

        public abstract LC allList();

        public abstract LC none();

        private final ARangeMath<T> math;

        protected ARangeFactory(final ARangeMath<T> math) {
            this.math = math;
        }

        public ARangeMath<T> math() {

            return math;
        }

    }

    /**
     * Two values that define a contiguous range of incremental values. The
     * start value may not fall after the finish value: It is not possible to
     * define a reverse value range.
     *
     * <p>
     * The range may be open on either end. An open start indicates that the
     * range extends indefinitely in the past. An open finish indicates that the
     * range won't end.
     *
     * <p>
     * The Range class is immutable. The combining functions typically create
     * new Range instances to return changed values, or return an already
     * created Range instance (one of the inputs or {@code ALL}) if one on hand
     * matches the result.
     *
     * <p>
     * The static Range member {@code ALL} is open-ended for both the start and
     * finish values. {@code ALL} is the only doubly open-ended Range. The API
     * does not allow you to create one, and the functions that result in a
     * doubly open-ended Range will return {@code ALL} rather create one.
     *
     * <p>
     * The start and finish values are <i>inclusive</i> in the range.
     *
     * @param <T>
     */
    public abstract class ARange<T> {

        /**
         * Create a new range from start to finish inclusive.
         *
         * @param start
         *            Start value inclusive, must not be null
         * @param finish
         *            Finish value inclusive, must not be null
         * @return a new range from start to finish inclusive.
         * @throws IllegalArgumentException
         *             when start is later than finish
         */
        public static <TS, RC extends ARange<TS>>
        RC of(ARangeFactory<TS, RC, ?> fact, TS start, TS finish) {

            Objects.requireNonNull(start);
            Objects.requireNonNull(finish);

            if (Objects.requireNonNull(fact).math().isAfter(start, finish)) {
                throw new RangeConstructionException("Start is after Finish");
            }
            return fact.makeRange(Optional.<TS> of(start),
                Optional.<TS> of(finish));
        }

        public Optional<T> lowestStart(ARange<T> other) {

            return this.leftRange().lowestStart(other);
        }

        public Optional<T> highestFinish(ARange<T> other) {

            return this.leftRange().highestFinish(other);
        }

        /**
         * Create a new DateRange with an open start date and an inclusive
         * finish date.
         *
         * @param finish
         *            Finish date inclusive, must not be null
         * @return a new DateRange open to finish inclusive.
         */
        public static <TS, RC extends ARange<TS>> RC ofOpenStart(
            ARangeFactory<TS, RC, ?> fact, TS finish)
        {

            return Objects.requireNonNull(fact).makeRange(Optional.<TS> empty(),
                Optional.<TS> of(Objects.requireNonNull(finish)));
        }

        /**
         * Create a new DateRange with an inclusive start and an open finish
         * date.
         *
         * @param start
         *            Start date inclusive, must not be null
         * @return a new DateRange start inclusive to an open finish.
         */
        public static <TS, RC extends ARange<TS>> RC ofOpenFinish(
            ARangeFactory<TS, RC, ?> fact, TS start)
        {

            return Objects.requireNonNull(fact).makeRange(
                Optional.<TS> of(Objects.requireNonNull(start)),
                Optional.<TS> empty());
        }

        /**
         * Convenience function to create a single-date date range. Same as
         * {@code make(date, date)
         */
        public static <TS, RC extends ARange<TS>> RC ofSingle(
            ARangeFactory<TS, RC, ?> fact, TS value)
        {

            return of(fact, value, value);
        }

        protected abstract String rangeName();

        protected abstract ARangeFactory<T, ?, ?> fact();

        protected abstract ILeftRange<T> leftRange();

        /**
         * Defines the Start value. When Optional.empty, this is an open start.
         * Otherwise the Optional value is the inclusive start value.
         */
        protected Optional<T>       startOpt;

        /**
         * Defines the Finish value. When Optional.empty, this is an open
         * finish. Otherwise the Optional value is the <i>exclusive<i> bound,
         * that is the <i>day after</i> the inclusive value.
         */
        protected final Optional<T> endOpt;

        /**
         * Returns true if start <= finish, so that these would be valid
         * arguments into the constructor.
         *
         * @param start
         *            inclusive start value
         * @param finish
         *            inclusive finish value
         */
        public boolean isValidRange(T start, T finish) {

            return !fact().math().isAfter(start, finish);
        }

        /**
         * is this range bounded on the start.
         */
        public boolean isBoundedStart() {

            return this.leftRange().isBoundedStart();
        }

        /**
         * is this range bounded on the finish.
         */
        public boolean isBoundedFinish() {

            return this.leftRange().isBoundedFinish();
        }

        /**
         * True if bounded on start and finish, false if either start or finish
         * is unbounded
         */
        public boolean isBounded() {

            return this.leftRange().isBounded();
        }

        /**
         * If not bounded, returns Long.MAX_VALUE
         *
         * @return
         */
        public long size() {

            return this.leftRange().size();
        }

        /**
         * Return the smallest range that spans (encompasses, groups) this range
         * and other range.
         *
         * @param other
         */
        public ARange<T> span(final ARange<T> other) {

//            return fact().span(this, other);
            return this.leftRange().span(other, fact());
        }

        /**
         * Return true when this range and some other range share at least one
         * day.
         */
        public boolean intersects(ARange<T> other) {

            return this.leftRange().intersects(other);
        }

        /**
         * Returns possibly 1 or 2 ranges Abutting (or intersecting) ranges
         * result in a single range, in which case the single result is the same
         * as {@link #span(range)}.
         * <p>
         * Disjoint (non-abutting, non-intersecting) ranges result in two
         * ranges, whose outside values are given by {@link #span(range)}.
         * <p>
         * If this range falls before the other range (this.end < other.start),
         * then the first result range is (span.start, this.end), and the second
         * result range is (other.start, span.end)
         * <p>
         * If the range falls entirely after the other range (other.end <
         * this.start), then the first result range is (span.start, other.end),
         * and the second result range is (this.start, span.end)
         *
         */
        public ARangeList<? super T> union(ARange<T> other) {

            return this.leftRange().union(other, fact());
        }

        /**
         * Returns possibly 0 or 1 ranges.
         */
        // and()
        public ARangeList<T> intersect(ARange<T> other) {

            return this.leftRange().intersect(other, fact());
        }

        // andNot()
        public ARangeList<T> exclude(ARange<T> other) {

            return this.leftRange().exclude(other, fact());
        }

        /**
         * Returns possibly 0, 1 or 2 ranges
         */
        public ARangeList<T> not() {

            return this.leftRange().not(fact());
        }

        /**
         * @throws NoSuchElementException
         *             if start not bounded
         */
        public T start() {

            return this.leftRange().start();
       }

        /**
         * @throws NoSuchElementException
         *             if end not bounded
         */
        public T finish() {

            return this.leftRange().finish();
        }

        /**
         * True when this shares a value with the given range (intesects) or if
         * given range abuts this range, so that either the given range's start
         * is the value after this finish, or else this finish is the value
         * before the given range's start.
         */
        public boolean adjoins(ARange<T> other) {

            return this.leftRange().adjoins(other);
        }

        public boolean contains(ARange<T> other) {

            return this.leftRange().contains(other);
        }

        public boolean isContainedBy(ARange<T> other) {

            return this.leftRange().isContainedBy(other);
        }

        public boolean startsBefore(ARange<T> other) {

            return this.leftRange().startsBefore(other);
        }

        public boolean endsAfter(ARange<T> other) {

            return this.leftRange().endsAfter(other);
        }

        public boolean isEqual(ARange<T> other) {

            return this.leftRange().isEqual(other);
        }

        @Override
        public String toString() {

            final String s = isBoundedStart() ? start().toString() : "<<";
            final String f = isBoundedFinish() ? finish().toString() : ">>";
            return String.format("(" + rangeName() + " %s..%s)", s, f);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object other) {

            return (other == this)
                || (other instanceof ARange<?> && isEqual((ARange<T>) other));

        }

        protected boolean startIsBeforeFinishOf(ARange<T> other) {

            return this.leftRange().startIsBeforeFinishOf(other);
        }

        protected boolean finishIsBeforeStartOf(ARange<T> other) {

            return this.leftRange().finishIsBeforeStartOf(other);
        }

        protected ARange(Optional<T> startOpt, Optional<T> finishOpt) {
            this.startOpt = Objects.requireNonNull(startOpt);
            this.endOpt = Objects.requireNonNull(finishOpt)
                .map(ld -> fact().math().incr(ld));
            if (startOpt.isPresent() && finishOpt.isPresent()
                && fact().math().isAfter(startOpt.get(), finishOpt.get())) {

                throw new RangeConstructionException(String.format(
                    "Failure In ARange: start %s is after finish %s",
                    startOpt.get().toString(), finishOpt.get().toString()));
            }
        }
    }

    public abstract class ARangeList<T> {

        protected abstract ARangeFactory<T, ?, ?> fact();

        public static <TS, LC extends ARangeList<TS>> LC of(
            ARangeFactory<TS, ?, LC> fact,
            @SuppressWarnings("unchecked") ARange<TS>... ranges)
        {

            final LC result = ARangeList
                .<TS, LC> collate(Arrays.<ARange<TS>> asList(ranges), fact);
            return result;
        }

        static <TS, LC extends ARangeList<TS>> LC collate(
            final List<ARange<TS>> unorderedRanges,
            final ARangeFactory<TS, ?, LC> fact)
        {

            final LC none = fact.none();
            final LC result = collate(none, unorderedRanges, fact);
            return result;
        }

        static <TS, LC extends ARangeList<TS>> LC collate(
            final LC collatedSource, final List<ARange<TS>> unorderedRanges,
            final ARangeFactory<TS, ?, LC> fact)
        {

            Objects.requireNonNull(collatedSource);
            Objects.requireNonNull(unorderedRanges);

            LC collated = collatedSource;
            for (ARange<TS> single : unorderedRanges) {
                List<ARange<TS>> nextRes = new ArrayList<ARange<TS>>();
                collateR(single, collated, nextRes, fact);
                collated = fact.makeRangeList(nextRes);
            }
            final LC result = normalize(collated, fact);
            return result;
        }

        static <TS, LC extends ARangeList<TS>> LC normalize(LC collated,
            final ARangeFactory<TS, ?, LC> fact)
        {

            final int nRanges = collated.numRanges();
            final LC result;
            if (nRanges == 0) {
                result = fact.none();
            } else {
                final ARange<? super TS> range1 = collated.ranges().get(0);
                if (nRanges == 1 && !range1.isBoundedStart()
                    && !range1.isBoundedFinish()) {
                    result = fact.allList();
                } else {
                    result = collated;
                }
            }
            return result;
        }

        static <TS, LC extends ARangeList<TS>> void collateR(
            final ARange<TS> single, final LC sourceRangeList,
            List<ARange<TS>> accum, final ARangeFactory<TS, ?, ?> fact)
        {

            Objects.requireNonNull(single);
            Objects.requireNonNull(sourceRangeList);
            Objects.requireNonNull(accum);
            Objects.requireNonNull(fact);

            @SuppressWarnings("unchecked")
            final List<ARange<TS>> collatedSource = (List<ARange<TS>>) (List<?>) sourceRangeList
                .ranges();

            if (collatedSource.size() == 0) {
                // we're done
                accum.add(single);
            } else {
                final ARange<TS> head = collatedSource.get(0);
                if (single.finishIsBeforeStartOf(head)) {
                    // single belongs wholly before head (and the rest)
                    // so we're done
                    accum.add(single);
                    accum.addAll(collatedSource);
                } else {
                    final ARangeList<TS> collatedTail = fact.makeRangeList(
                        collatedSource.subList(1, collatedSource.size()));
                    if (head.finishIsBeforeStartOf(single)) {
                        // head belongs wholly before single (and the rest)
                        accum.add(head);
                        collateR(single, collatedTail, accum, fact);
                    } else {
                        // single and head intersect, resulting in a lone
                        // unioned range.
                        final Optional<TS> s = single.lowestStart(head);
                        final Optional<TS> f = single.highestFinish(head);
                        final ARange<TS> comb = fact.makeRange(s, f);
                        collateR(comb, collatedTail, accum, fact);
                    }
                }
            }
        }

        private final List<? extends ARange<T>> ranges;

        // WARNING: PREREDUCED (PREORDERED) RANGES ONLY!
        protected ARangeList(final List<? extends ARange<T>> collatedList) {
            this.ranges = collatedList;
        }

        protected ARangeList(final ARange<T> range) {
            this(Collections.<ARange<T>> singletonList(range));
        }

        protected ARangeList(final ARange<T> range1, final ARange<T> range2) {
            this(Arrays.<ARange<T>> asList(range1, range2));
        }

        public int numRanges() {

            return ranges.size();
        }

        public boolean isBoundedStart() {

            // TODO
            return true;
        }

        public boolean isBoundedEnd() {

            // TODO
            return true;
        }

        public ARangeList<T> or(ARangeList<T> otherList) {

            // TODO
            return null;
        }

        public ARangeList<T> intersect(ARangeList<T> otherList) {

            // TODO
            return null;
        }

        public ARangeList<T> exclude(ARangeList<T> otherList) {

            // TODO
            return null;
        }

        public ARangeList<T> intersect(ARange<T> range) {

            // TODO
            return null;
        }

        public ARangeList<T> exclude(ARange<T> range) {

            // TODO
            return null;
        }

        public ARangeList<T> not() {

            // TODO
            return null;
        }

        public long size() {

            // TODO
            return Long.MAX_VALUE;
        }

        public boolean isBounded() {

            // TODO
            return true;
        }

        public boolean contains(ARangeList<T> otherList) {

            // TODO
            return true;
        }

        public boolean isContainedBy(ARangeList<T> otherList) {

            // TODO
            return true;
        }

        public boolean contains(ARange<T> range) {

            // TODO
            return true;
        }

        public boolean isContainedBy(ARange<T> range) {

            // TODO
            return true;
        }

        public boolean startsBefore(ARangeList<T> otherList) {

            // TODO
            return true;
        }

        public boolean isUnbounded() {

            // TODO
            return false;
        }

        public boolean endsAfter(ARangeList<T> otherList) {

            // TODO
            return true;
        }

        public boolean startsBefore(ARange<T> range) {

            // TODO
            return true;
        }

        public boolean endsAfter(ARange<T> range) {

            // TODO
            return true;
        }

        public boolean isEqual(ARangeList<T> otherList) {

            // TODO
            return true;
        }

        /**
         * @throws NoSuchElementException
         *             if start not bounded
         */
        public ARange<T> start() {

            // TODO
            return null;
        }

        /**
         * @throws NoSuchElementException
         *             if end not bounded
         */
        public ARange<T> finish() {

            // TODO
            return null;
        }

        @Override
        public boolean equals(Object o) {

            final boolean eq = (o == this) || ((o instanceof ARangeList<?>)
                && ranges.equals(((ARangeList<?>) o).ranges()));
            return eq;
        }

        @Override
        public String toString() {

            final String result = ranges.toString();
            return result;
        }

        protected List<? extends ARange<T>> ranges() {

            return ranges;
        }

    }

    public static class RangeConstructionException extends RuntimeException {

        public RangeConstructionException(String message) {
            super(message);
        }

        private static final long serialVersionUID = 112484177485086317L;

    }

}
