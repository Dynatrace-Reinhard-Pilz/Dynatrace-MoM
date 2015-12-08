package com.dynatrace.mom.runtime.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.dynatrace.fixpacks.InstallStatus;
import com.dynatrace.http.ConnectionStatus;

public class FixPackActions implements Iterable<FixPackAction> {
	
	private Collection<FixPackAction> actions = new ArrayList<>();
	
	public void add(FixPackAction action) {
		actions.add(action);
	}

	@Override
	public Iterator<FixPackAction> iterator() {
		return actions.iterator();
	}
	
	public FixPackAction getInstalledFixPack() {
		FixPackAction installedAction = null;
		for (FixPackAction fixPackAction : actions) {
			if (fixPackAction.isInstalled()) {
				if (fixPackAction.compareTo(installedAction) > 0) {
					installedAction = fixPackAction;
				}
			}
		}
		return installedAction;
	}
	
	public FixPackAction getInstallingFixPack() {
		for (FixPackAction fixPackAction : actions) {
			if (fixPackAction.isInstalling()) {
				return fixPackAction;
			}
		}
		return null;
	}
	
	public boolean isVisible(FixPackAction fixPackAction) {
		if (fixPackAction == null) {
			return false;
		}
		FixPackAction installedFixPack = getInstalledFixPack();
		if (installedFixPack != null) {
			if (!fixPackAction.includes(installedFixPack)) {
				return false;
			}
		}
		FixPackAction installingFixPack = getInstallingFixPack();
		if (installingFixPack != null) {
			if (!fixPackAction.includes(installingFixPack)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isEnabled(FixPackAction fixPackAction) {
		if (fixPackAction == null) {
			return false;
		}
		if (fixPackAction.getServerRecord().getConnectionStatus() != ConnectionStatus.ONLINE) {
			return false;
		}
		if (fixPackAction.getInstallStatus() == InstallStatus.RESTARTREQUIRED) {
			return true;
		}
		FixPackAction installedFixPack = getInstalledFixPack();
		if (installedFixPack == null) {
			return (getInstallingFixPack() == null);
		}
		return fixPackAction.includes(installedFixPack) && !fixPackAction.equals(installedFixPack);
	}

}
