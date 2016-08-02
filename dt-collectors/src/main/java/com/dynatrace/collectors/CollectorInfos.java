package com.dynatrace.collectors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "collectors")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CollectorInfos implements Collection<CollectorInfo> {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(CollectorInfos.class.getName());
	
	private Collection<CollectorInfo> collectors =
			new HashSet<CollectorInfo>();
	
	@XmlElementRef(
		type = CollectorInfo.class,
		name = "collectorinformation"
	)
	public Collection<CollectorInfo> getCollectors() {
		return collectors;
	}
	
	public void setCollectors(Collection<CollectorInfo> collectors) {
		this.collectors = collectors;
	}

	@Override
	public int size() {
		return collectors.size();
	}

	@Override
	@XmlTransient
	public boolean isEmpty() {
		return collectors.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return collectors.contains(o);
	}

	@Override
	public Iterator<CollectorInfo> iterator() {
		return collectors.iterator();
	}

	@Override
	public Object[] toArray() {
		return collectors.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return collectors.toArray(a);
	}

	@Override
	public boolean add(CollectorInfo e) {
		return collectors.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return collectors.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return collectors.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends CollectorInfo> c) {
		return collectors.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return collectors.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return collectors.retainAll(c);
	}

	@Override
	public void clear() {
		collectors.clear();
	}

}
