package com.dynatrace.profilediff;

import java.io.File;

import org.w3c.dom.Document;

import com.dynatrace.utils.DomUtil;

public class ProfileDiffMain {
	
	private static final File FLD_DT_HOME = new File("C:\\workspaces\\data\\trunk-classic\\jloadtrace");
	private static final File FLD_SERVER = new File(FLD_DT_HOME, "server");
	private static final File FLD_CONF = new File(FLD_SERVER, "conf");
	private static final File FLD_PROFILES = new File(FLD_CONF, "profiles");

	public static void main(String[] args) {
		// "C:\\Program Files\\dynaTrace\\dynaTrace 6.3\\server\\conf\\profiles"
		// "C:\workspaces\data\trunk-classic\jloadtrace\server\conf\profiles\AjaxWorld.profile.xml"
		// "C:\workspaces\data\trunk-classic\jloadtrace\server\conf\profiles\AjaxWorldTests.profile.xml"
		File profileA = new File(FLD_PROFILES, "AjaxWorld.profile.xml");
		File profileB = new File(FLD_PROFILES, "AjaxWorldModified.profile.xml");
		try {
			Document documentA = DomUtil.build(profileA);
			Document documentB = DomUtil.build(profileB);
			boolean result = NodeMatcher.matches(documentA, documentB);
			System.out.println(result);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
