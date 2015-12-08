package com.dynatrace.incidents;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.MulitServerOperation;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.xml.XMLUtil;

public class IncidentRuleRefresh extends MulitServerOperation<IncidentRule, XmlIncidentRule> {
	
	private static final Logger LOGGER =
			Logger.getLogger(IncidentRuleRefresh.class.getName());
	
	private final Collection<IncidentRule> incidentRules;
	
	public IncidentRuleRefresh(
		ExecutionContext ctx,
		ServerConfig scfg,
		Collection<IncidentRule> incidentRules
	) {
		super(ctx, scfg);
		this.incidentRules = incidentRules;
	}

	@Override
	protected void handleResult(IncidentRule input, XmlIncidentRule incidentRule) {
		IncidentAware ia = getAttribute(IncidentAware.class);
		IncidentRule stored = ia.getIncidentRule(input.getId());
		if (stored == null) {
			logger().log(Level.WARNING, "No incident Rule " + input.getId() + " stored - cannot update it");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				XMLUtil.serialize(incidentRule, baos);
				logger().log(Level.WARNING, new String(baos.toByteArray()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		
		stored.updateIncidentReferences(
			incidentRule.getIncidents()
		);
		for (IncidentReference incidentRef : incidentRule.getIncidents()) {
			if (incidentRef == null) {
				continue;
			}
			Incident incident = incidentRef.getIncident();
			if (Incident.isConfirmed(incident)) {
				continue;
			}
			IncidentRefresh refresh = new IncidentRefresh(
				getContext(),
				getServerConfig(),
				stored,
				incidentRef
			);
			execute(refresh);
		}
	}
	
	@Override
	protected Iterable<IncidentRule> getItems() {
		return new ArrayList<IncidentRule>(incidentRules);
	}
	
	@Override
	protected Request<XmlIncidentRule> createRequest(IncidentRule incidentRule) {
		return new GetIncidentReferences(incidentRule);
	}
	
	@Override
	protected Logger logger() {
		return LOGGER;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((incidentRules == null) ? 0 : incidentRules.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		IncidentRuleRefresh other = (IncidentRuleRefresh) obj;
		if (incidentRules == null) {
			if (other.incidentRules != null)
				return false;
		} else if (!incidentRules.equals(other.incidentRules))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return IncidentRuleRefresh.class.getSimpleName() + " [" + incidentRules.size() + " incidentRules]";
	}
}
