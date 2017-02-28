package com.dynatrace.mom;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.config.PWHConfig;
import com.dynatrace.mom.AbstractPWHChecker.CountRecord;
import com.dynatrace.utils.Labelled;

public final class PWHChecker extends Thread implements AutoCloseable {
	
	public static final PWHChecker NONE = new PWHChecker(null, null, null);
	public static final PWHChecker NULL = new PWHChecker(null, null, null);
	
	private final UUID uuid = UUID.randomUUID();
	private static final Logger LOGGER =
			Logger.getLogger(PWHChecker.class.getName());
	
	private static final int SLEEP_MILLIS = 10000;
	private static final SimpleDateFormat DF =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private volatile boolean isRunning = true;
	private final AtomicLong lastModTime = new AtomicLong(0);
	private AbstractPWHChecker checker = null;
	private final CountRecord[] countRecords = new CountRecord[] {
		new CountRecord("measurement_high"),
		new CountRecord("measurement_temp1"),
		new CountRecord("measurement_temp2")
	};

	private final PWHAware pwhAware;
	
	public PWHChecker(Labelled label, PWHConfig config, PWHAware pwhAware) {
		String lbl = "undefined";
		if (label != null) {
			lbl = label.name();
		}
		if (config != null) {
			setName("PWH Checker[" + lbl + " -> "+ config.toString() + "]");
//			LOGGER.log(Level.INFO, uuid + " / " + getName() + " created");
			setDaemon(true);
			isRunning = true;
			start();
		}
//		"localhost",
//		"dynaTrace6",
//		"dynaTrace",
//		"labpass"
		
		checker = PWHCheckerFactory.createPWHChecker(label, config);
		this.pwhAware = pwhAware;
	}
	
	public PWHChecker(Labelled label, PWHConfig config) {
		this(label, config, null);
	}
	
	public static enum DelaySeverity {
		ok(400000), critical(600000), severe(Long.MAX_VALUE);
		
		public final long delay;
		
		private DelaySeverity(long delay) {
			this.delay = delay;
		}
		
		public static DelaySeverity fromDelay(long delay) {
			if (delay <= ok.delay) {
				return DelaySeverity.ok;
			}
			if (delay <= critical.delay) {
				return DelaySeverity.critical;
			}
			return DelaySeverity.severe;
		}
	}

	public long getLastTimeStamp() {
		return lastModTime.get();
	}
	
	public String getLastFormattedTimeStamp() {
		return DF.format(new Date(lastModTime.get()));
	}
	
	public long getDelayInSeconds() {
		long delayInSec = getDelay() / 1000;
//		LOGGER.info(uuid + " / " + "delayInSec: " + delayInSec);
		return delayInSec;
	}
	
	public long getDelay() {
		long delay = (System.currentTimeMillis() - lastModTime.get());
//		LOGGER.info(uuid + " / " + "delay: " + delay);
		return delay;
	}
	
	public DelaySeverity getSeverity() {
		return DelaySeverity.fromDelay(getDelay());
	}
	
	@Override
	public void run() {
		while (isRunning) {
			for (CountRecord countRecord : countRecords) {
				if (isRunning) {
					update(countRecord);
				}
			}
			if (!isRunning) {
				return;
			}
			long localLastModTime = CountRecord.getLastModTime(countRecords);
			if (localLastModTime > 0) {
//				LOGGER.info(uuid + " / " + "lastModTime.set(" + localLastModTime + ");");
				lastModTime.set(localLastModTime);
			}
			if (pwhAware != null) {
				pwhAware.setLastModificationTime(localLastModTime);
			}
//			LOGGER.log(Level.INFO, uuid + " / " + "Last Modificaion Time on Measurement Tables: " + localLastModTime + " (" + DF.format(new Date(localLastModTime)) + ") (= " + getDelayInSeconds() + ")");
			try {
				Thread.sleep(SLEEP_MILLIS);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
	
	private void update(CountRecord countRecord) {
		if (countRecord == null) {
			return;
		}
		if (checker != null) {
			try {
				checker.update(countRecord);
			} catch (Throwable t) {
				LOGGER.log(Level.WARNING, "Unable to query for entries in " + countRecord.table, t);
			}
		}
	}

	@Override
	public void close() {
		if (isRunning) {
			this.interrupt();
//			LOGGER.log(Level.INFO, uuid + " / " + getName() + " shutting down");
			isRunning = false;
		}
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
