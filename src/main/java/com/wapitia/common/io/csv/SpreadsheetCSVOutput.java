package com.wapitia.common.io.csv;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class SpreadsheetCSVOutput {

    private final HeaderedSpreadsheetCharSpliterator spliter;

    public SpreadsheetCSVOutput(HeaderedSpreadsheet spreadsheet) {
        this.spliter = new HeaderedSpreadsheetCharSpliterator(spreadsheet);
    }

    public void writeTo(Writer w) throws IOException {

        for (final Iterator<Integer> eachChar = csvCharStream()
            .iterator(); eachChar.hasNext();) {
            Integer i = eachChar.next();
            w.write(i.intValue());
        }
    }

    public IntStream csvCharStream() {

        final IntStream result = StreamSupport.intStream(spliter, false);
        return result;
    }
}
