# Dynatrace Onboarding
dt-onboarding is a command line tool for creating System Profiles based on Templates.
The latest stable release can be downloaded [here](https://github.com/Dynatrace-Reinhard-Pilz/Dynatrace-MoM/blob/master/dt-onboarding/dt-onboarding.jar?raw=true) 

## System Profile Templates

The utility looks for System Profile Templates at two different locations:
* Embedded within ```dt-onboarding.jar``` in a folder called ```/resources/profiles```
  - If you would like to change that set of templates, simply use WinZip, WinRar or 7-zip to add or remove ```*.profile.xml``` files in here.
  - If a System Profile Template embedded within ```dt-onboarding.jar``` has been created by a dynaTrace Server newer  than the dynaTrace Server you want to access using this utility, the Template will get ignored.
  - A dynaTrace Server is considered newer than another one, if its Major or Minor version is higher
* On the dynaTrace Server among the existing System Profiles
  - A System Profile is considered to be a System Profile Template by this utility, if its name contains one or more expressions like ```{@variablename}```.
  - In case there exists a System Profile Template both, embedded within ```dt-onboarding.jar``` and on the dynaTrace Server, the Template located on the dynaTrace Server will be preferred by the utility.
  - It is recommended to keep System Profile Templates disabled on the dynaTrace Server to avoid Agents getting accidentally instrumented by these Templates

A System Profile Template is an ordinary System Profile and can also be modified using a dynaTrace Client. The only difference to actual System Profiles is, that within the name of the ```.profile.xml``` and optionally within the names of the Agent Groups and Agent Mappings defined within you can use ```{@variablename}``` expressions. For the ```dt-onboarding``` utility these expressions are variables that need to be defined using the options ```–Dvariable.<variablename>=<value>``` in order to resolve a proper name.
After the name for the System Profile to create based on the System Profile Template the onboarding tool either creates a new System Profile or adds an additional tier to an already existing System Profile with the same name on the dynaTrace Server.

## Dashboard Templates

The utility looks for Dashboard Templates at two different locations:
* Embedded within ```dt-onboarding.jar``` in a folder called ```/resources/dashboards```
  - If you would like to change the set of templates, simply use WinZip, WinRar or 7-zip to add or remove ```*.dashboard.xml``` files in here
  - If a System Profile Template embedded within ```dt-onboarding.jar``` has been created by a dynaTrace Server newer  than the dynaTrace Server you want to access using this utility, the Template will get ignored.
  - A dynaTrace Server is considered newer than another one, if its Major or Minor version is higher
* On the dynaTrace Server among the existing Dashboards
  - A Dashboard is considered to be a Dashboard Template by this utility, if its name contains one or more expressions like ```{@variablename}```.
  - In case there exists a Dashboard Template both, embedded within ```dt-onboarding.jar``` and on the dynaTrace Server, the Template located on the dynaTrace Server will be preferred by the utility.
A Dashboard Template is an ordinary Dashboard and can therefore be modified using a dynaTrace Client. The only difference to actual Dashboards is, that within the name of the ```.dashboard.xml``` and optionally within its configuration you can user ```{@variablename}``` expressions. For the ```dt-onboarding``` utility these expressions are variables that need to be defined using the options ```–Dvariable.<variablename>=<value>``` in order to resolve a proper name.

The Dashboard the utility eventually deploys to the dynaTrace Server will automatically have the System Profile deployed alongside with the Dashboard defined as its source.

## Usage and Examples

By typing in ```java –jar dt-onboarding.jar usage``` you will get a detailed list of configuration options available, which is listed at the end of this message.
Configuration options are all implemented as System Properties, so make sure you specify them before the ```–jar``` argument.

In order to perform all required operations the utility is connecting to the [REST API](https://community.dynatrace.com/community/display/DOCDT62/REST+Interfaces) of the dynaTrace Server. Therefore you are required to specify user credentials of a user configured on the dynaTrace Server that requires the following permissions
* Access the WebService Interface
* Create Support Archives
* Create System Profiles
* Create Dashboards
* Execute Tasks

The samples down there are definitely not all the combinations that are possible, but they should at least give you a couple of hints.

### Authentication
How do I specify the dynaTrace Server and user credentials the utility should deploy System Profiles and Dashboards to?
* ```-Dconfig.server.host=<hostname>``` connects the given host name trying out port ```8020``` and ```8021```.
* ```-Dconfig.server.host=<hostname>:<port>``` connects to the given host name using the given port
* ```-Dconfig.server.user=<username>``` and ```–Dconfig.server.pass=<password>``` specify the user credentials
* By default, if no options are given, the utility will connect to ```localhost:8021``` using ```admin/admin``` for authentication
* There is no need if the protocol to be used should be ```HTTP``` or ```HTTPS``` – the utility figures that out on its own.

### Example - Creating a System Profile
```java –Dconfig.templates.profile=”{@application} {@environment} JBoss” –jar dt-onboarding.jar```
or alternatively
```java –Dconfig. templates.profile=”{@application} {@environment} JBoss.profile.xml” –jar dt-onboarding.jar```
searches for a file named ```{@application} {@environment} JBoss.profile.xml``` within ```dt-onboarding.jar``` or on the dynaTrace Server and creates a new System Profile based on this Template on the dynaTrace Server.
* Make sure you wrap quotes ```“``` around the template name in case it contains white spaces.
* The name of the System Profile that is going to be deployed on the dynaTrace Server depends on the values you have defined for the variables application and environment.
* In order to define the variable application the configuration option ```–Dvariable.application=<value>``` needs to be specified.
* In order to define the variable environment the configuration option ```–Dvariable.environment=<value>``` needs to be specified.
* If either one of these variables are not defined via configuration option, the utility will log out an error message stating which configuration options are missing.

### Example - Creating a System Profile and a Dashboard
```java –Dconfig.templates.profile=”{@application} {@environment} JBoss” –Dconfig.templates.dashboard=“{@environment}_{@application}_JBoss Monitoring”  –jar dt-onboarding.jar```
or alternatively
```java –Dconfig.templates.profile=”{@application} {@environment} JBoss.profile.xml” –Dconfig.templates.dashboard=“{@environment}_{@application}_JBoss Monitoring.dashboard.xml”  –jar dt-onboarding.jar```
deploys a Dashboard alongside with the System Profile.
* The Template for this Dashboard is a file named ```{@environment}_{@application}_JBoss Monitoring.dashboard.xml``` which either needs to be embedded within ```dt-onboarding.jar``` or can be found among the Dashboards on the dynaTrace Server.
* Make sure you wrap quotes ```“``` around the template name in case it contains white spaces.
* The name of the Dashboard that is going to be deployed on the dynaTrace Server depends on the values you have defined for the variables application and environment.
* In order to define the variable application the configuration option ```–Dvariable.application=<value>``` needs to be specified.
* In order to define the variable environment the configuration option ```–Dvariable.environment=<value>``` needs to be specified.
* The variables within the Dashboard Template name do not necessarily have to be the same ones than the ones within the System Profile Template name.
* If either one of these variables are not defined via configuration option, the utility will log out an error message stating which configuration options are missing.
* The resulting Dashboard will have the System Profile deployed alongside with it assigned as its source.

### Example - Creating a System Profile and two Dashboard
```java –Dconfig.templates.profile=”{@application} {@environment} JBoss” –Dconfig.templates.dashboards.DASHBOARD1.name=“{@environment}_{@application}_JBoss Monitoring” –Dconfig.templates.dashboards.DASHBOARD2.name=”{@environment}_{@application}_TRIAGE_DASHBOARD” –jar dt-onboarding.jar```
will deploy two Dashboards on the dynaTrace Server
* The Dashboard Template files ```{@environment}_{@application}_JBoss Monitoring.dashboard.xml``` and ```{@environment}_{@application}_TRIAGE_DASHBOARD.dashboard.xml``` to either be located on the dynaTrace Server or embedded within ```dt-onboarding.jar```.
* Both resulting Dashboards will have the System Profile deployed alongside with them assigned as their source.
* The identifiers ```DASBHOARD1``` and ```DASHBOARD2``` can be chosen freely, but should not contain any white spaces. They may later on be used to refer to these dashboards in additional configuration options for granting permissions to specific user groups.

### Example - Creating a System Profile grant access to an LDAP Group
```java –Dconfig.templates.profile=”{@application} {@environment} JBoss” –Dconfig.user.group.name=GROUPNAME –jar dt-onboarding.jar```
will deploy a System Profile using the given Template on the dynaTrace Server and grant the the User Group ```GROUPNAME``` the Role Administrator for this System Profile.
* If the User Group does not exist yet, it will create a new LDAP group
  - The new User Group will have the ```Guest Role``` as ```Management Role``` on the dynaTrace Server

### Example - Creating a System Profile grant access to an LDAP Group (2)
```java –Dconfig.templates.profile=”{@application} {@environment} JBoss” –Dconfig.user.group.name=GROUPNAME –Dconfig.user.group.profile.role=Guest –jar dt-onboarding.jar```
will deploy a System Profile using the given Template on the dynaTrace Server and grant the the User Group ```GROUPNAME``` the Role ```Guest``` for this System Profile.
* If the User Group does not exist yet, it will create a new LDAP group
  - The new User Group will have the ```Guest Role``` as ```Management Role``` on the dynaTrace Server

### Example - Creating a System Profile grant access to multiple LDAP Groups
```java –Dconfig.templates.profile=”{@application} {@environment} JBoss” –Dconfig.user.groups.GRP2.name=GROUPNAME1 –Dconfig.user.groups.GRP1.name=GROUPNAME2 –Dconfig.user.group. GRP1.profile.role=Guest –Dconfig.user.group.GRP2.profile.role=Administrator –jar dt-onboarding.jar```
will deploy a System Profile using the given Template on the dynaTrace Server and grant the the User Group ```GROUPNAME1``` the Role Guest for this System Profile and the User Group ```GROUPNAME2``` the Role ```Administrator``` for this System Profile.
* If User Group does not exist yet, it will create a new LDAP group
  - The new User Group will have the ```Guest Role``` as ```Management Role``` on the dynaTrace Server

### Example - Creating a System Profile grant access to an LDAP Group (3)
```java –Dconfig.templates.profile=”{@application} {@environment} JBoss” –Dconfig.user.group.name=GROUPNAME –Dconfig.user.group.profile.role=Guest –Dconfig.user.group.management.role=Administrator –jar dt-onboarding.jar```
will deploy a System Profile using the given Template on the dynaTrace Server and grant the the User Group ```GROUPNAME``` the Role Guest for this System Profile.
* If the User Group does not exist yet, it will create a new LDAP group
  - The new User Group will have the ```Administrator Role``` as ```Management Role``` on the dynaTrace Server

### Example - Creating System Profiles and Dashboards and grant access to LDAP Groups (1)
```java –Dconfig.templates.profile=”{@application} {@environment} JBoss” –Dconfig.templates.dashboard=“{@environment}_{@application}_JBoss Monitoring.dashboard.xml” –Dconfig.user.group.name=GROUPNAME –Dconfig.user.group.profile.role=Guest –Dconfig.user.group.management.role=Administrator –Dconfig.user.group.dashboard.permission=Read_Write –jar dt-onboarding.jar```
will deploy a System Profile and a Dashboard using the given Templates on the dynaTrace Server and grant the the User Group ```GROUPNAME``` the Role Guest for this System Profile.
* The User Group will have ```Read/Write``` access to the new Dashboard
* If the User Group does not exist yet, it will create a new LDAP group
  - The new User Group will have the ```Administrator Role``` as Management Role on the dynaTrace Server

### Example - Creating System Profiles and Dashboards and grant access to LDAP Groups (2)
```java –Dconfig.templates.profile=”{@application} {@environment} JBoss” –Dconfig.templates.dashboards.DASHBOARD1.name=“{@environment}_{@application}_JBoss Monitoring” –Dconfig.templates.dashboards.DASHBOARD2.name=”{@environment}_{@application}_TRIAGE_DASHBOARD” –Dconfig.user.group.name=GROUPNAME –Dconfig.user.group.profile.role=Guest –Dconfig.user.group.management.role=Administrator –Dconfig.user.group.dashboard.permission=Read_Write –jar dt-onboarding.jar```
will deploy a System Profile and two Dashboards using the given Templates on the dynaTrace Server and grant the the User Group ```GROUPNAME``` the Role Guest for this System Profile.
* The User Group will have ```Read/Write``` access to the new Dashboards
* If the User Group does not exist yet, it will create a new LDAP group
  - The new User Group will have the ```Administrator``` Role as Management Role on the dynaTrace Server

### Example - Creating System Profiles and Dashboards and grant access to LDAP Groups (3)
```java –Dconfig.templates.profile=”{@application} {@environment} JBoss” –Dconfig.templates.dashboards.DASHBOARD1.name=“{@environment}_{@application}_JBoss Monitoring” –Dconfig.templates.dashboards.DASHBOARD2.name=”{@environment}_{@application}_TRIAGE_DASHBOARD” –Dconfig.user.group.name=GROUPNAME –Dconfig.user.group.profile.role=Guest –Dconfig.user.group.management.role=Administrator –Dconfig.user.group.dashboards.DASHBOARD1.permission=Read_Write –Dconfig.user.group.dashboards.DASHBOARD2.permission=Read –jar dt-onboarding.jar```
will deploy a System Profile and two Dashboards using the given Templates on the dynaTrace Server and grant the the User Group GROUPNAME the Role Guest for this System Profile.
* The User Group will have Read access to one of the Dashboards and ```Read/Write``` access to the other one
* If the User Group does not exist yet, it will create a new LDAP group
  - The new User Group will have the ```Administrator``` Role as Management Role on the dynaTrace Server

### Example - Creating System Profiles and Dashboards and grant access to LDAP Groups (4)
```java –Dconfig.templates.profile=”{@application} {@environment} JBoss” –Dconfig.templates.dashboards.DASHBOARD1.name=“{@environment}_{@application}_JBoss Monitoring” –Dconfig.templates.dashboards.DASHBOARD2.name=”{@environment}_{@application}_TRIAGE_DASHBOARD” –Dconfig.user.groups.GRP1.name=GROUPNAME1 –Dconfig.user.groups.GRP2.name=GROUPNAME2 –Dconfig.user.group.profile.role=Guest –Dconfig.user.group.management.role=Administrator –Dconfig.user.groups.dashboards.DASHBOARD2.permission=Read –Dconfig.user.groups.GRP1.dashboards.DASHBOARD1.permission=Read –Dconfig.user.groups.GRP2.dashboards.DASHBOARD1.permission=Read_Write –Dconfig.user.group.dashboards.DASHBOARD2.permission=Read –jar dt-onboarding.jar```
will deploy a System Profile and two Dashboards using the given Templates on the dynaTrace Server and grant the the User Group ```GROUPNAME``` the Role Guest for this System Profile.
* User Group ```GROUPNAME1``` will have ```Read``` access to the ```Triage Dashboard``` and the ```Monitoring Dashboard```
  - Because ```–Dconfig.user.group.dashboards.DASHBOARD2.permission=Read``` states, that by default every group should get ```Read``` access to it
  - Because ```–Dconfig.user.groups.GRP1.dashboards.DASHBOARD1.permissions=Read``` states, that specifically ```GROUP1``` should have ```Read``` permissions to ```DASHBOARD1```
* User Group ```GROUPNAME2``` will have ```Read``` access to the ```Triage Dashboard``` and ```Read/Write``` access to the ```Monitoring Dashboard```
* If the User Group does not exist yet, it will create a new LDAP group
  - The new User Group will have the ```Administrator``` Role as Management Role on the dynaTrace Server

## Full documentation of capabilities and options

```java [-D<option>=<value> *] -jar dt-onboarding.jar [usage|help]

  Options for accessing the dynaTrace Server:
      -Dconfig.server.host=<host>[:<port>]
           (optional, default = localost:8021)
          The host and port the dynaTrace Server listening
           for WebService/REST requests.
      -Dconfig.server.user=<username>
           (optional, default = admin)
          The user name of a user configured on the dynaTrace Server.
           This user must have administrative permissions.
      -Dconfig.server.pass=<password>
           (optional, default = admin)
          The password in order to authenticate when accessing
           the dynaTrace Server WebServices.

  Options for chosing Templates for System Profiles and Dashboards
   to deploy to the dynaTrace Server:
      -Dconfig.templates.profile=<profilename>[.profile.xml]
           (mandatory)
          The name of a System Profile embedded within
           dt-onboarding.jar (/resources/profiles/*.profile.xml) or
           on the dynaTrace Server. If a System Profile template
           located on the dynaTrace Server has the same name as a
           System Profile Template embedded within dt-onboarding.jar
           the Template located on the dynaTrace Server will be used.
          Variable names within the name and configuration of this file
           will require several -Dvariable.<variablename>=value options to
           be present in order to get resolved properly.
           See the section about variables for detailed information.
          Based on this Template either a new System Profile will be
           created or its Agent Groups and Configurations will be added
           to an existing one (-Dconfig.profile).
      -Dconfig.profile=<profilename>
           (optional)
          The name of an existig System Profile on the dynaTrace Server,
           to which to add the Agent Groups and Configurations found
           within the given System Profile Template (-Dconfig.templates.profile).
          If this option is not present, a new System Profile based on
           the name of the System Profile Templates name will be created
           on the dynaTrace Server.
      -Dconfig.templates.dashboard=<dashboardname>[.dashboard.xml]
           (optional)
          The name of a Dashboard embedded within
           dt-onboarding.jar (/resources/dashboards/*.dashboard.xml) or
           on the dynaTrace Server. If a Dashboard template
           located on the dynaTrace Server has the same name as a
           Dashboard Template embedded within dt-onboarding.jar
           the Template located on the dynaTrace Server will be used.
          Variable names within the name and configuration of this file
           will require several -Dvariable.<variablename>=value options to
           be present in order to get resolved properly.
           See the section about variables for detailed information.
          Based on this Template either a new Dashboard will be created or
           an already existing Dashboard with the same name on the
           dynaTrace Server will be replaced.
          The data source of this Dashboard will be preconfigured with the
           System Profile that is either being created or modified on the
           dynaTrace Server (-Dconfig.templates.profile, -Dconfig.profile).
      -Dconfig.dashboards.<dashboardkey>.name=<dashboardname>[.dashboard.xml]
           (optional)
          The name of a Dashboard embedded within
           dt-onboarding.jar (/resources/dashboards/*.dashboard.xml).
          This option is only necessary, when two or more Dashboards should
           get deployed with different Permissions assigned to the User Group(s)
           to create (-Dconfig.user.group, -Dconfig.user.groups.default.name,
           -Dconfig.user.groups.<groupkey>.name)
          Variable names within the name and configuration of this file
           will require several -Dvariable.<variablename>=value options to
           be present in order to get resolved properly.

  Options introducing new User Groups to the dynaTrace Server:
      -Dconfig.user.group=<usergroupname>
      -Dconfig.user.groups.default.name=<usergroupname>
           (optional)
          The name of a User Group to add to the User Permission Config
           of the dynaTrace Server.
          The new User Group will be an LDAP group.
          The new User Group will get assigned the Guest User Role on the
           dynaTrace Server unless otherwise specified
           (-Dconfig.user.group.management.role,
           -Dconfig.user.groups.<groupkey>.management.role).
          The new User Group will get assigned the Administrator User Role
           for the new System Profile unless otherwise specified
           (-Dconfig.user.group.profile.role,
           -Dconfig.user.groups.<groupkey>.profile.role).
          The new User Group will have 'Read' access to any new Dashboards
           unless otherwise specified (-Dconfig.user.group.profile.role,
           -Dconfig.user.groups.<groupkey>.profile.role).
      -Dconfig.user.groups.<groupkey>.name=<usergroupname>
          (optional)
          The name of a User Group to add to the User Permission Config
           of the dynaTrace Server.
          This option is only necessary to be used if two or more
           User Groups need to get added at the same time.
      -Dconfig.user.group.management.role=<rolename>
      -Dconfig.user.groups.default.management.role=<rolename>
          (optional, default = Guest)
          The Management Role to assign to the new User Group
           specified by -Dconfig.user.group
          The Management Role to assign to any new User Group
           specified by -Dconfig.user.groups.<groupkey>.name unless
           explicitely defined by -Dconfig.user.groups.<groupkey>.management.role
      -Dconfig.user.group.profile.role=<rolename>
      -Dconfig.user.groups.default.profile.role=<rolename>
          (optional, default = Administrator)
          The System Profile Role to assign to the new User Group
           specified by -Dconfig.user.group
          The System Profile Role to assign to any new User Group
           specified by -Dconfig.user.groups.<groupkey>.name unless explicitely
           defined by -Dconfig.user.groups.<groupkey>.profile.role
      -Dconfig.user.groups.<groupkey>.profile.role=<rolename>
          (optional, default = Administrator)
          The System Profile Role to assign a new User Group specified
           by -Dconfig.user.groups.<groupkey>.name
          This option is only required in case different User Groups
           need to get a different Role for the System Profile to be
           created or modified
      -Dconfig.user.group.dashboard.permission=<Read|Read_Write>
      -Dconfig.user.groups.default.dashboard.permission=<Read|Read_Write>
          (optional, default = Read)
          The Dashboard Permissions to assign to the new User Group
           specified by -Dconfig.user.group unless explicitely defined
           by -Dconfig.user.group.dashboards.<dashboardkey>.permission
           or -Dconfig.user.groups.<groupkey>.dashboards.<dashboardkey>.permission
          The Dashboard Permissions Role to assign to any new User Group
           specified by -Dconfig.user.groups.<groupkey>.name unless explicitely
           defined by -Dconfig.user.groups.<groupkey>.dashboard.permission
      -Dconfig.user.groups.<groupkey>.dashboards.<dashboardkey>.permission=<Read|Read_Write>
          (optional, default = Read)
          The Dashboard Permissions for a Dashboard specified by
           -Dconfig.dashboards.<dashboardkey>.name to assign to a specific
           User Group specified by -Dconfig.user.groups.<groupkey>.name
          This option is only necessary if different User Groups
           need to have different Permissions to various Dashboards

  Variables within System Profile Templates and Dashboard Templates:
      Both, the .profile.xml and .dashboard.xml files used as templates
       for creating System Profile or Dashboards are allowed to
        contain "variables" within their file names and their content.
      An valid example for a variables within the name of a Dashboard Template is:
        {@environment} {@application} Triage.dashboard.xml
      In order to resolve a proper name for the Dashboard to create based on this Template
        there are two additional options required:
        -Dvariable.environment=<value>
        and
        -Dvariable.environment=<value>
      The values for these options may be chosen freely and will be shared both,
        for resolving the eventual name(s) and values within the contents of
        System Profiles and Dashboards.
      Depending on how many variables are encoded within the System Profile Templates
        and Dashboard Templates specified by -Dconfig.template.dashboard,
        -Dconfig.dashboards.<dashboardkey>.name and
        -Dconfig.template.profile the number of -Dvariable.<variablename>=value
        options can only get evaluated during runtime and therefore my vary.
```
