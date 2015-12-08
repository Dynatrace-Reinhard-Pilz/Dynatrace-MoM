package com.dynatrace.fastpacks.metadata.resources;

import org.junit.Assert;
import org.junit.Test;

import com.dynatrace.fastpacks.EnumTest;

/**
 * Tests for class {@link ResourceType}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class ResourceTypeTest extends EnumTest<ResourceType>{
	
	/**
	 * {@link ResourceType#isValid(ResourceType)} is required to return
	 * {@code false} for parameter values {@link ResourceType#undefined} and
	 * {@code null}.
	 */
	@Test
	public void testIsNotValid() {
		Assert.assertFalse(ResourceType.isValid(null));
	}

	/**
	 * {@link ResourceType#isValid(ResourceType)} is required to return
	 * {@code true} for all parameter values except for
	 * {@link ResourceType#undefined}.
	 */
	@Test
	public void testIsValid() {
		ResourceType[] resourceTypes = ResourceType.values();
		for (ResourceType resourceType : resourceTypes) {
			Assert.assertTrue(ResourceType.isValid(resourceType));
		}
	}
	
	/**
	 * {@link ResourceType#validate(ResourceType)} is required to throw an
	 * {@link IllegalArgumentException} when getting passed {@code null}.
	 * 
	 * @throws IllegalArgumentException expected to be thrown
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testValidateNull() throws IllegalArgumentException {
		ResourceType.validate(null);
	}
	
	/**
	 * {@link ResourceType#validate(ResourceType)} is required to return
	 * without throwing an Exception when getting passed any value different
	 * from {@link ResourceType#undefined}.
	 */
	@Test
	public void testValidate() {
		ResourceType[] resourceTypes = ResourceType.values();
		for (ResourceType resourceType : resourceTypes) {
			ResourceType.validate(resourceType);
		}
	}
	
	@Test
	public void testGetStorage() {
		ResourceType[] resourceTypes = ResourceType.values();
		for (ResourceType resourceType : resourceTypes) {
			Assert.assertNotNull(resourceType.getStorage());
		}
	}

	@Override
	protected Class<ResourceType> getEnumClass() {
		return ResourceType.class;
	}
}
