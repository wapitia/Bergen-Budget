
package com.wapitia.common.parse.antlr;

import com.wapitia.common.CountablesBaseVisitor;
import com.wapitia.common.CountablesLexer;
import com.wapitia.common.CountablesParser;
import com.wapitia.common.CountablesParser.CountableContext;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

public class CountablesProducer extends CountablesBaseVisitor<Integer> {

    private static final int ERR_RESULT = -1;

    public static Integer readCountable(final CharSequence text) {

        final CommonAntlrErrorListener errListener = new CommonAntlrErrorListener();
        return readCountable(text, errListener);
    }

    public static Integer readCountable(final CharSequence text,
        final CommonAntlrErrorListener errListener)
    {

        final CountableContext countable = readCountableContext(text,
            errListener);

        final Integer result;
        if (errListener.hasErrors()) {
            result = ERR_RESULT;
        } else {
            result = countable.value;
        }

        return result;
    }

    public static CountableContext readCountableContext(final CharSequence text,
        final CommonAntlrErrorListener errListener)
    {

        // set up ANTLR to apply the text to produce a "countable" parse tree.
        CountablesLexer lexer = new CountablesLexer(
            new ANTLRInputStream(text.toString()));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errListener);
        final TokenStream tokens = new CommonTokenStream(lexer);
        CountablesParser parser = new CountablesParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(errListener);

        // this step does the actual parsing, error handling, tree creation.
        final CountableContext countable = parser.countable();

        String prodTree = countable.toStringTree(parser);
        System.out.println(prodTree);
        return countable;
    }

}
