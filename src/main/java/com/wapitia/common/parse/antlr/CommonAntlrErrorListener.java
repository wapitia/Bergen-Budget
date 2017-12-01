
package com.wapitia.common.parse.antlr;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;

public class CommonAntlrErrorListener extends BaseErrorListener {

    private List<CommonAntlrError> errors = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
        int line, int charPositionInLine, String msg, RecognitionException e)
    {

        super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine,
            msg, e);
        CommonAntlrError err = new CommonAntlrError(recognizer, offendingSymbol,
            line, charPositionInLine, msg, e);
        errors.add(err);
    }

    public boolean hasErrors() {

        return !errors.isEmpty();
    }

    public List<CommonAntlrError> getErrors() {
        return errors;
    }

//    public SchedNode asNode() {
//
//        ParseErrorNode result = new ParseErrorNode(errors);
//        return result;
//    }

}
