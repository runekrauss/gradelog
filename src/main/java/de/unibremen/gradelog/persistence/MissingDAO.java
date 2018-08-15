package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.*;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link Missing}.
 * 
 * @author Marco Glander
 * @author Mirco Bockholt
 */
@Stateless
public class MissingDAO extends JPADAO<Missing> {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = 3343984157198483629L;

	/**
	 * Fügt {@code missing} dem Datenbestand hinzu. Falls {@code missing}
	 * bereits im Datenbestand vorhanden ist (vgl.
	 * {@link JPADAO#create(JPAEntity)}, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param missing
	 *            Das zu speichernde {@link Missing}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code missing == null} oder {@code missing} kein durch
	 *             JPA verwaltetes Objekt ist.
	 */
	public synchronized void create(Missing missing) {
		assertNotNull(missing);
		try {
			super.create(missing);
		} catch (final DuplicateUniqueFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Aktualisiert den Eintrag von {@code missing} im Datenbestand. Falls
	 * {@code missing} noch nicht im Datenbestand vorhanden ist, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param missing
	 *            Das zu speichernde {@link Missing}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code missing == null} oder {@code missing} kein durch
	 *             JPA verwaltetes Objekt ist.
	 */
	public synchronized void update(Missing missing) {
		assertNotNull(missing);
		try {
			super.update(missing);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Löscht ein Missing-Objekt aus dem Datenbestand.
	 */
	public synchronized void delete(Missing missing) {
		super.delete(missing);
	}

	public Class<Missing> getClazz() {
		return Missing.class;
	}

	public Missing getById(int id) {
		EntityManager em = getEntityManager();
		return em.find(Missing.class, id);
	}
}
