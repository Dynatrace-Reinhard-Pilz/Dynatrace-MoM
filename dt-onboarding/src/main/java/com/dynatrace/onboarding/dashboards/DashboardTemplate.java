package com.dynatrace.onboarding.dashboards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collection;
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

public class DashboardTemplate {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(DashboardTemplate.class.getSimpleName());

	private final String id = UUID.randomUUID().toString();
	private final File tempFolder = createTempFolder();
	private final File source;
	private final String key;
	private final Version version;
	
	private synchronized File createTempFolder() {
		try {
			File globalTempFile = Files.createTempDirectory(
				DashboardTemplate.class.getSimpleName()
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
	
	public DashboardTemplate(File source, String key) throws IOException {
		Objects.requireNonNull(source);
		this.source = source;
		this.key = key;
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
		Objects.requireNonNull(element);
		Objects.requireNonNull(attribute);
		String tagname = element.getTagName();
		if ("dashboardconfig".equals(tagname)) {
			String attributeName = attribute.getNodeName();
			if ("sessionid".equals(attributeName)) {
				return "{@sessionid}";
			} else if ("name".equals(attributeName)) {
				return getTemplateName();				
			} else if ("id".equals(attributeName)) {
				return UUID.randomUUID().toString();				
			} else if ("revision".equals(attributeName)) {
				return new UUID(
					System.currentTimeMillis(),
					UUID.randomUUID().getLeastSignificantBits()
				).toString();
			}
			return attribute.getNodeValue();
		} else {
			return attribute.getNodeValue();
		}
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
		if (element.getTagName().equals("dashboardconfig")) {
			element.removeAttribute("source");
			element.setAttribute("locationassource", "true");
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
		return filename.substring(0, filename.length() - ".dashboard.xml".length());
	}
	
	public String getResolvedDashboardName(Variables variables) throws UnresolvedVariableException {
		return variables.resolve(getTemplateName());
	}
	
	public Dashboard resolve(Variables variables) throws IOException, UnresolvedVariableException {
		Document document = null;
		try (InputStream in = openStream()) {
			document = DomUtil.build(in);
		}
		
		Document resolvedDocument = resolve(document, variables);
		
		String filename = variables.resolve(getFilename());
		File dashboardFile = new File(tempFolder, filename);
		dashboardFile.deleteOnExit();
		try (OutputStream out = new FileOutputStream(dashboardFile)) {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(resolvedDocument);
			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new IOException(e);
		}
		return new Dashboard(dashboardFile, key);
	}
	
}
