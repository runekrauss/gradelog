package de.unibremen.gradelog.util;

/**
 * Diese Klasse stellt Komfortmethoden zur Validierung von Parametern zur
 * Verfügung.
 * 
 * @author Rune Krauss
 * @author Marco Glander
 * @author Steffen Gerken
 * @author Christopher Wojtkow
 * 
 */
public final class Assertion {

	/**
	 * Repräsentation eines Prüfungsmodus für die Validität von Zeichenketten.
	 */
	private static enum StringValidationMode {

		/**
		 * In diesem Modus ist eine Zeichenkette valide, wenn sie ungleich
		 * {@code null} ist.
		 */
		NOT_NULL("element is null") {
			@Override
			String valid(final String toBeChecked, final String exceptionMessage) {
				return assertNotNull(toBeChecked, exceptionMessage);
			}
		},

		/**
		 * In diesem Modus ist eine Zeichenkette valide, wenn sie ungleich
		 * {@code null} ist und nicht nur Zwischenraumzeichen beinhaltet.
		 */
		NOT_NULL_NOR_EMPTY("element is null or empty string") {
			@Override
			String valid(final String toBeChecked, final String exceptionMessage) {
				NOT_NULL.valid(toBeChecked, exceptionMessage);
				if (toBeChecked.trim().isEmpty()) {
					final String actualExceptionMessage = exceptionMessage == null ? PARAM_EMPTY_STRING
							: exceptionMessage;
					throw new IllegalArgumentException(actualExceptionMessage);
				}
				return toBeChecked;
			}
		};

		private final String errorDescription;

		private StringValidationMode(final String theErrorDescription) {
			assert theErrorDescription != null;
			errorDescription = theErrorDescription;
		}

		String getErrorDescription() {
			return errorDescription;
		}

		/**
		 * Prüft die gegebene Zeichenkette auf Validität entsprechend des Modus.
		 * Ist die Prüfung erfolgreich, wird die gegebene Zeichenkette
		 * zurückgegeben, ansonsten wird eine {@link IllegalArgumentException}
		 * geworfen.
		 *
		 * @param toBeChecked
		 *            Zu prüfende Zeichenkette.
		 * @return die gegebene (und geprüfte) Zeichenkette.
		 * @throws IllegalArgumentException
		 *             Wenn die gegebene Zeichenkette in diesem Modus nicht
		 *             valide ist.
		 */
		abstract String valid(final String toBeChecked, final String exceptionMessage);

	}

	/**
	 * Fehlernachricht für den Fall, dass ein Parameterwert keine gültige Größe
	 * darstellt, d. h. negativ ist.
	 */
	private static final String PARAM_NEGATIVE = "The parameter value must not be negative!";

	/**
	 * Fehlernachricht für den Fall, wenn ein Parameter {@code null} ist.
	 */
	private static final String PARAM_NULL = "The parameter must not be null!";

	/**
	 * Fehlernachricht für den Fall, dass ein Parameter nicht {@code null} ist,
	 * welcher {@code null} sein sollte.
	 */
	private static final String PARAM_NOT_NULL = "The parameter must be null!";

	/**
	 * Basis der Fehlernachricht für den Fall, wenn ein Parameter eine Sammlung
	 * ist, welche ein invalides Zeichen enthält.
	 * 
	 * Die tatsächliche Fehlernachricht enthält neben der Basis die Angabe,
	 * wieso das Element invalide ist.
	 */
	private static final String PARAM_ELEMENT_INVALID = "The parameter contains invalid element:";

	/**
	 * Fehlernachricht für den für den Fall, wenn ein Zeichenketten-Parameter
	 * nur Zwischenraumzeichen beinhaltet.
	 */
	private static final String PARAM_EMPTY_STRING = "The parameter string must not be null and contain at least one non-white space character!";

	private Assertion() {
	}

	/**
	 * Prüft, ob die gegebene Ganzzahl eine valide Größe (d. h. nicht-negativ)
	 * ist. Ist die Prüfung erfolgreich, wird die Ganzzahl zurückgegeben,
	 * ansonsten wird eine {@link IllegalArgumentException} geworfen.
	 *
	 * @param parameter
	 *            die zu prüfende Ganzzahl.
	 * @param exceptionMessage
	 *            Nachricht der IllegalArgumentException
	 * @return die gegebene (und geprüfte) Ganzzahl.
	 * @throws IllegalArgumentException
	 *             wenn die gegebene Ganzzahl negativ ist.
	 */
	public static int assertNotNegative(final int parameter, final String exceptionMessage) {
		if (parameter < 0) {
			final String actualExceptionMessage = exceptionMessage == null ? PARAM_NEGATIVE : exceptionMessage;
			throw new IllegalArgumentException(actualExceptionMessage);
		}
		return parameter;
	}

	/**
	 * Prüft, ob die gegebene Ganzzahl eine valide Größe (d. h. nicht-negativ)
	 * ist. Ist die Prüfung erfolgreich, wird die Ganzzahl zurückgegeben,
	 * ansonsten wird eine {@link IllegalArgumentException} geworfen.
	 *
	 * @param parameter
	 *            die zu prüfende Ganzzahl.
	 * @return die gegebene (und geprüfte) Ganzzahl.
	 * @throws IllegalArgumentException
	 *             wenn die gegebene Ganzzahl negativ ist.
	 */
	public static int assertNotNegative(final int parameter) {
		return assertNotNegative(parameter, null);
	}

	/**
	 * Prüft, ob die gegebene Sammlung valide ist. Ist die Prüfung erfolgreich,
	 * wird die gegebene Sammlung zurückgegeben. Ansonsten wird eine
	 * {@code IllegalArgumentException} geworfen.
	 *
	 * Eine Sammlung ist valide, wenn sie nicht {@code null} ist und alle ihre
	 * Elemente valide sind.
	 *
	 * Eine Zeichenkette ist valide, sofern sie die Validitätsprüfung des
	 * gegebenen Zeichenketten-Valididitätsmodus {@code stringValidationMode}
	 * besteht.
	 *
	 * Jedes andere Objekt ist valide, wenn es nicht {@code null} ist.
	 *
	 * @param <T>
	 *            Typ der Elemente der zu prüfenden Sammlung.
	 * @param <U>
	 *            Typ der zu prüfenden Sammlung.
	 * @param parameter
	 *            die zu prüfende Sammlung.
	 * @param stringValidationMode
	 *            gibt an, wie Zeichenketten validiert werden sollen.
	 * @param exceptionMessage
	 *            Nachricht der IllegalArgumentException
	 * @return die gegebene (und geprüfte) Sammlung.
	 * @throws IllegalArgumentException
	 *             wenn die gegebene Sammlung nicht valide ist (vgl. oben).
	 */
	private static <T, U extends Iterable<T>> U valid(final U parameter,
			final StringValidationMode stringValidationMode, final String exceptionMessage) {
		assertNotNull(parameter, exceptionMessage);
		for (final T element : parameter) {
			final String actualExceptionMessage = exceptionMessage == null
					? PARAM_ELEMENT_INVALID + " " + stringValidationMode.getErrorDescription() : exceptionMessage;
			valid(element, stringValidationMode, actualExceptionMessage);
		}
		return parameter;
	}

	/**
	 * Prüft, ob die gegebene Sammlung valide ist. Ist die Prüfung erfolgreich,
	 * wird die gegebene Sammlung zurückgegeben. Ansonsten wird eine
	 * {@code IllegalArgumentException} geworfen.
	 *
	 * Zum Begriff der Validität siehe
	 * {@link Assertion#valid(Iterable, StringValidationMode, String)}
	 *
	 * @param <T>
	 *            Typ der Elemente der zu prüfenden Sammlung.
	 * @param parameter
	 *            die zu prüfende Sammlung.
	 * @param stringValidationMode
	 *            gibt an, wie Zeichenketten validiert werden sollen.
	 * @param exceptionMessage
	 *            Nachricht der IllegalArgumentException
	 * @return die gegebene (und geprüfte) Sammlung.
	 * @throws IllegalArgumentException
	 *             wenn die gegebene Sammlung nicht valide ist (vgl. oben).
	 * @see Assertion#valid(Iterable, StringValidationMode, String)
	 */
	private static <T> T[] valid(final T[] parameter, final StringValidationMode stringValidationMode,
			final String exceptionMessage) {
		assertNotNull(parameter, exceptionMessage);
		for (final T t : parameter) {
			final String actualExceptionMessage = exceptionMessage == null
					? PARAM_ELEMENT_INVALID + " " + stringValidationMode.getErrorDescription() : exceptionMessage;
			valid(t, stringValidationMode, actualExceptionMessage);
		}
		return parameter;
	}

	/**
	 * Prüft, ob die gegebene Zeichenkette valide ist. Ist die Prüfung
	 * erfolgreich, wird die gegebene Zeichenkette zurückgegeben. Ansonsten wird
	 * eine {@code IllegalArgumentException} geworfen.
	 *
	 * Zum Begriff der Validität siehe
	 * {@link Assertion#valid(Iterable, StringValidationMode, String)}
	 *
	 * @param parameter
	 *            die zu prüfende Zeichenkette.
	 * @param stringValidationMode
	 *            gibt an, wie Zeichenketten validiert werden sollen.
	 * @param exceptionMessage
	 *            Nachricht der IllegalArgumentException
	 * @return die gegebene (und geprüfte) Zeichenkette.
	 * @throws IllegalArgumentException
	 *             wenn die gegebene Zeichenkette nicht valide ist (vgl. oben).
	 * @see Assertion#valid(Iterable, StringValidationMode, String)
	 */
	private static String valid(final String parameter, final StringValidationMode stringValidationMode,
			final String exceptionMessage) {
		return stringValidationMode.valid(parameter, exceptionMessage);
	}

	/**
	 * Prüft, ob das gegebene Objekt valide ist. Ist die Prüfung erfolgreich,
	 * wird das gegebene Objekt zurückgegeben. Ansonsten wird eine
	 * {@code IllegalArgumentException} geworfen.
	 *
	 * Zum Begriff der Validität siehe
	 * {@link Assertion#valid(Iterable, StringValidationMode, String)}
	 *
	 * @param <T>
	 *            Typ des zu prüfenden Objekts.
	 * @param parameter
	 *            das zu prüfende Objekt.
	 * @param stringValidationMode
	 *            gibt an, wie Zeichenketten validiert werden sollen.
	 * @param exceptionMessage
	 *            Nachricht der IllegalArgumentException
	 * @return das gegebene (und geprüfte) Objekt.
	 * @throws IllegalArgumentException
	 *             wenn das gegebene Objekt nicht valide ist (vgl. oben).
	 * @see Assertion#valid(Iterable, StringValidationMode, String)
	 */
	private static <T> T valid(final T parameter, final StringValidationMode stringValidationMode,
			final String exceptionMessage) {
		assertNotNull(parameter, exceptionMessage);
		if (parameter instanceof Iterable) {
			valid((Iterable<?>) parameter, stringValidationMode, exceptionMessage);
		}
		if (parameter instanceof Object[]) {
			valid((Object[]) parameter, stringValidationMode, exceptionMessage);
		}
		if (parameter instanceof String) {
			valid((String) parameter, stringValidationMode, exceptionMessage);
		}
		return parameter;
	}

	/**
	 * Prüft, ob das gegebene Objekt ungleich {@code null} ist; eine rekursive
	 * Prüfung findet nicht statt. Ist die Prüfung erfolgreich, wird der
	 * Parameter zurückgegeben, ansonsten wird eine
	 * {@link IllegalArgumentException} geworfen.
	 *
	 * @param <T>
	 *            Typ des zu prüfenden Objekts.
	 * @param parameter
	 *            das zu prüfende Objekt.
	 * @param exceptionMessage
	 *            Nachricht der IllegalArgumentException
	 * @return das gegebene (und geprüfte) Objekt.
	 * @throws IllegalArgumentException
	 *             wenn das gegebene Objekt {@code null} ist.
	 */
	public static <T> T assertNotNull(final T parameter, final String exceptionMessage) {
		if (parameter == null) {
			final String actualExceptionMessage = exceptionMessage == null ? PARAM_NULL : exceptionMessage;
			throw new IllegalArgumentException(actualExceptionMessage);
		}
		return parameter;
	}

	/**
	 * Prüft, ob das gegebene Objekt ungleich {@code null} ist; eine rekursive
	 * Prüfung findet nicht statt. Ist die Prüfung erfolgreich, wird der
	 * Parameter zurückgegeben, ansonsten wird eine
	 * {@link IllegalArgumentException} geworfen.
	 *
	 * @param <T>
	 *            Typ des zu prüfenden Objekts.
	 * @param parameter
	 *            das zu prüfende Objekt.
	 * @return das gegebene (und geprüfte) Objekt.
	 * @throws IllegalArgumentException
	 *             wenn das gegebene Objekt {@code null} ist.
	 */
	public static <T> T assertNotNull(final T parameter) {
		return assertNotNull(parameter, null);
	}

	/**
	 * Prüft, ob das gegebene Objekt gleich {@code null} ist; eine rekursive
	 * Prüfung findet prinzipbedingt nicht statt. Ist die Prüfung erfolgreich,
	 * wird {@code null} zurückgegeben, ansonsten wird eine
	 * {@link IllegalArgumentException} geworfen.
	 *
	 * @param <T>
	 *            Typ des zu prüfenden Objekts.
	 * @param parameter
	 *            das zu prüfende Objekt.
	 * @param exceptionMessage
	 *            Nachricht der IllegalArgumentException
	 * @return {@code null}.
	 * @throws IllegalArgumentException
	 *             wenn das gegebene Objekt ungleich {@code null} ist.
	 */
	public static <T> T assertNull(final T parameter, final String exceptionMessage) {
		if (parameter != null) {
			final String actualExceptionMessage = exceptionMessage == null ? PARAM_NOT_NULL : exceptionMessage;
			throw new IllegalArgumentException(actualExceptionMessage);
		}
		return null;
	}

	/**
	 * Prüft, ob das gegebene Objekt gleich {@code null} ist; eine rekursive
	 * Prüfung findet prinzipbedingt nicht statt. Ist die Prüfung erfolgreich,
	 * wird {@code null} zurückgegeben, ansonsten wird eine
	 * {@link IllegalArgumentException} geworfen.
	 *
	 * @param <T>
	 *            Typ des zu prüfenden Objekts.
	 * @param parameter
	 *            das zu prüfende Objekt.
	 * @return {@code null}.
	 * @throws IllegalArgumentException
	 *             wenn das gegebene Objekt ungleich {@code null} ist.
	 */
	public static <T> T assertNull(final T parameter) {
		return assertNull(parameter, null);
	}

	/**
	 * Prüft, ob die gegebenene Zeichenkette ungleich {@code null} und keine nur
	 * aus Zwischenraumzeichen bestehende Zeichenkette ist. Ist die Prüfung
	 * erfolgreich, wird die gegebene Zeichenkette zurückgegeben, ansonsten wird
	 * eine {@link IllegalArgumentException} geworfen.
	 *
	 * @param parameter
	 *            die zu prüfende Zeichenkette.
	 * @param exceptionMessage
	 *            Nachricht der IllegalArgumentException
	 * @return die gegebene (und geprüfte) Zeichenkette.
	 * @throws IllegalArgumentException
	 *             wenn die gegebene Zeichenkette {@code null} ist oder eine nur
	 *             Zwischenraumzeichen beinhaltende Zeichenkette ist.
	 */
	public static String assertNotEmpty(final String parameter, final String exceptionMessage) {
		return valid(parameter, StringValidationMode.NOT_NULL_NOR_EMPTY, exceptionMessage);
	}

	/**
	 * Prüft, ob die gegebenene Zeichenkette ungleich {@code null} und keine nur
	 * aus Zwischenraumzeichen bestehende Zeichenkette ist. Ist die Prüfung
	 * erfolgreich, wird die gegebene Zeichenkette zurückgegeben, ansonsten wird
	 * eine {@link IllegalArgumentException} geworfen.
	 *
	 * @param parameter
	 *            die zu prüfende Zeichenkette.
	 * @return die gegebene (und geprüfte) Zeichenkette.
	 * @throws IllegalArgumentException
	 *             wenn die gegebene Zeichenkette {@code null} ist oder eine nur
	 *             Zwischenraumzeichen beinhaltende Zeichenkette ist.
	 */
	public static String assertNotEmpty(final String parameter) {
		return assertNotEmpty(parameter, null);
	}

	/**
	 * Prüft, ob das gegebene Objekt ungleich {@code null} ist und (rekursiv)
	 * kein {@code null} enthält (sofern eine Sammlung vorliegt). Ist die
	 * Prüfung erfolgreich, wird der Parameter zurückgegeben, ansonsten wird
	 * eine {@link IllegalArgumentException} geworfen.
	 *
	 * @param <T>
	 *            Typ des zu prüfenden Objekts.
	 * @param parameter
	 *            das zu prüfende Objekt.@param exceptionMessage Nachricht der
	 *            IllegalArgumentException
	 * @return das gegebene (und geprüfte) Objekt.
	 * @throws IllegalArgumentException
	 *             wenn das gegebene Objekt {@code null} ist oder eine Sammlung
	 *             ist, welche {@code null} (rekursiv) enthält.
	 */
	public static <T> T assertWithoutNull(final T parameter, final String exceptionMessage) {
		return valid(parameter, StringValidationMode.NOT_NULL, exceptionMessage);
	}

	/**
	 * Prüft, ob das gegebene Objekt ungleich {@code null} ist und (rekursiv)
	 * kein {@code null} enthält (sofern eine Sammlung vorliegt). Ist die
	 * Prüfung erfolgreich, wird der Parameter zurückgegeben, ansonsten wird
	 * eine {@link IllegalArgumentException} geworfen.
	 *
	 * @param <T>
	 *            Typ des zu prüfenden Objekts.
	 * @param parameter
	 *            das zu prüfende Objekt.
	 * @return das gegebene (und geprüfte) Objekt.
	 * @throws IllegalArgumentException
	 *             wenn das gegebene Objekt {@code null} ist oder eine Sammlung
	 *             ist, welche {@code null} (rekursiv) enthält.
	 */
	public static <T> T assertWithoutNull(final T parameter) {
		return assertWithoutNull(parameter, null);
	}

	/**
	 * Prüft, ob der gegebenen Parameter ungleich {@code null} und keine nur aus
	 * Zwischenraumzeichen bestehende Zeichenkette ist, sowie keine Sammlung
	 * ist, welche {@code null} oder eine nur aus Zwischenraumzeichen bestehende
	 * Zeichenkette (rekursiv) enthält. Ist die Prüfung erfolgreich, wird das
	 * gegebene Objekt zurückgegeben, ansonsten wird eine
	 * {@link IllegalArgumentException} geworfen.
	 *
	 * @param <T>
	 *            Typ des zu prüfenden Objekts.
	 * @param parameter
	 *            das zu prüfende Objekt.@param exceptionMessage Nachricht der
	 *            IllegalArgumentException
	 * @return das gegebene (und geprüfte) Objekt.
	 * @throws IllegalArgumentException
	 *             wenn das gegebene Objekt {@code null} ist oder eine nur
	 *             Zwischenraumzeichen beinhaltende Zeichenkette ist oder eine
	 *             Sammlung ist, welche {@code null} oder eine nur aus
	 *             Zwischenraumzeichen bestehende Zeichenkette (rekursiv)
	 *             enthält.
	 */
	public static <T> T assertWithoutEmpty(final T parameter, final String exceptionMessage) {
		return valid(parameter, StringValidationMode.NOT_NULL_NOR_EMPTY, exceptionMessage);
	}

	/**
	 * Prüft, ob der gegebenen Parameter ungleich {@code null} und keine nur aus
	 * Zwischenraumzeichen bestehende Zeichenkette ist, sowie keine Sammlung
	 * ist, welche {@code null} oder eine nur aus Zwischenraumzeichen bestehende
	 * Zeichenkette (rekursiv) enthält. Ist die Prüfung erfolgreich, wird das
	 * gegebene Objekt zurückgegeben, ansonsten wird eine
	 * {@link IllegalArgumentException} geworfen.
	 *
	 * @param <T>
	 *            Typ des zu prüfenden Objekts.
	 * @param parameter
	 *            das zu prüfende Objekt.
	 * @return das gegebene (und geprüfte) Objekt.
	 * @throws IllegalArgumentException
	 *             wenn das gegebene Objekt {@code null} ist oder eine nur
	 *             Zwischenraumzeichen beinhaltende Zeichenkette ist oder eine
	 *             Sammlung ist, welche {@code null} oder eine nur aus
	 *             Zwischenraumzeichen bestehende Zeichenkette (rekursiv)
	 *             enthält.
	 */
	public static <T> T assertWithoutEmpty(final T parameter) {
		return assertWithoutEmpty(parameter, null);
	}

}
