package com.dynatrace.reporting;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.xml.XMLUtil;

/*

<chartdashlet name="Completed PurePaths per Interval" description="" showabsolutevalues="false">
  <measures>
    <measure measure="Completed PurePaths per Interval" color="#00c080" aggregation="Sum" avg="982.8444444444444" unit="num" min="0.0" max="1801.0" sum="44228.0" count="131">
      <measurement timestamp="1426185630000" avg="0.0" min="0.0" max="0.0" sum="0.0" count="1"></measurement>
      <measurement timestamp="1426185660000" avg="0.3333333333333333" min="0.0" max="1.0" sum="1.0" count="3"></measurement>
      <measurement timestamp="1426185690000" avg="25.0" min="0.0" max="75.0" sum="75.0" count="3"></measurement>
      <measurement timestamp="1426185720000" avg="245.66666666666666" min="102.0" max="348.0" sum="737.0" count="3"></measurement>
      <measurement timestamp="1426185750000" avg="272.6666666666667" min="171.0" max="371.0" sum="818.0" count="3"></measurement>
      <measurement timestamp="1426185780000" avg="405.6666666666667" min="330.0" max="479.0" sum="1217.0" count="3"></measurement>
      <measurement timestamp="1426185810000" avg="563.0" min="432.0" max="789.0" sum="1689.0" count="3"></measurement>
      <measurement timestamp="1426185840000" avg="505.0" min="429.0" max="569.0" sum="1515.0" count="3"></measurement>
      <measurement timestamp="1426185870000" avg="309.3333333333333" min="246.0" max="383.0" sum="928.0" count="3"></measurement>
      <measurement timestamp="1426185900000" avg="168.0" min="106.0" max="262.0" sum="504.0" count="3"></measurement>
      <measurement timestamp="1426185930000" avg="600.3333333333334" min="344.0" max="818.0" sum="1801.0" count="3"></measurement>
      <measurement timestamp="1426185960000" avg="322.0" min="288.0" max="368.0" sum="966.0" count="3"></measurement>
      <measurement timestamp="1426185990000" avg="530.3333333333334" min="371.0" max="654.0" sum="1591.0" count="3"></measurement>
      <measurement timestamp="1426186020000" avg="219.0" min="203.0" max="229.0" sum="657.0" count="3"></measurement>
      <measurement timestamp="1426186050000" avg="342.3333333333333" min="159.0" max="503.0" sum="1027.0" count="3"></measurement>
      <measurement timestamp="1426186080000" avg="318.6666666666667" min="243.0" max="408.0" sum="956.0" count="3"></measurement>
      <measurement timestamp="1426186110000" avg="335.6666666666667" min="317.0" max="372.0" sum="1007.0" count="3"></measurement>
      <measurement timestamp="1426186140000" avg="233.33333333333334" min="168.0" max="270.0" sum="700.0" count="3"></measurement>
      <measurement timestamp="1426186170000" avg="292.0" min="240.0" max="367.0" sum="876.0" count="3"></measurement>
      <measurement timestamp="1426186200000" avg="350.6666666666667" min="248.0" max="445.0" sum="1052.0" count="3"></measurement>
      <measurement timestamp="1426186230000" avg="364.3333333333333" min="231.0" max="476.0" sum="1093.0" count="3"></measurement>
      <measurement timestamp="1426186260000" avg="514.6666666666666" min="366.0" max="611.0" sum="1544.0" count="3"></measurement>
      <measurement timestamp="1426186290000" avg="249.33333333333334" min="207.0" max="324.0" sum="748.0" count="3"></measurement>
      <measurement timestamp="1426186320000" avg="372.0" min="276.0" max="501.0" sum="1116.0" count="3"></measurement>
      <measurement timestamp="1426186350000" avg="413.3333333333333" min="350.0" max="521.0" sum="1240.0" count="3"></measurement>
      <measurement timestamp="1426186380000" avg="282.6666666666667" min="109.0" max="441.0" sum="848.0" count="3"></measurement>
      <measurement timestamp="1426186410000" avg="312.3333333333333" min="118.0" max="567.0" sum="937.0" count="3"></measurement>
      <measurement timestamp="1426186440000" avg="556.3333333333334" min="478.0" max="679.0" sum="1669.0" count="3"></measurement>
      <measurement timestamp="1426186470000" avg="300.0" min="254.0" max="384.0" sum="900.0" count="3"></measurement>
      <measurement timestamp="1426186500000" avg="190.33333333333334" min="174.0" max="206.0" sum="571.0" count="3"></measurement>
      <measurement timestamp="1426186530000" avg="219.33333333333334" min="82.0" max="294.0" sum="658.0" count="3"></measurement>
      <measurement timestamp="1426186560000" avg="552.6666666666666" min="470.0" max="676.0" sum="1658.0" count="3"></measurement>
      <measurement timestamp="1426186590000" avg="163.0" min="91.0" max="227.0" sum="489.0" count="3"></measurement>
      <measurement timestamp="1426186620000" avg="268.6666666666667" min="126.0" max="389.0" sum="806.0" count="3"></measurement>
      <measurement timestamp="1426186650000" avg="528.0" min="408.0" max="668.0" sum="1584.0" count="3"></measurement>
      <measurement timestamp="1426186680000" avg="423.3333333333333" min="204.0" max="637.0" sum="1270.0" count="3"></measurement>
      <measurement timestamp="1426186710000" avg="508.0" min="415.0" max="687.0" sum="1524.0" count="3"></measurement>
      <measurement timestamp="1426186740000" avg="244.33333333333334" min="120.0" max="394.0" sum="733.0" count="3"></measurement>
      <measurement timestamp="1426186770000" avg="422.3333333333333" min="353.0" max="534.0" sum="1267.0" count="3"></measurement>
      <measurement timestamp="1426186800000" avg="366.3333333333333" min="297.0" max="487.0" sum="1099.0" count="3"></measurement>
      <measurement timestamp="1426186830000" avg="449.6666666666667" min="309.0" max="549.0" sum="1349.0" count="3"></measurement>
      <measurement timestamp="1426186860000" avg="225.0" min="171.0" max="254.0" sum="675.0" count="3"></measurement>
      <measurement timestamp="1426186890000" avg="386.3333333333333" min="167.0" max="527.0" sum="1159.0" count="3"></measurement>
      <measurement timestamp="1426186920000" avg="270.0" min="150.0" max="349.0" sum="810.0" count="3"></measurement>
      <measurement timestamp="1426186950000" avg="364.0" min="364.0" max="364.0" sum="364.0" count="1"></measurement>
    </measure>
  </measures>
</chartdashlet>

 */
@XmlRootElement(name = "chartdashlet")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class ChartDashlet extends Dashlet {

	private String name = null;
	private String description = null;
	private boolean showAbsoluteValues = false;
	private Collection<Measure> measures = null;
	
	public final void setName(final String name) {
		this.name = name;
	}
	
	@XmlAttribute(name = "name")
	public final String getName() {
		return name;
	}
	
	public final void setDescription(final String description) {
		this.description = description;
	}
	
	@XmlAttribute(name = "description")
	public final String getDescription() {
		return description;
	}
	
	public final void setShowAbsoluteValues(final boolean showAbsoluteValues) {
		this.showAbsoluteValues = showAbsoluteValues;
	}
	
	public final boolean isShowAbsoluteValues() {
		return showAbsoluteValues;
	}
	
	public final void setMeasures(final Collection<Measure> measures) {
		this.measures = measures;
	}
	
	@XmlElementWrapper(name = "measures")
	@XmlElementRef(type = Measure.class)
	public final Collection<Measure> getMeasures() {
		return measures;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return XMLUtil.toString(this);
	}
	
}
