
package com.wapitia.payment.sched;

import com.wapitia.finance.FinancialEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class Payment {

    static final Currency     USD = Currency.getInstance("USD");

    private final Currency    currency;

    // always a positive amount: to reverse a charge reverse source / target
    private final BigDecimal  amount;

    private final FinancialEntity source;
    private final FinancialEntity target;

    private final String      note;

    protected Payment(Currency cur, BigDecimal amount, FinancialEntity from,
        FinancialEntity to, String note)
    {
        this.currency = cur;
        this.amount = amount.setScale(2, RoundingMode.HALF_DOWN);
        this.source = from;
        this.target = to;
        this.note = note;
    }

    public Currency getCurrency() {

        return currency;
    }

    public BigDecimal getAmount() {

        return amount;
    }

    public FinancialEntity getSource() {

        return source;
    }

    public FinancialEntity getTarget() {

        return target;
    }

    public String getNote() {

        return note;
    }

    @Override
    public String toString() {

        return String.format("%s -> %s, %s%s, %s", source.toString(),
            target.toString(), getCurrency().getSymbol(),
            getAmount().toString(), getNote());
    }

    public static class Builder extends PaymentBuilder<Builder> {

        public static Builder from(FinancialEntity from) {

            final Builder result = PaymentBuilder.<Builder> from(from,
                Builder::new);
            return result;
        }

    }

}
