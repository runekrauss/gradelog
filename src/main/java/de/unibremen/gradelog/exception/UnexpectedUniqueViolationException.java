package de.unibremen.gradelog.exception;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

/**
 * Diese Exception signalisiert, dass eine Methode unerwarteterweise eine
 * {@link DuplicateUniqueFieldException} geworfen bzw. gefangen hat.
 */
public class UnexpectedUniqueViolationException extends RuntimeException {

    /**
     * Die eindeutige SerialisierungsID.
     */
    private static final long serialVersionUID = -6097618092016308323L;

    /**
     * Erzeugt eine neue {@link UnexpectedUniqueViolationException} mit der gegebenen
     * Ursache {@code theCause}.
     *
     * @param theCause
     *            Die Ursache der zu erzeugenden Exception.
     * @throws IllegalArgumentException
     *             Falls {@code theCause == null}.
     */
    public UnexpectedUniqueViolationException(final DuplicateUniqueFieldException theCause) {
        super(assertNotNull(theCause));
    }

}
