$(document).ready(function() {
	pageContext.request.chartUrl =
		pageContext.request.contextPath +
		"/rest/servers/" +
		pageContext.request.server.name +
		"/charts";
});

var charts = {};	
$(document).ready(function() {
	
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });	
	
	$.get(pageContext.request.chartUrl, function (data) {
		var charts = data.getElementsByTagName("chart");
		for (var i = 0; i < charts.length; i++) {
			var chartName = charts[i].getAttribute("name");
			if (chartName.indexOf("Communication") == -1) {
//				if (chartName.indexOf("Server Memory Usage") != -1) {
					addChart(chartName);
//					break;
//				}
			}
		}
	});
	
});


/*
 * 
 */
function addChart(title) {
	var highChartsElem = document.getElementById("highcharts");
	var container = document.createElement("div");
	container.style.width = "15%";
	container.style.minWidth = "15%";
	container.style.height = "150px";
	container.style.margin = "2px";
	container.style.display = "inline-block";
	container.style.border = "1px solid #DDDDDD";
	container.style.padding = "0px";
	container.id = guid();
	highChartsElem.appendChild(container);
	charts[container.id] = new Highcharts.Chart({
        chart: {
        	renderTo: container.id,
			type: 'area',
            animation: false,
//            marginLeft: 40,
            marginRight: 1,
//            marginTop: 0,
            spacingBottom: 1,
            spacingTop: 3
		},
        credits: {
            enabled: false
        },
        legend: {
        	itemStyle: {
        		fontWeight: 'normal',
        		fontSize: '10px'
        	}
        },
        title: {
        	text: title,
            style: {
                fontSize: '12px',
                fontWeight: 'bold'
            }
        },
        xAxis: {
            allowDecimals: false,
//            minRange: 3600 * 1000 * 24,
            labels: {
                formatter: function () {
                    return Highcharts.dateFormat('%H:%M', this.value);
                },
        		style: {
            		fontSize: '10px'
        		}
            }
        },
        yAxis: {
            title: {
                text: null,
                style: {
                    fontSize: '10px'
                }
            },
            labels: {
                formatter: function () {
                    return this.value;
                },
        		style: {
            		fontSize: '8px'
        		}
            }
        },
        tooltip: {
//            pointFormat: '{point.y:,.0f}'
			pointFormat: null,
			headerFormat: '',
            pointFormatter: function() {
            	return Highcharts.dateFormat('%H:%M', this.x) + ": " + this.y;
            }
        },
        plotOptions: {
            area: {
                // pointStart: 1940,
                marker: {
                    enabled: false,
                    symbol: 'circle',
                    radius: 2,
                    states: {
                        hover: {
                            enabled: true
                        }
                    }
                }
            }
        }
    });
	refreshChart(charts[container.id]);
}

/*
 * 
 */
function addMeasurement(series, measurement, shift) {
	if (series.data.length > 0) {
		var lastPoint = series.data[series.data.length - 1];
		if (lastPoint.x >= measurement.x) {
			return false;
		}
	}
	series.addPoint([ measurement.x, measurement.y ], false, shift, false);
	return true;
}

/*
 * 
 */
function Measurement(measurementElem) {
	this.x = parseInt(measurementElem.getAttribute("timestamp"));
	this.y = parseFloat(measurementElem.getAttribute("avg"));
}

/*
 * 
 */
function extractMeasurements(measurementElems) {
	if (isEmpty(measurementElems)) {
		return [];
	}
	var measurements = new Array(measurementElems.length);
	for (var i = 0; i < measurementElems.length; i++) {
		measurements[i] = new Measurement(measurementElems[i]);
	}
	measurements.sort(function(a, b) {
		return a.x - b.x;
	});
	return measurements;
}

/*
 * 
 */
function addMeasurements(series, measureElem) {
	var measurementElems = getMeasurementElems(measureElem)
	if (isEmpty(measurementElems)) {
		return false;
	}
	var numDataPoints = series.data.length;
	var haveMeasurementsBeenAdded = false;
	var measurements = extractMeasurements(measurementElems);
	for (var i = 0; i < measurements.length; i++) {
		if (addMeasurement(series, measurements[i], (numDataPoints > 20))) {
			haveMeasurementsBeenAdded = true;
			numDataPoints++;
		}
	}
	return haveMeasurementsBeenAdded;
}

/*
 * 
 */
function setYAxisTitle(chart, measure) {
	if (!chart.yAxis[0].title) {
		var unit = measure.getAttribute("unit");
		chart.yAxis[0].setTitle({ text: unit });
	}
}

/*
 * 
 */
function getMeasureElems(xmlDoc) {
	try {
		return xmlDoc.getElementsByTagName("measure");
	} catch (err) {
		return [];
	}
}

/*
 * 
 */
function getMeasurementElems(measureElem) {
	return measureElem.getElementsByTagName("measurement");
}

/*
 * 
 */
function refreshChart(aChart) {
	var chart = aChart;
	var url = pageContext.request.chartUrl + "/" + chart.title.textStr;
	$.get(url, function (xmlDoc) {
		var measureElems = getMeasureElems(xmlDoc);
		log(url);
		if (isNotEmpty(measureElems)) {
			guessSeriesAttribute(measureElems);
			var isRedrawRequired = false;
			for (var i = 0; i < measureElems.length; i++) {
				var measureElem = measureElems[i];
				log("...." + measureElem.getAttribute("measure"));
				var series = getOrCreateSeries(
					chart,
					measureElems.measurename(measureElem)
				);
				setYAxisTitle(chart, measureElem);
				if (addMeasurements(series, measureElem)) {
					isRedrawRequired = true;
				}
			}
			if (isRedrawRequired) {
				log("...... chart.redraw()");
				chart.redraw();
			}
		}
		scheduleRefresh(chart);
	});
}

/*
 * 
 */
function scheduleRefresh(chart) {
	window.setTimeout(function() {
		refreshChart(chart);
	}, 5000);
}

/*
 * 
 */
function getOrCreateSeries(chart, name) {
	for (var i = 0; i < chart.series.length; i++) {
		if (chart.series[i].name == name) {
			return chart.series[i];
		}
	}
	return chart.addSeries({                        
	    name: name,
	    data: [],
		fillOpacity: 0.5
	}, false);
}

/*
 * 
 */
function guessSeriesAttribute(measureElems) {
	if (measureElems.length == 1) {
		measureElems.measurename = function(measureElem) {
			return measureElem.getAttribute("measure");
		};
	}
	var measureNames = {};
	var aggregations = {};
	for (var i = 0; i < measureElems.length; i++) {
		measureNames[measureElems[i].getAttribute("measure")] = true;
		aggregations[measureElems[i].getAttribute("aggregation")] = true;
	}
	if (Object.keys(measureNames).length == 1) {
		if (Object.keys(aggregations).length == 1) {
			measureElems.measurename = function(measureElem) {
				return measureElem.getAttribute("measure");
			};
		} else {
			measureElems.measurename = function(measureElem) {
				return measureElem.getAttribute("aggregation");
			};
		}
	} else {
		if (Object.keys(aggregations).length == 1) {
			measureElems.measurename = function(measureElem) {
				return measureElem.getAttribute("measure");
			};
		} else {
			measureElems.measurename = function(measureElem) {
				return measureElem.getAttribute("measure") + " (" + measure.getAttribute("aggregation") + ")"
			};
		}
	}
}