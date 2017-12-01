package com.wapitia.common.io.csv;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HeaderedSpreadsheet {

    // single headered row of the spreadsheet
    static class HSRow {

        private LinkedHashMap<String, String> columnContents;

        protected HSRow() {
            this.columnContents = new LinkedHashMap<>();
        }

        protected void addCell(String hdrName, String contents) {

            columnContents.put(hdrName, contents);
        }

        protected Set<Entry<String, String>> getEntrySet() {

            return columnContents.entrySet();
        }

        public String toString() {

            final String result = getEntrySet().stream()
                .map(HSRow::entryToString).collect(Collectors.joining(","));
            return result;
        }

        protected static String entryToString(Entry<String, String> entry) {

            return String.format("%s:%s", entry.getKey(), entry.getValue());
        }

    }

    private List<HSRow> rows;

    // make an empty collection
    public HeaderedSpreadsheet() {
        this.rows = new ArrayList<>();
    }

    public Stream<Set<Entry<String, String>>> rowsStream() {

        Stream<Set<Entry<String, String>>> result = rows.stream()
            .map(HSRow::getEntrySet);
        return result;
    }

    public void addCell(String hdrName, int rowIndex, String contents) {

        while (rows.size() <= rowIndex) {
            rows.add(new HSRow());
        }

        HSRow row = rows.get(rowIndex);
        row.addCell(hdrName, contents);
    }

    public int rowCount() {

        return rows.size();
    }

    // public Set<String> getHeaderSet() {
    // final Set<String> result;
    // if (rows.size() == 0) {
    // result = Collections.emptySet();
    // }
    // else {
    // final HSRow hsRow = rows.get(0);
    // result = hsRow.getEntrySet().stream()
    // .map(Entry::getKey)
    // .collect(Collectors.toSet());
    // }
    // return result;
    // }

    public Set<Entry<String, String>> getRowEntrySet(int rowIndex) {

        final HSRow row = rows.get(rowIndex);
        final Set<Entry<String, String>> result = row.getEntrySet();
        return result;
    }

    public String toString() {

        final String result = rows.stream().map(Object::toString)
            .collect(Collectors.joining(System.lineSeparator()));
        return result;
    }

}
