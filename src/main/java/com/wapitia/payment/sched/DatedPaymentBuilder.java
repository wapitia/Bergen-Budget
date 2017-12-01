
package com.wapitia.payment.sched;

import com.wapitia.finance.FinancialEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Supplier;

class DatedPaymentBuilder<B extends DatedPaymentBuilder<B>>
    extends PaymentBuilder<B>
{

    protected Optional<LocalDate> paymentDateOpt = Optional.<LocalDate> empty();

    protected static <B extends DatedPaymentBuilder<B>> B from(
        LocalDate paymentDate, FinancialEntity from, Supplier<B> maker)
    {

        final B result = PaymentBuilder.<B> from(from, maker);
        result.paymentDate(paymentDate);
        return result;
    }

    public B paymentDate(LocalDate paymentDate) {

        this.paymentDateOpt = Optional.<LocalDate> of(paymentDate);
        @SuppressWarnings("unchecked")
        final B result = (B) this;
        return result;
    }

    public DatedPayment toDatedPayment() {

        this.remAlreadyPaid = alreadyPaid.orElse(BigDecimal.ZERO);
        final DatedPayment payment = new DatedPayment(
            paymentDateOpt.orElseThrow(
                () -> new RuntimeException("paymentDate undefined")),
            super.toPayment());
        return payment;
    }
}
