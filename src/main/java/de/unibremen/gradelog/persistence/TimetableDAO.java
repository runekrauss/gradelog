package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.JPAEntity;
import de.unibremen.gradelog.model.Timetable;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link Timetable}.
 * 
 * @author Marco Glander
 * @author Mirco Bockholt
 */
@Stateless
public class TimetableDAO extends JPADAO<Timetable> {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = 6249793970991285651L;

	/**
	 * Fügt {@code timetable} dem Datenbestand hinzu. Falls {@code timetable}
	 * bereits im Datenbestand vorhanden ist (vgl.
	 * {@link JPADAO#create(JPAEntity)}, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param timetable
	 *            Das zu speichernde {@link Timetable}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code timetable == null} oder {@code timetable} kein
	 *             durch JPA verwaltetes Objekt ist.
	 */
	public synchronized void create(Timetable timetable) {
		assertNotNull(timetable);
		try {
			super.create(timetable);
		} catch (final DuplicateUniqueFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Aktualisiert den Eintrag von {@code timetable} im Datenbestand. Falls
	 * {@code timetable} noch nicht im Datenbestand vorhanden ist, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param timetable
	 *            Das zu speichernde {@link Timetable}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code timetable == null} oder {@code timetable} kein
	 *             durch JPA verwaltetes Objekt ist.
	 */
	public synchronized void update(Timetable timetable) {
		assertNotNull(timetable);
		try {
			super.update(timetable);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Löscht ein Timetable-Objekt aus dem Datenbestand.
	 */
	public synchronized void delete(Timetable timetable) {
		super.delete(timetable);
	}

	public Class<Timetable> getClazz() {
		return Timetable.class;
	}

	public Timetable getById(int id) {
		EntityManager em = getEntityManager();
		return em.find(Timetable.class, id);
	}
}
