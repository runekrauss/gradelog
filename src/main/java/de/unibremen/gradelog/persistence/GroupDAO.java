package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotEmpty;
import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.*;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link Group}.
 * 
 * @author Marco Glander
 */
@Stateless
public class GroupDAO extends JPADAO<Group> {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = -8893658707631074549L;

	/**
	 * Fügt {@code group} dem Datenbestand hinzu. Falls {@code group} bereits im
	 * Datenbestand vorhanden ist (vgl. {@link JPADAO#create(JPAEntity)}, wird
	 * eine {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param group
	 *            Das zu speichernde {@link Group}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code group == null} oder {@code group} kein durch JPA
	 *             verwaltetes Objekt ist.
	 */
	public synchronized void create(final Group group) {
		assertNotNull(group);
		try {
			super.create(group);
		} catch (final DuplicateUniqueFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Aktualisiert den Eintrag von {@code group} im Datenbestand. Falls
	 * {@code group} noch nicht im Datenbestand vorhanden ist, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param group
	 *            Das zu speichernde {@link Group}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code group == null} oder {@code group} kein durch JPA
	 *             verwaltetes Objekt ist.
	 */
	public synchronized void update(Group group) {
		assertNotNull(group);
		try {
			super.update(group);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	public Group getGroupByName(final String theName) {
		assertNotEmpty(theName);
		final List<Group> groups = getEntityManager().createNamedQuery("Group.findByName", getClazz())
				.setParameter(1, theName).getResultList();
		return groups.isEmpty() ? null : groups.get(0);
	}

	/**
	 * Löscht ein Group-Objekt aus dem Datenbestand.
	 */
	public synchronized void delete(Group group) {
		super.delete(group);
	}

	public Class<Group> getClazz() {
		return Group.class;
	}

	public Group getById(int id) {
		EntityManager em = getEntityManager();
		return em.find(Group.class, id);
	}
}
