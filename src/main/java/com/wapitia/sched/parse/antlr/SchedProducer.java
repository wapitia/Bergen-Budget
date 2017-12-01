
package com.wapitia.sched.parse.antlr;

import com.wapitia.common.parse.ParseErrorRT;
import com.wapitia.common.parse.antlr.CommonAntlrErrorListener;
import com.wapitia.sched.SchedBaseVisitor;
import com.wapitia.sched.SchedLexer;
import com.wapitia.sched.SchedParser;
import com.wapitia.sched.WeekOfMonth;
import com.wapitia.sched.SchedParser.BiwSch1Context;
import com.wapitia.sched.SchedParser.DayOfWeekContext;
import com.wapitia.sched.SchedParser.DaysOfMonthListContext;
import com.wapitia.sched.SchedParser.DaysOfWeekListContext;
import com.wapitia.sched.SchedParser.DomSpecs1Context;
import com.wapitia.sched.SchedParser.DomSpecs2Context;
import com.wapitia.sched.SchedParser.MonSch1Context;
import com.wapitia.sched.SchedParser.MonSch2Context;
import com.wapitia.sched.SchedParser.MonSch3Context;
import com.wapitia.sched.SchedParser.OrdDayOfMonthContext;
import com.wapitia.sched.SchedParser.OrdDaysOfMonthContext;
import com.wapitia.sched.SchedParser.ScheduleContext;
import com.wapitia.sched.SchedParser.WeekOfMonthContext;
import com.wapitia.sched.SchedParser.WeeksOfMonthContext;
import com.wapitia.sched.parse.node.BiweeklySchedNode;
import com.wapitia.sched.parse.node.DayNode;
import com.wapitia.sched.parse.node.DayOfWeekNode;
import com.wapitia.sched.parse.node.DaysOfMonthNode;
import com.wapitia.sched.parse.node.DaysOfWeekNode;
import com.wapitia.sched.parse.node.DomSpecsNode;
import com.wapitia.sched.parse.node.DowomSpecsNode;
import com.wapitia.sched.parse.node.MonthlySchedNode;
import com.wapitia.sched.parse.node.ParseErrorNode;
import com.wapitia.sched.parse.node.SchedNode;
import com.wapitia.sched.parse.node.WeekOfMonthNode;
import com.wapitia.sched.parse.node.WeeksOfMonthNode;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SchedProducer extends SchedBaseVisitor<SchedNode> {

    @Override
    public SchedNode visitSchedule(ScheduleContext ctx) {

        // TODO Auto-generated method stub
        return super.visitSchedule(ctx);
    }

    @Override
    public BiweeklySchedNode visitBiwSch1(BiwSch1Context ctx) {

        final SchedNode domSpecs = visit(ctx.domSpecs());
        final BiweeklySchedNode result = new BiweeklySchedNode(domSpecs);
        return result;
    }

    @Override
    public MonthlySchedNode visitMonSch1(MonSch1Context ctx) {

        final SchedNode domSpecs = visit(ctx.domSpecs());
        final MonthlySchedNode result = new MonthlySchedNode(domSpecs);
        return result;
    }

    @Override
    public MonthlySchedNode visitMonSch2(MonSch2Context ctx) {

        final SchedNode domSpecs = visit(ctx.domSpecs());
        MonthlySchedNode result = new MonthlySchedNode(domSpecs);
        return result;
    }

    @Override
    public MonthlySchedNode visitMonSch3(MonSch3Context ctx) {

        final DaysOfMonthNode days = (DaysOfMonthNode) visit(
            ctx.ordDaysOfMonth());
        final DomSpecsNode domSpecs = new DomSpecsNode(days);
        final MonthlySchedNode result = new MonthlySchedNode(domSpecs);
        return result;
    }

    @Override
    public DomSpecsNode visitDomSpecs1(DomSpecs1Context ctx) {

        final DaysOfMonthNode daysNode = (DaysOfMonthNode) visit(
            ctx.daysOfMonthList());
        final DomSpecsNode result = new DomSpecsNode(daysNode);
        return result;
    }

    @Override
    public DowomSpecsNode visitDomSpecs2(DomSpecs2Context ctx) {

        final WeeksOfMonthNode womNode = (WeeksOfMonthNode) visit(
            ctx.weeksOfMonth());
        final DaysOfWeekNode daysNode = (DaysOfWeekNode) visit(
            ctx.daysOfWeekList());
        final DowomSpecsNode result = new DowomSpecsNode(womNode, daysNode);
        return result;
    }

    @Override
    public DaysOfMonthNode visitDaysOfMonthList(DaysOfMonthListContext ctx) {
        final Integer dom;

        if (ctx.monthnum() != null) {
            dom = Integer.valueOf(ctx.monthnum().getText());
        }
        else if (ctx.cardinal() != null) {
            dom = ctx.cardinal().value;
        }
        else {
            throw new ParseErrorRT();
        }
        final DayNode domNode = new DayNode(dom);
        final DaysOfMonthListContext tailOpt = ctx.daysOfMonthList();
        final DaysOfMonthNode result;
        if (tailOpt == null) {
            result = new DaysOfMonthNode(domNode);
        } else {
            final DaysOfMonthNode daysNode = (DaysOfMonthNode) visit(tailOpt);
            result = new DaysOfMonthNode(domNode, daysNode);
        }
        return result;
    }

    @Override
    public WeeksOfMonthNode visitWeeksOfMonth(WeeksOfMonthContext ctx) {

        WeekOfMonthNode dowNode = (WeekOfMonthNode) visit(ctx.weekOfMonth());
        final WeeksOfMonthContext weeksOfMonthCtx = ctx.weeksOfMonth();
        final WeeksOfMonthNode result;
        if (weeksOfMonthCtx == null) {
            result = new WeeksOfMonthNode(dowNode);
        } else {
            WeeksOfMonthNode days = (WeeksOfMonthNode) visit(weeksOfMonthCtx);
            result = new WeeksOfMonthNode(dowNode, days);
        }
        return result;
    }

    @Override
    public WeekOfMonthNode visitWeekOfMonth(WeekOfMonthContext ctx) {

        final WeekOfMonth dow = WeekOfMonth.ofOrdinal(ctx.value - 1)
            .orElseThrow(() -> new RuntimeException() // out of bounds ordinal
                                                      // (> fourth)
        );

        WeekOfMonthNode result = new WeekOfMonthNode(dow);
        return result;
    }

    @Override
    public DaysOfWeekNode visitDaysOfWeekList(DaysOfWeekListContext ctx) {

        DayOfWeekNode dowNode = (DayOfWeekNode) visit(ctx.dayOfWeek());
        final DaysOfWeekListContext daysOfWeekList = ctx.daysOfWeekList();
        final DaysOfWeekNode result;
        if (daysOfWeekList == null) {
            result = new DaysOfWeekNode(dowNode);
        } else {
            DaysOfWeekNode days = (DaysOfWeekNode) visit(daysOfWeekList);
            result = new DaysOfWeekNode(dowNode, days);
        }
        return result;
    }

    static Map<Integer, DayOfWeek> TOKDOW = new HashMap<>();
    static {
        TOKDOW.put(SchedParser.MON, DayOfWeek.MONDAY);
        TOKDOW.put(SchedParser.TUE, DayOfWeek.TUESDAY);
        TOKDOW.put(SchedParser.WED, DayOfWeek.WEDNESDAY);
        TOKDOW.put(SchedParser.THU, DayOfWeek.THURSDAY);
        TOKDOW.put(SchedParser.FRI, DayOfWeek.FRIDAY);
        TOKDOW.put(SchedParser.SAT, DayOfWeek.SATURDAY);
        TOKDOW.put(SchedParser.SUN, DayOfWeek.SUNDAY);
    }

    @Override
    public DayOfWeekNode visitDayOfWeek(DayOfWeekContext ctx) {

        final TerminalNode child = (TerminalNode) ctx.getChild(0);
        final int type = child.getSymbol().getType();
        final DayOfWeek dow = Optional.<DayOfWeek> ofNullable(TOKDOW.get(type))
            .orElseThrow(() -> new RuntimeException());
        DayOfWeekNode result = new DayOfWeekNode(dow);
        return result;
    }

//    @Override
//    public SchedNode visitMonthOfYear(MonthOfYearContext ctx) {
//
//        // TODO Auto-generated method stub
//        return super.visitMonthOfYear(ctx);
//    }

    @Override
    public DaysOfMonthNode visitOrdDaysOfMonth(OrdDaysOfMonthContext ctx) {

        final DayNode ord = (DayNode) visit(ctx.ordDayOfMonth());
        final OrdDaysOfMonthContext tailOpt = ctx.ordDaysOfMonth();
        final DaysOfMonthNode result;
        if (tailOpt == null) {
            result = new DaysOfMonthNode(ord);
        } else {
            final DaysOfMonthNode daysNode = (DaysOfMonthNode) visit(tailOpt);
            result = new DaysOfMonthNode(ord, daysNode);
        }
        return result;
    }

    @Override
    public DayNode visitOrdDayOfMonth(OrdDayOfMonthContext ctx) {

        final Integer day = ctx.ordinal().value;
        final DayNode result = new DayNode(day);
        return result;
    }

    public static SchedNode compileText(final CharSequence text) {

        final SchedNode result;

        // set up ANTLR to apply the text to produce a "schedule" tree.
        SchedLexer lexer = new SchedLexer(
            new ANTLRInputStream(text.toString()));
        lexer.removeErrorListeners();
        final CommonAntlrErrorListener errListener = new CommonAntlrErrorListener();
        lexer.addErrorListener(errListener);
        final TokenStream tokens = new CommonTokenStream(lexer);
        SchedParser parser = new SchedParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(errListener);

        // this step does the actual parsing, error handling, tree creation.
        final ScheduleContext scheduleCtx = parser.schedule();

        if (errListener.hasErrors()) {
            result = new ParseErrorNode(errListener.getErrors());
        } else {
            SchedNode compiledSched;
            try {
                final SchedProducer producer = new SchedProducer();
                compiledSched = producer.visitSchedule(scheduleCtx);
            } catch (UnsupportedOperationException e) {
                compiledSched = null; // Error Schedule instance
            }
            result = compiledSched;
        }

        return result;
    }

}
