package de.unibremen.gradelog.exception;

/**
 * Diese Exception signalisiert, dass ein zu speichernder Seitenname bereits
 * verwendet wird.
 */
public class DuplicatePageNameException extends DuplicateUniqueFieldException {

	/**
	 * Die eindeutige SerialisierungsID.
	 */
	private static final long serialVersionUID = 6886942863133075046L;

	/**
	 * Erzeugt eine neue {@link DuplicatePageNameException} mit der gegebenen
	 * Fehlernachricht.
	 * 
	 * @param theMessage
	 *            Die Fehlernachricht der zu erzeugenden Exception.
	 */
	public DuplicatePageNameException(final String theMessage) {
		super(theMessage);
	}

}
