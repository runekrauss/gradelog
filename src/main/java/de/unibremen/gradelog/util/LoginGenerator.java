package de.unibremen.gradelog.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Der LoginGenerator ist dazu in der Lage, Passwörter als auch Namen für
 * Benutzer zu generieren, wenn sie automatisch anhand einer Datei erstellt
 * werden sollen.
 * 
 * @author Rune Krauss
 */
public class LoginGenerator {

	/**
	 * Privater Konstruktor, der verhindert, dass eine Instanz dieser
	 * Utility-Klasse erzeugt werden kann.
	 */
	private LoginGenerator() {
	}

	/**
	 * Erstellt einen Namen anhand des Vor- und Nachnamen. Beispiel: (Max,
	 * Mustermann) = mmuste62 Bei langen Nachnamen > 6 wird demnach die
	 * Zeichenkette auf 5 abgeschnitten. Zusätzlich wird eine zufällige Nummer -
	 * 99 generiert.
	 * 
	 * @param firstName
	 *            Vorname
	 * @param lastName
	 *            Nachname
	 * @return Benutzername
	 */
	public static String generateName(String firstName, String lastName) {
		if (firstName == null || lastName == null)
			throw new IllegalArgumentException("The names of the parameters can not be null!");
		if (firstName.isEmpty() || lastName.isEmpty())
			throw new IllegalArgumentException("The names of the parameters can not be empty!");
		Random rnd = new Random();
		String result = Character.toString(firstName.charAt(0));
		if (lastName.length() > 5)
			result += lastName.substring(0, 5);
		else
			result += lastName;
		result += Integer.toString(rnd.nextInt(99));
		return result.toLowerCase();
	}

	/**
	 * Generiert ein spezifisches Passwort für einen Benutzer, was den
	 * Anforderungen (Klein- und Großbuchstabe, Nummer, Zahl) entspricht.
	 * 
	 * @param minLength
	 *            Minimallänge
	 * @param maxLength
	 *            Maximallänge
	 * @param minLCaseCount
	 *            Anzahl der Kleinbuchstaben
	 * @param minUCaseCount
	 *            Anzahl der Großbuchstaben
	 * @param minNumCount
	 *            Anzahl der Zahlen
	 * @param minSpecialCount
	 *            Anzahl der Sonderzeichen
	 * @return Passwort
	 */
	public static String generatePassword(int minLength, int maxLength, int minLCaseCount, int minUCaseCount,
			int minNumCount, int minSpecialCount) {
		minLength = Math.abs(minLength);
		maxLength = Math.abs(maxLength);
		minLCaseCount = Math.abs(minLCaseCount);
		minUCaseCount = Math.abs(minUCaseCount);
		minNumCount = Math.abs(minNumCount);
		minSpecialCount = Math.abs(minSpecialCount);

		if (minLength > maxLength)
			throw new IllegalArgumentException("Number of minLength may not be larger than maxlength!");
		if (minLength == 0 || maxLength == 0)
			throw new IllegalArgumentException("Number of minLength and maxlength can not be zero!");

		char[] rndStr;

		String lCaseChars = "abcdefgijkmnopqrstwxyz";
		String uCaseChars = "ABCDEFGHJKLMNPQRSTWXYZ";
		String numericChars = "23456789";
		String specialChars = "@#$%";

		Map<String, Integer> groups = new HashMap<String, Integer>();
		groups.put("lcase", minLCaseCount);
		groups.put("ucase", minUCaseCount);
		groups.put("number", minNumCount);
		groups.put("special", minSpecialCount);

		byte[] rndB = new byte[4];
		new Random().nextBytes(rndB);
		int seed = (rndB[0] & 0x7f) << 24 | rndB[1] << 16 | rndB[2] << 8 | rndB[3];

		Random rnd = new Random(seed);
		int rndIndex = -1;
		if (minLength < maxLength) {
			rndIndex = rnd.nextInt((maxLength - minLength) + 1) + minLength;
			rndStr = new char[rndIndex];
		} else {
			rndStr = new char[minLength];
		}

		int charsLeft = minLCaseCount + minUCaseCount + minNumCount + minSpecialCount;

		for (int i = 0; i < rndStr.length; i++) {
			String selChars = "";
			if (charsLeft < rndStr.length - i) {
				selChars = lCaseChars + uCaseChars + numericChars + specialChars;
			} else {
				for (Map.Entry<String, Integer> charGroup : groups.entrySet()) {
					if ((int) charGroup.getValue() > 0) {
						if ("lcase".equals(charGroup.getKey())) {
							selChars += lCaseChars;
						} else if ("ucase".equals(charGroup.getKey())) {
							selChars += uCaseChars;
						} else if ("number".equals(charGroup.getKey())) {
							selChars += numericChars;
						} else if ("special".equals(charGroup.getKey())) {
							selChars += specialChars;
						}
					}
				}
			}

			rndIndex = rnd.nextInt((selChars.length()) - 1);
			char nextChar = selChars.charAt(rndIndex);
			rndStr[i] = nextChar;
			if (lCaseChars.indexOf(nextChar) > -1) {
				groups.put("lcase", groups.get("lcase") - 1);
				if (groups.get("lcase") >= 0) {
					charsLeft--;
				}
			} else if (uCaseChars.indexOf(nextChar) > -1) {
				groups.put("ucase", groups.get("ucase") - 1);
				if (groups.get("ucase") >= 0) {
					charsLeft--;
				}
			} else if (numericChars.indexOf(nextChar) > -1) {
				groups.put("number", groups.get("number") - 1);
				if (groups.get("number") >= 0) {
					charsLeft--;
				}
			} else if (specialChars.indexOf(nextChar) > -1) {
				groups.put("special", groups.get("special") - 1);
				if (groups.get("special") >= 0) {
					charsLeft--;
				}
			}
		}
		return new String(rndStr);
	}
}
