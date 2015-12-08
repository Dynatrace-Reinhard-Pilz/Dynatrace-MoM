package com.dynatrace.profiles;

import java.io.File;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class SystemProfileTest {

	@Test
	public void testGetProfileName() {
		String profileName = UUID.randomUUID().toString();
		File file = new File(profileName + ".profile.xml");
		Assert.assertEquals(profileName, SystemProfile.getProfileName(file));
	}
}
