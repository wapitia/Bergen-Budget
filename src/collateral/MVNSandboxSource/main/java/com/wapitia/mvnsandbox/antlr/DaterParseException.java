package com.wapitia.mvnsandbox.antlr;

import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class DaterParseException extends RuntimeException {

	private final Recognizer<?, ?> recognizer;
	private final Object offendingSymbol;
	private final int line;
	private final int charPositionInLine;
	private final String msg;
	private final RecognitionException antlrException;
	
	public DaterParseException(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException antlrException) {
		this.recognizer = recognizer;
		this.offendingSymbol = offendingSymbol;
		this.line = line;
		this.charPositionInLine = charPositionInLine;
		this.msg = msg;
		this.antlrException = antlrException;
	}
	
	public DaterParseException(String msg) {
		this.recognizer = null;
		this.offendingSymbol = null;
		this.line = 0;
		this.charPositionInLine = 0;
		this.msg = msg;
		this.antlrException = null;
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

	public String getMsg() {
		return msg;
	}

	public RecognitionException getAntlrException() {
		return antlrException;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public String toString() {
		String exceptionString = Optional.<RecognitionException>ofNullable(antlrException)
				.map(Object::toString).orElse("");
		StringBuilder bldr = new StringBuilder();
		bldr.append(String.format("%s. Symbol: %s %s/%s, exception: %s", 
				msg, offendingSymbol, line, charPositionInLine, 
				exceptionString));
		return bldr.toString();
	}

	private static final long serialVersionUID = -176397779813809432L;

}
