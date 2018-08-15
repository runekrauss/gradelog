package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.*;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link File}.
 * 
 * @author Marco Glander
 */
@Stateless
public class FileDAO extends JPADAO<File> {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = -5777442291049795837L;

	/**
	 * Fügt {@code file} dem Datenbestand hinzu. Falls {@code file} bereits im
	 * Datenbestand vorhanden ist (vgl. {@link JPADAO#create(JPAEntity)}, wird
	 * eine {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param file
	 *            Das zu speichernde {@link File}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code file == null} oder {@code file} kein durch JPA
	 *             verwaltetes Objekt ist.
	 */
	public synchronized void create(File file) {
		assertNotNull(file);
		try {
			super.create(file);
		} catch (final DuplicateUniqueFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Aktualisiert den Eintrag von {@code file} im Datenbestand. Falls
	 * {@code file} noch nicht im Datenbestand vorhanden ist, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param file
	 *            Das zu speichernde {@link File}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code file == null} oder {@code file} kein durch JPA
	 *             verwaltetes Objekt ist.
	 */
	public synchronized void update(File file) {
		assertNotNull(file);
		try {
			super.update(file);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Löscht ein File-Objekt aus dem Datenbestand.
	 */
	public synchronized void delete(File file) {
		super.delete(file);
	}

	public Class<File> getClazz() {
		return File.class;
	}

	public File getById(int id) {
		EntityManager em = getEntityManager();
		return em.find(File.class, id);
	}
}
