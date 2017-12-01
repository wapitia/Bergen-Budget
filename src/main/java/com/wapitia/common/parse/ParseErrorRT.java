package com.wapitia.common.parse;

public class ParseErrorRT extends RuntimeException {

    /**
     * Void constructor.
     */
    public ParseErrorRT() {
     }

    /**
     * @param message
     * @param cause
     */
    public ParseErrorRT(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public ParseErrorRT(String message) {
        super(message);
    }

    private static final long serialVersionUID = 69256519358891715L;
}
