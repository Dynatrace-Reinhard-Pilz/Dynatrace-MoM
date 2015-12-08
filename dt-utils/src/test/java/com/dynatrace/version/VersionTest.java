package com.dynatrace.version;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.dynatrace.utils.Strings;
import com.dynatrace.utils.Version;
import com.dynatrace.utils.Versionable;
import com.dynatrace.xml.XMLUtil;

/**
 * Tests for class {@link Version}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class VersionTest {

	@Test
	public void testConstructor() {
		int major = 1;
		int minor = 2;
		int revision = 3;
		int build = 4;
		Version version = new Version(major, minor, revision, build);
		Assert.assertEquals(major, version.getMajor());
		Assert.assertEquals(minor, version.getMinor());
		Assert.assertEquals(revision, version.getRevision());
		Assert.assertEquals(build, version.getBuild());
	}
	
	@Test
	public void testParse() {
		int major = 1;
		int minor = 2;
		int revision = 3;
		int build = 4;
		Version version = Version.parse(
			major + "." + minor + "." + revision + "." + build
		);
		Assert.assertEquals(major, version.getMajor());
		Assert.assertEquals(minor, version.getMinor());
		Assert.assertEquals(revision, version.getRevision());
		Assert.assertEquals(build, version.getBuild());
	}
	
	@Test
	public void testIsValid() {
		int major = 1;
		int minor = 2;
		int revision = 3;
		int build = 4;
		Assert.assertFalse(Version.isValid(null));
		Assert.assertFalse(Version.isValid(Version.UNDEFINED));
		Assert.assertTrue(Version.isValid(
			new Version(major, minor, revision, build))
		);
	}
	
	@Test
	public void testToString() {
		int major = 1;
		int minor = 2;
		int revision = 3;
		int build = 4;
		Version version = new Version(major, minor, revision, build);
		Assert.assertEquals(
			major + "." + minor + "." + revision + "." + build,
			version.toString()
		);
	}
	
	@Test
	public void testHashCode() {
		Version version = new Version(1, 2, 3, 4);
		
		Assert.assertEquals(
			version.hashCode(),
			Version.parse(version.toString()).hashCode()
		);
		
		Assert.assertNotEquals(
			version.hashCode(),
			new Version(0, 2, 3, 4).hashCode()
		);
		
		Assert.assertNotEquals(
			version.hashCode(),
			new Version(1, 0, 3, 4).hashCode()
		);
		
		Assert.assertNotEquals(
			version.hashCode(),
			new Version(1, 2, 0, 4).hashCode()
		);
		
		Assert.assertNotEquals(
			version.hashCode(),
			new Version(1, 2, 3, 0).hashCode()
		);
	}
	
	
	@Test
	public void testEquals() {
		Version version = new Version(1, 2, 3, 4);
		Assert.assertFalse(version.equals(null));
		Assert.assertFalse(version.equals(this));
		Assert.assertFalse(version.equals(new Version(0, 2, 3, 4)));
		Assert.assertFalse(version.equals(new Version(1, 0, 3, 4)));
		Assert.assertFalse(version.equals(new Version(1, 2, 0, 4)));
		Assert.assertFalse(version.equals(new Version(1, 2, 3, 0)));
		Assert.assertTrue(version.equals(Version.parse(version.toString())));
	}
	
	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Version version = new Version(1, 2, 3, 4);
		ObjectOutputStream oout = new ObjectOutputStream(out);
		oout.writeObject(version);
		oout.close();
		out.close();
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		ObjectInputStream oin = new ObjectInputStream(in);
		Assert.assertEquals(version, oin.readObject());
	}
	
	@Test(expected = NullPointerException.class)
	public void testParseNull() {
		Assert.assertNull(Version.parse(null, false));
		Version.parse(null, true);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testParseEmpty() {
		Assert.assertNull(Version.parse(Strings.EMPTY, false));
		Version.parse(Strings.EMPTY, true);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testParseMajorNonNumber() {
		String sVersion = "a.";
		Assert.assertNull(Version.parse(sVersion, false));
		Version.parse(sVersion, true);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testParseMinorNonNumber() {
		String sVersion = "1.a.";
		Assert.assertNull(Version.parse(sVersion, false));
		Version.parse(sVersion, true);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testParseNoMinor() {
		String sVersion = "1.";
		Assert.assertNull(Version.parse(sVersion, false));
		Version.parse(sVersion, true);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testParseRevisionNonNumber() {
		String sVersion = "1.2.a";
		Assert.assertNull(Version.parse(sVersion, false));
		Version.parse(sVersion, true);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testParseNoRevision() {
		String sVersion = "1.2.";
		Assert.assertNull(Version.parse(sVersion, false));
		Version.parse(sVersion, true);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testParseBuildNonNumber() {
		String sVersion = "1.2.3.a";
		Assert.assertNull(Version.parse(sVersion, false));
		Version.parse(sVersion, true);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testParseNoBuild() {
		String sVersion = "1.2.3.";
		Assert.assertNull(Version.parse(sVersion, false));
		Version.parse(sVersion, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseTooLong() {
		String sVersion = "1.2.3.4.";
		Assert.assertNull(Version.parse(sVersion, false));
		Version.parse(sVersion, true);
	}
	
	@Test
	public void testXmlSerialization() throws IOException {
		Version version = new Version(1, 2, 3, 4);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLUtil.serialize(version, out);
		out.close();
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Assert.assertEquals(version, XMLUtil.deserialize(in, Version.class));
	}
	
	@Test
	public void testMatchesMajor() {
		Version version = new Version(1, 2, 2, 2);
		Assert.assertTrue(version.matchesMajor(new Version(1, 2, 3, 3)));
		Assert.assertFalse(version.matchesMajor(new Version(2, 2, 3, 3)));
	}
	
	@Test
	public void testMatchesMinor() {
		Version version = new Version(1, 2, 2, 2);
		Assert.assertTrue(version.matchesMinor(new Version(1, 2, 3, 3)));
		Assert.assertFalse(version.matchesMinor(new Version(3, 2, 3, 3)));
		Assert.assertFalse(version.matchesMinor(new Version(1, 3, 3, 3)));
	}
	
	@Test
	public void testMatchesRevision() {
		Version version = new Version(1, 2, 3, 2);
		Assert.assertTrue(version.matchesRevision(new Version(1, 2, 3, 3)));
		Assert.assertFalse(version.matchesRevision(new Version(3, 2, 3, 3)));
		Assert.assertFalse(version.matchesRevision(new Version(1, 3, 3, 3)));
		Assert.assertFalse(version.matchesRevision(new Version(3, 3, 3, 3)));
		Assert.assertFalse(version.matchesRevision(new Version(1, 2, 4, 3)));
	}
	
	@Test
	public void testIncludes() {
		Version version = new Version (6, 1, 1, 1);
		Assert.assertFalse(version.includes(new Versionable() {
			
			@Override
			public int compareTo(Versionable o) {
				return 0;
			}
			
			@Override
			public boolean includes(Versionable versionable) {
				return false;
			}
			
			@Override
			public Version getVersion() {
				return null;
			}
			
			@Override
			public void updateVersion(Version version) {
				
			}
			
			@Override
			public boolean equals(Versionable versionable) {
				return false;
			}
			
		}));
		Assert.assertFalse(version.includes(null));
		Assert.assertTrue(version.includes(new Version(6, 1, 1, 0)));
		Assert.assertTrue(version.includes(new Version(6, 1, 1, 1)));
		Assert.assertTrue(version.includes(new Version(6, 1, 1, 0)));
		Assert.assertTrue(version.includes(new Version(6, 1, 1, 1)));
		Assert.assertFalse(version.includes(new Version(6, 2, 1, 1)));
		Assert.assertFalse(version.includes(new Version(6, 1, 2, 1)));
		Assert.assertFalse(version.includes(new Version(6, 1, 1, 2)));
		
		version = new Version (5, 1, 1, 1);		
		Assert.assertTrue(version.includes(new Version(5, 1, 1, 0)));
		Assert.assertTrue(version.includes(new Version(5, 1, 1, 1)));
		Assert.assertTrue(version.includes(new Version(5, 1, 1, 0)));
		Assert.assertTrue(version.includes(new Version(5, 1, 1, 1)));
		Assert.assertFalse(version.includes(new Version(5, 2, 1, 1)));
		Assert.assertFalse(version.includes(new Version(5, 1, 2, 1)));
		Assert.assertFalse(version.includes(new Version(5, 1, 1, 2)));

		version = new Version (6, 2, 1, 1);
		Assert.assertFalse(version.includes(new Version(6, 1, 1, 0)));
		Assert.assertFalse(version.includes(new Version(6, 2, 3, 1)));
		Assert.assertTrue(version.includes(new Version(6, 2, 1, 0)));
		Assert.assertTrue(version.includes(new Version(6, 2, 0, 0)));
		Assert.assertFalse(version.includes(new Version(7, 1, 1, 0)));
	}
	
	@Test
	public void testCompareTo() {
		Version version = new Version(3, 4, 5, 6);
		Assert.assertTrue(version.compareTo(new Version(3, 4, 5, 6)) == 0);
		Assert.assertTrue(version.compareTo(new Version(2, 4, 5, 6)) == 1);
		Assert.assertTrue(version.compareTo(new Version(4, 4, 5, 6)) == -1);
		Assert.assertTrue(version.compareTo(new Version(3, 3, 5, 6)) == 1);
		Assert.assertTrue(version.compareTo(new Version(3, 5, 5, 6)) == -1);
		Assert.assertTrue(version.compareTo(new Version(3, 4, 4, 6)) == 1);
		Assert.assertTrue(version.compareTo(new Version(3, 4, 6, 6)) == -1);
		Assert.assertTrue(version.compareTo(new Version(3, 4, 5, 5)) == 0);
		Assert.assertTrue(version.compareTo(new Version(3, 4, 5, 6)) == 0);
		Assert.assertTrue(version.compareTo(new Version(3, 4, 5, 7)) == 0);
		Assert.assertTrue(version.compareTo(null) == 1);
		Assert.assertTrue(version.compareTo(new Versionable() {
			
			@Override
			public int compareTo(Versionable o) {
				return 0;
			}
			
			@Override
			public boolean includes(Versionable versionable) {
				return false;
			}
			
			@Override
			public Version getVersion() {
				return null;
			}

			@Override
			public void updateVersion(Version version) {
			}
			
			@Override
			public boolean equals(Versionable version) {
				return false;
			}
		}) == 1);
	}
	
//	@Test
//	public void testXmlSerializationFromVersionResult() throws IOException {
//		String s = "<result value=\"1.0.0.0\" />";
//		ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes());
//		
//		Assert.assertEquals(new Version(1, 0, 0, 0),
//			XMLUtil.deserialize(in, Version.class)
//		);
//	}
}
