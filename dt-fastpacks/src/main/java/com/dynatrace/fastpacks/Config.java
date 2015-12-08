package com.dynatrace.fastpacks;

import java.util.Collection;

import com.dynatrace.cmd.CommandLineException;
import com.dynatrace.cmd.MultiStringOption;
import com.dynatrace.cmd.Options;
import com.dynatrace.cmd.StringOption;

public class Config {
	
	private static final String DEFAULT_AUTHOR = "no@author.com";
	private static final String OPTION_NAME = "-name";
	private static final String OPTION_AUTHOR = "-author";
	private static final String OPTION_DASHBOARDS = "-dashboards";
	private static final String OPTION_PROFILES = "-profiles";
	private static final String OPTION_OUTPUT = "-output";
	private static final String OPTION_SERVERS = "-servers";

	private final StringOption optName = new StringOption(OPTION_NAME, true);
	private final StringOption optAuthor = new StringOption(
		OPTION_AUTHOR,
		DEFAULT_AUTHOR
	);
	private final MultiStringOption optDashboards =
		new MultiStringOption(OPTION_DASHBOARDS, false);
	private final MultiStringOption optProfiles =
		new MultiStringOption(OPTION_PROFILES, false);
	private final StringOption optOutput = new StringOption(OPTION_OUTPUT, false);
	private final StringOption optServers = new StringOption(OPTION_SERVERS, false);
	private final Options arguments = new Options(
		optName,
		optAuthor,
		optProfiles,
		optDashboards,
		optOutput,
		optServers
	);
	
	public Config(String[] args) throws CommandLineException  {
		arguments.consume(args);
	}
	
	public String getFastPackName() {
		return optName.getValue();
	}
	
	public String getAuthor() {
		return optAuthor.getValue();
	}
	
	public String getOutput() {
		String output = optOutput.getValue();
		if (output == null) {
			return optName.getValue() + ".dtp";
		}
		return output;
	}
	
	public Collection<String> getDashboards() {
		return optDashboards.getValue();
	}
	
	public Collection<String> getProfiles() {
		return optProfiles.getValue();
	}
	
	public String getServers() {
		return optServers.getValue();
	}

}
