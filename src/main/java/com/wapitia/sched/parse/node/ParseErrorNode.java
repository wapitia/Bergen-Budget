
package com.wapitia.sched.parse.node;

import com.wapitia.common.parse.antlr.CommonAntlrError;

import java.util.List;

public class ParseErrorNode implements SchedNode {

    // private final List<String> errors;
    //
    // public ParseErrorNode(List<String> errors) {
    // this.errors = errors;
    // }
    //
    // @Override
    // public String toString() {
    // final String result = errors.stream().findAny().orElse("ERROR");
    // return result;
    // }
    private final List<CommonAntlrError> errors;

    public ParseErrorNode(List<CommonAntlrError> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {

        final String result = errors.stream().map(Object::toString).findAny()
            .orElse("ERROR");
        return result;
    }
}
