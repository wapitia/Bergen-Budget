
package com.wapitia.test.payment.sched;

import com.wapitia.common.parse.antlr.CountablesProducer;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestSchedProducer {

    static SchedExpected trial(String schedText, Integer expected) {
        return new SchedExpected(schedText, expected);
    }

    static class SchedExpected {
        String schedText;
        Integer expected;
        public SchedExpected(String schedText, Integer expected) {
            this.schedText = schedText;
            this.expected = expected;
        }
        public String getSchedText() {
            return schedText;
        }
        public Integer getExpected() {
            return expected;
        }

    }

    List<SchedExpected> allTrials = Arrays.asList(
            trial("SECOND", 2),
            trial("TWENTIETH", 20),
            trial("TWENTYTHIRD", 23),
            trial("TWENTY THIRD", 23),
            trial("TWENTY-THIRD", 23),
            trial("TWENTY-3RD", 23),
            trial("3RD", 3),
            trial("ELEVENTH", 11),
            trial("11TH", 11),
            trial("23RD", 23),
            trial("14", 14),
            trial("14TH", 14),
            trial("FOURTEEN", 14),
            trial("FOURTEENTH", 14),
            trial("TWENTY-THREE", 23),
            trial("SIXTH", 6),
            trial("NINETY-EIGHTH", 98),
            trial("THIRTY-EIGHT MILLION AND EIGHT THOUSANDTH", 38008000),
            trial("THREE MILLION AND EIGHT THOUSAND 4 HUNDRED SEVENTY-SEVENTH", 3008477),
            trial("FIVE SIXTY-SECOND", 562),
            trial("FIFTY-SIX HUNDRED AND TWENTY FIVE", 5625),
            trial("FOUR FOURTY-FOUR", 444)

   );

    @Test
    public void testX() {

        List<SchedExpected> trials = Arrays.asList(
           trial("FOUR FOURTY-FOUR", 444)

        );

        trials.forEach(this::trialRun);
    }

    @Test
    public void testAllTrials() {
        allTrials.forEach(this::trialRun);
    }

    void trialRun(SchedExpected schedExp) {
        final String schedText = schedExp.getSchedText();
        final Integer schedNode = CountablesProducer.readCountable(schedText);
        System.out.println(schedText + " => " + schedNode);
        Assert.assertEquals(schedExp.getExpected(), schedNode);
    }
}
