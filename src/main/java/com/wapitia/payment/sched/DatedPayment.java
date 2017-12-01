
package com.wapitia.payment.sched;

import com.wapitia.finance.FinancialEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

public class DatedPayment extends Payment {

    private final LocalDate paymentDate;

    protected DatedPayment(LocalDate paymentDate, Currency cur,
        BigDecimal amount, FinancialEntity from, FinancialEntity to, String note)
    {
        super(cur, amount, from, to, note);
        this.paymentDate = paymentDate;
    }

    public DatedPayment(LocalDate paymentDate, final Payment bp) {
        super(bp.getCurrency(), bp.getAmount(), bp.getSource(), bp.getTarget(),
            bp.getNote());
        this.paymentDate = paymentDate;
    }

    public LocalDate getPaymentDate() {

        return paymentDate;
    }

    public DatedPayment reverse() {

        DatedPayment result = Builder.from(paymentDate, getTarget())
            .to(getSource()).currency(getCurrency())
            .amount(getAmount().negate()).note(getNote()).toDatedPayment();
        return result;
    }

    @Override
    public String toString() {

        return paymentDate.toString() + ", " + super.toString();
    }

    public static class Builder extends DatedPaymentBuilder<Builder> {

        public static Builder from(LocalDate paymentDate, FinancialEntity from) {

            final Builder result = DatedPaymentBuilder
                .<Builder> from(paymentDate, from, Builder::new);
            return result;
        }
    }

}
