package com.dynatrace.utils;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for class {@link StopWatch}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class StopWatchTest {

	@Test
	public void testCtor() {
		Assert.assertTrue(new StopWatch(TimeUnit.NANOSECONDS).stop() > 0);
	}
}
