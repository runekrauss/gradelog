package de.unibremen.gradelog.exception;

/**
 * Diese Exception signalisiert, dass das Speichern eines Datums (vgl.
 * {@link de.unibremen.gradelog.persistence.GenericDAO#save(Object)}) dazu führen würde,
 * dass ein als `unique` deklariertes Attribut doppelt vorkommen würde.
 */
public class DuplicateUniqueFieldException extends Exception {

    /**
     * Die eindeutige SerialisierungsID.
     */
    private static final long serialVersionUID = 5908905036654024577L;


	/**
     * Erzeugt eine neue {@link DuplicateUniqueFieldException} mit der gegebenen
     * Fehlernachricht.
     *
     * @param theMessage
     *            Die Fehlernachricht der zu erzeugenden Exception.
     */
    public DuplicateUniqueFieldException(final String theMessage) {
        super(theMessage);
    }

}
