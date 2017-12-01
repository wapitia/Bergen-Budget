
package com.wapitia.payment.sched;

import com.wapitia.finance.FinancialEntity;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

class PaymentBuilder<B extends PaymentBuilder<B>> {

    protected Optional<Currency>    currencyOpt      = Optional
        .<Currency> of(Payment.USD);
    protected Optional<BigDecimal>  amountOpt        = Optional
        .<BigDecimal> empty();
    protected Optional<FinancialEntity> sourceOpt        = Optional
        .<FinancialEntity> empty();
    protected Optional<FinancialEntity> targetOpt        = Optional
        .<FinancialEntity> empty();
    protected Optional<String>      noteOpt          = Optional
        .<String> empty();
    protected Optional<BigDecimal>  firstPayment     = Optional
        .<BigDecimal> empty();
    protected Optional<BigDecimal>  alreadyPaid      = Optional
        .<BigDecimal> empty();
    protected Boolean               skipFirstPayment = Boolean.FALSE;

    protected static <B extends PaymentBuilder<B>> B from(FinancialEntity from,
        Supplier<B> maker)
    {

        final B result = maker.get().currency(Payment.USD).source(from);
        return result;
    }

    public B currency(Currency currency) {

        this.currencyOpt = Optional.of(currency);
        @SuppressWarnings("unchecked")
        final B result = (B) this;
        return result;
    }

    public B amount(BigDecimal amount) {

        this.amountOpt = Optional.of(amount);
        @SuppressWarnings("unchecked")
        final B result = (B) this;
        return result;
    }

    public B amount(double d) {

        this.amountOpt = Optional.of(new BigDecimal(d));
        @SuppressWarnings("unchecked")
        final B result = (B) this;
        return result;
    }

    public B source(FinancialEntity source) {

        this.sourceOpt = Optional.of(source);
        @SuppressWarnings("unchecked")
        final B result = (B) this;
        return result;
    }

    public B to(FinancialEntity target) {

        this.targetOpt = Optional.of(target);
        @SuppressWarnings("unchecked")
        final B result = (B) this;
        return result;
    }

    public B note(String note) {

        this.noteOpt = Optional.of(note);
        @SuppressWarnings("unchecked")
        final B result = (B) this;
        return result;
    }

    public B firstPayment(BigDecimal firstPayment) {

        this.firstPayment = Optional.ofNullable(firstPayment);
        @SuppressWarnings("unchecked")
        final B result = (B) this;
        return result;
    }

    public B alreadyPaid(BigDecimal alreadyPaid) {

        this.alreadyPaid = Optional.ofNullable(alreadyPaid);
        @SuppressWarnings("unchecked")
        final B result = (B) this;
        return result;
    }

    public B skipFirstPayment(Boolean skipFirstPayment) {

        this.skipFirstPayment = Optional.ofNullable(skipFirstPayment)
            .orElse(Boolean.FALSE);
        @SuppressWarnings("unchecked")
        final B result = (B) this;
        return result;
    }

    public Payment toPayment() {

        BigDecimal amt = amountOpt.orElse(BigDecimal.ZERO);
        if (!this.madeFirstPayment && this.skipFirstPayment) {
            amt = BigDecimal.ZERO;
            this.madeFirstPayment = true;
        }
        if (!this.madeFirstPayment && this.firstPayment.isPresent()) {
            amt = amt.max(this.firstPayment.get());
            this.madeFirstPayment = true;
        }
        if (remAlreadyPaid.compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal deductAmt = amt.min(remAlreadyPaid);
            amt = amt.subtract(deductAmt);
            remAlreadyPaid.subtract(deductAmt);
        }
        final Payment payment = new Payment(currencyOpt.orElse(Payment.USD),
            amt,
            sourceOpt
                .orElseThrow(() -> new RuntimeException("Source undefined")),
            targetOpt
                .orElseThrow(() -> new RuntimeException("Target undefined")),
            noteOpt.orElse(""));
        return payment;
    }

    // TODO: Wrap volatile stream state!
    boolean    madeFirstPayment;
    BigDecimal remAlreadyPaid;

    public Stream<Payment> steadyStream() {

        this.madeFirstPayment = false;
        this.remAlreadyPaid = alreadyPaid.orElse(BigDecimal.ZERO);
        final Payment steadyPayment = toPayment();
        final Stream<Payment> result = Stream.generate(() -> steadyPayment);
        return result;
    }

}
