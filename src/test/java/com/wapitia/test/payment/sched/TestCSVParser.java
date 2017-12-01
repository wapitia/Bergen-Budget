package com.wapitia.test.payment.sched;

import com.wapitia.common.io.FileUtilities;
import com.wapitia.common.io.csv.CSVParser;
import com.wapitia.common.io.csv.HeaderedCSVCollector;
import com.wapitia.common.io.csv.HeaderedSpreadsheet;
import com.wapitia.common.io.csv.SpreadsheetCSVOutput;
import com.wapitia.finance.AccountEntry;
import com.wapitia.payment.io.CSVAccountEntryBuilder;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class TestCSVParser {

//	static class MyCSVColl implements CSVCellCollector {
//
//		List<CSVCell> cells = new ArrayList<>();
//
//		@Override
//		public void addCell(int column, int row, String cellContents) {
//			CSVCell csvCell = new CSVCell(Integer.toString(column), row, cellContents);
//			cells.add(csvCell);
//		}
//
//		public String toString() {
//			StringBuilder bldr = new StringBuilder();
//			cells.forEach(c -> {
//				bldr.append(c.toString());
//				bldr.append(System.lineSeparator());
//				} );
//			return bldr.toString();
//		}
//	}

	@Test
	public void testCSV1() {
//		final String s = "a,b,c";
		List<String> csvLines = Arrays.asList(
				// works
//				"a,b,c",
//				"'a', 'b'",
//				"'a',  'b ''v ', \"c\", '''''', ''',''', ''",
//				"'a'" + System.lineSeparator() + "x",
//				"'a','b','c'" + System.lineSeparator() + "x,y,\"z\""
				"'a','b','c'" + System.lineSeparator() + "x,y,\"z\"" + System.lineSeparator() + "'a',  'b ''v ', \"c\""
//				System.lineSeparator() + "'a','b','c'" + System.lineSeparator() + "x,y,\"z\"" + System.lineSeparator()
				);
		csvLines.forEach(t -> {
			try {
				oneTrial(t);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	public void oneTrial(final String s) throws IOException {

//		CSVCellCollector collector = new MyCSVColl();
		HeaderedCSVCollector collector = new HeaderedCSVCollector();
		CSVParser p = new CSVParser();
		p.parse(s, collector);
		final HeaderedSpreadsheet spreadSheet = collector.getSpreadSheet();
		System.out.println("1. ============================");
		System.out.println(spreadSheet.toString());

		StringWriter wtr = new StringWriter();
		new SpreadsheetCSVOutput(spreadSheet).writeTo(wtr);
		System.out.println("2. ============================");
		System.out.println(wtr.toString());

	}


    @Test
    public void testCDMMoneyRoll() {

        final String inputCsvFile = "src/test/resources/cdm_flow.csv";

        try {
            final CharSequence s = FileUtilities.fromFile(inputCsvFile);
            final HeaderedSpreadsheet spreadSheet = HeaderedCSVCollector.spreadsheeetOf(s);
            final List<AccountEntry> mitems = spreadSheet.rowsStream()
                .map(CSVAccountEntryBuilder::accountEntryOfRow)
                .collect(Collectors.toList());

            mitems.forEach(System.out::println);

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }


    }

}
