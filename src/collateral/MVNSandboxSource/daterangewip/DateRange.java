package com.wapitia.dates;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Made final and sealed so that the functionality around returning ALL is
 * not abridged
 */
public class DateRange {
	
	public static final DateRange ALL = new DateRange(Optional.<LocalDate> empty(), Optional.<LocalDate> empty() ) {
		
		@Override
		public boolean isBoundedStart() {
			return false;
		}

		@Override
		public boolean isBoundedFinish() {
			return false;
		}

		@Override
		public boolean isBounded() {
			return false;
		}
		
		@Override
		public boolean isUnbounded() {
			return true; // ALL is the only unbounded DateRange
		}
		
		@Override
		public boolean adjoins(DateRange dateRange) {
			return true;   // ALL adjoins everything
		}
		
		@Override
		public boolean intersects(DateRange dateRange) {
			return true;   // ALL intersects everything
		}
		
		/**
		 * Not bounded so returns Long.MAX_VALUE
		 */
		@Override
		public long days() {
			return Long.MAX_VALUE;
		}
		
		/**
		 * Returns possibly 1 or 2 DateRanges 
		 */
		@Override
		public DateRangeList union(DateRange dr) {
			return DateRangeList.ALL;
		}
		
		/**
		 * Returns 1 DateRange, the samme as the input 
		 */
		// and()
		@Override
		public DateRangeList intersect(DateRange dr) {
			return DateRangeList.of(dr);
		}
		
		// andNot()
		@Override
		public DateRangeList except(DateRange dr) {
			if (dr == this) {
				return DateRangeList.NONE;
			}
			else if (dr.isBoundedStart()) {
				
				DateRange dr1 = DateRange.ofOpenStart(dr.startDate().minusDays(1));
				if (dr.isBoundedFinish()) {
					DateRange dr2 = DateRange.ofOpenEnd(dr.finishDate().plusDays(1));
					return DateRangeList.of(dr1, dr2);
				}
				else {
					return DateRangeList.of(dr1);
					
				}
			}
			else {
				if (dr.isBoundedFinish()) {
					DateRange dr2 = DateRange.ofOpenEnd(dr.finishDate().plusDays(1));
					return DateRangeList.of(dr2);
				}
				else {
					// shouldn't happen!!!
					return DateRangeList.NONE;
				}
				
			}
		}
		
		/**
		 * Returns possibly 0, 1 or 2 DateRanges 
		 */
		@Override
		public DateRangeList not() {
			return DateRangeList.NONE;
		}
		
		@Override
		public boolean contains(DateRange dr) {
			return true;  // ALL contains everything
		}
		
		
		@Override
		public boolean isContainedBy(DateRange dr) {
			return dr == this; // Only ALL can contains this
		}
		
		@Override
		public boolean startsBefore(DateRange dr) {
			return dr.isBoundedStart();
		}
		
		
		@Override
		public boolean endsAfter(DateRange dr) {
			return dr.isBoundedFinish();
		}
		
		@Override
		public boolean isEqual(DateRange dr) {
			return dr == this;
		}
		
		/**
		 * @throws NoSuchElementException if start not bounded
		 */
		@Override
		public LocalDate startDate() {
			throw new NoSuchElementException();
		}
		
		/**
		 * @throws NoSuchElementException if end not bounded
		 */
		@Override
		public LocalDate finishDate() {
			throw new NoSuchElementException();
		}
		
	};


	/**
	 * @param start
	 * @param finish
	 * @return
	 * @throws IllegalArgumentException if finish date is less than start date 
	 */
	public static DateRange of(LocalDate start, LocalDate finish) {
		return new DateRange(
				Optional.<LocalDate> of(Objects.requireNonNull(start)), 
				Optional.<LocalDate> of(Objects.requireNonNull(finish)) );
	}
	
	public static DateRange ofOpenStart(LocalDate finish) {
		return new DateRange(
				Optional.<LocalDate> empty(), 
				Optional.<LocalDate> of(Objects.requireNonNull(finish)) );
	}

	public static DateRange ofOpenEnd(LocalDate start) {
		return new DateRange(
				Optional.<LocalDate> of(Objects.requireNonNull(start)), 
				Optional.<LocalDate> empty() );
	}

	public static DateRange ofSingleDay(LocalDate oneDay) {
		return of(oneDay, oneDay); 
	}
	
	/**
	 * Returns true if b <= e
	 */
	public static boolean isValidRange(LocalDate start, LocalDate finish) {
		return ! start.isAfter(finish); 
	}

	private Optional<LocalDate> startOpt;
	// eOpt is the inclusive upper bound of the range
	
	private Optional<LocalDate> endOpt;
	
	public boolean isBoundedStart() {
		return startOpt.isPresent();
	}

	public boolean isBoundedFinish() {
		return endOpt.isPresent();
	}

	/**
	 * True if bounded on both ends
	 * @return
	 */
	public boolean isBounded() {
		return isBoundedStart() && isBoundedFinish();
	}
	
	public boolean isUnbounded() {
		// only ALL is unbounded
		return false;
	}
	
	/**
	 * this intersects dateRange, or else this.finish is the 
	 * day before dateRange.start, or else this.start is the day
	 * after dateRange.finish.
	 * In other words, there are no days as a gap between this and
	 * the date Range
	 */
	public boolean adjoins(DateRange dateRange) {
		if (intersects(dateRange)) return true;
		boolean adj = this.endOpt.get().isEqual(dateRange.startOpt.get()) 
				   || dateRange.endOpt.get().isEqual(this.startOpt.get()); 
		return adj;		
	}
	
	/**
	 * Date range and this share at least one day.
	 */
	public boolean intersects(DateRange dateRange) {
		return startIsBeforeFinish(this, dateRange) && startIsBeforeFinish(dateRange, this);
	}
	
	static boolean startIsBeforeFinish(DateRange a, DateRange b) {
		final boolean result;
		if (a.isBoundedStart()) {
			if (b.isBoundedFinish()) {
				// both this.start and dr.finish are bounded
				result = a.startDate().isBefore(b.finishDate());
			}
			else {
				// this.start is bounded, but dr.finish is unbounded, so this.startDate is before
				result = true;
			}
		}
		else {
			if (b.isBoundedFinish()) {
				// this.start is unbounded and b is bounded
				result = false;
			}
			else {
				// both are unbounded
				result = true; // ??
			}
		}
		return result;
	}
	
	/**
	 * If not bounded, returns Long.MAX_VALUE
	 * @return
	 */
	public long days() {
		final long result;
		if (startOpt.isPresent() && endOpt.isPresent()) {
			result = startOpt.get().until(endOpt.get(), ChronoUnit.DAYS);
		}
		else {
			result = Long.MAX_VALUE;
		}
		return result;
	}
	
	/**
	 * Returns possibly 1 or 2 DateRanges 
	 * Abutting (or intersecting) DateRanges result in a single DateRange
	 * Disjoint (non-abutting, non-intersecting) DateRanges result in two DateRanges.
	 */
	public DateRangeList union(DateRange dr) {
		if (adjoins(dr)) {
			
		}
		else {
			// when the ranges are disjoint, then this results in a 2-range list
			// with this and dr as its members, unchanged. Need to put them in 
			// Chronological order though.
		}
		// TODO 
		return null;
	}
	
	/**
	 * Returns possibly 0 or 1 DateRanges.
	 */
	// and()
	public DateRangeList intersect(DateRange dr) {
		// TODO 
		return null;
	}
	
	// andNot()
	public DateRangeList except(DateRange dr) {
		// TODO 
		return null;
	}
	
	/**
	 * Returns possibly 0, 1 or 2 DateRanges 
	 */
	public DateRangeList not() {
		// TODO 
		return null;
	}
	
	public boolean contains(DateRange dr) {
		// TODO 
		return false;
	}
	
	
	public boolean isContainedBy(DateRange dr) {
		// TODO 
		return false;
	}
	
	public boolean startsBefore(DateRange dr) {
		// TODO 
		return false;
	}
	
	
	public boolean endsAfter(DateRange dr) {
		// TODO 
		return false;
	}
	
	public boolean isEqual(DateRange dr) {
		return this.startOpt.equals(dr.startOpt) && this.endOpt.equals(dr.endOpt);
	}
	
	/**
	 * @throws NoSuchElementException if start not bounded
	 */
	public LocalDate startDate() {
		return startOpt.get();
	}
	
	/**
	 * @throws NoSuchElementException if end not bounded
	 */
	public LocalDate finishDate() {
		return endOpt.get().minusDays(1L);
	}
	
	public boolean equals(Object dr) {
		return (dr == this) || 
				(dr instanceof DateRange && isEqual( (DateRange) dr) );
	}
	
	public String toString() {
		final String startString = isBoundedStart() ? startDate().toString() : "<<";
		final String finishString = isBoundedFinish() ? finishDate().toString() : ">>";
		final String result = String.format("(DateRange %s..%s)", startString, finishString);
		return result;
	}
	
	
	protected DateRange(Optional<LocalDate> bOpt, Optional<LocalDate> eOpt) {

		this.startOpt = Objects.requireNonNull(bOpt);
		this.endOpt = Objects.requireNonNull(eOpt).map(ld -> ld.plusDays(1L));
		if (bOpt.isPresent() && eOpt.isPresent() && bOpt.get().isAfter(eOpt.get())) {
			// TODO Better Exception
			throw new RuntimeException(String.format(
					"Failure In DateRange: beginning date %s is after end date %s",
					bOpt.get().toString(), eOpt.get().toString()));
		}
	}
	
	

}
