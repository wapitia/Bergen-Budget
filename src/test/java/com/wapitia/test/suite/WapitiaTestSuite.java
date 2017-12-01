
package com.wapitia.test.suite;

import com.wapitia.test.common.collection.TestFeederSpliterator;
import com.wapitia.test.common.collection.TestLink;
import com.wapitia.test.dates.TestDateRange;
import com.wapitia.test.mvnsandbox.antlr.TestDaterProducer;
import com.wapitia.test.payment.sched.TestCSVParser;
import com.wapitia.test.payment.sched.TestDateStream;
import com.wapitia.test.payment.sched.TestPaymentStream;
import com.wapitia.test.payment.sched.TestSchedProducer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  TestDateRange.class,
  TestDaterProducer.class,
  TestFeederSpliterator.class,
  TestLink.class,
  TestSchedProducer.class,
  TestCSVParser.class,
  TestDateStream.class,
  TestPaymentStream.class
})
public class WapitiaTestSuite {

}
