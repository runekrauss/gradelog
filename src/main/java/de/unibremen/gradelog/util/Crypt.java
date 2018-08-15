package de.unibremen.gradelog.util;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

/**
 * Diese Klasse stellt Kryptofunktionalität bereit.
 * 
 * @author Rune Krauss
 */
public class Crypt {

	/**
	 * Der Logger für diese Klasse.
	 */
	private static final Logger logger = Logger.getLogger(Crypt.class);

	/**
	 * Privater Konstruktor, der verhindert, dass eine Instanz dieser
	 * Utility-Klasse erzeugt werden kann.
	 */
	private Crypt() {
	}

	/**
	 * Konvertiert einen Byte-Strom in die hexadezimale Form.
	 *
	 * @param data
	 *            Der Byte-Strom.
	 * @return Hexadezimalform.
	 */
	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	/**
	 * Erzeugt einen SHA512-Hash für die gegebene Zeichenkette und gibt diesen
	 * zurück.
	 *
	 * @param input
	 *            Die Zeichenkette, für die der MD5-Hash erstellt werden soll.
	 * @return SHA512-Hash für die gegebene Zeichenkette.
	 * @throws IllegalArgumentException
	 *             Falls das gegebene Passwort den Wert {@code null} hat.
	 * @throws UnsupportedOperationException
	 *             falls das System nicht über die Voraussetzungen verfügt, das
	 *             Hashen durchzuführen. Die
	 *             {@code UnsupportedOperationException} kapselt eine Exception,
	 *             die den Grund näher beschreibt: eine
	 *             {@code NoSuchAlgorithmException}, falls es keinen Security
	 *             Provider für den Algorithmus {@code SHA512} im System gibt;
	 *             eine {@code UnsupportedEncodingException}, falls der
	 *             Zeichensatz {@code UTF-8} nicht unterstützt wird.
	 */
	public static String hash(String text) {
		if (text == null) {
			throw new IllegalArgumentException("The password can not be null.");
		}
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("SHA-512");
			byte[] sha1hash = new byte[40];
			md.update(assertNotNull(text.getBytes("UTF-8")), 0, text.length());
			sha1hash = md.digest();
			return convertToHex(sha1hash);
		} catch (final NoSuchAlgorithmException | UnsupportedEncodingException e) {
			logger.error("Error while hashing the password.", e);
			throw new UnsupportedOperationException(e);
		}
	}

}
