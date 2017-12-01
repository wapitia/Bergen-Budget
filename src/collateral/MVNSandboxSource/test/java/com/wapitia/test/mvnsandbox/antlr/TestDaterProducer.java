package com.wapitia.test.mvnsandbox.antlr;

import com.wapitia.dates.parse.node.DaterNode;
import com.wapitia.dates.parse.antlr.DaterProducer;

import org.junit.Assert;
import org.junit.Test;


public class TestDaterProducer {

	@Test
	public void test1() {
		
		runOneTrial("between 2017-09-24 and 2017-10-25", 
				"Closed date range from 2017-09-24 through 2017-10-25");
		
		runOneTrial("between 2017-September-24 and 2017-Oct-25", 
				"Closed date range from 2017-09-24 through 2017-10-25");
		
		runOneTrial("on or after 2017-September-24", 
				"Half-open date range starting from 2017-09-24");
		
		runOneTrial("after 2017-September-24", 
				"Half-open date range starting from 2017-09-25");

		runOneTrial("after 2017-09-24", 
				"Half-open date range starting from 2017-09-25");
		
		runOneTrial("on or before 2017-September-24", 
				"Half-open date range finishing on 2017-09-24");
		
		runOneTrial("before 2017-September-24", 
				"Half-open date range finishing on 2017-09-23");
	}

	@Test
	public void testX() {
		runOneTrial("2017-09-24", 
				"Closed date range from 2017-09-24 through 2017-09-24");
		
	}
	
	/**
	 * @param daterScript
	 * @param expectedString
	 */
	void runOneTrial(String daterScript, String expectedString) {
		
		final DaterNode node = DaterProducer.compileOpenDateRange(daterScript.trim().toUpperCase());
		final String nodeStr = node.toString();
		Assert.assertEquals(expectedString, nodeStr);
	}
	
}
