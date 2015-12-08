package com.dynatrace.http.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class HttpPost extends Task {
	
	private String text = null;

	@Override
	public void execute() throws BuildException {
		log(text);
	}
	
	public void addText(String text) {
		this.text = text;
	}
}
