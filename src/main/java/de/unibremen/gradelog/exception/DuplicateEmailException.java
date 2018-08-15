package de.unibremen.gradelog.exception;

/**
 * Diese Exception signalisiert, dass eine zu speichernde E-Mail-Adresse bereits verwendet
 * wird.
 */
public class DuplicateEmailException extends DuplicateUniqueFieldException {

    /**
     * Die eindeutige SerialisierungsID.
     */
    private static final long serialVersionUID = -5511592181641751315L;

    /**
     * Erzeugt eine neue {@link DuplicateEmailException} mit der gegebenen
     * Fehlernachricht.
     *
     * @param theMessage
     *            Die Fehlernachricht der zu erzeugenden Exception.
     */
    public DuplicateEmailException(final String theMessage) {
        super(theMessage);
    }

}
