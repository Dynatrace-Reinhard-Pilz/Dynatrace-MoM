package com.dynatrace.onboarding.fastpacks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.fastpacks.FastPackInstallStatus;
import com.dynatrace.fastpacks.FastPackUpload;
import com.dynatrace.fastpacks.FastpackBuilder;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.permissions.PermissionDeniedException;
import com.dynatrace.onboarding.config.Config;
import com.dynatrace.onboarding.config.Debug;
import com.dynatrace.onboarding.dashboards.Dashboard;
import com.dynatrace.onboarding.dashboards.DashboardTemplate;
import com.dynatrace.onboarding.profiles.Profile;
import com.dynatrace.onboarding.profiles.ProfileTemplate;
import com.dynatrace.onboarding.serverconfig.ServerProperties;
import com.dynatrace.onboarding.usergroups.XmlOnboarding;
import com.dynatrace.onboarding.variables.UnresolvedVariableException;
import com.dynatrace.onboarding.variables.Variables;
import com.dynatrace.utils.DefaultExecutionContext;
import com.dynatrace.xml.XMLUtil;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class FastPackHelper {
	
	private static final Logger LOGGER =
			Logger.getLogger(FastPackHelper.class.getName());

	/**
	 * Uploads the Fast Pack defined within the given {@link FastpackBuilder}
	 * to the dynaTrace Server
	 * 
	 * @param builder the {@link FastpackBuilder} containing the meta info
	 * 		about the resources to upload to the dynaTrace Server
	 * @param serverConfig info about how to connect to the dynaTrace Server
	 * 
	 * @return {@code true} if the Fast Pack has been uploaded successfully,
	 * 		{@code false} otherwise
	 */
	public static boolean createAndUploadFastPack(FastpackBuilder builder, ServerConfig serverConfig) {
		LOGGER.log(Level.INFO, "Creating and uploading Fast Pack");
		final File dtpFile = new File(Config.temp(), UUID.randomUUID() + ".dtp");
		if (!Debug.DEBUG) {
			dtpFile.deleteOnExit();
		}
		
		try (OutputStream out = new FileOutputStream(dtpFile)) {
			builder.build(out);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unable to build fast pack for dashboard", e);
			return false;
		}
		
		DefaultExecutionContext ctx = new DefaultExecutionContext();
		FastPackUpload upload = new FastPackUpload(ctx, serverConfig) {
			@Override
			protected InputStream openStream() throws IOException {
				return dtpFile.toURI().toURL().openStream();
			}
			
			@Override
			public void onPermissionDenied(PermissionDeniedException exception) {
				if (exception == null) {
					return;
				}
				String permission = exception.getPermission();
				if (permission != null) {
					LOGGER.log(Level.SEVERE, "Missing Permission: " + permission);
				}
			}
			
			@Override
			public FastPackInstallStatus handlePermissionDeniedException(PermissionDeniedException e) {
				String permission = e.getPermission();
				if (!"Administrative Permission".equals(permission)) {
					return super.handlePermissionDeniedException(e);
				}
				ServerProperties serverProperties = ServerProperties.load(Config.serverConfig(), false);
				if (serverProperties.isPermissionTaskInstalled) {
					return new FastPackInstallStatus();
				}
				return null;
			}
		};
		if (!upload.execute()) {
			LOGGER.log(Level.SEVERE, "Unable to deploy Fast Pack");
			return false;
		}
		return true;
	}
	
	/**
	 * Appends the {@code .dashboard.xml} based on the template referenced
	 * via System Property to the given {@link FastpackBuilder}.
	 * 
	 * @param fpb the {@link FastpackBuilder} to be configured to contain the
	 * 		Dashboard to create from the template
	 * 
	 * @param profile the System Profile the Dashboard to create should be
	 * 		contain as its source
	 * 
	 * @return the {@link Dashboard} that has been created and appended to the
	 * 		given {@link FastpackBuilder} or {@code null} if an error
	 * 		occurred or not all variables have been defined
	 */
	public static Dashboard[] appendDashboards(FastpackBuilder fpb, Profile profile) {
		Variables variables = new Variables(profile.getName());
		Collection<Dashboard> dashboards = new ArrayList<>();
		
		String[] dashboardKeys = Config.dashboardKeys();
		for (String dashboardKey : dashboardKeys) {
			String dashboardName = Config.dashboardTemplate(dashboardKey);
			DashboardTemplate dashboardTemplateFile = Config.dashboardTemplates(dashboardName);
			if (dashboardTemplateFile == null) {
				LOGGER.log(Level.SEVERE, "No Dashboard Template found with name '" + dashboardName + "'");
				for (String curDashBoardName : Config.dashboardTemplates().keySet()) {
					LOGGER.log(Level.SEVERE, curDashBoardName);
				}
				return null;
			}
			DashboardTemplate dashboardTemplate = null;
			try {
				dashboardTemplate = new DashboardTemplate(dashboardTemplateFile.getSource(), dashboardKey);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Invalid Dashboard Template " + dashboardTemplateFile.getSource().getName(), e);
				return null;
			}
			Dashboard dashboard = null;
			try {
				dashboard = dashboardTemplate.resolve(variables);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Unable to resolve dashboard xml file " + dashboardTemplate.getSource().getPath(), e);
				return null;
			} catch (UnresolvedVariableException e) {
				LOGGER.log(Level.SEVERE, "The variable '" + e.getVariable() + "' (-Dvariable." + e.getVariable() + "=<value>) needs to be defined in order to deploy dashboard '" + dashboardTemplate.getSource().getName() + "'");
				return null;
			}
			
			fpb.addDashboard(dashboard.getFile());
			LOGGER.log(Level.INFO, "'" + dashboard.getName() + ".dashboard.xml' has been appended to Fast Pack");
			dashboards.add(dashboard);
		}
		return dashboards.toArray(new Dashboard[dashboards.size()]);
	}
	
	/**
	 * Appends a System Profile based on a System Profile Template (identified
	 * by configuration System Property) to the given {@link FastpackBuilder}
	 * 
	 * @param fpb the {@link FastpackBuilder} to be configured to contain the
	 * 		System Profile to create from the template
	 * 
	 * @return the {@link Profile} that has been created and appended to the
	 * 		given {@link FastpackBuilder} or {@code null} if an error
	 * 		occurred or not all variables have been defined
	 */
	public static Profile appendProfile(FastpackBuilder fpb, ServerProperties serverProperties) {
		Objects.requireNonNull(fpb);
		
		Profile targetProfile = null;
		String targetProfileName = Config.profile();
		if (targetProfileName != null) {
			targetProfile = serverProperties.profiles(targetProfileName);
			if (targetProfile == null) {
				LOGGER.severe("The System Profile '" + targetProfileName + "' does not exist");
				return null;
			}
		}
		
		ProfileTemplate profileTemplateFile = Config.profileTemplates(Config.profileTemplate());
		if (profileTemplateFile == null) {
			LOGGER.severe("No System Profile Template found with name '" + Config.profileTemplate() + "'");
			for (String cureProfileTemplateName : Config.profileTemplates().keySet()) {
				LOGGER.severe(cureProfileTemplateName);
			}
			return null;
		}
		
		ProfileTemplate profileTemplate = null;
		try {
			profileTemplate = new ProfileTemplate(profileTemplateFile.getSource());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Invalid System Profile Template " + profileTemplateFile.getSource().getName(), e);
			return null;
		}
		Variables variables = new Variables();
		
		Profile profile = null;
		try {
			profile = profileTemplate.resolve(variables, targetProfile);
			targetProfile = serverProperties.profiles(profile.getName());
			if (targetProfile != null) {
				LOGGER.log(Level.INFO, "The System Profile '" + targetProfile.getName() + "' already exists on the dynaTrace Server - adding an additional tier instead of replacing it");
				profile = profileTemplate.resolve(variables, targetProfile);
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unable to resolve profile template xml file " + profileTemplate.getSource().getName(), e);
			return null;
		} catch (UnresolvedVariableException e) {
			LOGGER.log(Level.SEVERE, "The variable '" + e.getVariable() + "' (-Dvariable." + e.getVariable() + "=<value>) needs to be defined in order to resolve the profile template '" + profileTemplate.getSource().getName() + "'");
			return null;
		}
		
		fpb.addProfile(profile.getFile());
		LOGGER.log(Level.INFO, "'" + profile.getName() + ".profile.xml' has been appended to Fast Pack");
		return profile;
	}
	
	public static enum NextStep {
		Proceed,
		Error,
		Nochange
	}
	
	public static NextStep appendOnboardingConfig(FastpackBuilder fpb, Dashboard[] dashboards, Profile profile) {
		Objects.requireNonNull(fpb);
		Objects.requireNonNull(dashboards);
		Objects.requireNonNull(profile);
		if (!Config.areUserGroupsConfigured()) {
			return NextStep.Nochange;
		}
		if ((dashboards == null) || (dashboards.length == 0)) {
			return NextStep.Nochange;
		}
		XmlOnboarding xmlOnboarding = XmlOnboarding.create(dashboards, profile.getName());
		if (xmlOnboarding == null) {
			return NextStep.Nochange;
		}
		if (xmlOnboarding != null) {
			File resource = new File(Config.temp(), "onboarding." + UUID.randomUUID().toString() + ".xml");
			if (!Debug.DEBUG) {
				resource.deleteOnExit();
			}
			try (OutputStream out = new FileOutputStream(resource)) {
				XMLUtil.serialize(xmlOnboarding, out);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Unable to create config file for Onboarding Task", e);
			}
			
			fpb.addResource(resource, "conf/com.dynatrace.tasks.ensure.user.groups");
			LOGGER.log(Level.INFO, "Configuration File for Onboarding Task has been added to Fast Pack");
		}
		return NextStep.Proceed;
	}
	
	
	
}
