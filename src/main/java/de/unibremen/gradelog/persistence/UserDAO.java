package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotEmpty;
import static de.unibremen.gradelog.util.Assertion.assertNotNull;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;

import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUniqueFieldException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.CustomPage;
import de.unibremen.gradelog.model.Group;
import de.unibremen.gradelog.model.JPAEntity;
import de.unibremen.gradelog.model.User;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link User}.
 * 
 * @author Rune Krauss
 * @author Christopher Wojtkow
 */
@Stateless
public class UserDAO extends JPADAO<User> {

	/**
	 * Die eindeutige ID für Serialisierung.
	 */
	private static final long serialVersionUID = 2816778704932701100L;

	/**
	 * Fügt {@code user} dem Datenbestand hinzu. Falls {@code user} bereits im
	 * Datenbestand vorhanden ist (vgl. {@link JPADAO#create(JPAEntity)}, wird
	 * eine {@link IllegalArgumentException} ausgelöst.
	 * 
	 * @param user
	 *            Das zu speichernde {@link User}-Objekt.
	 * @throws DuplicateUsernameException
	 *             Falls der Benutzername bereits vergeben ist.
	 * @throws DuplicateEmailException
	 *             Falls die E-Mail-Adresse bereits vergeben ist.
	 * @throws UnexpectedUniqueViolationException
	 *             Falls der Aufruf der Oberklassenmethode unerwarteterweise
	 *             eine {@link DuplicateUniqueFieldException} ausgelöst hat.
	 * @throws IllegalArgumentException
	 *             Falls {@code user == null}, {@code user.getId() != null},
	 *             {@code user.getUsername() == null},
	 *             {@code user.getEmail() == null} oder {@code user} kein durch
	 *             JPA verwaltetes Objekt ist.
	 * @throws TransactionRequiredException
	 *             Falls zum Zeitpunkt des Aufrufs keine gültige Transaktion
	 *             vorliegt (vgl.
	 *             {@link javax.persistence.EntityManager#persist(Object)}).
	 */
	@Override
	public synchronized void create(final User user) throws DuplicateUsernameException, DuplicateEmailException {
		assertNotNull(user);
		final String userName = assertNotNull(user.getLogin(), "The username of the parameter must not be null!");
		final String userEmail = assertNotNull(user.getEmail(), "The email of the parameter must not be null!");

		final User userByName = getUserByUsername(userName);
		if (userByName != null && userByName.getLogin() != null && userByName.getLogin().equals(user.getLogin())) {
			throw new DuplicateUsernameException(format("Username '%s' is already in use", userName));
		}
		final User userByEmail = getUserByEmail(userEmail);
		if (userByEmail != null) {
			throw new DuplicateEmailException(format("Email '%s' is already in use", userEmail));
		}
		try {
			super.create(user);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	/**
	 * Aktualisiert den Eintrag von {@code user} im Datenbestand. Falls
	 * {@code user} noch nicht im Datenbestand vorhanden ist, wird eine
	 * {@link IllegalArgumentException} ausgelöst.
	 *
	 * @param user
	 *            Das zu aktualisierende {@link User}-Objekt.
	 * @throws DuplicateUsernameException
	 *             Falls der zu aktualisierende Benutzername bereits an ein
	 *             anderes Objekt vergeben ist.
	 * @throws DuplicateEmailException
	 *             Falls die zu aktualisierende E-Mail-Adresse bereits an ein
	 *             anderes Objekt vergeben ist.
	 * @throws UnexpectedUniqueViolationException
	 *             Falls der Aufruf der Oberklassenmethode unerwarteterweise
	 *             eine {@link DuplicateUniqueFieldException} ausgelöst hat.
	 * @throws IllegalArgumentException
	 *             Falls {@code user == null}, {@code user.getId() == null}, es
	 *             noch keinen Eintrag für {@code user} im Datenbestand gibt,
	 *             {@code user.getUsername() == null},
	 *             {@code user.getEmail() == null} oder {@code user} kein durch
	 *             JPA verwaltetes Objekt ist.
	 * @throws TransactionRequiredException
	 *             Falls zum Zeitpunkt des Aufrufs keine gültige Transaktion
	 *             vorliegt (vlg.
	 *             {@link javax.persistence.EntityManager#merge(Object)}).
	 */
	@Override
	public synchronized void update(final User user) throws DuplicateUsernameException, DuplicateEmailException {
		assertNotNull(user);
		final int userId = assertNotNull(user.getId(), "The id of the parameter must not be null!");
		assertNotNull(getById(userId), "The parameter is not yet registered!");
		final String userName = assertNotNull(user.getLogin(), "The username of the parameter must not be null!");
		final String userEmail = assertNotNull(user.getEmail(), "The email of the parameter must not be null!");

		final User userByName = getUserByUsername(userName);

		if (userByName != null && userByName.getId() != userId) {
			throw new DuplicateUsernameException(format("Username '%s' is already in use", userName));
		}
		final User userByEmail = getUserByEmail(userEmail);
		if (userByEmail != null && userByEmail.getId() != userId) {
			throw new DuplicateEmailException(format("Email '%s' is already in use", userEmail));
		}
		try {
			super.update(user);
		} catch (final DuplicateUniqueFieldException e) {
			throw new UnexpectedUniqueViolationException(e);
		}
	}

	public List<User> getAllUsers() {
		return getEntityManager().createNamedQuery("User.findAll", getClazz()).getResultList();
	}

	public User getUserByUsername(final String username) {
		assertNotEmpty(username);
		final List<User> users = getEntityManager().createNamedQuery("User.findByName", getClazz())
				.setParameter(1, username).getResultList();
		return users.isEmpty() ? null : users.get(0);
	}

	public User getUserByEmail(final String email) {
		assertNotEmpty(email);
		final List<User> users = getEntityManager().createNamedQuery("User.findByEmail", getClazz())
				.setParameter(1, email).getResultList();
		return users.isEmpty() ? null : users.get(0);
	}

	public List<CustomPage> getSharedPages(User object) {
		ArrayList<CustomPage> result = new ArrayList<>();
		EntityManager em = getEntityManager();
		TypedQuery<Group> groupQuery = em.createNamedQuery("Group.findAll", Group.class);
		List<Group> groups = groupQuery.getResultList();
		Set<CustomPage> pages = object.getSharedPages();
		for (Group g : groups) {
			if (object.getGroups().contains(g)) {
				pages.addAll(g.getSharedPages());
			}
		}
		result.addAll(pages);
		return result;
	}

	public List<User> getAllTeachers() {
		return getEntityManager().createNamedQuery("User.findAllTeachers", User.class)
				.setParameter(1, User.Roles.TEACHER).setParameter(2,  false).getResultList();
	}

	@Override
	Class<User> getClazz() {
		return User.class;
	}
}
