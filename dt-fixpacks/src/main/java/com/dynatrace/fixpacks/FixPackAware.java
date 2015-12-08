package com.dynatrace.fixpacks;

public interface FixPackAware {

	void updateFixPackState(FixPack fixPack, FixPackStatus status);
	FixPackStatus getFixpackStatus(FixPack fixPack);
	void setFixPackInstallStatus(FixPackInstallStatus status);
}
