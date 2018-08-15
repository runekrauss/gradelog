package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.*;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link Calendar}.
 * 
 * @author Rune Krauss
 * @author Marco Glander
 */
@Stateless
public class CalendarDAO extends JPADAO<Calendar> {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 5186096820182568832L;

	/**
	 * Fügt {@code calendar} dem Datenbestand hinzu. Falls {@code calendar}
	 * bereits im Datenbestand vorhanden ist (vgl.
	 * {@link JPADAO#create(JPAEntity)}, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param calendar
	 *            Das zu speichernde {@link Calendar}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code calendar == null} oder {@code calendar} kein
	 *             durch JPA verwaltetes Objekt ist.
	 */
	public synchronized void create(Calendar calendar) {
		assertNotNull(calendar);
		try {
			super.create(calendar);
		} catch (final DuplicateUniqueFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Aktualisiert den Eintrag von {@code calendar} im Datenbestand. Falls
	 * {@code calendar} noch nicht im Datenbestand vorhanden ist, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param calendar
	 *            Das zu speichernde {@link Calendar}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code calendar == null} oder {@code calendar} kein
	 *             durch JPA verwaltetes Objekt ist.
	 */
	public synchronized void update(Calendar calendar) {
		assertNotNull(calendar);
		try {
			super.update(calendar);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Löscht ein Calendar-Objekt aus dem Datenbestand.
	 */
	public synchronized void delete(Calendar object) {
		super.delete(object);
	}

	public Class<Calendar> getClazz() {
		return Calendar.class;
	}

	public Calendar getById(int id) {
		EntityManager em = getEntityManager();
		return em.find(Calendar.class, id);
	}
}
