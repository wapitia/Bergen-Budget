
package com.wapitia.payment.sched;

import com.wapitia.common.collection.Blenderator;
import com.wapitia.common.collection.Zipperator;
import com.wapitia.finance.FinancialEntity;
import com.wapitia.sched.Cycle;
import com.wapitia.sched.Cycles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PayCycles {

    public static class Register {

        static class DPRStreamer implements Spliterator<DatedPaymentRegist> {

            Spliterator<DatedPayment> dpstream;
            DatedPaymentRegist        header;
            DatedPaymentRegist        current;

            public DPRStreamer(double amt, Stream<DatedPayment> dpstream) {
                this.dpstream = dpstream.spliterator();
                this.header = DatedPaymentRegist
                    .makeBaseline(new BigDecimal(amt));
                this.current = new DatedPaymentRegist();
                current.setRunningTotal(header.getRunningTotal());
            }

            @Override
            public boolean tryAdvance(
                Consumer<? super DatedPaymentRegist> action)
            {

                // peel off the header the one time
                if (header != null) {
                    action.accept(header);
                    this.header = null;
                    return true;
                }

                final boolean advanced = dpstream
                    .tryAdvance((DatedPayment dp) -> {
                        final BigDecimal runTot = current.getRunningTotal();
                        final BigDecimal pmt = dp.getAmount();
                        DatedPaymentRegist newReg = new DatedPaymentRegist();
                        newReg.setLastDatedPayment(dp);
                        final BigDecimal newRunTot = runTot.add(pmt);
                        newReg.setRunningTotal(newRunTot);
                        current.setRunningTotal(newRunTot);
                        action.accept(newReg);
                    });

                return advanced;
            }

            @Override
            public Spliterator<DatedPaymentRegist> trySplit() {

                return null;
            }

            @Override
            public long estimateSize() {

                return dpstream.estimateSize();
            }

            @Override
            public int characteristics() {

                return dpstream.characteristics();
            }
        }

        public Stream<DatedPaymentRegist> registerStream(double startBalance,
            Stream<DatedPayment> stream)
        {

            final DPRStreamer spliter = new DPRStreamer(startBalance, stream);
            Stream<DatedPaymentRegist> result = StreamSupport.stream(spliter,
                false);
            return result;
        }

        public static Optional<DatedPayment> targetEntity(final FinancialEntity ent,
            final DatedPayment dp)
        {

            final Optional<DatedPayment> result;
            if (dp.getTarget().equals(ent)) {
                result = Optional.<DatedPayment> of(dp);
            } else if (dp.getSource().equals(ent)) {
                result = Optional.<DatedPayment> of(dp.reverse());
            } else {
                result = Optional.empty();
            }
            return result;
        }

        static Comparator<DatedPayment> comp =
            (a, b) -> a.getPaymentDate().compareTo(b.getPaymentDate());

        List<Stream<DatedPayment>>      streams = new ArrayList<>();

        public void add(final Stream<LocalDate> payDates,
            final Stream<Payment> neonPayDeposit)
        {

            Stream<DatedPayment> payStream = zip(payDates, neonPayDeposit);
            streams.add(payStream);
        }

        public void addSteadyMonthly(int dayOfMonth, FinancialEntity from,
            FinancialEntity to, BigDecimal amount, LocalDate startDate,
            String note)
        {

            final Cycle cycle = Cycles.monthlyCycle().addDayOfMonth(dayOfMonth)
                .startOnOrAfter(startDate).toCycle();

            final Stream<Payment> pmts = Payment.Builder.from(from).to(to)
                .amount(amount).note(note).steadyStream();

            add(cycle.stream(), pmts);

        }

        public Stream<DatedPayment> stream() {

            final Blenderator.Builder<DatedPayment> builder =
                new Blenderator.Builder<DatedPayment>(comp);

            streams.forEach(s -> builder.add(s));
            Spliterator<DatedPayment> meldSpliter = builder.toBlenderator();
            Stream<DatedPayment> meldStream = StreamSupport.stream(meldSpliter,
                false);
            return meldStream;
        }
    }

    public static Stream<DatedPayment> zip(final Stream<LocalDate> payDates,
        final Stream<Payment> steadyPaycheck)
    {

        Spliterator<DatedPayment> pder = Zipperator
            .<LocalDate, Payment, DatedPayment> zip(payDates.spliterator(),
                steadyPaycheck.spliterator(), DatedPayment::new);

        Stream<DatedPayment> dpstream = StreamSupport.stream(pder, false);
        return dpstream;
    }

}
