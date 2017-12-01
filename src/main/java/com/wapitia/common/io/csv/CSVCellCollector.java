package com.wapitia.common.io.csv;

@FunctionalInterface
public interface CSVCellCollector {

    public void addCell(int column, int row, String cellContents);

}
