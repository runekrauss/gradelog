package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.*;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link TimetableEntry}.
 * 
 * @author Marco Glander
 * @author Mirco Bockholt
 */
@Stateless
public class TimetableEntryDAO extends JPADAO<TimetableEntry> {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = -4377695623377043838L;

	/**
	 * Fügt {@code timetableEntry} dem Datenbestand hinzu. Falls {@code timetableEntry}
	 * bereits im Datenbestand vorhanden ist (vgl.
	 * {@link JPADAO#create(JPAEntity)}, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param timetableEntry
	 *            Das zu speichernde {@link TimetableEntry}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code timetableEntry == null} oder {@code timetableEntry} kein
	 *             durch JPA verwaltetes Objekt ist.
	 */
	public synchronized void create(TimetableEntry timetableEntry) {
		assertNotNull(timetableEntry);
		try {
			super.create(timetableEntry);
		} catch (final DuplicateUniqueFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Aktualisiert den Eintrag von {@code timetableEntry} im Datenbestand. Falls
	 * {@code timetableEntry} noch nicht im Datenbestand vorhanden ist, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param timetableEntry
	 *            Das zu speichernde {@link TimetableEntry}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code timetableEntry == null} oder {@code timetableEntry} kein durch JPA
	 *             verwaltetes Objekt ist.
	 */
	public synchronized void update(TimetableEntry timetableEntry) {
		assertNotNull(timetableEntry);
		try {
			super.update(timetableEntry);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Löscht ein TimetableEntry-Objekt aus dem Datenbestand.
	 */
	public synchronized void delete(TimetableEntry timetableEntry) {
		super.delete(timetableEntry);
	}

	public Class<TimetableEntry> getClazz() {
		return TimetableEntry.class;
	}

	public TimetableEntry getById(int id) {
		EntityManager em = getEntityManager();
		return em.find(TimetableEntry.class, id);
	}
}
