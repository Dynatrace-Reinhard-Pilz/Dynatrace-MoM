package com.dynatrace.mom.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.reporting.Measure;

@XmlRootElement(name = RESTMeasureList.TAG)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class RESTMeasureList {
	public static final String TAG = "chart";
	
	private final Collection<Measure> measures = new ArrayList<Measure>();
	private final HrefGenerator hrefGenerator;
	
	public RESTMeasureList() {
		this(null);
	}
	
	public RESTMeasureList(final HrefGenerator hrefGenerator) {
		this.hrefGenerator = hrefGenerator;
	}
	
	public RESTMeasureList(
			final Iterable<Measure> measures,
			final HrefGenerator hrefGenerator
	) {
		this(hrefGenerator);
		addAll(measures);
	}
	
	@XmlAttribute(name = "href")
	public final String getHref() {
		return hrefGenerator.getHref(null);
	}

	@XmlElementRef(type = Measure.class)
	public final Collection<Measure> getMeasures() {
		synchronized (measures) {
			return new ArrayList<Measure>(measures);
		}
	}
	
	public final void addAll(final Iterable<Measure> measures) {
		if (measures == null) {
			return;
		}
		synchronized (this.measures) {
			for (Measure measure : measures) {
				if (measure != null) {
					this.measures.add(measure);
				}
			}
		}
	}
	
	public final void add(final Measure measure) {
		Objects.requireNonNull(measure);
		synchronized (this.measures) {
			this.measures.add(measure);
		}
	}
}
