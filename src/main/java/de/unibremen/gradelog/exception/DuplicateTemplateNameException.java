package de.unibremen.gradelog.exception;

/**
 * Diese Exception signalisiert, dass ein zu speichernder Templatename bereits
 * verwendet wird.
 */
public class DuplicateTemplateNameException extends DuplicateUniqueFieldException {

	/**
	 * Die eindeutige SerialisierungsID.
	 */
	private static final long serialVersionUID = 6886942863133075046L;

	/**
	 * Erzeugt eine neue {@link DuplicateTemplateNameException} mit der
	 * gegebenen Fehlernachricht.
	 * 
	 * @param theMessage
	 *            Die Fehlernachricht der zu erzeugenden Exception.
	 */
	public DuplicateTemplateNameException(final String theMessage) {
		super(theMessage);
	}

}
