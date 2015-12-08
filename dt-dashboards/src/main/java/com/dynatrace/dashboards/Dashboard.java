package com.dynatrace.dashboards;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/*
<dashboard
	author="<author>"
	description="<description>"
	hrefrel="/rest/management/reports/create/<id>" 
	jnlp="<webstart-url>"
	modified="2011-07-26T19:15:22Z"
	modifiedby="admin"
	session="dynaTrace Self-Monitoring"
	id="Incident Dashboard"
	href="http(s)://<server>:<port>/rest/management/reports/create/<id>"
/>
 */
@XmlRootElement(name = "dashboard")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Dashboard {
	
	private String author = null;
	private String description = null;
	private String hrefrel = null;
	private String jnlp = null;
	private String id = null;
	private String modified = null;
	private String modifiedby = null;
	private String session = null;
	private String href = null;
	
	@XmlAttribute(name = "href")
	public String getHref() {
		return href;
	}
	
	public void setHref(String href) {
		this.href = href;
	}
	
	@XmlAttribute(name = "session")
	public String getSession() {
		return session;
	}
	
	public void setSession(String session) {
		this.session = session;
	}
	
	@XmlAttribute(name = "modified")
	public String getModified() {
		return modified;
	}
	
	public void setModified(String modified) {
		this.modified = modified;
	}
	
	@XmlAttribute(name = "modifiedby")
	public String getModifiedby() {
		return modifiedby;
	}
	
	public void setModifiedby(String modifiedby) {
		this.modifiedby = modifiedby;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	@XmlAttribute(name = "author")
	public String getAuthor() {
		return author;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlAttribute(name = "description")
	public String getDescription() {
		return description;
	}
	
	public void setHrefrel(String hrefrel) {
		this.hrefrel = hrefrel;
	}
	
	@XmlAttribute(name = "hrefrel")
	public String getHrefrel() {
		return hrefrel;
	}
	
	public void setJnlp(String jnlp) {
		this.jnlp = jnlp;
	}
	
	@XmlAttribute(name = "jnlp")
	public String getJnlp() {
		return jnlp;
	}

}
