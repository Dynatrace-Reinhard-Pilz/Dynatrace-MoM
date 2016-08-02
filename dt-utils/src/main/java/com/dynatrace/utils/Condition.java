package com.dynatrace.utils;

public abstract class Condition {

	public abstract boolean isMet();
	public abstract Object getLock();
	
	public boolean await(long timeout)
		throws InterruptedException
	{
		return await(this, timeout);
	}		
	
	public static boolean await(Condition condition, long timeout)
		throws InterruptedException
	{
		if (condition == null) {
			return false;
		}
		long start = System.currentTimeMillis();
		Object lock = condition.getLock();
		while (!condition.isMet()) {
			if (System.currentTimeMillis() - start > timeout) {
				return false;
			}
			synchronized (lock) {
				lock.wait(timeout);
			}
		}
		return true;
	}	
}
