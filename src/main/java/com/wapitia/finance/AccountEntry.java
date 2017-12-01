package com.wapitia.finance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class AccountEntry {

    private final Integer              item;
    private final String               from;
    private final String               to;
    private final BigDecimal           amount;
    private final String               cycle;
    private final String               notes;
    private final Optional<LocalDate>  afterDate;
    private final Optional<LocalDate>  untilDate;
    private final Optional<BigDecimal> untilAmount;
    private final Optional<BigDecimal> firstPayment;
    private final Optional<BigDecimal> alreadyPaid;
    private final Boolean              skip;
    private final Boolean              skipFirstPayment;

    public AccountEntry(Integer item, String from, String to, BigDecimal amount,
        String cycle, String notes, Optional<LocalDate> afterDate,
        Optional<LocalDate> untilDate, Optional<BigDecimal> untilAmount,
        Optional<BigDecimal> firstPayment, Optional<BigDecimal> alreadyPaid,
        Boolean skip, Boolean skipFirstPayment)
    {
        this.item = item;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.cycle = cycle;
        this.notes = notes;
        this.afterDate = afterDate;
        this.untilDate = untilDate;
        this.untilAmount = untilAmount;
        this.skip = skip;
        this.skipFirstPayment = skipFirstPayment;
        this.firstPayment = firstPayment;
        this.alreadyPaid = alreadyPaid;
    }

    public Integer getItem() {

        return item;
    }

    public String getFrom() {

        return from;
    }

    public String getTo() {

        return to;
    }

    public BigDecimal getAmount() {

        return amount;
    }

    public String getCycle() {

        return cycle;
    }

    public String getNotes() {

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

    public Optional<BigDecimal> getFirstPayment() {

        return firstPayment;
    }

    public Optional<BigDecimal> getAlreadyPaid() {

        return alreadyPaid;
    }

    public Boolean getSkip() {

        return skip;
    }

    public Boolean getSkipFirstPayment() {

        return skipFirstPayment;
    }

    @Override
    public String toString() {

        final String result = String.format(
            "Item %s: From %s, To %s, Amt: %s, Cycle: %s, Notes: %s, "
                + "After: %s, Until %s, Until %s, First Pmt %s, Already Pd %s, "
                + "Skip %s, Skip First Pmt %s",
            item.toString(), from, to, amount.toString(), cycle, notes,
            afterDate.toString(), untilDate.toString(), untilAmount.toString(),
            firstPayment.toString(), alreadyPaid.toString(), skip,
            skipFirstPayment);
        return result;
    }
}
