package de.unibremen.gradelog.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.unibremen.gradelog.util.Crypt;

/**
 * Testet Methoden in der Klasse {@link Crypt} als Komponententests.
 */
public class CryptTest {

	/**
	 * Testet die erwartete Exception falls der Parameterwert {@code null} ist.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testHashNull() {
		Crypt.hash(null);
	}

	/**
	 * Testet den erwarteten SHA512-Hash für den leeren String.
	 */
	@Test
	public void testHashEmpty() {
		final String expected = "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e";
		final String result = Crypt.hash("");
		assertEquals(expected, result);
	}

	/**
	 * Testet den erwarteten SHA512-Hash für den Buchstaben a.
	 */
	@Test
	public void testHashCharacter() {
		final String expected = "1f40fc92da241694750979ee6cf582f2d5d7d28e18335de05abc54d0560e0f5302860c652bf08d560252aa5e74210546f369fbbbce8c12cfc7957b2652fe9a75";
		final String result = Crypt.hash("a");
		assertEquals(expected, result);
	}

	/**
	 * Testet den erwarteten SHA512-Hash für eine längere Zeichenkette.
	 */
	@Test
	public void testHashArbitratyString() {
		final String expected = "c6ee9e33cf5c6715a1d148fd73f7318884b41adcb916021e2bc0e800a5c5dd97f5142178f6ae88c8fdd98e1afb0ce4c8d2c54b5f37b30b7da1997bb33b0b8a31";
		final String result = Crypt.hash("Test");
		assertEquals(expected, result);
	}

}
