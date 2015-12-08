package com.dynatrace.xml.mock;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class MockJAXBContext extends JAXBContext {
	
	private final Marshaller marshaller;
	private final Unmarshaller unmarshaller;
	
	public MockJAXBContext() {
		this(null, null);
	}
	
	public MockJAXBContext(Marshaller marshaller, Unmarshaller unmarshaller) {
		this.marshaller = marshaller;
		this.unmarshaller = unmarshaller;
	}

	public MockJAXBContext(Marshaller marshaller) {
		this(marshaller, null);
	}
	
	public MockJAXBContext(Unmarshaller unmarshaller) {
		this(null, unmarshaller);
	}
	
	@Override
	public Unmarshaller createUnmarshaller() throws JAXBException {
		if (unmarshaller == null) {
			throw new JAXBException("no unmarshaller set");
		}
		return unmarshaller;
	}

	@Override
	public Marshaller createMarshaller() throws JAXBException {
		if (marshaller == null) {
			throw new JAXBException("no marshaller set");
		}
		return marshaller;
	}

	@Override
	@Deprecated
	public javax.xml.bind.Validator createValidator() throws JAXBException {
		throw new JAXBException("not implemented");
	}

}
