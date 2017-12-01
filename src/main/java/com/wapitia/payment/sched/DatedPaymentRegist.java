
package com.wapitia.payment.sched;

import java.math.BigDecimal;

public class DatedPaymentRegist {

    public static DatedPaymentRegist makeBaseline(BigDecimal currentBalance) {

        DatedPaymentRegist result = new DatedPaymentRegist();
        result.setRunningTotal(currentBalance);
        return result;
    }

    private int        nDaysFromStart;
    private DatedPayment lastDatedPayment;
    private BigDecimal runningTotal;
    private BigDecimal minBalance;
    private BigDecimal dailyBudget;
    private BigDecimal weeklyBudget;
    private BigDecimal monthlyBudget;
    private String     note;

    public DatedPaymentRegist() {
        this.note = "";
    }

    public int getnDaysFromStart() {

        return nDaysFromStart;
    }

    public void setnDaysFromStart(int nDaysFromStart) {

        this.nDaysFromStart = nDaysFromStart;
    }

    public DatedPayment getLastDatedPayment() {

        return lastDatedPayment;
    }

    public void setLastDatedPayment(DatedPayment lastDatedPayment) {

        this.lastDatedPayment = lastDatedPayment;
    }

    public BigDecimal getRunningTotal() {

        return runningTotal;
    }

    public void setRunningTotal(BigDecimal runningTotal) {

        this.runningTotal = runningTotal;
    }

    public BigDecimal getMinBalance() {

        return minBalance;
    }

    public void setMinBalance(BigDecimal minBalance) {

        this.minBalance = minBalance;
    }

    public BigDecimal getDailyBudget() {

        return dailyBudget;
    }

    public void setDailyBudget(BigDecimal dailyBudget) {

        this.dailyBudget = dailyBudget;
    }

    public BigDecimal getWeeklyBudget() {

        return weeklyBudget;
    }

    public void setWeeklyBudget(BigDecimal weeklyBudget) {

        this.weeklyBudget = weeklyBudget;
    }

    public BigDecimal getMonthlyBudget() {

        return monthlyBudget;
    }

    public void setMonthlyBudget(BigDecimal monthlyBudget) {

        this.monthlyBudget = monthlyBudget;
    }

    public String getNote() {

        return note;
    }

    public void setNote(String note) {

        this.note = note;
    }

    @Override
    public String toString() {

        if (this.lastDatedPayment == null) {
            return "START WITH: " + this.runningTotal.toString();
        } else {
            return lastDatedPayment.toString() + "; "
                + this.runningTotal.toString();
        }

    }

}
