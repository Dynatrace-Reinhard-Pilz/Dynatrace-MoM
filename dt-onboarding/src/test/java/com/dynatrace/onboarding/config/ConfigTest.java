package com.dynatrace.onboarding.config;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

public class ConfigTest {

	@Test
	public void testDiscoverGroupNames() {
		Properties properties = new Properties();
		String groupKey = "<groupkey>";
		properties.setProperty("config.user.groups." + groupKey + ".name", "My User Group");
		Config.setDefaultValues(properties);
		String[] groupNames = Config.discoverGroupKeys(properties);
		Assert.assertNotNull(groupNames);
		Assert.assertEquals(1, groupNames.length);
		Assert.assertEquals(groupKey, groupNames[0]);
		
		properties = new Properties();
		properties.setProperty("config.user.group", "My User Group");
		Config.setDefaultValues(properties);
		groupNames = Config.discoverGroupKeys(properties);
		Assert.assertNotNull(groupNames);
		Assert.assertEquals(1, groupNames.length);
		Assert.assertEquals(Config.DEFAULT, groupNames[0]);
		
	}
}
