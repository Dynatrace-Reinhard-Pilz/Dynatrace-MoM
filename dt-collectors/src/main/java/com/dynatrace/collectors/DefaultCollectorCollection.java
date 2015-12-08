package com.dynatrace.collectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import com.dynatrace.utils.Iterables;
import com.dynatrace.utils.Labelled;

public class DefaultCollectorCollection implements CollectorCollection {
	
	private final Map<CollectorInfo, CollectorRecord> collectors =
			new HashMap<CollectorInfo, CollectorRecord>();
	
	private final Labelled server;
	
	public DefaultCollectorCollection(Labelled server) {
		this.server = server;
	}
	
	public void onCollectorRemoved(CollectorRecord collectorRecord) {
		// subclasses may override
	}
	
	public void onCollectorAdded(CollectorRecord collectorRecord) {
		// subclasses may override
	}
	
	public void onCollectorUpdated(CollectorRecord collectorRecord) {
		// subclasses may override
	}
	
	private void removeMissing(Iterable<CollectorInfo> collectorInfos) {
		Iterator<CollectorInfo> it = null;
		for (it = this.collectors.keySet().iterator(); it.hasNext(); ) {
			CollectorInfo collectorInfo = it.next();
			boolean found = false;
			for (CollectorInfo cInfo : collectorInfos) {
				if (collectorInfo.equals(cInfo)) {
					found = true;
				}
			}
			if (!found) {
				CollectorRecord collectorRecord = get(collectorInfo);
				if (!collectorRecord.isRestarting()) {
					collectorRecord.getCollectorInfo().setConnected(false);
//					it.remove();
//					onCollectorRemoved(collectorRecord);
				}
			}
		}
	}
	
	public void addAll(Iterable<CollectorInfo> collectorInfos) {
		if (collectorInfos == null) {
			return;
		}
		synchronized (this.collectors) {
			removeMissing(collectorInfos);
			for (CollectorInfo collectorInfo : collectorInfos) {
				add(collectorInfo);
			}
		}
	}
	
	public void add(CollectorInfo collectorInfo) {
		if (collectorInfo == null) {
			return;
		}
		if (collectorInfo.isSelfMonitoring()) {
			return;
		}
		synchronized (this.collectors) {
			CollectorRecord collectorRecord = null;
			if (!this.collectors.containsKey(collectorInfo)) {
				collectorRecord = new CollectorRecord(server, collectorInfo);
				onCollectorAdded(collectorRecord);
			} else {
				collectorRecord = this.collectors.get(collectorInfo);
				collectorRecord.setCollectorInfo(collectorInfo);
				onCollectorUpdated(collectorRecord);
			}
			this.collectors.put(collectorInfo, collectorRecord);
		}
	}
	
	public Collection<CollectorRecord> values() {
		synchronized (this) {
			return new ArrayList<CollectorRecord>(
				collectors.values()
			);
		}
	}
	
	public CollectorRecord get(CollectorInfo collectorInfo) {
		Objects.requireNonNull(collectorInfo);
		synchronized (this) {
			return collectors.get(collectorInfo);
		}
	}
	
	public void remove(CollectorInfo collectorInfo) {
		Objects.requireNonNull(collectorInfo);
		synchronized (this) {
			collectors.remove(collectorInfo);
		}
	}

	@Override
	public Iterator<CollectorRecord> iterator() {
		return values().iterator();
	}
	
	public int size() {
		synchronized (collectors) {
			return collectors.size();
		}
	}
	
	public static CollectorRecord get(Iterable<CollectorRecord> collectors, String name, String host) {
		if (Iterables.isNullOrEmpty(collectors)) {
			return null;
		}
		for (CollectorRecord collectorRecord : collectors) {
			String collectorName = collectorRecord.getName();
			if (!collectorName.equals(name)) {
				continue;
			}
			String collectorHost = collectorRecord.getHost();
			if (!collectorHost.equals(host)) {
				continue;
			}
			return collectorRecord;
		}
		return null;
	}

	@Override
	public CollectorCollection filter(Filter<CollectorRecord> filter) {
		final ArrayList<CollectorRecord> records = new ArrayList<>();
		records.addAll(collectors.values());
		for (Iterator<CollectorRecord> it = records.iterator(); it.hasNext(); ) {
			if (!filter.accept(it.next())) {
				it.remove();
			}
		}
		return new CollectorCollection() {

			@Override
			public int size() {
				return records.size();
			}

			@Override
			public Iterator<CollectorRecord> iterator() {
				return records.iterator();
			}

			@Override
			public void addAll(Iterable<CollectorInfo> collectorInfos) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void add(CollectorInfo collectorInfo) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Collection<CollectorRecord> values() {
				return records;
			}

			@Override
			public CollectorRecord get(CollectorInfo collectorInfo) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void remove(CollectorInfo collectorInfo) {
				throw new UnsupportedOperationException();
			}

			@Override
			public CollectorCollection filter(Filter<CollectorRecord> filter) {
				throw new UnsupportedOperationException();
			}
		};
	}
	
}
