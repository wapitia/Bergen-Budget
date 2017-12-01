package com.wapitia.mvnsandbox.antlr;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class DaterErrorListener extends BaseErrorListener {

	List<DaterParseException> parseExceptions = new ArrayList<>();

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {
		super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
		parseExceptions.add(new DaterParseException(recognizer, offendingSymbol, line, charPositionInLine,
				msg, e));
	}

	public boolean hasErrors() {
		return ! parseExceptions.isEmpty();
	}
	
	public List<DaterParseException> getParseExceptions() {
		return parseExceptions;
	}

}
