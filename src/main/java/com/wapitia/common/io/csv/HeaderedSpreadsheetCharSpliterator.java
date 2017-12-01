package com.wapitia.common.io.csv;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Map.Entry;
import java.util.function.IntConsumer;

/**
 * Spliterator of Integers representing each character of a HeaderedSpreadsheet
 * in CSV format.
 */
public class HeaderedSpreadsheetCharSpliterator implements Spliterator.OfInt {

    private final HeaderedSpreadsheet spreadsheet;
    private final int                 nSpreadsheetRows;

    HeaderedSpreadsheetCharSpliterator(final HeaderedSpreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;
        this.nSpreadsheetRows = spreadsheet.rowCount();

        // current state
        this.isOnHeaderRow = true;
        this.state = STATE.ROW_START;
        this.row = 0;
        this.nextChar = CharacterIterator.DONE;
        primeNextChar();
    }

    static enum STATE {
        ROW_START, ROW_ENTRY_STREAM, PRE_QUOTE, RAW_CONT, Q, QQ, COMMA,
    };

    // current state keeps track of which char that is referenced in the given
    // spreadsheet.
    int                                      nextChar;
    boolean                                  isOnHeaderRow;
    HeaderedSpreadsheetCharSpliterator.STATE state;
    int                                      row;
    Iterator<Entry<String, String>>          rowEntryIterator;
    StringCharacterIterator                  contentIter;

    /**
     * Advance to the next character to output from the spreadsheet. After
     * returning, this.nextChar will be the one to output via tryAdvance(), or
     * will equal DONE when done.
     */
    private void primeNextChar() {

        switch (state) {
            case ROW_START:
                // at row start, need to get this spreadsheet's row of data
                // loaded into an Entry stream.
                if (row < nSpreadsheetRows) {
                    this.rowEntryIterator = spreadsheet.getRowEntrySet(row)
                        .iterator();
                    this.state = STATE.ROW_ENTRY_STREAM;
                    primeNextChar();
                } else {
                    this.nextChar = CharacterIterator.DONE;
                }
                break;

            case ROW_ENTRY_STREAM:
                if (rowEntryIterator.hasNext()) {
                    final Entry<String, String> entry = rowEntryIterator.next();
                    final String content = isOnHeaderRow ? entry.getKey()
                        : entry.getValue();
                    this.contentIter = new StringCharacterIterator(content);
                    this.state = STATE.PRE_QUOTE;
                    primeNextChar();
                } else {
                    this.nextChar = new Character(CSVLexer.LF);
                    if (isOnHeaderRow) {
                        this.isOnHeaderRow = false;
                    } else {
                        ++this.row;
                    }
                    this.state = STATE.ROW_START;
                }
                break;

            case PRE_QUOTE:
                this.nextChar = new Character(CSVLexer.SQUOTE);
                this.state = STATE.RAW_CONT;
                break;

            case RAW_CONT:
                char ch = contentIter.current();
                if (ch != CharacterIterator.DONE) {
                    contentIter.next();
                    if (ch == CSVLexer.SQUOTE) {
                        this.state = STATE.QQ;
                        primeNextChar();
                    } else {
                        this.nextChar = new Character(ch);
                        // still RAW_CONT
                    }
                } else {
                    // end quote
                    this.nextChar = new Character(CSVLexer.SQUOTE);
                    this.state = STATE.COMMA;
                }
                break;
            case QQ:
                this.nextChar = new Character(CSVLexer.SQUOTE);
                this.state = STATE.Q;
                break;
            case Q:
                this.nextChar = new Character(CSVLexer.SQUOTE);
                this.state = STATE.RAW_CONT;
                break;
            case COMMA:
                if (rowEntryIterator.hasNext()) {
                    this.nextChar = new Character(CSVLexer.COMMA);
                    this.state = STATE.ROW_ENTRY_STREAM;
                } else {
                    this.state = STATE.ROW_ENTRY_STREAM;
                    primeNextChar();
                }
                break;
        }
    }

    @Override
    public boolean tryAdvance(IntConsumer action) {

        boolean isAdvancing = (nextChar != CharacterIterator.DONE);
        if (isAdvancing) {
            action.accept(nextChar);
            primeNextChar();
        }
        return isAdvancing;
    }

    @Override
    public long estimateSize() {

        // unknown or two expensive to estimate, so return expected value to
        // reflect that
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {

        int result = Spliterator.CONCURRENT | Spliterator.IMMUTABLE
            | Spliterator.NONNULL;
        return result;
    }

    @Override
    public java.util.Spliterator.OfInt trySplit() {

        return null;
    }

}
