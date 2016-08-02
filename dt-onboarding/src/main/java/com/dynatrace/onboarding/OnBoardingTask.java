package com.dynatrace.onboarding;

import java.io.ByteArrayOutputStream;
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
import com.dynatrace.onboarding.serverconfig.ServerProperties;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class OnBoardingTask {
	
	private static final Logger LOGGER =
			Logger.getLogger(OnBoardingTask.class.getName());

	public static boolean trigger(Profile profile) {
		LOGGER.log(Level.INFO, "Triggering Onboarding Task");
		Objects.requireNonNull(profile);
		String profileName = profile.getId();
		try {
			String encProfileName = URLEncoder.encode(profileName, "UTF-8").replace("+", "%20");
			String encTask = URLEncoder.encode("Automatic Onboarding Task", "UTF-8").replace("+", "%20");
			URL execTaskMethod = Config.serverConfig().createURL("/rest/management/profiles/" + encProfileName + "/tasks/" + encTask + "/run");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Http.client().request(execTaskMethod, Method.GET, Config.serverConfig().getCredentials(), baos);
			String response = new String(baos.toByteArray());
			if (!response.equals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><result value=\"true\"/>")) {
				LOGGER.log(Level.SEVERE, "Executing the Onboarding task was not successful (response was: " + response + ")");
			}
		} catch (Throwable t) {
			LOGGER.log(Level.SEVERE, "Executing the Onboarding task was not successful", t);
			return false;
		}
		return true;
	}
	
	public static boolean ensureInstalled(ServerProperties serverProperties, ServerConfig serverConfig) {
		OnBoardingTaskVerifier verifier = new OnBoardingTaskVerifier(serverConfig);
		return verifier.install();
	}
	
}
