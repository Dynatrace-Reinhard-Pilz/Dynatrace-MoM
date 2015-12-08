package com.dynatrace.utils;

public abstract class Condition {

	public abstract boolean isMet();
	public abstract Object getLock();
	
	public void await(long timeout)
		throws InterruptedException
	{
		await(this, timeout);
	}		
	
	public static void await(Condition condition, long timeout)
		throws InterruptedException
	{
		if (condition == null) {
			return;
		}
		Object lock = condition.getLock();
		while (!condition.isMet()) {
			synchronized (lock) {
				lock.wait(timeout);
			}
		}
	}	
}
