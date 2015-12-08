package com.dynatrace.fastpacks.metadata.instances;

import org.junit.Assert;
import org.junit.Test;

import com.dynatrace.fastpacks.EnumTest;
import com.dynatrace.fastpacks.metadata.InstallerType;

/**
 * Tests for class {@link InstanceType}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class InstanceTypeTest extends EnumTest<InstanceType> {

	
	/**
	 * {@link InstanceType#isValid(InstanceType)} is required to return
	 * {@code false} when getting passed {@code null} as parameter.
	 */
	@Test
	public void testIsNotValid() {
		Assert.assertFalse(InstanceType.isValid(null));
	}

	/**
	 * {@link InstanceType#isValid(InstanceType)} is required to return
	 * {@code true} for all parameter values except for
	 * {@link InstanceType#undefined}.
	 */
	@Test
	public void testIsValid() {
		InstanceType[] instanceTypes = InstanceType.values();
		for (InstanceType instanceType : instanceTypes) {
			Assert.assertTrue(InstanceType.isValid(instanceType));
		}
	}
	
	/**
	 * {@link InstanceType#validate(InstanceType)} is required to throw an
	 * {@link IllegalArgumentException} when getting passed {@code null}.
	 * 
	 * @throws IllegalArgumentException expected to be thrown
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testValidateNull() throws IllegalArgumentException {
		InstanceType.validate(null);
	}
	
	/**
	 * {@link InstallerType#validate(InstallerType)} is required to return
	 * without throwing an Exception when getting passed any value different
	 * from {@link InstallerType#undefined}.
	 */
	@Test
	public void testValidate() {
		InstanceType[] instanceTypes = InstanceType.values();
		for (InstanceType instanceType : instanceTypes) {
			InstanceType.validate(instanceType);
		}
	}

	@Override
	protected Class<InstanceType> getEnumClass() {
		return InstanceType.class;
	}
	
}
