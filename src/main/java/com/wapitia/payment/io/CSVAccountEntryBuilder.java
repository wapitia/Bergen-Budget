package com.wapitia.payment.io;

import com.wapitia.finance.AccountEntry;
import com.wapitia.finance.AccountEntryBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

public class CSVAccountEntryBuilder
    extends AccountEntryBuilder<AccountEntry, CSVAccountEntryBuilder>
{

    static Map<String, BiConsumer<CSVAccountEntryBuilder, String>> CSV_MONEY_COLUMN_MAP = new HashMap<>();
    static {
        CSV_MONEY_COLUMN_MAP.put("Item", AccountEntryBuilder::item);
        CSV_MONEY_COLUMN_MAP.put("From", AccountEntryBuilder::from);
        CSV_MONEY_COLUMN_MAP.put("To", AccountEntryBuilder::to);
        CSV_MONEY_COLUMN_MAP.put("Amount", AccountEntryBuilder::amount);
        CSV_MONEY_COLUMN_MAP.put("Cycle", AccountEntryBuilder::cycle);
        CSV_MONEY_COLUMN_MAP.put("Notes", AccountEntryBuilder::notes);
        CSV_MONEY_COLUMN_MAP.put("After Date", AccountEntryBuilder::afterDate);
        CSV_MONEY_COLUMN_MAP.put("Until Date", AccountEntryBuilder::untilDate);
        CSV_MONEY_COLUMN_MAP.put("Until Amount",
            AccountEntryBuilder::untilAmount);
        CSV_MONEY_COLUMN_MAP.put("First Payment",
            AccountEntryBuilder::firstPayment);
        CSV_MONEY_COLUMN_MAP.put("Already Paid",
            AccountEntryBuilder::alreadyPaid);
        CSV_MONEY_COLUMN_MAP.put("Skip", AccountEntryBuilder::skip);
        CSV_MONEY_COLUMN_MAP.put("Skip First Payment",
            AccountEntryBuilder::skipFirstPayment);
    }

    public static Optional<BiConsumer<CSVAccountEntryBuilder, String>> getMoneyColumnBuildItem(
        String columnName)
    {

        final BiConsumer<CSVAccountEntryBuilder, String> biConsumer = CSV_MONEY_COLUMN_MAP
            .get(columnName);
        return Optional.ofNullable(biConsumer);
    }

    public static AccountEntry accountEntryOfRow(
        Set<Entry<String, String>> es)
    {

        final CSVAccountEntryBuilder bldr = new CSVAccountEntryBuilder();
        es.forEach(bldr::loadCSVRow);
        final AccountEntry item = bldr.makeEntry();
        return item;
    }

    // public static
    // CSVAccountEntryBuilder csvBuilder() {
    // final CSVAccountEntryBuilder result = new CSVAccountEntryBuilder();
    // return result;
    // }

    // private Optional<Integer> row;

    // public Optional<Integer> getRow() {
    // return row;
    // }

    public CSVAccountEntryBuilder() {
        super();
        // this.row = Optional.<Integer> empty();
    }

    // @Override
    // public
    // AccountEntry makeEntry() {
    // AccountEntry result = new AccountEntry(
    // getItem().orElseThrow(() -> new CSVMoneyItemBuildException("Missing
    // Item")),
    // getFrom().orElseThrow(() -> new CSVMoneyItemBuildException("Missing
    // From")),
    // getTo().orElseThrow(() -> new CSVMoneyItemBuildException("Missing To")),
    // getAmount().orElseThrow(() -> new CSVMoneyItemBuildException("Missing
    // Amount")),
    // getCycle().orElseThrow(() -> new CSVMoneyItemBuildException("Missing
    // Cycle")),
    // getNotes().orElse(""),
    // getAfterDate(),
    // getUntilDate(),
    // getUntilAmount());
    // return result;
    // }

    public void loadCSVRow(final Entry<String, String> entry) {

        final String columnName = entry.getKey();
        final Optional<BiConsumer<CSVAccountEntryBuilder, String>> columnItemFunc = getMoneyColumnBuildItem(
            columnName);
        if (columnItemFunc.isPresent()) {
            String value = entry.getValue();
            columnItemFunc.get().accept(this, value);
        } else {
            System.out.println("Unrecognized Column: '" + columnName + "'");
        }
    }

}
