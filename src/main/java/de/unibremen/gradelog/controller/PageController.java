package de.unibremen.gradelog.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.model.Page;
import de.unibremen.gradelog.persistence.PageDAO;
import de.unibremen.gradelog.persistence.UserDAO;
import de.unibremen.gradelog.util.Assertion;

/**
 * Verwaltet die Seiten wie z. B. 'FAQ' im Frontend.
 * 
 * @author Rune Krauss
 */
@Named("frontendBean")
@ViewScoped
public class PageController extends AbstractController {

	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Seiten-Objekte übernimmt.
	 */
	private final PageDAO pageDAO;

	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Benutzer-Objekte übernimmt.
	 */
	private final UserDAO userDAO;

	/**
	 * Die Liste aller innerhalb der Applikation bekannten Seiten.
	 */
	private List<Page> allPages;

	/**
	 * Die eindeutige SerialisierungsID.
	 */
	private static final long serialVersionUID = -6560012086857165997L;

	/**
	 * Die Liste aller innerhalb der Applikation bekannten Benutzer.
	 */
	private List<User> allUsers;

	/**
	 * Erzeugt einen {@link PageController} mit definierter {@link Session},
	 * {@link PageDAO} und {@link UserDAO}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden {@link PageController}s.
	 * @param pPageDao
	 * 		Die {@link PageDAO} des zu erzeugenden {@link PageController}s.
	 * @param pUserDao
	 * 		Die {@link UserDAO} des zu erzeugenden {@link PageController}s.
	 * @throws IllegalArgumentException
	 * 		Falls einer der übergebenen Parameter {@code null} ist.
	 */
	@Inject
	public PageController(final Session pSession,
						  final PageDAO pPageDao,
						  final UserDAO pUserDao) {
		super(Assertion.assertNotNull(pSession));
		pageDAO = Assertion.assertNotNull(pPageDao);
		userDAO = Assertion.assertNotNull(pUserDao);
	}

	/**
	 * Initialisiert alle Seiten und Benutzer. Diese werden für das Frontend
	 * benötigt, um bspw. alle registrierten Benutzer anzuzeigen.
	 */
	@PostConstruct
	public void init() {
		allPages = pageDAO.getAllPages();
		allUsers = userDAO.getAllUsers();
	}

	/**
	 * Geht die Benutzer durch und prüft, wer gerade online ist.
	 * 
	 * @return Anzahl der Benutzer, welche gerade online sind.
	 */
	public int getAllOnlineUsers() {
		int n = 0;
		for (User u : allUsers) {
			if (u.isOnline())
				n++;
		}
		return n;
	}

	public List<Page> getAllPages() {
		return allPages;
	}

	public void setAllPages(final List<Page> allPages) {
		this.allPages = allPages;
	}

	public int getAllUsers() {
		return allUsers.size();
	}
}
