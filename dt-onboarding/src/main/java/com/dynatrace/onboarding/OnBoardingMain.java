package com.dynatrace.onboarding;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.fastpacks.FastpackBuilder;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.onboarding.config.Config;
import com.dynatrace.onboarding.dashboards.Dashboard;
import com.dynatrace.onboarding.fastpacks.FastPackHelper;
import com.dynatrace.onboarding.fastpacks.FastPackHelper.NextStep;
import com.dynatrace.onboarding.profiles.Profile;
import com.dynatrace.onboarding.serverconfig.ServerProperties;
import com.dynatrace.utils.Logging;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class OnBoardingMain {
	
	private static final Logger LOGGER =
			Logger.getLogger(OnBoardingMain.class.getName());
	
/*

	public static final String PROP_DB_PERMISSION = "config.user.group.dashboard.permission";



 */
	/**
	 * main method
	 * @param args no arguments are expected - everything configured via
	 * 		system properties
	 */
	public static void main(String[] args) {
		if ((args != null) && (args.length > 0)) {
			if ("usage".equals(args[0]) || "help".equals(args[0])) {
				System.out.println("java [-D<option>=<value> *] -jar dt-onboarding.jar [usage|help]");
				System.out.println();
				System.out.println("  Options for accessing the dynaTrace Server:");
				System.out.println("      -Dconfig.server.host=<host>[:<port>]");
				System.out.println("           (optional, default = localost:8021)");
				System.out.println("          The host and port the dynaTrace Server listening");
				System.out.println("           for WebService/REST requests.");
				System.out.println("      -Dconfig.server.user=<username>");
				System.out.println("           (optional, default = admin)");
				System.out.println("          The user name of a user configured on the dynaTrace Server.");
				System.out.println("           This user must have administrative permissions.");
				System.out.println("      -Dconfig.server.pass=<password>");
				System.out.println("           (optional, default = admin)");
				System.out.println("          The password in order to authenticate when accessing");
				System.out.println("           the dynaTrace Server WebServices.");
				System.out.println();
				System.out.println("  Options for chosing Templates for System Profiles and Dashboards");
				System.out.println("   to deploy to the dynaTrace Server:");
				System.out.println("      -Dconfig.templates.profile=<profilename>[.profile.xml]");
				System.out.println("           (mandatory)");
				System.out.println("          The name of a System Profile embedded within");
				System.out.println("           dt-onboarding.jar (/resources/profiles/*.profile.xml) or");
				System.out.println("           on the dynaTrace Server. If a System Profile template");
				System.out.println("           located on the dynaTrace Server has the same name as a");
				System.out.println("           System Profile Template embedded within dt-onboarding.jar");
				System.out.println("           the Template located on the dynaTrace Server will be used.");
				System.out.println("          Variable names within the name and configuration of this file");
				System.out.println("           will require several -Dvariable.<variablename>=value options to");
				System.out.println("           be present in order to get resolved properly.");
				System.out.println("           See the section about variables for detailed information.");
				System.out.println("          Based on this Template either a new System Profile will be");
				System.out.println("           created or its Agent Groups and Configurations will be added");
				System.out.println("           to an existing one (-Dconfig.profile).");
				System.out.println("      -Dconfig.profile=<profilename>");
				System.out.println("           (optional)");
				System.out.println("          The name of an existig System Profile on the dynaTrace Server,");
				System.out.println("           to which to add the Agent Groups and Configurations found");
				System.out.println("           within the given System Profile Template (-Dconfig.templates.profile).");
				System.out.println("          If this option is not present, a new System Profile based on");
				System.out.println("           the name of the System Profile Templates name will be created");
				System.out.println("           on the dynaTrace Server.");
				System.out.println("      -Dconfig.templates.dashboard=<dashboardname>[.dashboard.xml]");
				System.out.println("           (optional)");
				System.out.println("          The name of a Dashboard embedded within");
				System.out.println("           dt-onboarding.jar (/resources/dashboards/*.dashboard.xml) or");
				System.out.println("           on the dynaTrace Server. If a Dashboard template");
				System.out.println("           located on the dynaTrace Server has the same name as a");
				System.out.println("           Dashboard Template embedded within dt-onboarding.jar");
				System.out.println("           the Template located on the dynaTrace Server will be used.");
				System.out.println("          Variable names within the name and configuration of this file");
				System.out.println("           will require several -Dvariable.<variablename>=value options to");
				System.out.println("           be present in order to get resolved properly.");
				System.out.println("           See the section about variables for detailed information.");
				System.out.println("          Based on this Template either a new Dashboard will be created or");
				System.out.println("           an already existing Dashboard with the same name on the");
				System.out.println("           dynaTrace Server will be replaced.");
				System.out.println("          The data source of this Dashboard will be preconfigured with the");
				System.out.println("           System Profile that is either being created or modified on the");
				System.out.println("           dynaTrace Server (-Dconfig.templates.profile, -Dconfig.profile).");
				System.out.println("      -Dconfig.dashboards.<dashboardkey>.name=<dashboardname>[.dashboard.xml]");
				System.out.println("           (optional)");
				System.out.println("          The name of a Dashboard embedded within");
				System.out.println("           dt-onboarding.jar (/resources/dashboards/*.dashboard.xml).");
				System.out.println("          This option is only necessary, when two or more Dashboards should");
				System.out.println("           get deployed with different Permissions assigned to the User Group(s)");
				System.out.println("           to create (-Dconfig.user.group, -Dconfig.user.groups.default.name,");
				System.out.println("           -Dconfig.user.groups.<groupkey>.name)");
				System.out.println("          Variable names within the name and configuration of this file");
				System.out.println("           will require several -Dvariable.<variablename>=value options to");
				System.out.println("           be present in order to get resolved properly.");
				System.out.println();
				System.out.println("  Options introducing new User Groups to the dynaTrace Server:");
				System.out.println("      -Dconfig.user.group=<usergroupname>");
				System.out.println("      -Dconfig.user.groups.default.name=<usergroupname>");
				System.out.println("           (optional)");
				System.out.println("          The name of a User Group to add to the User Permission Config");
				System.out.println("           of the dynaTrace Server.");
				System.out.println("          The new User Group will be an LDAP group.");
				System.out.println("          The new User Group will get assigned the Guest User Role on the");
				System.out.println("           dynaTrace Server unless otherwise specified");
				System.out.println("           (-Dconfig.user.group.management.role,");
				System.out.println("           -Dconfig.user.groups.<groupkey>.management.role).");
				System.out.println("          The new User Group will get assigned the Administrator User Role");
				System.out.println("           for the new System Profile unless otherwise specified");
				System.out.println("           (-Dconfig.user.group.profile.role,");
				System.out.println("           -Dconfig.user.groups.<groupkey>.profile.role).");
				System.out.println("          The new User Group will have 'Read' access to any new Dashboards");
				System.out.println("           unless otherwise specified (-Dconfig.user.group.profile.role,");
				System.out.println("           -Dconfig.user.groups.<groupkey>.profile.role).");
				System.out.println("      -Dconfig.user.groups.<groupkey>.name=<usergroupname>");
				System.out.println("          (optional)");
				System.out.println("          The name of a User Group to add to the User Permission Config");
				System.out.println("           of the dynaTrace Server.");
				System.out.println("          This option is only necessary to be used if two or more");
				System.out.println("           User Groups need to get added at the same time.");
				System.out.println("      -Dconfig.user.group.management.role=<rolename>");
				System.out.println("      -Dconfig.user.groups.default.management.role=<rolename>");
				System.out.println("          (optional, default = Guest)");
				System.out.println("          The Management Role to assign to the new User Group");
				System.out.println("           specified by -Dconfig.user.group");
				System.out.println("          The Management Role to assign to any new User Group");
				System.out.println("           specified by -Dconfig.user.groups.<groupkey>.name unless");
				System.out.println("           explicitely defined by -Dconfig.user.groups.<groupkey>.management.role");
				System.out.println("      -Dconfig.user.group.profile.role=<rolename>");
				System.out.println("      -Dconfig.user.groups.default.profile.role=<rolename>");
				System.out.println("          (optional, default = Administrator)");
				System.out.println("          The System Profile Role to assign to the new User Group");
				System.out.println("           specified by -Dconfig.user.group");
				System.out.println("          The System Profile Role to assign to any new User Group");
				System.out.println("           specified by -Dconfig.user.groups.<groupkey>.name unless explicitely");
				System.out.println("           defined by -Dconfig.user.groups.<groupkey>.profile.role");
				System.out.println("      -Dconfig.user.groups.<groupkey>.profile.role=<rolename>");
				System.out.println("          (optional, default = Administrator)");
				System.out.println("          The System Profile Role to assign a new User Group specified");
				System.out.println("           by -Dconfig.user.groups.<groupkey>.name");
				System.out.println("          This option is only required in case different User Groups");
				System.out.println("           need to get a different Role for the System Profile to be");
				System.out.println("           created or modified");
				System.out.println("      -Dconfig.user.group.dashboard.permission=<Read|Read_Write>");
				System.out.println("      -Dconfig.user.groups.default.dashboard.permission=<Read|Read_Write>");
				System.out.println("          (optional, default = Read)");
				System.out.println("          The Dashboard Permissions to assign to the new User Group");
				System.out.println("           specified by -Dconfig.user.group unless explicitely defined");
				System.out.println("           by -Dconfig.user.group.dashboards.<dashboardkey>.permission");
				System.out.println("           or -Dconfig.user.groups.<groupkey>.dashboards.<dashboardkey>.permission");
				System.out.println("          The Dashboard Permissions Role to assign to any new User Group");
				System.out.println("           specified by -Dconfig.user.groups.<groupkey>.name unless explicitely");
				System.out.println("           defined by -Dconfig.user.groups.<groupkey>.dashboard.permission");
				System.out.println("      -Dconfig.user.groups.<groupkey>.dashboards.<dashboardkey>.permission=<Read|Read_Write>");
				System.out.println("          (optional, default = Read)");
				System.out.println("          The Dashboard Permissions for a Dashboard specified by");
				System.out.println("           -Dconfig.dashboards.<dashboardkey>.name to assign to a specific");
				System.out.println("           User Group specified by -Dconfig.user.groups.<groupkey>.name");
				System.out.println("          This option is only necessary if different User Groups");
				System.out.println("           need to have different Permissions to various Dashboards");
				System.out.println();
				System.out.println("  Variables within System Profile Templates and Dashboard Templates:");
				System.out.println("      Both, the .profile.xml and .dashboard.xml files used as templates");
				System.out.println("       for creating System Profile or Dashboards are allowed to");
				System.out.println("        contain \"variables\" within their file names and their content.");
				System.out.println("      An valid example for a variables within the name of a Dashboard Template is:");
				System.out.println("        {@environment} {@application} Triage.dashboard.xml");
				System.out.println("      In order to resolve a proper name for the Dashboard to create based on this Template");
				System.out.println("        there are two additional options required:");
				System.out.println("        -Dvariable.environment=<value>");
				System.out.println("        and");
				System.out.println("        -Dvariable.environment=<value>");
				System.out.println("      The values for these options may be chosen freely and will be shared both,");
				System.out.println("        for resolving the eventual name(s) and values within the contents of");
				System.out.println("        System Profiles and Dashboards.");
				System.out.println("      Depending on how many variables are encoded within the System Profile Templates");
				System.out.println("        and Dashboard Templates specified by -Dconfig.template.dashboard,");
				System.out.println("        -Dconfig.dashboards.<dashboardkey>.name and");
				System.out.println("        -Dconfig.template.profile the number of -Dvariable.<variablename>=value");
				System.out.println("        options can only get evaluated during runtime and therefore my vary.");
				return;
			}
		}
		Logging.init();
		OnBoardingMain main = new OnBoardingMain();
		try {
			if (!main.execute()) {
				System.exit(-1);
			} else {
				System.exit(0);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Unrecoverable error", e);
			System.exit(-1);
		}
	}

	private boolean execute() {
		// exiting early if there are configuration parameters missing
		// exiting early if the dynaTrace Server cannot be reached
		// exiting early if user credentials are not valid
		if (!Config.isValid()) {
			return false;
		}
		
		// keeping connection information locally
		ServerConfig serverConfig = Config.serverConfig();
		
		// querying for information from dynaTrace Server
		// A: is the onboarding task plugin already installed?
		// B: downloading System Profiles
		// C: downloading Dashboards
		ServerProperties serverProperties = ServerProperties.load(serverConfig);
		if (serverProperties == null) {
			return false;
		}
		
		// in case there are System Profile Templates located on the
		// dynaTrace Server, they should be available for deployment
		// Templates on the dynaTrace Server which are matching the name of
		// and embedded Template will supersede the embedded ones.
		Config.resources().publishProfiles(serverProperties.profiles());
		// in case there are Dashboard Templates located on the
		// dynaTrace Server, they should be available for deployment
		// Templates on the dynaTrace Server which are matching the name of
		// and embedded Template will supersede the embedded ones.
		Config.resources().publishDashboards(serverProperties.dashboards());
		
		// if the onboarding task plugin is not installed, upload it
		if (!OnBoardingTask.ensureInstalled(serverProperties, serverConfig)) {
			return false;
		}
		
		// There will be a Fast Pack with contents
		// A: System Profile (preconfigured with Onboarding Task)
		// B: Dashboard
		// C: Configuration File for the Onboarding Task
		String fastPackId = UUID.randomUUID().toString();
		FastpackBuilder fpb = new FastpackBuilder(fastPackId, fastPackId);
		
		// appending System Profile to the Fast Pack
		Profile profile = FastPackHelper.appendProfile(fpb, serverProperties);
		if (profile == null) {
			return false;
		}

		// appending Dashboard to the Fast Pack
		Dashboard[] dashboards = FastPackHelper.appendDashboards(fpb, profile);
		if (dashboards == null) {
			return false;
		}
		
		NextStep nextStep = NextStep.Nochange;
		
		// appending configuration file for Onboarding Task to the Fast Pack
		nextStep = FastPackHelper.appendOnboardingConfig(fpb, dashboards, profile);
		
		// finally uploading Fast Pack
		if (!FastPackHelper.createAndUploadFastPack(fpb, serverConfig)) {
			return false;
		}
		
		if (nextStep == NextStep.Proceed) {
			// triggering the onboarding task within the previously uploaded
			// System Profile
			return OnBoardingTask.trigger(profile);
		} else {
			LOGGER.info("There is no need to trigger an onboarding task - no user groups configured");
		}
		return true;
	}

}
