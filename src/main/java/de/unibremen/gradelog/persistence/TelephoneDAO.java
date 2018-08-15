package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.*;

/**
 * Zuständig für die Persistierung der Telefonkette.
 * 
 * @author Mirco Bockholt
 */
@Stateless
public class TelephoneDAO extends JPADAO<Telephone> {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = -8592973234324608143L;

	/**
	 * Fügt {@code telephone} dem Datenbestand hinzu. Falls {@code telephone}
	 * bereits im Datenbestand vorhanden ist (vgl.
	 * {@link JPADAO#create(JPAEntity)}, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param telephone
	 *            Das zu speichernde {@link Telephone}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code telephone == null} oder {@code telephone} kein
	 *             durch JPA verwaltetes Objekt ist.
	 */
	public synchronized void create(Telephone telephone) {
		assertNotNull(telephone);
		try {
			super.create(telephone);
		} catch (final DuplicateUniqueFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Aktualisiert den Eintrag von {@code telephone} im Datenbestand. Falls
	 * {@code telephone} noch nicht im Datenbestand vorhanden ist, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param telephone
	 *            Das zu speichernde {@link Telephone}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code telephone == null} oder {@code telephone} kein
	 *             durch JPA verwaltetes Objekt ist.
	 */
	public synchronized void update(Telephone telephone) {
		assertNotNull(telephone);
		try {
			super.update(telephone);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Löscht ein Telephone-Objekt aus dem Datenbestand.
	 */
	public synchronized void delete(Telephone telephone) {
		super.delete(telephone);
	}

	public Class<Telephone> getClazz() {
		return Telephone.class;
	}

	public Telephone getById(int id) {
		EntityManager em = getEntityManager();
		return em.find(Telephone.class, id);
	}
}
