package com.dynatrace.http;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.utils.Strings;

@XmlRootElement(name = BooleanResult.TAG)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class BooleanResult {
	
	public static final BooleanResult TRUE = new BooleanResult(true);
	public static final BooleanResult FALSE = new BooleanResult(false);

	static final String TAG = "result";
	static final String ATTRIBUTE_VALUE = "value";
	
	private boolean value = false;
	
	public BooleanResult() {
		
	}
	
	public BooleanResult(boolean value) {
		this.value = value;
	}
	
	@XmlAttribute(name = BooleanResult.ATTRIBUTE_VALUE)
	public boolean isValue() {
		return value;
	}
	
	public void setValue(boolean value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return new StringBuilder("{").append(TAG).append(Strings.SPC).
				append(ATTRIBUTE_VALUE).append(Strings.EQ).append(value).
				append("}").toString();
	}
}	

