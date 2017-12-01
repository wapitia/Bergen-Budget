
package com.wapitia.test.payment.sched;

import com.wapitia.common.collection.Zipperator;
import com.wapitia.common.io.FileUtilities;
import com.wapitia.common.io.csv.HeaderedCSVCollector;
import com.wapitia.common.io.csv.HeaderedSpreadsheet;
import com.wapitia.finance.AccountEntry;
import com.wapitia.finance.FinancialEntity;
import com.wapitia.payment.io.CSVAccountEntryBuilder;
import com.wapitia.payment.io.MalformedAccountEntryException;
import com.wapitia.payment.sched.DatedPayment;
import com.wapitia.payment.sched.DatedPaymentRegist;
import com.wapitia.payment.sched.PayCycles;
import com.wapitia.payment.sched.Payment;
import com.wapitia.sched.CycDow;
import com.wapitia.sched.Cycle;
import com.wapitia.sched.Cycles;
import com.wapitia.sched.Cycles.MonthlyBuilder;
import com.wapitia.sched.Cycles.WeeklyBuilder;
import com.wapitia.sched.parse.antlr.SchedProducer;
import com.wapitia.sched.parse.node.BiweeklySchedNode;
import com.wapitia.sched.parse.node.MonthlySchedNode;
import com.wapitia.sched.parse.node.SchedNode;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TestPaymentStream {

    @Test @Ignore
    public void testPayStr1() {
        FinancialEntity bank1 = FinancialEntity.makeNamedEntity("PRS", "Purse");
        FinancialEntity battellePay = FinancialEntity.makeNamedEntity("BEI",
                        "Battelle Income");
        BigDecimal paycheck = new BigDecimal(3094.00D);

        final LocalDate startDate = LocalDate.of(2017, 11, 1);

        Cycle payCycle = Cycles.weeklyCycle().weeksInCycle(2)
            .cycleWeekZeroHasDate(LocalDate.of(2017, 10, 20))
            .addDay(DayOfWeek.FRIDAY, 0).startOnOrAfter(startDate)
            .toCycle();

        final Stream<LocalDate> stream = payCycle.stream();
        stream.limit(20L).forEach(System.out::println);

        System.out.println("==============");

        Cycle mortgageCycle = Cycles.monthlyCycle().addDayOfMonth(4)
                        // .addDayOfFirstWeek(DayOfWeek.MONDAY)
                        // .addDayOfSecondWeek(DayOfWeek.MONDAY)
                        // .addDayOfThirdWeek(DayOfWeek.THURSDAY)
                        // .addDayOfFourthWeek(DayOfWeek.THURSDAY)
                        // .addDayOfLastWeek(DayOfWeek.THURSDAY)
                        .startOnOrAfter(startDate).toCycle();

        final Stream<LocalDate> stream2 = mortgageCycle.stream();
        // stream2.limit(20L).forEach(System.out::println);

        System.out.println("==============");

        final Payment paycheckPmt = Payment.Builder.from(battellePay)
                        .amount(paycheck).to(bank1).toPayment();

        final Stream<Payment> steadyPaycheck = Stream
                        .generate(() -> paycheckPmt);
        // steadyPaycheck.limit(20L).forEach(System.out::println);

        Spliterator<DatedPayment> pder = Zipperator
            .<LocalDate, Payment, DatedPayment> zip(
                stream2.spliterator(),
                steadyPaycheck.spliterator(),
                DatedPayment::new);
        Stream<DatedPayment> dpstream = StreamSupport.stream(pder, false);
        dpstream.limit(20L).forEach(System.out::println);

    }

    @Test @Ignore
    public void dryRun() {

        // entities
        final FinancialEntity purse = FinancialEntity.makeNamedEntity("PRS", "Purse");
        final FinancialEntity battellePay = FinancialEntity.makeNamedEntity("BEI",
                        "DIRECT DEP NEON INC");
        final FinancialEntity homeMtg = FinancialEntity.makeNamedEntity("1BM",
                        "RE First Bank mortgage");
        final FinancialEntity ben = FinancialEntity.makeNamedEntity("BEN",
                        "Ben");
        final FinancialEntity rac = FinancialEntity.makeNamedEntity("RAC",
                        "Rachel");

        final LocalDate startDate = LocalDate.of(2017, 11, 1);
        PayCycles.Register register = new PayCycles.Register();

        final Stream<LocalDate> mtgDates = Cycles.monthlyCycle()
            .addDayOfMonth(4).startOnOrAfter(startDate).stream();
        final Stream<Payment> mtgPmts = Payment.Builder
                        .from(purse)
                        .to(homeMtg)
                        .amount(1414.84D)
                        .note("Mtg Juniper")
                        .steadyStream();
        // .steadyStreamUntil(100000D);

        register.add(mtgDates, mtgPmts);

        // payStream
        final Stream<LocalDate> incomeDates = Cycles.weeklyCycle()
            .weeksInCycle(2)
            .cycleWeekZeroHasDate(LocalDate.of(2017, 10, 20))
            .addDay(DayOfWeek.FRIDAY, 0).startOnOrAfter(startDate)
            .stream();

        // .streamUntil(PaymentDate.of(2029,01,02));

        final Stream<Payment> incomePayments = Payment.Builder
                        .from(battellePay)
                        .to(purse)
                        .amount(3051.49D)
                        .note("Paycheck")
                        .steadyStream();
        // .steadyStreamUntil(100000D);

        register.add(incomeDates, incomePayments);

        // kids
        register.addSteadyMonthly(1, purse, ben, new BigDecimal(1000.00D),
                        startDate, "Ben Monthy Rent");

        register.addSteadyMonthly(8, rac, ben, new BigDecimal(80.00D),
                        startDate, "Rac to Ben Monthly");

        register.addSteadyMonthly(15, purse, ben, new BigDecimal(300.00D),
                        startDate, "Ben Mid-Month");

        register.addSteadyMonthly(1, purse, rac, new BigDecimal(1000.00D),
                        startDate, "Rac Monthy Rent");

        // insurance
        register.addSteadyMonthly(11, purse,
                        FinancialEntity.makeNamedEntity("HRTF", "Hartford"),
                        new BigDecimal(285.95D),
                        startDate, "Hartford Auto Ins");

        register.addSteadyMonthly(29, purse,
                        FinancialEntity.makeNamedEntity("GEIC", "Geico"),
                        new BigDecimal(266.96D),
                        startDate, "Geico Rachels Auto Ins");

        register.addSteadyMonthly(2, purse,
                        FinancialEntity.makeNamedEntity("NYL", "NY Life"),
                        new BigDecimal(111.05D),
                        startDate, "New York Life Insurance Premium");

        register.addSteadyMonthly(2, purse,
                        FinancialEntity.makeNamedEntity("NYL I", "NY Life Ins Prem"),
                        new BigDecimal(19.57D),
                        startDate, "New York Life Insurance Premium");

        // utilities
        register.addSteadyMonthly(11, purse,
                        FinancialEntity.makeNamedEntity("EXEL", "Excel Energy"),
                        new BigDecimal(200.00D),
                        startDate, "Excel Energy Monthly Ave Bill");

        register.addSteadyMonthly(11, purse,
                        FinancialEntity.makeNamedEntity("COMC", "Comcast"),
                        new BigDecimal(103.45D),
                        startDate, "Comcast Monthly Ave Bill");

        register.addSteadyMonthly(3, purse,
                        FinancialEntity.makeNamedEntity("EVM", "Evergreen Metro"),
                        new BigDecimal(74.00D),
                        startDate, "Evergreen Metro Monthly Ave Bill");


        register.addSteadyMonthly(25, purse,
                        FinancialEntity.makeNamedEntity("VER", "Verizon"),
                        new BigDecimal(241.55D),
                        startDate, "Verizon Monthly Ave Phone Bill");

        Stream<DatedPayment> meldStream = register.stream();

        Function<DatedPayment, Optional<DatedPayment>> intoPurse =
           (DatedPayment dp) -> PayCycles.Register.targetEntity(purse, dp);

           double currentBalance = 1366.00D;
           Stream<DatedPaymentRegist> regStream = register.registerStream(
                           currentBalance,
                           meldStream
                        .map(intoPurse)
                        .filter(Optional::isPresent)
                        .map(Optional::get));
           regStream.limit(60L).forEach(System.out::println);


    }

    @Test
    public void dataDrivenRun() {
        final String inputCsvFile = "src/test/resources/cdm_flow.csv";
        final LocalDate startDate = LocalDate.of(2017, 11, 8);

        final PayCycles.Register register = new PayCycles.Register();
        try {
            final CharSequence s = FileUtilities.fromFile(inputCsvFile);
            final HeaderedSpreadsheet spreadSheet = HeaderedCSVCollector.spreadsheeetOf(s);
            spreadSheet.rowsStream()
                       .map(CSVAccountEntryBuilder::accountEntryOfRow)
                      .forEach((AccountEntry ae) -> this.f(ae, startDate, register));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

        Stream<DatedPayment> meldStream = register.stream();

        Function<DatedPayment, Optional<DatedPayment>> intoPurse =
           (DatedPayment dp) -> PayCycles.Register.targetEntity(moneyEntityOf("Purse", "Purse"), dp);

           double currentBalance = 160.25D;
           Stream<DatedPaymentRegist> regStream = register.registerStream(
                           currentBalance,
                           meldStream
                        .map(intoPurse)
                        .filter(Optional::isPresent)
                        .map(Optional::get));
           regStream.limit(60L).forEach(System.out::println);
    }

    /**
     * Each account entry is the source of two streams:
     * The Cycle stream defines a generic payment cycle, one
     *    which defines a repetition of payment dates as a Stream
     *    but does not define the details of a payment such as from,
     *    to, amount, note or other filters or boundaries.
     * The Payment stream defines a (possibly variable) dollar amount stream,
     * as well as payer, payee, note.
     * @param ae
     * @param startDate
     * @param register
     */
    public void f(final AccountEntry ae, LocalDate startDate,
        PayCycles.Register register) {

        final Cycle cycle = cycleOfSchedNode(ae.getCycle(), startDate);

        // @TODO
        ae.getAfterDate();
        ae.getUntilDate();
        ae.getUntilAmount();
        ae.getAlreadyPaid();
        ae.getFirstPayment();

        final Stream<Payment> pmts = ae.getSkip()
               ? Stream.<Payment> empty()
               : Payment.Builder
                      .from(moneyEntityOf(ae.getFrom(), ae.getFrom()))
                      .to(moneyEntityOf(ae.getTo(), ae.getTo()))
                      .amount(ae.getAmount())
                      .note(ae.getNotes())
                      .firstPayment(ae.getFirstPayment().orElse(null))
                      .alreadyPaid(ae.getAlreadyPaid().orElse(null))
                      .skipFirstPayment(ae.getSkipFirstPayment())
                      .steadyStream();

        register.add(cycle.stream(), pmts);
    }


    private FinancialEntity moneyEntityOf(String ident, String name) {
        return new FinancialEntity(ident, name);
    }

    Cycle cycleOfSchedNode(String schedText, LocalDate startDate)
        throws MalformedAccountEntryException {

        final Cycle result;

        final String cycleUpperCase = schedText.toUpperCase();
        final SchedNode schedNode = SchedProducer.compileText(cycleUpperCase);

        if (schedNode instanceof MonthlySchedNode) {
            final MonthlyBuilder cbldr = Cycles.monthlyCycle();
            MonthlySchedNode mnthSchedNode = (MonthlySchedNode) schedNode;
            IntStream doms = mnthSchedNode.daysOfMonthStream();
            doms.forEach(cbldr::addDayOfMonth);
            cbldr.startOnOrAfter(startDate);
            result = cbldr.toCycle();
        }
        else if (schedNode instanceof BiweeklySchedNode){
            final WeeklyBuilder wbldr = Cycles.biweeklyCycle();
            BiweeklySchedNode biwSchedNode = (BiweeklySchedNode) schedNode;
            Stream<CycDow> dows = biwSchedNode.cycledDaysOfWeekStream();

            // TODO
//            wbldr.cycleWeekZeroHasDate(PaymentDate.of(2017, 10, 20));
            dows.forEach((CycDow dow) -> wbldr.addDay(dow.getDow(), dow.getWoc().ord()));
//            wbldr.addDay(DayOfWeek.FRIDAY, 0);

            wbldr.startOnOrAfter(startDate);
            result = wbldr.toCycle();
        }
        else {
            throw new MalformedAccountEntryException(
                "Unrecognized SchedNode: " + schedNode.toString());
        }

        return result;
    }

}
