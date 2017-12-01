package com.wapitia.common.csv;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;


public class AccountEntryBuilder<ITEM extends AccountEntry, PRT extends AccountEntryBuilder<ITEM,PRT>> {

	private Optional<Integer> item;
	private Optional<String> from;
	private Optional<String> to;
	private Optional<BigDecimal> amount;
	private Optional<String> cycle;
	private Optional<String> notes;
	private Optional<LocalDate> afterDate;
	private Optional<LocalDate> untilDate;
	private Optional<BigDecimal> untilAmount;
	
	public 
	ITEM makeEntry() {
		@SuppressWarnings("unchecked")  // Cast to ITEM safe here, since makeEntry() is called on behalf of
		                                // the first generic argument
		ITEM result = (ITEM) new AccountEntry(
				getItem().orElseThrow(() -> new CSVMoneyItemBuildException("Missing Item")),
				getFrom().orElseThrow(() -> new CSVMoneyItemBuildException("Missing From")),
				getTo().orElseThrow(() -> new CSVMoneyItemBuildException("Missing To")),
				getAmount().orElseThrow(() -> new CSVMoneyItemBuildException("Missing Amount")),
				getCycle().orElseThrow(() -> new CSVMoneyItemBuildException("Missing Cycle")),
				getNotes().orElse(""),
				getAfterDate(),
				getUntilDate(), 
				getUntilAmount());
		return result;
	}
	
	public Optional<Integer> getItem() {
		return item;
	}

	public Optional<String> getFrom() {
		return from;
	}

	public Optional<String> getTo() {
		return to;
	}

	public Optional<BigDecimal> getAmount() {
		return amount;
	}

	public Optional<String> getCycle() {
		return cycle;
	}

	public Optional<String> getNotes() {
		return notes;
	}

	public Optional<LocalDate> getAfterDate() {
		return afterDate;
	}

	public Optional<LocalDate> getUntilDate() {
		return untilDate;
	}

	public Optional<BigDecimal> getUntilAmount() {
		return untilAmount;
	}

	public AccountEntryBuilder<ITEM,PRT> item(String itemStr) {
		try {
			this.item = Optional.of(Integer.parseInt(itemStr));
		}
		catch (NumberFormatException e) {
			throw new CSVMoneyItemBuildException(e.getMessage());
		}
		return this;
	}
	
	public AccountEntryBuilder<ITEM,PRT> from(String fromStr) {
		this.from = Optional.ofNullable(fromStr);
		return this;
	}
	
	public AccountEntryBuilder<ITEM,PRT> to(String toStr) {
		this.to = Optional.ofNullable(toStr);
		return this;
	}
	
	public AccountEntryBuilder<ITEM,PRT> amount(String amtStr) {
		try {
			this.amount = Optional.of(new BigDecimal(amtStr));
		}
		catch (NumberFormatException e) {
			throw new CSVMoneyItemBuildException(e.getMessage());
		}
		return this;
	}
	
	public AccountEntryBuilder<ITEM,PRT> cycle(String cycle) {
		this.cycle = Optional.ofNullable(cycle);
		return this;
	}
	
	public AccountEntryBuilder<ITEM,PRT> notes(String notes) {
		this.notes = Optional.ofNullable(notes);
		return this;
	}
	
	public AccountEntryBuilder<ITEM,PRT> afterDate(String afterDateStr) {
		if (! isBlank(afterDateStr)) {
			try {
				this.afterDate = Optional.of(LocalDate.parse(afterDateStr));
			}
			catch (DateTimeParseException e) {
				throw new CSVMoneyItemBuildException(e.getMessage());
			}
		}
		return this;
	}
	
	public AccountEntryBuilder<ITEM,PRT> untilDate(String untilDateStr) {
		if (! isBlank(untilDateStr)) {
			try {
				this.untilDate = Optional.of(LocalDate.parse(untilDateStr));
			}
			catch (DateTimeParseException e) {
				throw new CSVMoneyItemBuildException(e.getMessage());
			}
		}
		return this;
	}
	
	public AccountEntryBuilder<ITEM,PRT> untilAmount(String untilAmtStr) {
		if (! isBlank(untilAmtStr)) {
			try {
				this.untilAmount = Optional.of(new BigDecimal(untilAmtStr));
			}
			catch (NumberFormatException e) {
				throw new CSVMoneyItemBuildException(e.getMessage());
			}
		}
		return this;
	}
	
	
	
	// StringUtils.isBlank
	public static boolean isBlank(String s) {
		final boolean result = (s == null) || s.trim().length() == 0;
		return result;
				
	}

	public AccountEntryBuilder() {
		
		this.item = Optional.<Integer>empty();
		this.from = Optional.<String> empty();
		this.to = Optional.<String> empty();
		this.amount = Optional.<BigDecimal> empty();
		this.cycle = Optional.<String> empty();
		this.notes = Optional.<String> empty();
		this.afterDate = Optional.<LocalDate> empty();
		this.untilDate = Optional.<LocalDate> empty();
		this.untilAmount = Optional.<BigDecimal> empty();
	}
	
}