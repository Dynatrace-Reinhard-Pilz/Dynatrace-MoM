package com.dynatrace.onboarding.profiles;

import static com.dynatrace.utils.Unchecked.cast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.dynatrace.onboarding.variables.UnresolvedVariableException;
import com.dynatrace.onboarding.variables.Variables;
import com.dynatrace.utils.DomUtil;
import com.dynatrace.utils.Iterables;
import com.dynatrace.utils.Version;

public class ProfileTemplate {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(ProfileTemplate.class.getSimpleName());

	private final String id = UUID.randomUUID().toString();
	private final File tempFolder = createTempFolder();
	private final File source;
	private Variables variables = new Variables();
	private final Version version;
	
	private synchronized File createTempFolder() {
		try {
			File globalTempFile = Files.createTempDirectory(
				ProfileTemplate.class.getSimpleName()
			).toFile();
			if (!globalTempFile.exists()) {
				if (!globalTempFile.mkdirs()) {
					throw new RuntimeException(
						"Unable to create directory " +
						"'" + globalTempFile.getAbsolutePath() + "'"
					);
				}
			} else if (!globalTempFile.isDirectory()) {
				throw new RuntimeException(
					"'" + globalTempFile.getAbsolutePath() + "'" +
					" is not a directory"
				);
			}
			File tempFolder = new File(globalTempFile, id);
			if (tempFolder.exists()) {
				if (!tempFolder.isDirectory()) {
					throw new RuntimeException(
						"'" + tempFolder.getAbsolutePath() + "'" +
						" is not a directory"
					);
				}
			} else if (!tempFolder.mkdirs()) {
				throw new RuntimeException(
					"Unable to create directory " +
					"'" + tempFolder.getAbsolutePath() + "'"
				);
			}
			tempFolder.deleteOnExit();
			return tempFolder;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public ProfileTemplate(File source) throws IOException {
		this.source = source;
		Document document = DomUtil.build(source);
		version = DomUtil.extractVersion(document, source.getName());
	}
	
	public Version getVersion() {
		return version;
	}
	
	public File getSource() {
		return source;
	}
	
	private InputStream openStream() throws IOException {
		return new FileInputStream(source);
	}
	
	public Iterable<String> getVariables() throws IOException, ParserConfigurationException, SAXException {
		Collection<String> variables = new HashSet<>();
		try (InputStream in = openStream()) {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(in);
			Iterables.addAll(variables, getVariables(document));
		}
		Iterables.addAll(variables, Variables.getVariables(getFilename()));
		return variables;
	}
	
	private Iterable<String> getVariables(Document document) {
		return getVariables(document.getDocumentElement());
	}
	
	private Iterable<String> getVariables(Element element) {
		Collection<String> variables = new HashSet<>();
		NamedNodeMap attributes = element.getAttributes();
		int attributeCount = attributes.getLength();
		if (attributeCount > 0) {
			for (int i = 0; i < attributeCount; i++) {
				Node attribute = attributes.item(i);
				String attributeValue = attribute.getNodeValue();
				Iterables.addAll(
					variables,
					Variables.getVariables(attributeValue)
				);
			}
		}
		NodeList childNodes = element.getChildNodes();
		int childCount = childNodes.getLength();
		for (int i = 0; i < childCount; i++) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Iterables.addAll(
						variables,
						getVariables((Element) childNode)
					);
			}
		}
		return variables;
	}
	
	private Document resolve(Document document, Variables variables) throws UnresolvedVariableException {
		Objects.requireNonNull(document);
		Objects.requireNonNull(variables);
		Document clone = (Document) document.cloneNode(true);
		resolve(clone.getDocumentElement(), variables);
		return clone;
	}
	
	private String tokenize(Element element, Node attribute) {
		if (element.getTagName().equals("agentmapping")) {
			if (attribute.getNodeName().equals("namepattern")) {
				return tokenize(element, element.getAttributeNode("id"));
			}
		}
		return attribute.getNodeValue();
	}
	
	private void resolve(Element element, Variables variables) throws UnresolvedVariableException {
		NamedNodeMap attributes = element.getAttributes();
		int attributeCount = attributes.getLength();
		if (attributeCount > 0) {
			for (int i = 0; i < attributeCount; i++) {
				Node attribute = attributes.item(i);
				String attributeValue = tokenize(element, attribute);
				attribute.setNodeValue(variables.resolve(attributeValue));
			}
		}
		NodeList childNodes = element.getChildNodes();
		int childCount = childNodes.getLength();
		for (int i = 0; i < childCount; i++) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				resolve((Element) childNode, variables);
			}
		}
	}
	
	private String getFilename() {
		return this.source.getName();
	}
	
	public String getTemplateName() {
		String filename = getFilename();
		return filename.substring(0, filename.length() - ".profile.xml".length());
	}
	
	public String getResolvedProfileName(Variables variables) throws UnresolvedVariableException {
		return variables.resolve(getTemplateName());
	}
	
	public Profile resolve(Variables variables) throws IOException, UnresolvedVariableException {
		return resolve(variables, null);
	}
	
	private Element getSensorConfig(Element configurationElement, String agentGroupId) {
		if (configurationElement == null) {
			return null;
		}
		if (agentGroupId == null) {
			return null;
		}
		NodeList elements = configurationElement.getElementsByTagName("sensorconfig");
		if (elements == null) {
			return null;
		}
		int numSensorConfigs = elements.getLength();
		for (int i = 0; i < numSensorConfigs; i++) {
			Element sensorConfigElement = cast(elements.item(i));
			if (sensorConfigElement == null) {
				continue;
			}
			String refAgentGroupId = sensorConfigElement.getAttribute("refagentgroup");
			if (agentGroupId.equals(refAgentGroupId)) {
				return sensorConfigElement;
			}
		}
		return null;
	}
	
	private Element getActiveConfiguration(Document document) {
		if (document == null) {
			return null;
		}
		NodeList configurationElements = document.getElementsByTagName("configuration");
		if (configurationElements == null) {
			return null;
		}
		int numConfigurations = configurationElements.getLength();
		for (int i = 0; i < numConfigurations; i++) {
			Element configurationElement = cast(configurationElements.item(i));
			if ("true".equals(configurationElement.getAttribute("active"))) {
				return configurationElement;
			}
		}
		return null;
	}
	
	private Element getAgentGroupsElement(Document document) {
		if (document == null) {
			return null;
		}
		NodeList elements = document.getElementsByTagName("agentgroups");
		if (elements == null) {
			return null;
		}
		int numElements = elements.getLength();
		if (numElements == 0) {
			return null;
		}
		return cast(elements.item(0));
	}
	
	private Element getConfigurationsElement(Document document) {
		if (document == null) {
			return null;
		}
		NodeList elements = document.getElementsByTagName("configurations");
		if (elements == null) {
			return null;
		}
		int numElements = elements.getLength();
		if (numElements == 0) {
			return null;
		}
		return cast(elements.item(0));
	}
	
	private Element getAgentGroup(Document document, String agentGroupId) {
		if (document == null) {
			return null;
		}
		if (agentGroupId == null) {
			return null;
		}
		Element agentGroups = getAgentGroupsElement(document);
		NodeList elements = agentGroups.getElementsByTagName("agentgroup");
		if (elements == null) {
			return null;
		}
		int numElements = elements.getLength();
		for (int i = 0; i < numElements; i++) {
			Element agentGroup = cast(elements.item(i));
			if (agentGroup == null) {
				continue;
			}
			if (agentGroupId.equals(agentGroup.getAttribute("id"))) {
				return agentGroup;
			}
		}
		return null;
	}
	
	private void appendAgentGroup(Document document, Element agentGroupElement) {
		if (document == null) {
			return;
		}
		if (agentGroupElement == null) {
			return;
		}
		String agentGroupId = agentGroupElement.getAttribute("id");
		agentGroupId = variables.resolve(agentGroupId);
		Element agentGroup = getAgentGroup(document, agentGroupId);
		if (agentGroup != null) {
			getAgentGroupsElement(document).removeChild(agentGroup);
		}
		agentGroupElement = cast(document.importNode(agentGroupElement, true));
		Element agentGroupsElement = getAgentGroupsElement(document);
		agentGroupsElement.appendChild(agentGroupElement);
	}
	
	private Element getConfigurationElement(Document document, String configurationId) {
		if (document == null) {
			return null;
		}
		if (configurationId == null) {
			return null;
		}
		NodeList elements = document.getElementsByTagName("configuration");
		if (elements == null) {
			return null;
		}
		int numElements = elements.getLength();
		for (int i = 0; i < numElements; i++) {
			Element element = cast(elements.item(i));
			if (element == null) {
				continue;
			}
			if (configurationId.equals(element.getAttribute("id"))) {
				return element;
			}
		}
		return null;
	}
	
	private static Collection<Element> convert(NodeList nodes) {
		if (nodes == null) {
			return Collections.emptyList();
		}
		Collection<Element> elements = new ArrayList<>();
		int numNodes = nodes.getLength();
		for (int i = 0; i < numNodes; i++) {
			Element element = cast(nodes.item(i));
			if (element == null) {
				continue;
			}
			elements.add(element);
		}
		return elements;
	}
	
	private void clearConfigurationElement(Element configurationElement, boolean isActive) {
		Collection<Element> elements = convert(
			configurationElement.getElementsByTagName("configuration")
		);
		for (Element element : elements) {
			configurationElement.removeChild(element);
		}
		configurationElement.setAttribute("active", Boolean.toString(isActive));
	}
	
	private boolean containsConfigurations(Document document) {
		if (document == null) {
			return false;
		}
		NodeList elements = document.getElementsByTagName("configuration");
		if (elements == null) {
			return false;
		}
		return elements.getLength() > 0;
	}
	
	private void appendSensorConfig(Element configuration, Element sensorConfig) {
		if (configuration == null) {
			return;
		}
		if (sensorConfig == null) {
			return;
		}
		String agentGroupId = sensorConfig.getAttribute("refagentgroup");
		agentGroupId = variables.resolve(agentGroupId);
		Element existingSensorConfig = getSensorConfig(
			configuration,
			agentGroupId
		);
		if (existingSensorConfig != null) {
			configuration.removeChild(existingSensorConfig);
		}
		configuration.appendChild(
			configuration.getOwnerDocument().importNode(sensorConfig, true)
		);
	}
	
	private void appendConfiguration(Document document, Element configurationElement) {
		if (document == null) {
			return;
		}
		if (configurationElement == null) {
			return;
		}
		String configurationId = configurationElement.getAttribute("id");
		configurationId = variables.resolve(configurationId);
		Element existingConfiguration =
				getConfigurationElement(document, configurationId);
		if (existingConfiguration != null) {
			return;
		}
		configurationElement = cast(document.importNode(configurationElement, true));
		clearConfigurationElement(
			configurationElement,
			!containsConfigurations(document)
		);
		
		Element activeConfiguration = getActiveConfiguration(document);
		if (activeConfiguration == null) {
			throw new InternalError();
		}
		
		NodeList agentGroupElements = document.getElementsByTagName("agentgroup");
		if (agentGroupElements != null) {
			int numAgentGroups = agentGroupElements.getLength();
			for (int i = 0; i < numAgentGroups; i++) {
				Element agentGroup = cast(agentGroupElements.item(i));
				if (agentGroup == null) {
					continue;
				}
				Element sensorConfig = getSensorConfig(
					activeConfiguration,
					agentGroup.getAttribute("id")
				);
				if (sensorConfig == null) {
					throw new InternalError();
				}
				appendSensorConfig(configurationElement, sensorConfig);
			}
		}
		
		Element configurationsElement = getConfigurationsElement(document);
		configurationsElement.appendChild(configurationElement);
	}
	
	public Profile resolve(Variables variables, Profile profile) throws IOException, UnresolvedVariableException {
		this.variables = variables;
		Document document = null;
		
		if (profile != null) {
			document = DomUtil.build(profile.getFile());
			Document tplDoc = null;
			try (InputStream in = openStream()) {
				tplDoc = DomUtil.build(in);
			}
			
			NodeList configurations =
					tplDoc.getElementsByTagName("configuration");
			if (configurations == null) {
				throw new InternalError();
			}
			int numConfigurations = configurations.getLength();
			for (int i = 0; i < numConfigurations; i++) {
				Element configuration = cast(configurations.item(i));
				if (configuration == null) {
					continue;
				}
				appendConfiguration(document, configuration);
			}
			
			NodeList tplAgentGroups = tplDoc.getElementsByTagName("agentgroup");
			Element tplActiveConfiguration = getActiveConfiguration(tplDoc);
			if (tplAgentGroups != null) {
				int length = tplAgentGroups.getLength();
				for (int i = 0; i < length; i++) {
					Element tplAgentGroup = (Element) tplAgentGroups.item(i);
					String agentGroupId = tplAgentGroup.getAttribute("id");
					appendAgentGroup(document, tplAgentGroup);
					Element tplActiveSensorConfig = getSensorConfig(tplActiveConfiguration, agentGroupId);
					configurations = document.getElementsByTagName("configuration");
					numConfigurations = configurations.getLength();
					for (int j = 0; j < numConfigurations; j++) {
						Element configuration = cast(configurations.item(j));
						if (configuration == null) {
							continue;
						}
						String configurationId = configuration.getAttribute("id");
						Element tplConfiguration = getConfigurationElement(
							tplDoc,
							configurationId
						);
						if (tplConfiguration != null) {
							appendSensorConfig(
								configuration,
								getSensorConfig(tplConfiguration, agentGroupId)
							);
						} else {
							appendSensorConfig(configuration, tplActiveSensorConfig);
						}
					}
				}
			}
		} else {
			try (InputStream in = openStream()) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				document = dBuilder.parse(in);
			} catch (ParserConfigurationException | SAXException e) {
				throw new IOException(e);
			}
		}
		
		Document resolvedDocument = resolve(document, variables);

/*
	      <task id="Automatic Onboarding Task" rev="3fc7f2fe-432b-4b22-b54e-3e7fb0e5e2a8" creationtype="MANUAL" desc="" hidden="false" target="dynaTrace Server" paused="false" type="plugintask" timeout="0">
	        <taskdetail executor="TaskPluginExecutor">
	          <plugintask>
	            <config sourcebundlename="dt-ensure-user-group-task" bundleversion="1.0.0.0" rolekey="com.dynatrace.tasks.ensure.user.groups.task" rolesourcebundlename="dt-ensure-user-group-task" key="com.dynatrace.user.group.ensure.task.config" roletype="4" />
	          </plugintask>
	          <schedule id="41b3b8a8-5360-4f20-8b61-b6405a6f18b1" timezone="America/New_York" manually="true" useservertimezone="false" />
	        </taskdetail>
	      </task>
*/	
		Collection<Element> tasks = convert(resolvedDocument.getElementsByTagName("task"));
		for (Element task : tasks) {
			if (task == null) {
				continue;
			}
			if ("Automatic Onboarding Task".equals(task.getAttribute("id"))) {
				task.getParentNode().removeChild(task);
			}
		}
		appendOnboardingTask(resolvedDocument);
		
		String filename = variables.resolve(getFilename());
		if (profile != null) {
			filename = profile.getFile().getName();
		}
		File profileFile = new File(tempFolder, filename);
		profileFile.deleteOnExit();
		try (OutputStream out = new FileOutputStream(profileFile)) {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(resolvedDocument);
			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new IOException(e);
		}
		return new Profile(profileFile);
	}

	private void appendOnboardingTask(Document resolvedDocument) {
		NodeList tasksElements = resolvedDocument.getElementsByTagName("tasks");
		Node tasksElement = tasksElements.item(0);
		Element taskElement = resolvedDocument.createElement("task");
		taskElement.setAttribute("id", "Automatic Onboarding Task");
		taskElement.setAttribute("rev", UUID.randomUUID().toString());
		taskElement.setAttribute("creationtype", "MANUAL");
		taskElement.setAttribute("desc", "");
		taskElement.setAttribute("hidden", "false");
		taskElement.setAttribute("target", "dynaTrace Server");
		taskElement.setAttribute("paused", "false");
		taskElement.setAttribute("type", "plugintask");
		taskElement.setAttribute("timeout", "0");
		
		Element taskDetailElement = resolvedDocument.createElement("taskdetail");
		taskDetailElement.setAttribute("executor", "TaskPluginExecutor");
		
		Element pluginTaskElement = resolvedDocument.createElement("plugintask");
		Element configElement = resolvedDocument.createElement("config");
		configElement.setAttribute("sourcebundlename", "dt-ensure-user-group-task");
		configElement.setAttribute("bundleversion", "1.0.0.0");
		configElement.setAttribute("rolekey", "com.dynatrace.tasks.ensure.user.groups.task");
		configElement.setAttribute("rolesourcebundlename", "dt-ensure-user-group-task");
		configElement.setAttribute("key", "com.dynatrace.user.group.ensure.task.config");
		configElement.setAttribute("roletype", "4");
		pluginTaskElement.appendChild(configElement);
		
		taskDetailElement.appendChild(pluginTaskElement);
		
		Element scheduleElement = resolvedDocument.createElement("schedule");
		scheduleElement.setAttribute("id", UUID.randomUUID().toString());
		scheduleElement.setAttribute("timezone", "America/New_York");
		scheduleElement.setAttribute("manually", "true");
		scheduleElement.setAttribute("useservertimezone", "false");
		taskDetailElement.appendChild(scheduleElement);
		
		taskElement.appendChild(taskDetailElement);		
		tasksElement.appendChild(taskElement);
	}

}
