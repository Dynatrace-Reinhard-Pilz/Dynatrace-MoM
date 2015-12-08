package com.dynatrace.mom.runtime;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.http.config.ConnectionConfig;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.mom.runtime.components.ServerRecord;
import com.dynatrace.utils.Version;

@XmlRootElement(name = "servers")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class XMLServerRepository implements BaseServerRepository {
	
	private Collection<ServerRecord> serverRecords = new ArrayList<>();
	private String href = null;
	private boolean follow = true;

	public XMLServerRepository() {
	}
	
	public XMLServerRepository(BaseServerRepository repository) {
		this.serverRecords = repository.getServerRecords();
	}
	
	public void setFollow(boolean follow) {
		this.follow = follow;
	}
	
	@XmlAttribute(name = "follow", required=false)
	public boolean isFollow() {
		return follow;
	}
	
	public void setHref(String href) {
		this.href = href;
	}
	
	@XmlAttribute(name = "href")
	public String getHref() {
		return href;
	}

	@XmlElementRef(name = ServerRecord.TAG, type = ServerRecord.class)
	@Override
	public final Collection<ServerRecord> getServerRecords() {
		return this.serverRecords;
	}
	
	@Override
	public void setServerRecords(Collection<ServerRecord> serverRecords) {
		this.serverRecords = serverRecords;
		if (serverRecords != null) {
			for (ServerRecord serverRecord : serverRecords) {
				if (serverRecord == null) {
					continue;
				}
				ServerConfig config = serverRecord.getConfig();
				ConnectionConfig connConf = config.getConnectionConfig();
				String host = connConf.getHost();
				int port = connConf.getPort();
				serverRecord.setName(host + ":" + port);
				serverRecord.setVersion(Version.UNDEFINED);
//				this.serverRecords.add(serverRecord);
			}
		}
	}

}
