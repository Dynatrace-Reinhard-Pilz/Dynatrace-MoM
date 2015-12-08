package com.dynatrace.http.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class HttpUpload extends Task {

	@Override
	public void execute() throws BuildException {
		System.out.println("HTTP UPLOAD");
	}
}
