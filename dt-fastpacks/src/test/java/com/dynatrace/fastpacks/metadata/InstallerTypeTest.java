package com.dynatrace.fastpacks.metadata;

import org.junit.Assert;
import org.junit.Test;

import com.dynatrace.fastpacks.EnumTest;

/**
 * Tests for class {@link InstallerType}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class InstallerTypeTest extends EnumTest<InstallerType> {
	
	/**
	 * {@link InstallerType#isValid(InstallerType)} is required to return
	 * {@code false} when getting passed {@code null} as parameter.
	 */
	@Test
	public void testIsNotValid() {
		Assert.assertFalse(InstallerType.isValid(null));
	}

	/**
	 * {@link InstallerType#isValid(InstallerType)} is required to return
	 * {@code true} for all parameter values.
	 */
	@Test
	public void testIsValid() {
		InstallerType[] installerTypes = InstallerType.values();
		for (InstallerType installerType : installerTypes) {
			Assert.assertTrue(InstallerType.isValid(installerType));
		}
	}
	
	/**
	 * {@link InstallerType#validate(InstallerType)} is required to throw an
	 * {@link IllegalArgumentException} when getting passed {@code null}.
	 * 
	 * @throws IllegalArgumentException expected to be thrown
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testValidateNull() throws IllegalArgumentException {
		InstallerType.validate(null);
	}
	
	/**
	 * {@link InstallerType#validate(InstallerType)} is required to return
	 * without throwing an Exception when getting passed any value.
	 */
	@Test
	public void testValidate() {
		InstallerType[] installerTypes = InstallerType.values();
		for (InstallerType installerType : installerTypes) {
			InstallerType.validate(installerType);
		}
	}

	@Override
	protected Class<InstallerType> getEnumClass() {
		return InstallerType.class;
	}

}
