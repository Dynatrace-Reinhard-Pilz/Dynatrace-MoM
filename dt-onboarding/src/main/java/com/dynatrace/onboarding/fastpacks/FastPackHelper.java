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
import com.dynatrace.onboarding.dashboards.LocalDashboard;
import com.dynatrace.onboarding.profiles.InvalidProfileNameException;
import com.dynatrace.onboarding.profiles.Profile;
import com.dynatrace.onboarding.profiles.ProfileTemplate;
import com.dynatrace.onboarding.serverconfig.ServerProperties;
import com.dynatrace.onboarding.usergroups.XmlOnboarding;
import com.dynatrace.onboarding.variables.DefaultVariables;
import com.dynatrace.utils.DefaultExecutionContext;
import com.dynatrace.utils.FileSource;
import com.dynatrace.variables.UnresolvedVariableException;
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
	public static boolean createAndUploadFastPack(
		FastpackBuilder builder,
		ServerConfig serverConfig,
		InstallationVerifier verifier
	) {
		LOGGER.log(Level.FINE, "Creating and uploading Fast Pack");
		final File dtpFile = new File(
			Config.temp(),
			UUID.randomUUID() + ".dtp"
		);
		if (!Debug.DEBUG) {
			dtpFile.deleteOnExit();
		} else {
			LOGGER.log(Level.INFO, "DTP File: " + dtpFile.getAbsolutePath());
		}
		
		try (OutputStream out = new FileOutputStream(dtpFile)) {
			builder.build(out);
		} catch (IOException e) {
			LOGGER.log(
				Level.SEVERE,
				"Unable to build fast pack for dashboard",
				e
			);
			return false;
		}
		return uploadFastPack(serverConfig, dtpFile, verifier);

	}
	
	public static boolean uploadFastPack(ServerConfig serverConfig, final File dtpFile, final InstallationVerifier verifier) {
		DefaultExecutionContext ctx = new DefaultExecutionContext();
		FastPackUpload upload = new FastPackUpload(ctx, serverConfig) {
			@Override
			protected InputStream openStream() throws IOException {
				return dtpFile.toURI().toURL().openStream();
			}
			
			@Override
			public void onPermissionDenied(PermissionDeniedException ex) {
				if (ex == null) {
					return;
				}
				String permission = ex.getPermission();
				if (permission != null) {
					LOGGER.log(
						Level.SEVERE,
						"Missing Permission: " + permission
					);
				}
			}
			
			@Override
			public FastPackInstallStatus handlePermissionDeniedException(
				PermissionDeniedException e
			) {
				String permission = e.getPermission();
				if (!"Administrative Permission".equals(permission)) {
					return super.handlePermissionDeniedException(e);
				}
				
				if (verifier.isInstalled()) {
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
	 * @return the {@link LocalDashboard} that has been created and appended to the
	 * 		given {@link FastpackBuilder} or {@code null} if an error
	 * 		occurred or not all variables have been defined
	 */
	public static LocalDashboard[] appendDashboards(
		FastpackBuilder fpb,
		Profile profile
	) {
		DefaultVariables variables = new DefaultVariables(profile.getId());
		Collection<Dashboard> dashboards = new ArrayList<>();
		
		String[] dbKeys = Config.dashboardKeys();
		for (String dbKey : dbKeys) {
			String dbName = Config.dashboardTemplate(dbKey);
			DashboardTemplate dbTplFile = Config.dashboardTemplates(dbName);
			if (dbTplFile == null) {
				LOGGER.log(
					Level.SEVERE,
					"No Dashboard Template found with name '" + dbName + "'"
				);
				for (String curDbName : Config.dashboardTemplates().keySet()) {
					LOGGER.log(Level.SEVERE, curDbName);
				}
				return null;
			}
			DashboardTemplate dbTpl = null;
			try {
				dbTpl = dbTplFile.localize();
			} catch (IOException ioe) {
				LOGGER.log(Level.SEVERE, "Unable to make dashboard available offline", ioe);
				return null;
			}
			Dashboard dashboard = null;
			try {
				dashboard = dbTpl.resolve(variables);
			} catch (IOException e) {
				LOGGER.log(
					Level.SEVERE,
					"Unable to resolve dashboard xml file " +
						dbTpl.getName(),
					e
				);
				return null;
			} catch (UnresolvedVariableException e) {
				LOGGER.log(
					Level.SEVERE,
					"The variable '" +
						e.getVariable() + "' (-Dvariable." +
						e.getVariable() +
						"=<value>) needs to be defined in order to deploy " +
						"dashboard '" +						
						dbTpl.getName() + "'"
				);
				return null;
			}
			
			fpb.addDashboard(dashboard);
			LOGGER.log(
				Level.INFO,
				"'" + dashboard.getName() +
				"' has been appended to Fast Pack"
			);
			dashboards.add(dashboard);
		}
		return dashboards.toArray(new LocalDashboard[dashboards.size()]);
	}
	
	/**
	 * Appends a System Profile based on a System Profile Template (identified
	 * by configuration System Property) to the given {@link FastpackBuilder}
	 * 
	 * @param fpb the {@link FastpackBuilder} to be configured to contain the
	 * 		System Profile to create from the template
	 * 
	 * @return the {@link ProfileFile} that has been created and appended to the
	 * 		given {@link FastpackBuilder} or {@code null} if an error
	 * 		occurred or not all variables have been defined
	 */
	public static Profile appendProfile(
		FastpackBuilder fpb,
		ServerProperties serverProps
	) {
		Objects.requireNonNull(fpb);
		
		Profile tgtProf = null;
		String tgtProfName = Config.profile();
		if (tgtProfName != null) {
			tgtProf = serverProps.profiles(tgtProfName);
			if (tgtProf == null) {
				LOGGER.severe(
					"The System Profile '" + tgtProfName + "' does not exist"
				);
				return null;
			}
		}
		
		ProfileTemplate profTplFile = Config.profileTemplates(
			Config.profileTemplate()
		);
		if (profTplFile == null) {
			LOGGER.severe(
				"No System Profile Template found with name '" +
					Config.profileTemplate() + "'"
			);
			for (String curProfTplName : Config.profileTemplates().keySet()) {
				LOGGER.severe(curProfTplName);
			}
			return null;
		}
		
		ProfileTemplate profTpl = null;
		try {
			profTpl = profTplFile.localize();
		} catch (IOException e) {
			LOGGER.log(
				Level.SEVERE,
				"Invalid System Profile Template " +
					profTplFile.getName(),
				e
			);
			return null;
		}
		DefaultVariables variables = new DefaultVariables();
		
		Profile profile = null;
		try {
			profile = profTpl.resolve(variables, tgtProf);
			tgtProf = serverProps.profiles(profile.getId());
			if (tgtProf != null) {
				LOGGER.log(
					Level.INFO,
					"The System Profile '" + tgtProf.getId() +
						"' already exists on the dynaTrace Server - adding an" +
						" additional tier instead of replacing it"
				);
				profile = profTpl.resolve(variables, tgtProf);
			}
		} catch (InvalidProfileNameException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			return null;
		} catch (IOException e) {
			LOGGER.log(
				Level.SEVERE,
				"Unable to resolve profile template xml file " +
					profTpl.getId(),
				e
			);
			return null;
		} catch (UnresolvedVariableException e) {
			LOGGER.log(
				Level.SEVERE,
				"The variable '" + e.getVariable() + "' (-Dvariable." +
					e.getVariable() + "=<value>) needs to be defined in order" +
					" to resolve the profile template '" +
					profTpl.getId() + "'"
			);
			return null;
		}
		
		fpb.addProfile(profile);
		LOGGER.log(
			Level.INFO,
			"'" + profile.getId() +
				".profile.xml' has been appended to Fast Pack"
		);
		return profile;
	}
	
	public static enum NextStep {
		Proceed,
		Error,
		Nochange
	}
	
	public static NextStep appendOnboardingConfig(
		FastpackBuilder fpb,
		LocalDashboard[] dashboards,
		Profile profile
	) {
		Objects.requireNonNull(fpb);
		Objects.requireNonNull(dashboards);
		Objects.requireNonNull(profile);
		if (!Config.areUserGroupsConfigured()) {
			return NextStep.Nochange;
		}
//		if ((dashboards == null) || (dashboards.length == 0)) {
//			return NextStep.Nochange;
//		}
		XmlOnboarding xmlOnboarding = XmlOnboarding.create(
			dashboards, profile.getId()
		);
		if (xmlOnboarding == null) {
			System.out.println("xmlOnboarding == null");
			return NextStep.Nochange;
		}
		if (xmlOnboarding != null) {
			File resource = new File(
				Config.temp(),
				"onboarding." + UUID.randomUUID().toString() + ".xml"
			);
			if (!Debug.DEBUG) {
				resource.deleteOnExit();
			}
			try (OutputStream out = new FileOutputStream(resource)) {
				XMLUtil.serialize(xmlOnboarding, out);
			} catch (IOException e) {
				LOGGER.log(
					Level.SEVERE,
					"Unable to create config file for Onboarding Task",
					e
				);
			}
			
			fpb.addResource(
				new FileSource(resource),
				"conf/com.dynatrace.tasks.ensure.user.groups"
			);
			LOGGER.log(
				Level.INFO,
				"Configuration File for Onboarding Task has been added " +
					"to Fast Pack"
			);
		}
		return NextStep.Proceed;
	}
	
}
