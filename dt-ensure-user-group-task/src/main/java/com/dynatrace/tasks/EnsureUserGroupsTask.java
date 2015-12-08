package com.dynatrace.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.diagnostics.pdk.Status;
import com.dynatrace.diagnostics.pdk.Status.StatusCode;
import com.dynatrace.diagnostics.pdk.Task;
import com.dynatrace.diagnostics.pdk.TaskEnvironment;
import com.dynatrace.diagnostics.sdk.security.DashboardPermissionMapping;
import com.dynatrace.diagnostics.sdk.security.DashboardPermissionMapping.DashboardPermission;
import com.dynatrace.diagnostics.server.security.PermissionManager;
import com.dynatrace.diagnostics.server.shared.security.BaseRole;
import com.dynatrace.diagnostics.server.shared.security.UserGroup;
import com.dynatrace.diagnostics.server.shared.security.UserPermissionConfig;
import com.dynatrace.diagnostics.server.shared.security.UserRoleSystemMapping;
import com.dynatrace.tasks.utils.XMLUtil;
import com.dynatrace.tasks.xml.XmlDashboardAssignment;
import com.dynatrace.tasks.xml.XmlDashboardPermission;
import com.dynatrace.tasks.xml.XmlOnboarding;
import com.dynatrace.tasks.xml.XmlSystemRole;
import com.dynatrace.tasks.xml.XmlUserGroup;


public class EnsureUserGroupsTask implements Task {

	private static final Logger LOGGER =
			Logger.getLogger(EnsureUserGroupsTask.class.getName());
	
	private static final String FLDNAME_CONFIG =
			"com.dynatrace.tasks.ensure.user.groups";

	@Override
	public Status setup(TaskEnvironment env) throws Exception {
		return new Status(StatusCode.Success);
	}

	@Override
	public Status execute(TaskEnvironment env) throws Exception {
		
		
		File fldServerConf = new File("conf");
		if (!fldServerConf.exists()) {
			return new Status(
				StatusCode.ErrorInfrastructure,
				"The folder DT_HOME/server/conf (identified by '" + fldServerConf.getAbsolutePath() + "' does not exist"
			);
		}
		if (!fldServerConf.isDirectory()) {
			return new Status(
				StatusCode.ErrorInfrastructure,
				"The folder DT_HOME/server/conf (identified by '" + fldServerConf.getAbsolutePath() + "' is not a directory"
			);
		}
		File workFolder = new File(fldServerConf, FLDNAME_CONFIG);
		if (!workFolder.exists()) {
			return new Status(
				StatusCode.ErrorInfrastructure,
				"The folder DT_HOME/server/conf/" + FLDNAME_CONFIG + " (identified by '" + workFolder.getAbsolutePath() + "' does not exist"
			);
		}
		if (!workFolder.isDirectory()) {
			return new Status(
				StatusCode.ErrorInfrastructure,
				"The folder DT_HOME/server/conf" + FLDNAME_CONFIG + " (identified by '" + workFolder.getAbsolutePath() + "' is not a directory"
			);
		}
		ErrorMessage errorMessage = new ErrorMessage();
		File[] configFiles = workFolder.listFiles();
		for (File configFile : configFiles) {
			try {
				Status status = handleConfigFile(configFile);
				if (status.getStatusCode() != Status.StatusCode.Success) {
					errorMessage.println(status.getMessage());
				}
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "Unable to handle config file " + configFile.getAbsolutePath());
				errorMessage.println("Unable to handle config file " + configFile.getAbsolutePath());
			} finally {
				LOGGER.log(Level.INFO, "Handled config file '" + configFile.getName() + "' - deleting it");
				if (!configFile.delete()) {
					LOGGER.log(Level.WARNING, "Unable to delete config file " + configFile.getAbsolutePath());
				}
			}
		}
		if (!errorMessage.isEmpty()) {
			return new Status(Status.StatusCode.PartialSuccess, errorMessage.toString());
		}
		return new Status(Status.StatusCode.Success);
	}
	
	private void handleSystemRole(UserPermissionConfig upc, UserGroup userGroup, XmlSystemRole xmlSystemRole) {
		Objects.requireNonNull(upc);
		Objects.requireNonNull(userGroup);
		if (xmlSystemRole == null) {
			return;
		}
		
		String systemProfileName = xmlSystemRole.getSystem();
		String systemProfileRole = xmlSystemRole.getRole();
		
		
		UserRoleSystemMapping userRoleSystemMapping = null;
		List<UserRoleSystemMapping> systemProfileRoles = userGroup.getSystemProfileRoles();
		if (systemProfileRoles != null) {
			for (UserRoleSystemMapping mapping : systemProfileRoles) {
				if (mapping == null) {
					continue;
				}
				if (systemProfileName.equals(mapping.getSystemName())) {
					userRoleSystemMapping = mapping;
					break;
				}
			}
		}
		boolean isNew = (userRoleSystemMapping == null);
		if (isNew) {
			userRoleSystemMapping =	new UserRoleSystemMapping();
		}
		
		userRoleSystemMapping.setSystemName(systemProfileName);
		BaseRole systemRole = upc.getRole(systemProfileRole);
		if (systemRole == null) {
			LOGGER.log(Level.WARNING, "No Role named '" + systemProfileRole + "' exists - using Guest Role");
			systemRole = upc.getGuestRole();
		}
		userRoleSystemMapping.setRole(systemRole);
		if (isNew) {
			userGroup.addSystemProfileRole(userRoleSystemMapping);
		}
	}
	
	private void handleDashboardPermission(
		UserGroup userGroup,
		XmlDashboardPermission xmlDashboardPermission
	) {
		Objects.requireNonNull(userGroup);
		if (xmlDashboardPermission == null) {
			return;
		}
		String dashboardName = xmlDashboardPermission.getDashboard();
		
		boolean isNew = false;
		DashboardPermissionMapping mapping = fetchDashboardPermissionMapping(
				userGroup,
				dashboardName
		);
		if (mapping == null) {
			mapping = new DashboardPermissionMapping();
			isNew = true;
		}
		
		com.dynatrace.tasks.xml.DashboardPermission permission =
				xmlDashboardPermission.getPermission();
		mapping.setDashboardName(dashboardName);
		mapping.setAutoOpen(true);
		mapping.setPermission(DashboardPermission.valueOf(permission.name()));
		if (isNew) {
			userGroup.addDashboardPermission(mapping);
		}
	}
	
	private DashboardPermissionMapping fetchDashboardPermissionMapping(UserGroup userGroup, String dashboardName) {
		if (userGroup == null) {
			return null;
		}
		if (dashboardName == null) {
			return null;
		}
		List<DashboardPermissionMapping> dashboardPermissions = userGroup.getDashboardPermissions();
		if (dashboardPermissions != null) {
			for (DashboardPermissionMapping mapping : dashboardPermissions) {
				if (mapping == null) {
					continue;
				}
				String mappingDashboardName = mapping.getDashboardName();
				if (dashboardName.equals(mappingDashboardName)) {
					return mapping;
				}
			}
		}
		return null;
	}
	
	private void handleUserGroup(XmlUserGroup xmlUserGroup) {
		if (xmlUserGroup == null) {
			return;
		}
		boolean ldap = xmlUserGroup.isLdap();
		boolean editable = xmlUserGroup.isEditable();
		String description = xmlUserGroup.getDescription();
		String managementrole = xmlUserGroup.getManagementrole();
		String name = xmlUserGroup.getName();
		
		PermissionManager mgr = PermissionManager.getInstance();
		UserPermissionConfig upc = mgr.getUserPermissionConfig();
		
		boolean isUserGroupNew = true;
		UserGroup userGroup = upc.getUserGroup(name);
		if (userGroup != null) {
			isUserGroupNew = false;
		} else {
			userGroup = new UserGroup();
		}
		userGroup.setLdapGroup(ldap);
		userGroup.setEditable(editable);
		userGroup.setDescription(description);
		userGroup.setName(name);
		BaseRole managementRole = upc.getRole(managementrole);
		if (managementRole == null) {
			LOGGER.log(Level.WARNING, "No Role named '" + managementrole + "' exists - using guest role for management role");
			managementRole = upc.getGuestRole();
		}
		userGroup.setManagementRole(managementRole);
		
		Collection<XmlSystemRole> systemRoles = xmlUserGroup.getSystemRoles();
		if (systemRoles != null) {
			for (XmlSystemRole xmlSystemRole : systemRoles) {
				handleSystemRole(upc, userGroup, xmlSystemRole);
			}
		}
		
		Collection<XmlDashboardPermission> dashboardPermissions = xmlUserGroup.getDashboardPermissions();
		if (dashboardPermissions != null) {
			for (XmlDashboardPermission xmlDashboardPermission : dashboardPermissions) {
				handleDashboardPermission(userGroup, xmlDashboardPermission);
			}
		}
		
		if (isUserGroupNew) {
			upc.addUserGroup(userGroup);
		}
		
		
		try {
			mgr.storePermissionConfig();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to store Permission Config", e);
		}		
		
	}
	
	private void handleDashboardAssignment(XmlDashboardAssignment xmlDashboardAssignment) {
		if (xmlDashboardAssignment == null) {
			return;
		}
		String dashboard = xmlDashboardAssignment.getDashboard();
		String profile = xmlDashboardAssignment.getProfile();
		DashboardHelper.assign(dashboard, profile);
	}
	
	private Status handleConfigFile(File configFile) throws FileNotFoundException, IOException {
		if (!configFile.isFile()) {
			return new Status(
				StatusCode.ErrorInternalConfigurationProblem,
				"Configuration File '" + configFile.getAbsolutePath() + "' is not a file"
			);
		}
		
		XmlOnboarding onboarding = null;
		try (InputStream in = new FileInputStream(configFile)) {
			onboarding = XMLUtil.deserialize(in, XmlOnboarding.class);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to parse config file " + configFile.getAbsolutePath());
			return new Status(
				StatusCode.ErrorInternalConfigurationProblem,
				"Unable to parse config file " + configFile.getAbsolutePath()
			);
		}

		Collection<XmlUserGroup> usergroups = onboarding.getUsergroups();
		if (usergroups != null) {
			for (XmlUserGroup xmlUserGroup : usergroups) {
				handleUserGroup(xmlUserGroup);
			}
		}
		Collection<XmlDashboardAssignment> assignments = onboarding.getAssignments();
		if (assignments != null) {
			for (XmlDashboardAssignment xmlDashboardAssignment : assignments) {
				handleDashboardAssignment(xmlDashboardAssignment);
			}
		}

		return new Status(Status.StatusCode.Success);
	}

	@Override
	public void teardown(TaskEnvironment env) throws Exception {
		// nothing do do here
	}
}
