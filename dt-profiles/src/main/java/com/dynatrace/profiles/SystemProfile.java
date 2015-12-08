package com.dynatrace.profiles;

import static com.dynatrace.utils.Strings.isNotEmpty;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.dynatrace.incidents.IncidentRule;
import com.dynatrace.profiles.metainfo.MetaInfo;
import com.dynatrace.profiles.metainfo.Metaable;
import com.dynatrace.utils.DomUtil;

/*
 <systemprofile
 	isrecording="<true>|false>"
 	id="<profilename>"
 	href="http(s)://<server>:<port>/rest/management/profiles/<profilename>"
 />
 */
@XmlRootElement(name = "systemprofile")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SystemProfile implements Metaable {
	
	private static final Logger LOGGER =
			Logger.getLogger(SystemProfile.class.getName());

	private boolean isRecording = false;
	private String id = null;
	private String href = null;
	private final Map<String, IncidentRule> incidentRules =
			new HashMap<String, IncidentRule>();
	private File localFile = null;
	private Document document = null;
	
	public static final String SELFMONITORING = "dynaTrace Self-Monitoring";
	
	/**
	 * c'tor
	 */
	public SystemProfile() {
		
	}
	
	/**
	 * c'tor
	 * 
	 * @param localFile the {@link File} holding the {@code .profile.xml} in
	 * 		case this System Profile is available locally
	 */
	public SystemProfile(File localFile) {
		this.id = getProfileName(localFile);
		setLocalFile(localFile);
	}
	
	public static SystemProfile get(File localFile) {
		SystemProfile profile = new SystemProfile();
		profile.id = getProfileName(localFile);
		profile.localFile = localFile;
		return profile;
	}
	
	public boolean isSelfMonitoringProfile() {
		return SELFMONITORING.equals(getId());
	}
	
	public void setLocalFile(File localFile) {
		if (localFile == null) {
			return;
		}
		this.localFile = localFile;
		if (this.document != null) {
			this.document = null;
		}
		evalIncidentRules();
	}
	
	@XmlTransient
	public File getLocalFile() {
		return localFile;
	}
	
	@XmlTransient
	public boolean isAvailableLocally() {
		return (localFile != null);
	}
	
	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}
	
	@XmlAttribute(name = "isrecording")
	public boolean isRecording() {
		return isRecording;
	}
	
	@XmlAttribute(name = "href")
	public String getHref() {
		return href;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
	}
	
	public void setHref(String href) {
		this.href = href;
	}
	
	@XmlTransient
	public Collection<IncidentRule> getIncidentRules() {
		Collection<IncidentRule> values = incidentRules.values();
		ArrayList<IncidentRule> result = new ArrayList<>();
		for (IncidentRule incidentRule : values) {
			if (incidentRule == null) {
				continue;
			}
			if ("Host CPU Unhealthy".equals(incidentRule.getId())) {
				continue;
			}
			result.add(incidentRule);
		}
		return result;
	}
	
	public Map<String, IncidentRule> getIncidentRuleMap() {
		return incidentRules;
	}
	
	public IncidentRule getIncidentRule(String incidentRuleName) {
		return incidentRules.get(incidentRuleName);
	}
	
	public void evalIncidentRules() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		try {
			saxParser = factory.newSAXParser();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING,
				"Unable to parse System Profile",
				e
			);
			return;
		}
		DefaultHandler handler = new DefaultHandler() {
			
			private static final String ELEMENT_INCIDENTRULE = "incidentrule";
			private static final String ATTRIBUTE_ID = "id";
			
			@Override
			public void startElement(
				String uri,
				String localName,
				String qName,
				Attributes attributes
			) throws SAXException {
				if (ELEMENT_INCIDENTRULE.equals(qName)) {
					String id = attributes.getValue(ATTRIBUTE_ID);
					if (isNotEmpty(id) && !incidentRules.containsKey(id)) {
						incidentRules.put(id, new IncidentRule(id));
					}
				}
			}
			
		};
		try (FileInputStream in = new FileInputStream(localFile)) {
			saxParser.parse(in, handler);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse System Profile", e);
		}
	}
	
	public static String getProfileName(File localFile) {
		if (localFile == null) {
			return null;
		}
		return getProfileName(localFile.getName());
	}
	
	public static String getProfileName(String fileName) {
		if (fileName == null) {
			return null;
		}
		if (!fileName.endsWith(".profile.xml")) {
			return null;
		}
		return fileName.substring(0, fileName.length() - ".profile.xml".length());
	}
	
	private Document buildDocument() {
		if (localFile == null) {
			LOGGER.log(Level.WARNING, "Cannot build Document - local file is null");
			return null;
		}
		if (document != null) {
			return document;
		}
		try {
			document = DomUtil.build(localFile);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Cannot build Document", e);
			return null;
		}
		return document;
	}
	
	@Override
	public MetaInfo getMetaInfo() {
		Document document = buildDocument();
		if (document == null) {
			LOGGER.log(Level.WARNING, "No Meta Info available, because no Document present");
			return null;
		}
		NodeList profileElements = document.getElementsByTagName("systemprofile");
		if (profileElements == null) {
			LOGGER.log(Level.WARNING, "No Meta Info available, because no systemprofile elements present (1)");
			return null;
		}
		if (profileElements.getLength() == 0) {
			LOGGER.log(Level.WARNING, "No Meta Info available, because no systemprofile elements present (2)");
			return null;
		}
		Element profileElement = (Element) profileElements.item(0);
		String description = profileElement.getAttribute("description");
		if (description == null) {
			LOGGER.log(Level.WARNING, "No Meta Info available, because no description attribute present");
			return null;
		}
		MetaInfo metaInfo = MetaInfo.parse(description);
		return metaInfo;
	}
	
	@Override
	public String getMetaInfo(String key) {
		MetaInfo metaInfo = getMetaInfo();
		if (metaInfo == null) {
			return null;
		}
		return metaInfo.get(key);
	}
	
	public AgentGroup getAgentGroup(String agentGroupId) {
		if (agentGroupId == null) {
			return null;
		}
		Document document = buildDocument();
		if (document == null) {
			return null;
		}
		NodeList agentGroupElements = document.getElementsByTagName("agentgroup");
		if (agentGroupElements == null) {
			return null;
		}
		int len = agentGroupElements.getLength();
		for (int i = 0; i < len; i++) {
			Element agentGroupElement = (Element) agentGroupElements.item(i);
			if (agentGroupElement == null) {
				continue;
			}
			if (agentGroupId.equals(agentGroupElement.getAttribute("id"))) {
				return new AgentGroup(agentGroupElement);
			}
		}
		return null;
	}
	
	public Collection<AgentGroup> getAgentGroups() {
		Document document = buildDocument();
		if (document == null) {
			return Collections.emptyList();
		}
		NodeList agentGroupElements = document.getElementsByTagName("agentgroup");
		if (agentGroupElements == null) {
			return Collections.emptyList();
		}
		Collection<AgentGroup> agentGroups = new ArrayList<>();
		int len = agentGroupElements.getLength();
		for (int i = 0; i < len; i++) {
			Element agentGroupElement = (Element) agentGroupElements.item(i);
			if (agentGroupElement == null) {
				continue;
			}
			agentGroups.add(new AgentGroup(agentGroupElement));
		}
		return agentGroups;
	}
	
}