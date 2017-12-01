package com.wapitia.common.csv;

import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * Collects the values of a CSV Spreadsheet into a column-labeled spreadsheet.
 */
public class HeaderedCSVCollector implements CSVCellCollector {
	
	static enum HDR_ROW_STATE {
		BEFORE_HDR_ROW,
		ON_HDR_ROW,
		AFTER_HDR_ROW
	};

	// map header 0-based column indexes to Header Names
	private LinkedHashMap<Integer, String> reverseHeaderMap;
	private HeaderedSpreadsheet result;
	private HeaderedCSVCollector.HDR_ROW_STATE collectingHeaders;
	// 0-based header row
	private int headerRow;
	
	public HeaderedCSVCollector() {
		this.reverseHeaderMap = new LinkedHashMap<>();
		this.collectingHeaders = HDR_ROW_STATE.BEFORE_HDR_ROW;
		this.result = new HeaderedSpreadsheet();
		this.headerRow = 0;
	}
	
	public HeaderedSpreadsheet getSpreadSheet() {
		return result;
	}
	
	@Override
	public void addCell(int  column, int row, String cellContents) {
		
		switch (collectingHeaders) {
		case BEFORE_HDR_ROW:
			if (isHeaderStart(column, row, cellContents)) {
				this.headerRow = row;
				this.collectingHeaders = HDR_ROW_STATE.ON_HDR_ROW;
				addCell(column, row, cellContents);
			}
			break;
		case ON_HDR_ROW:
			if (row == headerRow) {
				addHeader(column, cellContents);
			}
			else if (row > headerRow) {
				this.collectingHeaders = HDR_ROW_STATE.AFTER_HDR_ROW;
				addCell(column, row, cellContents);
			}
			break;
		case AFTER_HDR_ROW:
			getHeaderName(column).ifPresent((String hdrName) -> {
				addContents(hdrName, row - headerRow - 1, cellContents);
			});
			break;
		}
		
	}
	
	/**
	 * 
     * by default, the first non-empty cell is the start of the header.
	 */
	protected boolean isHeaderStart(int  column, long row, String cellContents) {
		final boolean isNotEmpty = (cellContents != null) && (cellContents.trim().length() > 0);
		return isNotEmpty;
	}


	private void addHeader(int column, String headerName) {
		reverseHeaderMap.put(column, headerName);
	}
	
	private Optional<String> getHeaderName(int column) {
		final String hdrName = reverseHeaderMap.get(column);
		final Optional<String> result = Optional.<String>ofNullable(hdrName);
		return result;
	}
	
	private void addContents(String hdrName, int row, String contents) {
		result.addCell(hdrName, row, contents);
		
	}

	/**
	 * Convenience method
	 * @param s
	 * @return
	 */
	public HeaderedSpreadsheet getHeaderedSpreadsheet(final CharSequence s) {
		final CSVParser p = new CSVParser();
		p.parse(s, this);
		final HeaderedSpreadsheet spreadSheet = getSpreadSheet();
		return spreadSheet;
	}

	public static HeaderedSpreadsheet makeHeaderedSpreadsheet(final CharSequence s) {
		final HeaderedSpreadsheet result = new HeaderedCSVCollector().getHeaderedSpreadsheet(s);
		return result;
	}
	
}