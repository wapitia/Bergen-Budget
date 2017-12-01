package com.wapitia.common.io.csv;

import com.wapitia.common.io.csv.CSVLexer.TOK;
import com.wapitia.common.parse.ParseErrorRT;
import com.wapitia.common.parse.ParseErrorUnexpectedEndOfFile;

/**
 * CSV Production:
 *
 * csvdoc : csvlines EOS
 *
 * csvlines : e csvlines : cont EOL csvlines
 *
 * cont : SP* e cont : SP* wscell SP* (COMMA cont)?
 *
 * wscell : SP wscell wscell : cell
 *
 * cell : rawcell cell : sqcell cell : dqcell
 *
 * rawcell : rawchar rawcell?
 *
 * sqcell : SQUOTE sqrest
 *
 * dqcell : DQUOTE dqrest
 *
 * sqrest : SQUOTE : SQUOTE SQUOTE sqrest : rawchar sqrest : SP sqrest : COMMA
 * sqrest : DQUOTE sqrest
 *
 * dqrest : DQUOTE : DQUOTE DQUOTE dqrest : rawchar dqrest : SP dqrest : COMMA
 * dqrest : SQUOTE dqrest
 *
 * rawchar : any printable, non-['", ] char
 *
 */
public class CSVParser {

    public void parse(CharSequence csvText, CSVCellCollector collector)
        throws ParseErrorRT
    {

        CSVLexer toks = new CSVLexer(csvText);
        CSVLocation csvloc = new CSVLocation();
        csvdoc(toks, collector, csvloc);
    }

    /**
     * CSV Production:
     *
     * csvdoc : csvlines EOS
     * 
     * @param csvloc
     */

    private void csvdoc(CSVLexer toks, CSVCellCollector collector,
        CSVLocation csvloc) throws ParseErrorRT
    {

        csvlines(toks, collector, csvloc);
        final TOK tok = toks.curToken();
        if (tok == CSVLexer.TOK.EOS) {
            toks.advance();
            return;
        }
        throw new ParseErrorUnexpectedEndOfFile();
    }

    /**
     * CSV Production:
     *
     * csvlines : e csvlines : cont EOL csvlines
     * 
     * @param csvloc
     */

    private void csvlines(CSVLexer toks, CSVCellCollector collector,
        CSVLocation csvloc) throws ParseErrorRT
    {

        TOK tok = toks.curToken();

        if (tok == CSVLexer.TOK.EOS) {
            return;
        }
        cont(toks, collector, csvloc);
        tok = toks.curToken();
        if (tok == CSVLexer.TOK.EOL) {
            toks.advance();
            csvloc.incrRow();
            csvlines(toks, collector, csvloc);

        }
    }

    /**
     * CSV Production:
     *
     * cont : SP* e cont : SP* wscell SP* (COMMA cont)?
     *
     */
    private void cont(CSVLexer toks, CSVCellCollector collector,
        CSVLocation csvloc) throws ParseErrorRT
    {

        TOK tok = toks.curToken();
        // SP* skip
        while (tok == CSVLexer.TOK.SPACE) {
            toks.advance();
            tok = toks.curToken();
        }

        // e ...
        if (tok == CSVLexer.TOK.EOL || tok == CSVLexer.TOK.EOS) {
            return;
        }

        // wscell...
        wscell(toks, collector, csvloc);
        tok = toks.curToken();

        // SP* skip
        while (tok == CSVLexer.TOK.SPACE) {
            toks.advance();
            tok = toks.curToken();
        }

        // (COMMA cont)?
        if (tok == CSVLexer.TOK.COMMA) {
            toks.advance(); // advance past comma
            csvloc.incrColumn();
            cont(toks, collector, csvloc);
        }
    }

    /**
     * CSV Production:
     *
     * wscell : SP wscell wscell : cell
     */
    private void wscell(CSVLexer toks, CSVCellCollector collector,
        CSVLocation csvloc) throws ParseErrorRT
    {

        final TOK tok = toks.curToken();
        if (tok == CSVLexer.TOK.SPACE) {
            toks.advance(); // advance past the space
            wscell(toks, collector, csvloc);
        }
        cell(toks, collector, csvloc);
    }

    /**
     * CSV Production: cell : rawcell cell : sqcell cell : dqcell
     */
    private void cell(CSVLexer toks, CSVCellCollector collector,
        CSVLocation csvloc) throws ParseErrorRT
    {

        final TOK tok = toks.curToken();
        final StringBuilder stringBuilder = new StringBuilder();
        if (tok == CSVLexer.TOK.SQUOTE) {
            sqcell(toks, collector, csvloc, stringBuilder);
        } else if (tok == CSVLexer.TOK.DQUOTE) {
            dqcell(toks, collector, csvloc, stringBuilder);
        } else if (tok == CSVLexer.TOK.RAWCHAR) {
            rawcell(toks, collector, csvloc, stringBuilder);
        } else if (tok == CSVLexer.TOK.COMMA) {
            rawcell(toks, collector, csvloc, stringBuilder);
        } else {
            throw new ParseErrorUnexpectedChar();
        }
        collector.addCell(csvloc.getColumn(), csvloc.getRow(),
            stringBuilder.toString());
    }

    /**
     * CSV Production:
     *
     * rawcell : rawchar rawcell?
     */
    private void rawcell(CSVLexer toks, CSVCellCollector collector,
        CSVLocation csvloc, StringBuilder stringBuilder)
    {

        TOK tok = toks.curToken();
        if (tok == CSVLexer.TOK.SQUOTE) {
            stringBuilder.append(CSVLexer.SQUOTE);
            toks.advance();
            rawcell(toks, collector, csvloc, stringBuilder);
        } else if (tok == CSVLexer.TOK.SPACE) {
            stringBuilder.append(CSVLexer.SPACE);
            toks.advance();
            rawcell(toks, collector, csvloc, stringBuilder);
        } else if (tok == CSVLexer.TOK.DQUOTE) {
            stringBuilder.append(CSVLexer.DQUOTE);
            toks.advance();
            rawcell(toks, collector, csvloc, stringBuilder);
        } else if (tok == CSVLexer.TOK.RAWCHAR) {
            stringBuilder.append(toks.curChar());
            toks.advance();
            rawcell(toks, collector, csvloc, stringBuilder);
        }

    }

    /**
     * CSV Production:
     *
     * sqcell : SQUOTE sqrest
     *
     * sqrest : SQUOTE : SQUOTE SQUOTE sqrest : rawchar sqrest : SP sqrest :
     * COMMA sqrest : DQUOTE sqrest
     */
    private void sqcell(CSVLexer toks, CSVCellCollector collector,
        CSVLocation csvloc, StringBuilder stringBuilder)
    {

        final TOK tok = toks.curToken();
        if (tok != CSVLexer.TOK.SQUOTE) {
            throw new ParseErrorUnexpectedChar();
        }
        // advance past the single quote
        toks.advance();
        sqrest(toks, collector, csvloc, stringBuilder);
    }

    private void sqrest(CSVLexer toks, CSVCellCollector collector,
        CSVLocation csvloc, StringBuilder stringBuilder)
    {

        TOK tok = toks.curToken();
        // SQUOTE, SQUOTE SQUOTE sqrest...
        if (tok == CSVLexer.TOK.SQUOTE) {
            toks.advance();
            tok = toks.curToken();
            if (tok == CSVLexer.TOK.SQUOTE) {
                // parsed two single quotes to be treated as a single embedded
                // single quote
                stringBuilder.append(CSVLexer.SQUOTE);
                toks.advance();
                sqrest(toks, collector, csvloc, stringBuilder);
            }
        } else if (tok == CSVLexer.TOK.SPACE) {
            stringBuilder.append(CSVLexer.SPACE);
            toks.advance();
            sqrest(toks, collector, csvloc, stringBuilder);
        } else if (tok == CSVLexer.TOK.COMMA) {
            stringBuilder.append(CSVLexer.COMMA);
            toks.advance();
            sqrest(toks, collector, csvloc, stringBuilder);
        } else if (tok == CSVLexer.TOK.DQUOTE) {
            stringBuilder.append(CSVLexer.DQUOTE);
            toks.advance();
            sqrest(toks, collector, csvloc, stringBuilder);
        } else if (tok == CSVLexer.TOK.RAWCHAR) {
            stringBuilder.append(toks.curChar());
            toks.advance();
            sqrest(toks, collector, csvloc, stringBuilder);
        }
    }

    /**
     * CSV Production:
     *
     * dqcell : DQUOTE dqrest
     *
     * dqrest : DQUOTE : DQUOTE DQUOTE dqrest : rawchar dqrest : SP dqrest :
     * COMMA dqrest : SQUOTE dqrest
     */
    private void dqcell(CSVLexer toks, CSVCellCollector collector,
        CSVLocation csvloc, StringBuilder stringBuilder)
    {

        final TOK tok = toks.curToken();
        if (tok != CSVLexer.TOK.DQUOTE) {
            throw new ParseErrorUnexpectedChar();
        }
        // advance past the single quote
        toks.advance();
        dqrest(toks, collector, csvloc, stringBuilder);
    }

    private void dqrest(CSVLexer toks, CSVCellCollector collector,
        CSVLocation csvloc, StringBuilder stringBuilder)
    {

        TOK tok = toks.curToken();
        // DQUOTE, DQUOTE DQUOTE sqrest...
        if (tok == CSVLexer.TOK.DQUOTE) {
            toks.advance();
            tok = toks.curToken();
            if (tok == CSVLexer.TOK.DQUOTE) {
                // parsed two double quotes to be treated as a single embedded
                // double quote
                stringBuilder.append(CSVLexer.DQUOTE);
                toks.advance();
                dqrest(toks, collector, csvloc, stringBuilder);
            }
        } else if (tok == CSVLexer.TOK.SPACE) {
            stringBuilder.append(CSVLexer.SPACE);
            toks.advance();
            dqrest(toks, collector, csvloc, stringBuilder);
        } else if (tok == CSVLexer.TOK.COMMA) {
            stringBuilder.append(CSVLexer.COMMA);
            toks.advance();
            dqrest(toks, collector, csvloc, stringBuilder);
        } else if (tok == CSVLexer.TOK.SQUOTE) {
            stringBuilder.append(CSVLexer.SQUOTE);
            toks.advance();
            dqrest(toks, collector, csvloc, stringBuilder);
        } else if (tok == CSVLexer.TOK.RAWCHAR) {
            stringBuilder.append(toks.curChar());
            toks.advance();
            dqrest(toks, collector, csvloc, stringBuilder);
        }
    };
}
