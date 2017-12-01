package com.wapitia.finance;

import com.wapitia.payment.io.CSVMoneyItemBuildException;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AccountEntryBuilder<ITEM extends AccountEntry, PRT extends AccountEntryBuilder<ITEM, PRT>> {

    private Optional<Integer>    item;
    private Optional<String>     from;
    private Optional<String>     to;
    private Optional<BigDecimal> amount;
    private Optional<String>     cycle;
    private Optional<String>     notes;
    private Optional<LocalDate>  afterDate;
    private Optional<LocalDate>  untilDate;
    private Optional<BigDecimal> untilAmount;
    private Optional<BigDecimal> firstPayment;
    private Optional<BigDecimal> alreadyPaid;
    private Boolean              skip;
    private Boolean              skipFirstPayment;

    public ITEM makeEntry() {

        @SuppressWarnings("unchecked") // Cast to ITEM safe here, since
                                       // makeEntry() is called on behalf of
                                       // the first generic argument
        ITEM result = (ITEM) new AccountEntry(
            getItem().orElseThrow(
                () -> new CSVMoneyItemBuildException("Missing Item")),
            getFrom().orElseThrow(
                () -> new CSVMoneyItemBuildException("Missing From")),
            getTo().orElseThrow(
                () -> new CSVMoneyItemBuildException("Missing To")),
            getAmount().orElseThrow(
                () -> new CSVMoneyItemBuildException("Missing Amount")),
            getCycle().orElseThrow(
                () -> new CSVMoneyItemBuildException("Missing Cycle")),
            getNotes().orElse(""), getAfterDate(), getUntilDate(),
            getUntilAmount(), getFirstPayment(), getAlreadyPaid(), getSkip(),
            getSkipFirstPayment());
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

    public AccountEntryBuilder<ITEM, PRT> item(String itemStr) {

        try {
            this.item = Optional.of(Integer.parseInt(itemStr));
        } catch (NumberFormatException e) {
            throw new CSVMoneyItemBuildException(e.getMessage());
        }
        return this;
    }

    public AccountEntryBuilder<ITEM, PRT> from(String fromStr) {

        this.from = Optional.ofNullable(fromStr);
        return this;
    }

    public AccountEntryBuilder<ITEM, PRT> to(String toStr) {

        this.to = Optional.ofNullable(toStr);
        return this;
    }

    public AccountEntryBuilder<ITEM, PRT> amount(String amtStr) {

        try {
            this.amount = Optional.of(new BigDecimal(amtStr));
        } catch (NumberFormatException e) {
            throw new CSVMoneyItemBuildException(e.getMessage());
        }
        return this;
    }

    public AccountEntryBuilder<ITEM, PRT> cycle(String cycle) {

        this.cycle = Optional.ofNullable(cycle);
        return this;
    }

    public AccountEntryBuilder<ITEM, PRT> notes(String notes) {

        this.notes = Optional.ofNullable(notes);
        return this;
    }

    public AccountEntryBuilder<ITEM, PRT> afterDate(String afterDateStr) {

        if (!StringUtils.isBlank(afterDateStr)) {
            try {
                this.afterDate = Optional.of(LocalDate.parse(afterDateStr));
            } catch (DateTimeParseException e) {
                throw new CSVMoneyItemBuildException(e.getMessage());
            }
        }
        return this;
    }

    public AccountEntryBuilder<ITEM, PRT> untilDate(String untilDateStr) {

        if (!StringUtils.isBlank(untilDateStr)) {
            try {
                this.untilDate = Optional.of(LocalDate.parse(untilDateStr));
            } catch (DateTimeParseException e) {
                throw new CSVMoneyItemBuildException(e.getMessage());
            }
        }
        return this;
    }

    public AccountEntryBuilder<ITEM, PRT> untilAmount(String untilAmtStr) {

        if (!StringUtils.isBlank(untilAmtStr)) {
            try {
                this.untilAmount = Optional.of(new BigDecimal(untilAmtStr));
            } catch (NumberFormatException e) {
                throw new CSVMoneyItemBuildException(e.getMessage());
            }
        }
        return this;
    }

    public AccountEntryBuilder<ITEM, PRT> firstPayment(String firstPmtStr) {

        if (!StringUtils.isBlank(firstPmtStr)) {
            try {
                this.firstPayment = Optional.of(new BigDecimal(firstPmtStr));
            } catch (NumberFormatException e) {
                throw new CSVMoneyItemBuildException(e.getMessage());
            }
        }
        return this;
    }

    public AccountEntryBuilder<ITEM, PRT> alreadyPaid(String alreadyPdStr) {

        if (!StringUtils.isBlank(alreadyPdStr)) {
            try {
                this.alreadyPaid = Optional.of(new BigDecimal(alreadyPdStr));
            } catch (NumberFormatException e) {
                throw new CSVMoneyItemBuildException(e.getMessage());
            }
        }
        return this;
    }

    public AccountEntryBuilder<ITEM, PRT> skip(String skip) {

        this.skip = isCheckedCell(skip);
        return this;
    }

    public AccountEntryBuilder<ITEM, PRT> skipFirstPayment(
        String skipFirstPayment)
    {

        this.skipFirstPayment = isCheckedCell(skipFirstPayment);
        return this;
    }

    /**
     * Cell contents contain a string suitable for marking as "checked" which in
     * our case is any non-empty string that is not also "f", "false", "0", "n",
     * "no"
     * 
     * @return
     */
    static boolean isCheckedCell(String cell) {

        return StringUtils.isNotBlank(cell)
            && !nos.contains(cell.trim().toUpperCase());

    }

    static final Set<String> nos = new HashSet<>();
    static {
        nos.add("F");
        nos.add("FALSE");
        nos.add("0");
        nos.add("N");
        nos.add("NO");
    }

    // // StringUtils.isBlank
    // public static boolean isBlank(String s) {
    // final boolean result = (s == null) || s.trim().length() == 0;
    // return result;
    //
    // }
    //
    public AccountEntryBuilder() {

        this.item = Optional.<Integer> empty();
        this.from = Optional.<String> empty();
        this.to = Optional.<String> empty();
        this.amount = Optional.<BigDecimal> empty();
        this.cycle = Optional.<String> empty();
        this.notes = Optional.<String> empty();
        this.afterDate = Optional.<LocalDate> empty();
        this.untilDate = Optional.<LocalDate> empty();
        this.untilAmount = Optional.<BigDecimal> empty();
        this.alreadyPaid = Optional.<BigDecimal> empty();
        this.firstPayment = Optional.<BigDecimal> empty();
    }

}
