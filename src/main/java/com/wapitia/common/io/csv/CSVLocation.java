package com.wapitia.common.io.csv;

/**
 * Mutable object keeps track of the current location. Rows and columns are
 * 0-based
 */
class CSVLocation {

    private int curColumn;
    private int curRow;

    public CSVLocation() {
        this.curColumn = 0;
        this.curRow = 0;
    }

    public void incrColumn() {

        ++this.curColumn;
    }

    public void incrRow() {

        this.curColumn = 0;
        ++this.curRow;
    }

    public int getColumn() {

        return curColumn;
    }

    public int getRow() {

        return curRow;
    }

    public String toString() {

        return curColumn + ":" + curRow;
    }
}
