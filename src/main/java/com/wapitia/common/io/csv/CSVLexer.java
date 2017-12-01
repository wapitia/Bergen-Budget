package com.wapitia.common.io.csv;

public class CSVLexer {

    public final static char SPACE  = ' ';
    public final static char SQUOTE = '\'';
    public final static char DQUOTE = '"';
    public final static char COMMA  = ',';
    public final static char CR     = 0x0D;
    public final static char LF     = 0x0A;
    public final static char NUL    = '\u0000';

    public static enum TOK {
        SQUOTE, DQUOTE, COMMA, SPACE, RAWCHAR, EOL, EOS;
    };

    private final CharSequence seq;
    private int                curSeqIx = 0;
    private TOK                curTok;
    private char               curChar;

    public CSVLexer(final CharSequence seq) {
        this.seq = seq;
        this.curSeqIx = 0;
        prime();
    }

    public TOK curToken() {

        return curTok;
    }

    public char curChar() {

        return curChar;
    }

    public int curIndex() {

        return curSeqIx;
    }

    public CharSequence getSequence() {

        return seq;
    }

    public void advance() {

        if (curSeqIx < seq.length()) {
            ++curSeqIx;
            prime();
        }
    }

    public String toString() {

        return curTok + ":" + curChar + ":" + curSeqIx;
    }

    /**
     * After initial construction or advance, compute and hold the next token to
     * become the current one
     */
    private void prime() {

        if (curSeqIx >= seq.length()) {
            this.curTok = TOK.EOS;
        } else {
            // get current character, advance to next character
            char ch = seq.charAt(curSeqIx);

            switch (ch) {
                case SPACE:
                    this.curTok = TOK.SPACE;
                    break;
                case SQUOTE:
                    this.curTok = TOK.SQUOTE;
                    break;
                case DQUOTE:
                    this.curTok = TOK.DQUOTE;
                    break;
                case COMMA:
                    this.curTok = TOK.COMMA;
                    break;
                case CR:
                    if ((curSeqIx < seq.length() - 1)
                        && (seq.charAt(curSeqIx + 1) == LF)) {
                        ++curSeqIx;
                    }
                    this.curTok = TOK.EOL;
                    break;
                case LF:
                    this.curTok = TOK.EOL;
                    break;
                default:
                    this.curTok = TOK.RAWCHAR;
                    this.curChar = ch;
                    break;
            }
        }
    }
}
