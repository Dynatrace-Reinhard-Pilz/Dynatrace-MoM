package com.dynatrace.onboarding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.Http;
import com.dynatrace.http.Method;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.onboarding.config.Config;
import com.dynatrace.onboarding.fastpacks.OnBoardingTaskVerifier;
import com.dynatrace.onboarding.profiles.Profile;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class OnBoardingTask {
	
	private static final String EXMSG_EXEC_UNSUCCESSFUL =
			"Executing the Onboarding task was not successful";
	
	private static final String MSG_TRIGGERING = "Triggering Onboarding Task";
	private static final String TASK_NAME = "Automatic Onboarding Task";
	
	private static final Logger LOGGER =
			Logger.getLogger(OnBoardingTask.class.getName());

	public static boolean trigger(Profile profile) {
		LOGGER.log(Level.INFO, MSG_TRIGGERING);
		Objects.requireNonNull(profile);
		String profileName = profile.id();
		try {
			String encProfileName = encode(profileName);
			String encTask = encode(TASK_NAME);
			URL execTaskMethod = Config.serverConfig().createURL("/rest/management/profiles/" + encProfileName + "/tasks/" + encTask + "/run");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Http.client().request(execTaskMethod, Method.GET, Config.serverConfig().getCredentials(), baos);
			String response = new String(baos.toByteArray());
			if (!response.equals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><result value=\"true\"/>")) {
				LOGGER.log(Level.SEVERE, "Executing the Onboarding task was not successful (response was: " + response + ")");
			}
		} catch (IOException ioe) {
			LOGGER.log(Level.SEVERE, EXMSG_EXEC_UNSUCCESSFUL, ioe);
			return false;
		}
		return true;
	}
	
	private static String encode(String profileName) throws IOException {
		if (profileName == null) {
			return null;
		}
		return URLEncoder.encode(profileName, "UTF-8").replace("+", "%20");
	}
	
	/**
	 * Ensures that the Onboarding Task is installed on the Dynatrace Server
	 * referred to by <tt>srvConfg</tt> and on demand installs it.
	 * 
	 * @param srvConf
	 * @return
	 */
	public static boolean ensure(ServerConfig srvConf) {
		OnBoardingTaskVerifier verifier = new OnBoardingTaskVerifier(srvConf);
		return verifier.install();
	}
	
}
