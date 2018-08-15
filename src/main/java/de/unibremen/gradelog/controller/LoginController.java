package de.unibremen.gradelog.controller;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.time.LocalDateTime;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.inject.Inject;
import javax.inject.Named;

import de.unibremen.gradelog.util.Assertion;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.unibremen.gradelog.util.Crypt;
import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.persistence.UserDAO;

/**
 * Diese Bean ist der Controller für den Login-Dialog.
 * 
 * @author Rune Krauss
 */
@Named("loginBean")
@RequestScoped
public class LoginController extends AbstractController {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = -90160472367374670L;

	/**
	 * Der Benutzername, der im Facelet angezeigt wird und durch Interaktion mit
	 * dem Facelet geändert wird.
	 */
	private String username;

	/**
	 * Das Passwort, das im Facelet angezeigt wird und durch Interaktion mit dem
	 * Facelet geändert wird.
	 */
	private String password;

	/**
	 * LoginButton, an den Fehlermeldungen gebunden werden, welche beim Login
	 * auftreten können.
	 */
	private transient UIComponent loginButton;

	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Benutzer-Objekte übernimmt.
	 */
	private final UserDAO userDAO;

	/**
	 * Erzeugt einen {@link LoginController} mit definierter {@link Session}
	 * und {@link UserDAO}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden {@link LoginController}s.
	 * @param pUserDAO
	 * 		Die {@link UserDAO} des zu erzeugenden {@link LoginController}s.
	 * @throws IllegalArgumentException
	 * 		Falls einer der übergebenen Parameter {@code null} ist.
     */
	@Inject
	public LoginController(final Session pSession, final UserDAO pUserDAO) {
		super(Assertion.assertNotNull(pSession));
		userDAO = Assertion.assertNotNull(pUserDAO);
	}

	/**
	 * Loggt den aktuell angezeigten Benutzer in die zugehörige Session ein,
	 * falls noch niemand in der zugehörigen Session eingeloggt ist und der
	 * temporäre Benutzer authentifiziert werden kann. Die Eingabefelder des
	 * werden zurückgesetzt. Wenn der angezeigte Benutzer nicht eingeloggt
	 * werden kann, wird eine entsprechende Fehlermeldung in der UI angezeigt.
	 * Sollte der Benutzer geblockt sein, so kann er sich ebenfalls nicht
	 * einloggen.
	 *
	 * @return Den Namen des Facelets, zu dem im Erfolgsfall navigiert werden
	 *         soll oder {@code null} falls der temporäre Benutzer nicht
	 *         eingeloggt werden konnte.
	 */
	public String login() {
		final Session session = getSession();
		Logger logger = getLogger();
		if (session.isLoggedIn()) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("User %s tried to relogin via login().", username));
			}
			return null;
		}
		final User registeredUser = userDAO.getUserByUsername(username);
		if (registeredUser == null) {
			addMessage(loginButton, "errorUnknownUsername");
			password = "";
			return null;
		}
		if (registeredUser.isBlocked()) {
			addMessage(loginButton, "errorBlockedUser");
			password = "";
			return null;
		}
		if (registeredUser.getPassword().equals(password)) {
			registeredUser.setOnline(true);
			try {
				userDAO.update(registeredUser);
			} catch (DuplicateUsernameException e) {
				addMessageWithLogging(e, logger, Level.DEBUG, "errorUsernameAlreadyInUse");
			} catch (DuplicateEmailException e) {
				addMessageWithLogging(e, logger, Level.DEBUG, "errorEmailAlreadyInUse");
			}
			setUser(registeredUser);
			session.setLoginTime(LocalDateTime.now());
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Successful login for user %s.", username));
			}
			username = "";
			password = "";
			setLocale();
			return "index.xhtml?faces-redirect=true";
		} else {
			addMessage(loginButton, "errorUnknownPassword");
			password = "";
			return null;
		}
	}

	/**
	 * Loggt den aktuell in der zugehörigen Session eingeloggten Benutzer aus
	 * (falls jemand eingeloggt ist) und gibt den Namen des Facelets, zu dem im
	 * Erfolgsfall navigiert werden soll, zurück. Wenn niemand eingeloggt ist,
	 * passiert nichts.
	 *
	 * @return Den Namen des Facelets, zu dem im Erfolgsfall navigiert werden
	 *         soll oder {@code null} falls niemand in der zugehörigen Session
	 *         eingeloggt war.
	 */
	public String logout() {
		Logger logger = getLogger();
		if (isLoggedIn()) {
			User user = getSession().getUser();
			if (logger.isInfoEnabled())
				logger.info(String.format("User %s logged out.", user.getLogin()));
			user.setOnline(false);
			try {
				userDAO.update(user);
			} catch (DuplicateUsernameException e) {
				addMessageWithLogging(e, logger, Level.DEBUG, "errorUsernameAlreadyInUse");
			} catch (DuplicateEmailException e) {
				addMessageWithLogging(e, logger, Level.DEBUG, "errorEmailAlreadyInUse");
			}
			setInvalidateSession();
			return "/scheduler/index.xhtml?faces-redirect=true";
		}
		return null;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String theUsername) {
		username = theUsername.toLowerCase();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String thePassword) {
		Logger logger = getLogger();
		try {
			password = Crypt.hash(thePassword);
		} catch (IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorIllegalArgument");
		}
	}

	public UIComponent getLoginButton() {
		return loginButton;
	}

	public void setLoginButton(final UIComponent theLoginButton) {
		Logger logger = getLogger();
		try {
			loginButton = assertNotNull(theLoginButton);
		} catch (IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorIllegalArgument");
		}
	}
}
