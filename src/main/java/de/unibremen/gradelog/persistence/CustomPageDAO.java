package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.*;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link CustomPage}.
 * 
 * @author Marco Glander
 * @author Christopher Wojtkow
 */
@Stateless
public class CustomPageDAO extends JPADAO<CustomPage> {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = -5819986024645333926L;

	/**
	 * Fügt {@code customPage} dem Datenbestand hinzu. Falls {@code customPage}
	 * bereits im Datenbestand vorhanden ist (vgl.
	 * {@link JPADAO#create(JPAEntity)}, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param customPage
	 *            Das zu speichernde {@link CustomPage}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code customPage == null} oder {@code customPage} kein
	 *             durch JPA verwaltetes Objekt ist.
	 */
	public synchronized void create(CustomPage customPage) {
		assertNotNull(customPage);
		try {
			super.create(customPage);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Aktualisiert den Eintrag von {@code customPage} im Datenbestand. Falls
	 * {@code customPage} noch nicht im Datenbestand vorhanden ist, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param object
	 *            Das zu speichernde {@link CustomPage}-Objekt.
	 * 
	 * @throws IllegalArgumentException
	 *             Falls {@code customPage == null} oder {@code customPage} kein
	 *             durch JPA verwaltetes Objekt ist.
	 */
	public synchronized void update(CustomPage object) {
		assertNotNull(object);
		try {
			super.update(object);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Liefert eine Liste aller CustomPage-Einträge aus der Datenbank.
	 * 
	 * @return Liste von allen CustomPages
	 */
	public List<CustomPage> getAllCustomPages() {
		return getEntityManager().createNamedQuery("CustomPage.findAll", getClazz()).getResultList();
	}

	/**
	 * Löscht ein Task-Objekt aus dem Datenbestand.
	 */
	public synchronized void delete(CustomPage object) {
		super.delete(object);
	}

	public Class<CustomPage> getClazz() {
		return CustomPage.class;
	}

	public CustomPage getById(int id) {
		EntityManager em = getEntityManager();
		return em.find(CustomPage.class, id);
	}

	/**
	 * Liefert einen Wahrheitswert zurück, der anzeigt, ob die gegebene
	 * {@code id} einer CustomPage bereits existiert.
	 * 
	 * @param id
	 *            ID der CustomPage
	 * @return Wahrheitswert. True=CustomPage existiert, andernfalls nicht
	 */
	public Boolean idExists(int id) {
		EntityManager em = getEntityManager();
		return em.find(CustomPage.class, id) != null;
	}

	public List<CustomPage> getOwnedByUser(final User user) {
		return getEntityManager().createNamedQuery("CustomPage.findOwnedByUser", getClazz())
				.setParameter(1, assertNotNull(user.getId())).getResultList();
	}

	/**
	 * Fässt für eine CustomPage alle freigegeben User zu einer Liste zusammen.
	 * Auch User die indirekt durch Gruppen Freigabe erteilt bekommen haben
	 * werden erfasst.
	 * 
	 * @param object
	 * @return
	 */
	public List<User> getReaders(CustomPage object) {
		ArrayList<User> result = new ArrayList<>();
		EntityManager em = getEntityManager();
		TypedQuery<Group> groupQuery = em.createNamedQuery("Group.findAll", Group.class);
		List<Group> groups = groupQuery.getResultList();
		for (Group g : groups) {
			if (g.getSharedPages().contains(object))
				result.addAll(g.getUsers());
		}
		TypedQuery<User> userQuery = em.createNamedQuery("User.findAll", User.class);
		List<User> users = userQuery.getResultList();
		for (User u : users) {
			if (getOwnedByUser(u).contains(object))
				result.add(u);
		}
		return result;
	}
}
