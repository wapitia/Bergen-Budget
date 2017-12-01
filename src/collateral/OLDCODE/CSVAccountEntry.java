package com.wapitia.common.csv;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class CSVAccountEntry extends AccountEntry {

	private final Integer row;
	
	public CSVAccountEntry(Integer row, Integer item, String from, String to, BigDecimal amount, String cycle, String notes,
			Optional<LocalDate> afterDate, Optional<LocalDate> untilDate, Optional<BigDecimal> untilAmount) {
		super(item, from, to, amount, cycle, notes, afterDate, untilDate, untilAmount);
		this.row  = row;
	}
	
	public Integer getRow() {
		return row;
	}

	public String toString() {
		return String.format("#%d %s", getRow(), super.toString());
	}
}
