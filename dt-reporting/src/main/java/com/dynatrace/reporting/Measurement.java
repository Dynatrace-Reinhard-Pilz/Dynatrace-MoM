package com.dynatrace.reporting;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.dynatrace.xml.XMLUtil;

/*
<measurement timestamp="1426185630000" avg="0.0" min="0.0" max="0.0" sum="0.0" count="1"></measurement>
*/

@XmlRootElement(name = "measurement")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class Measurement {

	private long timeStamp = 0;
	private double avg = Double.NaN;
	private double min = Double.NaN;
	private double max = Double.NaN;
	private double sum = Double.NaN;
	private int count = 0;
	
	public final void setTimeStamp(final long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	@XmlAttribute(name = "timestamp")
	public final long getTimeStamp() {
		return timeStamp;
	}
	
	public final void setAvg(final double avg) {
		this.avg = avg;
	}
	
	@XmlAttribute(name = "avg")
	@XmlJavaTypeAdapter(type = double.class, value = DoubleAdapter.class)
	public final double getAvg() {
		return avg;
	}
	
	public final void setMin(final double min) {
		this.min = min;
	}
	
	@XmlAttribute(name = "min")
	@XmlJavaTypeAdapter(type = double.class, value = DoubleAdapter.class)
	public final double getMin() {
		return min;
	}
	
	public final void setMax(final double max) {
		this.max = max;
	}
	
	@XmlAttribute(name = "max")
	@XmlJavaTypeAdapter(type = double.class, value = DoubleAdapter.class)
	public final double getMax() {
		return max;
	}
	
	public final void setSum(final double sum) {
		this.sum = sum;
	}
	
	@XmlAttribute(name = "sum")
	@XmlJavaTypeAdapter(type = double.class, value = DoubleAdapter.class)
	public final double getSum() {
		return sum;
	}
	
	public final void setCount(final int count) {
		this.count = count;
	}
	
	@XmlAttribute(name = "count")
	public final int getCount() {
		return count;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return XMLUtil.toString(this);
	}
}
