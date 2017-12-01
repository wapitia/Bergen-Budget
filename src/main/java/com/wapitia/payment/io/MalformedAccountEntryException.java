
package com.wapitia.payment.io;

public class MalformedAccountEntryException extends RuntimeException {

    private static final long serialVersionUID = -2512377511152390916L;

    public MalformedAccountEntryException(String message) {
        super(message);
    }

    public MalformedAccountEntryException(String message, Throwable t) {
        super(message, t);
    }
}
