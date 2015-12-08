package com.dynatrace.cmd;

public abstract class AbstractArgumentConsumer implements ArgumentConsumer {

	public ArgumentConsumer or(ArgumentConsumer c) {
		return new OrArgumentConsumer(this, c);
	}

}
