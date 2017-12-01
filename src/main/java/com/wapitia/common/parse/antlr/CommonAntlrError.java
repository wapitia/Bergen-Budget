
package com.wapitia.common.parse.antlr;

import org.antlr.v4.runtime.Recognizer;

public class CommonAntlrError extends RuntimeException {

    private Recognizer<?, ?> recognizer;
    private Object           offendingSymbol;
    private int              line;
    private int              charPositionInLine;

    public CommonAntlrError(String message) {
        this(message, null);
    }

    public CommonAntlrError(String message, Throwable cause) {
        this(null, null, 0, 0, message, cause);
    }

    public CommonAntlrError(Recognizer<?, ?> recognizer, Object offendingSymbol,
        int line, int charPositionInLine, String msg, Throwable cause)
    {

        super(String.format("%s (at column %d)", msg, charPositionInLine), cause);
        this.recognizer = recognizer;
        this.offendingSymbol = offendingSymbol;
        this.line = line;
        this.charPositionInLine = charPositionInLine;
    }

    public Recognizer<?, ?> getRecognizer() {

        return recognizer;
    }

    public Object getOffendingSymbol() {

        return offendingSymbol;
    }

    public int getLine() {

        return line;
    }

    public int getCharPositionInLine() {

        return charPositionInLine;
    }

   @Override
    public String toString() {

        return getMessage();
    }

    private static final long serialVersionUID = -2309114983197875718L;

}
