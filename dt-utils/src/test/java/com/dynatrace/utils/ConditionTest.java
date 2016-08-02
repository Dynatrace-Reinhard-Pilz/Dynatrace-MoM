package com.dynatrace.utils;

import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.Test;

public class ConditionTest {

	@Test
	public void testCondition() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		final Condition c = new Condition() {
			
			private int cnt = 0;

			@Override
			public boolean isMet() {
				return ++cnt > 1;
			}

			@Override
			public Object getLock() {
				return this;
			}
			
		};
		final Ref<Object> ref = new Ref<Object>();
		final Thread locker = new Thread() {
			
			@Override
			public void run() {
				synchronized (c) {
					try {
						latch.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		locker.start();
		final Ref<Boolean> conditionMet = new Ref<Boolean>();
		conditionMet.set(false);
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					conditionMet.set(c.await(1000));
				} catch (InterruptedException e) {
					ref.set(e);
				}
			}
		};
		t.start();
		latch.countDown();
		
		locker.join();
		t.join();
		Assert.assertNull(ref.get());
		Assert.assertTrue(conditionMet.get());
	}
	
	@Test
	public void testConditionNotMetInTime() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		final Condition c = new Condition() {
			
			@Override
			public boolean isMet() {
				return false;
			}

			@Override
			public Object getLock() {
				return this;
			}
			
		};
		final Ref<Object> ref = new Ref<Object>();
		final Thread locker = new Thread() {
			
			@Override
			public void run() {
				synchronized (c) {
					try {
						latch.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		final Ref<Boolean> conditionMet = new Ref<Boolean>();
		conditionMet.set(false);
		locker.start();
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					conditionMet.set(c.await(100));
				} catch (InterruptedException e) {
					ref.set(e);
				}
			}
		};
		t.start();
		latch.countDown();
		
		locker.join();
		t.join();
		Assert.assertNull(ref.get());
		Assert.assertFalse(conditionMet.get());
	}
	
	@Test
	public void testConditionInterrupted() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		final Condition c = new Condition() {

			@Override
			public boolean isMet() {
				Thread.currentThread().interrupt();
				return false;
			}

			@Override
			public Object getLock() {
				return this;
			}
			
		};
		final Ref<Object> ref = new Ref<Object>();
		final Thread locker = new Thread() {
			
			@Override
			public void run() {
				synchronized (c) {
					try {
						latch.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		locker.start();
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					c.await(1000);
				} catch (InterruptedException e) {
					ref.set(e);
				}
			}
		};
		t.start();
		latch.countDown();
		
		locker.join();
		t.join();
		Assert.assertEquals(InterruptedException.class, ref.get().getClass());
	}
	
	@Test
	public void testAwaitStatic() throws InterruptedException {
		Condition.await(null, 0);
	}
}
