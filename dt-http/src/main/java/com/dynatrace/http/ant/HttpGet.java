package com.dynatrace.http.ant;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.dynatrace.http.Http;
import com.dynatrace.http.Method;
import com.dynatrace.http.config.Credentials;

public class HttpGet extends Task {
	
	private String url = null;
	private String user = null;
	private String pass = null;
	private final Collection<HttpParam> params = new ArrayList<HttpParam>();
	
	public void setUrl(String url) {
		Objects.requireNonNull(url);
		this.url = url;
	}
	
	public void setUser(String user) {
		Objects.requireNonNull(user);
		this.user = user;
	}
	
	public void setPass(String pass) {
		Objects.requireNonNull(pass);
		this.pass = pass;
	}
	
	public void add(HttpParam param) {
		Objects.requireNonNull(param);
		params.add(param);
	}

	@Override
	public void execute() throws BuildException {
		Credentials credentials = null;
		if ((user != null) && (pass != null)) {
			credentials = new Credentials(user, pass);
		}
		Method method = Method.GET;
		URL url = null;
		try {
			url = new URL(this.url);
		} catch (MalformedURLException e) {
			throw new BuildException(e);
		}
		try {
			Http.client().request(url, method, credentials, System.out);
		} catch (IOException e) {
			throw new BuildException(e);
		}
	}
}
