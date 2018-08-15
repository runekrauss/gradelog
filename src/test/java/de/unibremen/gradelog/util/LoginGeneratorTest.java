package de.unibremen.gradelog.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class LoginGeneratorTest {

	@Test(expected = IllegalArgumentException.class)
	public void testGenerateNameNull() {
		LoginGenerator.generateName(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGenerateNameEmpty() {
		LoginGenerator.generateName("", "");
	}

	@Test
	public void testGenerateNameForShortName() {
		String result = LoginGenerator.generateName("Justus", "Jonas");
		assertTrue(7 <= result.length() && result.length() <= 8);
	}

	@Test
	public void testGenerateNameForLongName() {
		String result = LoginGenerator.generateName("Bob", "Andrews");
		assertTrue(7 <= result.length() && result.length() <= 8);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGeneratePasswordMinLengthGreaterThanMaxLength() {
		LoginGenerator.generatePassword(5, 4, 1, 1, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGeneratePasswordMinLengthMaxLengthZero() {
		LoginGenerator.generatePassword(0, 0, 1, 1, 1, 1);
	}

	@Test
	public void testGeneratePasswordRange() {
		String result = LoginGenerator.generatePassword(1, 8, 1, 1, 1, 1);
		assertTrue(1 <= result.length() && result.length() <= 8);
	}

	@Test
	public void testGeneratePasswordLength() {
		String result = LoginGenerator.generatePassword(8, 8, 1, 1, 1, 1);
		assertEquals(8, result.length());
	}

	@Test
	public void testGeneratePasswordWithNegativeNumbers() {
		String result = LoginGenerator.generatePassword(8, -8, -1, 1, 1, 1);
		assertEquals(8, result.length());
	}

	@Test
	public void testGeneratePasswordContainsSpecialChars() {
		String result = LoginGenerator.generatePassword(8, 8, 1, 1, 1, 1);
		assertTrue(result.contains("@") || result.contains("#") || result.contains("$") || result.contains("%"));
	}

}
