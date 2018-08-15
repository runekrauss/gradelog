package de.unibremen.gradelog.exception;

/**
 * Diese Exception signalisiert, dass ein zu speichernder Benutzername bereits verwendet
 * wird.
 */
public class DuplicateUsernameException extends DuplicateUniqueFieldException {

    /**
     * Die eindeutige SerialisierungsID.
     */
    private static final long serialVersionUID = 6886942863133075046L;

    /**
     * Erzeugt eine neue {@link DuplicateUsernameException} mit der gegebenen
     * Fehlernachricht.
     * 
     * @param theMessage
     *            Die Fehlernachricht der zu erzeugenden Exception.
     */
    public DuplicateUsernameException(final String theMessage) {
        super(theMessage);
    }

}
