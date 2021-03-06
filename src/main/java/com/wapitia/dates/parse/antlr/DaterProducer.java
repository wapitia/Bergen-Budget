package com.wapitia.dates.parse.antlr;

import com.wapitia.common.parse.antlr.CommonAntlrError;
import com.wapitia.common.parse.antlr.CommonAntlrErrorListener;
import com.wapitia.dates.DaterBaseVisitor;
import com.wapitia.dates.DaterLexer;
import com.wapitia.dates.DaterParser;
import com.wapitia.dates.DaterParser.AFTContext;
import com.wapitia.dates.DaterParser.AprContext;
import com.wapitia.dates.DaterParser.AugContext;
import com.wapitia.dates.DaterParser.BEFContext;
import com.wapitia.dates.DaterParser.CDATE1Context;
import com.wapitia.dates.DaterParser.CDATE2Context;
import com.wapitia.dates.DaterParser.CDATE3Context;
import com.wapitia.dates.DaterParser.DayofmonthContext;
import com.wapitia.dates.DaterParser.DecContext;
import com.wapitia.dates.DaterParser.FebContext;
import com.wapitia.dates.DaterParser.JanContext;
import com.wapitia.dates.DaterParser.JulContext;
import com.wapitia.dates.DaterParser.JunContext;
import com.wapitia.dates.DaterParser.MONAMEContext;
import com.wapitia.dates.DaterParser.MONOContext;
import com.wapitia.dates.DaterParser.MarContext;
import com.wapitia.dates.DaterParser.MayContext;
import com.wapitia.dates.DaterParser.MonthnameContext;
import com.wapitia.dates.DaterParser.MonthnumContext;
import com.wapitia.dates.DaterParser.NovContext;
import com.wapitia.dates.DaterParser.ODRContext;
import com.wapitia.dates.DaterParser.OOAContext;
import com.wapitia.dates.DaterParser.OOBContext;
import com.wapitia.dates.DaterParser.OctContext;
import com.wapitia.dates.DaterParser.OpendaterangeContext;
import com.wapitia.dates.DaterParser.SDContext;
import com.wapitia.dates.DaterParser.SepContext;
import com.wapitia.dates.DaterParser.YearnumContext;
import com.wapitia.dates.parse.node.DaterNode;
import com.wapitia.dates.parse.node.DaterNode.DateRangeNode;
import com.wapitia.dates.parse.node.DaterNode.DayOfMonthNode;
import com.wapitia.dates.parse.node.DaterNode.HardDateNode;
import com.wapitia.dates.parse.node.DaterNode.MonthNode;
import com.wapitia.dates.parse.node.DaterNode.YearNode;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Collections;

/**
 * DaterProducer
 */
public class DaterProducer extends DaterBaseVisitor<DaterNode> {

//	@Override
//	public HardDateNode visitDatespec(DatespecContext ctx) {
//		final YearNode yy = visitYearnum(ctx.yy);
//		final MonthNode mm = (MonthNode) visit(ctx.mm);
//		final DayOfMonthNode dd = visitDayofmonth(ctx.dd);
//		return new HardDateNode(yy, mm, dd);
//	}

	@Override
	public YearNode visitYearnum(YearnumContext ctx) {
		int yy = Integer.valueOf(ctx.INT().getText());
		final int cent;
		if (yy >= 100) {
			cent = 0; // big enough year, don't add any centuries
		}
		else if (yy > (CURRENT_TWO_DIGIT_YEAR + TWO_DIGIT_YEAR_AHEAD_CUTOFF) % 100) {
			cent = LAST_CENTURY;
		}
		else {
			cent = CURRENT_CENTURY;
		}
		return new YearNode(cent + yy);
	}

	@Override
	public HardDateNode visitCDATE1(CDATE1Context ctx) {
		final YearNode yy = (YearNode) visit(ctx.yy);
		final MonthNode mm = (MonthNode) visit(ctx.mm);
		final DayOfMonthNode dd = visitDayofmonth(ctx.dd);
		return new HardDateNode(yy, mm, dd);
	}

	@Override
	public HardDateNode visitCDATE2(CDATE2Context ctx) {
		final YearNode yy = (YearNode) visit(ctx.yy);
		final MonthNode mm = (MonthNode) visit(ctx.mm);
		final DayOfMonthNode dd = visitDayofmonth(ctx.dd);
		return new HardDateNode(yy, mm, dd);
	}

	@Override
    public DaterNode visitCDATE3(CDATE3Context ctx) {
        final YearNode yy = (YearNode) visit(ctx.yy);
        final MonthNode mm = (MonthNode) visit(ctx.mo);
        final DayOfMonthNode dd = visitDayofmonth(ctx.dd);
        return new HardDateNode(yy, mm, dd);
    }

    private static final int CURRENT_TWO_DIGIT_YEAR = LocalDate.now().getYear() % 100;
	private static final int CURRENT_CENTURY = (LocalDate.now().getYear() / 100) * 100;
	private static final int LAST_CENTURY = CURRENT_CENTURY - 100;
	private static final int TWO_DIGIT_YEAR_AHEAD_CUTOFF = 10;  // years after which we revert to previous century

//	@Override
//	public YearNode visitYEAR2(YEAR2Context ctx) {
//		int yy = Integer.valueOf(ctx.Year2().getText());
//		int cent =  (yy > (CURRENT_TWO_DIGIT_YEAR + TWO_DIGIT_YEAR_AHEAD_CUTOFF) % 100)
//				? LAST_CENTURY
//				: CURRENT_CENTURY;
//		return new YearNode(cent + yy);
//	}

//	@Override
//	public YearNode visitYEAR4(YEAR4Context ctx) {
//		int yy = Integer.valueOf(ctx.Year4().getText());
//		return new YearNode(yy);
//	}

	@Override
	public DateRangeNode visitSD(SDContext ctx) {
		final HardDateNode d = (HardDateNode) visit(ctx.d);
		return DateRangeNode.makeClosedRange(d,d);
	}

	@Override
	public DateRangeNode visitODR(ODRContext ctx) {
		final HardDateNode d1 = (HardDateNode) visit(ctx.d1);
		final HardDateNode d2 = (HardDateNode) visit(ctx.d2);
		return DateRangeNode.makeClosedRange(d1,d2);
	}

	@Override
	public DateRangeNode visitAFT(AFTContext ctx) {

		final HardDateNode d3 = (HardDateNode) visit(ctx.d);
		final HardDateNode nextDay = d3.plusDays(1);
		return DateRangeNode.makeOpenEnd(nextDay);
	}

	@Override
	public DateRangeNode visitOOA(OOAContext ctx) {

		final HardDateNode d4 = (HardDateNode) visit(ctx.d);
		return DateRangeNode.makeOpenEnd(d4);
	}

	@Override
	public DateRangeNode visitBEF(BEFContext ctx) {
		final HardDateNode d5 = (HardDateNode) visit(ctx.d);
		final HardDateNode prevDay = d5.plusDays(-1);
		return DateRangeNode.makeOpenStart(prevDay);
	}

	@Override
	public DateRangeNode visitOOB(OOBContext ctx) {
		final HardDateNode d6 = (HardDateNode) visit(ctx.d);
		return DateRangeNode.makeOpenStart(d6);
	}

	// Month nodes

    @Override
    public MonthNode visitMonthname(MonthnameContext ctx) {
        return (MonthNode) this.visit(ctx.getChild(0));
    }

	@Override
	public MonthNode visitJan(JanContext ctx) {
		return new MonthNode(1);
	}

	@Override
	public MonthNode visitFeb(FebContext ctx) {
		return new MonthNode(2);
	}

	@Override
	public MonthNode visitMar(MarContext ctx) {
		return new MonthNode(3);
	}

	@Override
	public MonthNode visitApr(AprContext ctx) {
		return new MonthNode(4);
	}

	@Override
	public MonthNode visitMay(MayContext ctx) {
		return new MonthNode(5);
	}

	@Override
	public MonthNode visitJun(JunContext ctx) {
		return new MonthNode(6);
	}

	@Override
	public MonthNode visitJul(JulContext ctx) {
		return new MonthNode(7);
	}

	@Override
	public MonthNode visitAug(AugContext ctx) {
		return new MonthNode(8);
	}

	@Override
	public MonthNode visitSep(SepContext ctx) {
		return new MonthNode(9);
	}

	@Override
	public MonthNode visitOct(OctContext ctx) {
		return new MonthNode(10);
	}

	@Override
	public MonthNode visitNov(NovContext ctx) {
		return new MonthNode(11);
	}

	@Override
	public MonthNode visitDec(DecContext ctx) {
		return new MonthNode(12);
	}

	@Override
	public MonthNode visitMONO(MONOContext ctx) {
		return visitMonthnum(ctx.monthnum());
	}

	@Override
	public MonthNode visitMONAME(MONAMEContext ctx) {
		return visitMonthname(ctx.monthname());
	}



	@Override
	public DayOfMonthNode visitDayofmonth(DayofmonthContext ctx) {
		int dd = Integer.valueOf(ctx.INT().getText());
		return new DayOfMonthNode(dd);
	}

	@Override
	public MonthNode visitMonthnum(MonthnumContext ctx) {
		int mm = Integer.valueOf(ctx.INT().getText());
		return new MonthNode(mm);
	}

	public static DaterNode compileOpenDateRange(final CharSequence daterText) {

		final DaterNode result;

		if (StringUtils.isBlank(daterText)) {

			result = new DaterNode.BlankNode();
		}
		else {
			// Set up ANTLR to parse NICL.
			// Nicl020Lexer and Nicl020Parser are generated by ANTLR from the Nicl.g4 language
			// NiclCompilerErrorListener accumulates compiler errors.
			DaterLexer lexer = new DaterLexer(new ANTLRInputStream(daterText.toString()));
			CommonAntlrErrorListener errorHandler = new CommonAntlrErrorListener();
			lexer.removeErrorListeners();
			lexer.addErrorListener(errorHandler);
			final TokenStream tokens = new CommonTokenStream(lexer);
			DaterParser parser = new DaterParser(tokens);
			parser.removeErrorListeners();
			parser.addErrorListener(errorHandler);

			// Here's where ANTLR parses the incoming text into an ANTLR expressing tree
			// suitable for compiling.
			// By calling parser.statement() we expect the incoming text to be a NICL "statement".
			final OpendaterangeContext opendaterangeCtx = parser.opendaterange();

			System.out.println("opendaterange Context: \n" + opendaterangeCtx.toStringTree(parser));


			// ANTLR has parsed the text into its expression tree held in the context object,
			// or has failed in the attempt,
			// the latter case for which errorHandler will have compiler failures to pass along.
			if (errorHandler.hasErrors()) {
				// errorHandler collects ANTLR syntax compile errors: the text doesn't match the language
				result = new DaterNode.ErrorNode(errorHandler.getErrors());
			} else {
				// Compiler phase.
				DaterNode compileResult;
				try {
					final DaterProducer functionParser = new DaterProducer();
					compileResult = functionParser.visit(opendaterangeCtx);
				}
				catch (UnsupportedOperationException ex) {
					compileResult = new DaterNode.ErrorNode(
							Collections.singletonList(new CommonAntlrError(ex.toString())));
				}
				result = compileResult;
			}
		}

		return result;
	}
}