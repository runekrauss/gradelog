package de.unibremen.gradelog.persistence;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;

import java.io.Serializable;

/**
 * Das Basisinterface aller DAOs. Ein DAO ermöglicht das Speichern, Laden und
 * Löschen von Daten und wird auch 'CRUD service' genannt. Das konkret zu
 * verarbeitende Datum wird über den Typparameter {@link T} definiert. Diese
 * Klasse macht keine Annahme darüber, wie der Datenbestand konkret realisiert
 * wird.
 *
 * @param <T>
 *            Das zu verwaltene Datum.
 * 
 * @author Rune Krauss
 *
 */
public interface GenericDAO<T> extends Serializable {

	/**
	 * Fügt {@code theT} dem Datenbestand hinzu.
	 *
	 * @param object
	 *            Das zu speichernde Objekt.
	 * @throws DuplicateUniqueFieldException
	 *             Falls durch das Speichern von {@code theT} ein als `unique`
	 *             deklariertes Attribut doppelt vorkommen würde.
	 * @throws IllegalArgumentException
	 *             Falls {@code theT == null} oder ein sonstiger, vom
	 *             Datenbestand abhängiger Fehler aufgetreten ist.
	 */
	void create(final T object) throws DuplicateUniqueFieldException;

	/**
	 * Entfernt {@code theT} aus dem Datenbestand.
	 *
	 * @param object
	 *            Das zu entfernende Objekt.
	 * @throws IllegalArgumentException
	 *             Falls {@code theT == null} oder ein sonstiger, vom
	 *             Datenbestand abhängiger Fehler aufgetreten ist.
	 */
	void delete(final T object);

	/**
	 * Aktualisiert einen bereits bestehenden Datensatz
	 * 
	 * @param object
	 *            Das zu aktualisierende Model
	 */
	void update(T object) throws DuplicateUniqueFieldException;

}
