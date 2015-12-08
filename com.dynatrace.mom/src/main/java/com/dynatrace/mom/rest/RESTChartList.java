package com.dynatrace.mom.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = RESTChartList.TAG)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class RESTChartList extends RESTReferrable {
	public static final String TAG = "charts";
	
	private final Collection<RESTChart> metrics = new ArrayList<RESTChart>();
	
	RESTChartList() {
		this(null);
	}
	
	public RESTChartList(final HrefGenerator hrefGenerator) {
		super(hrefGenerator);
	}

	@XmlElementRef(type = RESTChart.class)
	public final Collection<RESTChart> getMetrics() {
		synchronized (metrics) {
			return new ArrayList<RESTChart>(metrics);
		}
	}
	
	public final void add(final String name) {
		Objects.requireNonNull(name);
		synchronized (this.metrics) {
			this.metrics.add(new RESTChart(name, new PrefixedHrefGenerator(getHref())));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final String getHrefPart() {
		return null;
	}
}
