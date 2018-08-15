package de.unibremen.gradelog.persistence;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.User;

/**
 * Dieses DAO verwaltet Objekte der Klasse {@link Session}.
 *
 * @author Rune Krauss
 */
@Named
@ApplicationScoped
public class SessionDAO extends TransientDAO<Session> {

	/**
	 * Die eindeutige SerialisierungsID.
	 */
	private static final long serialVersionUID = -4448901874981927743L;

	/**
	 * Gibt zurück, ob der gegebene Benutzer eine aktive Session hat, d. h.
	 * eingeloggt ist.
	 *
	 * @param user
	 *            der zu prüfende Benutzer
	 * @return {@code true} falls der gegebene Benutzer aktuell eingeloggt ist,
	 *         sonst {@code false}.
	 * @throws IllegalArgumentException
	 *             falls der übergebene Parameter den Wert {@code null} hat
	 */
	public boolean isUserLoggedIn(final User user) {
		assertNotNull(user);
		final List<Session> sessions = getAll();
		for (final Session session : sessions) {
			final User sessionUser = session.getUser();
			if (sessionUser != null && sessionUser.equals(user)) {
				return true;
			}
		}
		return false;
	}

}
