package com.dynatrace.onboarding.fastpacks;

public abstract class AbstractPluginPeer implements PluginPeer {

	@Override
	public boolean ensureInstalled() {
		if (!isInstalled()) {
			
		}
		return install();
	}
	
	protected abstract boolean install();

}
