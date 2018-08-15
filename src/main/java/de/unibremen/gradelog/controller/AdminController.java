package de.unibremen.gradelog.controller;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;
import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.unibremen.gradelog.model.*;
import de.unibremen.gradelog.util.Assertion;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicatePageNameException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.persistence.PageDAO;
import de.unibremen.gradelog.persistence.RepresentationDAO;
import de.unibremen.gradelog.persistence.SessionDAO;
import de.unibremen.gradelog.persistence.UserDAO;
import de.unibremen.gradelog.util.CSVParser;
import de.unibremen.gradelog.util.LoginGenerator;

/**
 * Dieser Controller umfasst die Aufgaben, die im Adminbereich anfallen wie z.
 * B. Benutzer erstellen, Templates bearbeiten oder einen Wartungsmodus
 * anzulegen.
 * 
 * @author Rune Krauss
 */
@Named("adminBean")
@ViewScoped
public class AdminController extends AbstractController {
	/**
	 * Der aktuell angezeigte Benutzer, dessen Attribute durch die UIKomponenten
	 * des Facelets geschrieben und gelesen werden.
	 */
	private User user;
	/**
	 * Benutzer, der in das System importiert wird.
	 */
	private User tmpUser;
	/**
	 * Die aktuell selektierten Benutzer in einer Tabelle.
	 */
	private List<User> selectedUsers;
	/**
	 * Die aktuell selektierte Seite im Frontend.
	 */
	private Page selectedPage;
	/**
	 * Seite aus dem Frontend
	 */
	private Page page;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Benutzer-Objekte übernimmt.
	 */
	private final UserDAO userDAO;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Seiten-Objekte übernimmt.
	 */
	private final PageDAO pageDAO;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Vertretungen übernimmt.
	 */
	private final RepresentationDAO representationDAO;
	/**
	 * Die Liste aller innerhalb der Applikation bekannten Seiten.
	 */
	private List<Page> allPages;

	/**
	 * Das Data-Access-Objekt, das die Verwaltung der aktiven Sessions
	 * übernimmt. Wird benötigt, um im Falle des Löschens eines Benutzers
	 * abzufragen, ob dieser aktuell eingeloggt ist.
	 */
	private final SessionDAO sessionDAO;

	/**
	 * Wird benötigt, um gerade importierte Benutzer zu exportieren.
	 */
	private Session exporter;

	/**
	 * Die eindeutige SerialisierungsID.
	 */
	private static final long serialVersionUID = -6560012086857165997L;

	/**
	 * Die Liste aller innerhalb der Applikation bekannten Benutzer.
	 */
	private List<User> allUsers;

	/**
	 * Die Liste aller innerhalb der Applikation bekannten Benutzer.
	 */
	private List<User> allFilteredUsers;

	/**
	 * Hinter diesem Datum werden Benutzer gelöscht.
	 */
	 private java.util.Date deletionDate;

	/**
	 * Erzeugt einen {@link AdminController} mit definierter {@link Session},
	 * {@link UserDAO}, {@link PageDAO}, {@link RepresentationDAO} und
	 * {@link SessionDAO}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden {@link AdminController}s.
	 * @param pUserDAO
	 * 		Die {@link UserDAO} des zu erzeugenden {@link AdminController}s.
	 * @param pPageDAO
	 * 		Die {@link PageDAO} des zu erzeugenden {@link AdminController}s.
	 * @param pRepresentationDAO
	 * 		Die {@link RepresentationDAO} des zu erzeugenden
	 * 		{@link AdminController}s.
	 * @param pSessionDAO
	 * 		Die {@link SessionDAO} des zu erzeugenden {@link AdminController}s.
     * @param pExporter
	 * 		Die {@link Session}, mit derer importierte Benutzer exportiert
	 * 		werden.
	 * @throws IllegalArgumentException
	 * 		Falls einer der übergebenen Parameter {@code null} ist.
     */
	@Inject
	public AdminController(final Session pSession,
						   final UserDAO pUserDAO,
						   final PageDAO pPageDAO,
						   final RepresentationDAO pRepresentationDAO,
						   final SessionDAO pSessionDAO,
						   final Session pExporter) {
		super(Assertion.assertNotNull(pSession));
		userDAO = Assertion.assertNotNull(pUserDAO);
		pageDAO = Assertion.assertNotNull(pPageDAO);
		representationDAO = Assertion.assertNotNull(pRepresentationDAO);
		sessionDAO = Assertion.assertNotNull(pSessionDAO);
		exporter = Assertion.assertNotNull(pExporter);
	}

	/**
	 * Wird bei der Initialisierung eines Objektes dieser Klasse aufgerufen. So
	 * wird ein neuer Benutzer zur Erstellung erzeugt als auch alle Benutzer und
	 * Seiten geholt. Außerdem wird der Vertretungsplan gesetzt, sodass man sich
	 * diesen jederzeit ausdrucken kann.
	 */
	@PostConstruct
	public void init() {
		user = new User();
		selectedPage = new Page();
		allUsers = userDAO.getAllUsers();
		allPages = pageDAO.getAllPages();
		getSession().setImportedRepresentations(representationDAO.getAllRepresentations());
	}

	/**
	 * Erstellt einen Benutzer anhand eingegebener Daten. In einem Fehlerfall
	 * wie z. B. eines doppelten Benutzernamens wird eine Exception geworfen.
	 */
	public void create() {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to create user values!");
			return;
		}
		try {
			userDAO.create(assertNotNull(user));
			addMessage("successUserdataCreated");
			init();
		} catch (final IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorUserdataCreated");
		} catch (final DuplicateUsernameException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorUsernameAlreadyInUse", user.getLogin());
		} catch (final DuplicateEmailException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorEmailAlreadyInUse", user.getEmail());
		}
	}

	/**
	 * Löscht den Datensatz dieses Models aus der Datenbank. Sollte der zu
	 * löschende Benutzer gleich dem eingeloggten Benutzer sein, so ist der
	 * Löschprozess nicht möglich und wird demnach unterbunden.
	 */
	public void remove() {
		Logger logger = getLogger();
		if (sessionDAO.isUserLoggedIn(assertNotNull(user))) {
			addMessage("errorLoggedinDeletion");
			if (logger.isInfoEnabled()) {
				final User sessionUser = getSession().getUser();
				final String sessionUserIdentifier = sessionUser == null ? "Session without user"
						: sessionUser.getLogin();
				logger.info(
						String.format("%s tried to delete currently user %s.", sessionUserIdentifier, user.getLogin()));
			}
			return;
		}
		for(int i=0; i<selectedUsers.size(); i++){
			if (getSession().getUser().getLogin().equals(selectedUsers.get(i).getLogin())) {
				addMessage("errorSelfUserDeletion");
			} else {
				userDAO.delete(selectedUsers.get(i));
				addMessage("successUserDelete");
				init();
			}
		}
	}

	/**
	 * Löscht alle Benutzer anhand einem gegebenen Datum, d. h. es werden
	 * die erstellten Benutzer vor diesem Datum gelöscht.
	 */
	public void removeUsers() {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to create user values!");
			return;
		}
		int counter = 0;
		for (User user : allUsers) {
			System.out.println(user.getCreationDate());
			if ( user.getCreationDate().before(deletionDate) &&
					!getSession().getUser().getLogin().equals(user.getLogin())) {
				userDAO.delete(user);
				counter++;
			}
		}
		if (counter != 0)
			addMessage("successUsersDelete");
		else
			addMessage("errorUsersDelete");
	}

		/**
         * Importiert die jeweiligen Benutzer anhand eines DIF-Files und erzeugt
         * Benutzernamen als auch Passwörter mithilfe eines Generators. Im Anschluss
         * daran wird eine Möglichkeit generiert, sich diese Liste in der jeweiligen
         * Session ausdrucken zu lassen.
         *
         * @param event
         *            Das hochzuladene File
         */
	public void uploadDIF(FileUploadEvent event) {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to import users!");
			return;
		}
		try {
			ArrayList<User> list = new ArrayList<>();
			Map<String, String> importedUsers = new HashMap<>();
			CSVParser csvParser = new CSVParser(event.getFile().getInputstream(), 15);
			while (csvParser.nextLine()) {
				User user = new User();
				user.getProfile().setFirstName(csvParser.getString(7));
				user.getProfile().setLastName(csvParser.getString(0));
				user.getProfile().setCourse(csvParser.getString(9));
				int sex = csvParser.getInt(10);
				if (sex == 1)
					user.getProfile().setSex("female");
				else if (sex == 2)
					user.getProfile().setSex("male");
				user.getProfile().setBirthday(csvParser.getDate(12));
				user.setEmail(csvParser.getString(13));

				String name = LoginGenerator.generateName(user.getProfile().getFirstName(), user.getProfile().getLastName());
				user.setLogin(name);
				String password = LoginGenerator.generatePassword(8, 16, 1, 1, 1, 1);
				user.setPassword(password);
				if (userDAO.getUserByUsername(name) != null) {
					tmpUser = user;
					throw new DuplicateUsernameException(format("Username '%s' is already in use", tmpUser.getLogin()));
				}
				if (userDAO.getUserByEmail(user.getEmail()) != null) {
					tmpUser = user;
					throw new DuplicateEmailException(format("Email '%s' is already in use", tmpUser.getEmail()));
				}
				list.add(user);
				importedUsers.put(name, password);
			}
			for (User u : list) {
				userDAO.create(u);
			}
			for (String key : importedUsers.keySet()) {
				exporter.getImportedUserMap().put(key, importedUsers.get(key));
			}
			addMessage("successImportUsers");
			init();
		} catch (IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorInvalidFormat");
		} catch (IOException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorInvalidBinaryStream");
		} catch (DuplicateUsernameException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorUsernameAlreadyInUse", tmpUser.getLogin());
		} catch (DuplicateEmailException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorEmailAlreadyInUse", tmpUser.getEmail());
		}
	}

	/**
	 * Importiert den jeweiligen Vertretungsplan anhand eines DIF-Files. Im
	 * Anschluss daran wird eine Möglichkeit generiert, sich den Plan in der
	 * jeweiligen Session ausdrucken zu lassen.
	 * 
	 * @param event
	 *            Das hochzuladene File
	 */
	public void uploadDIFRepresentations(FileUploadEvent event) {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to import a representation plan!");
			return;
		}
		try {
			ArrayList<Representation> list = new ArrayList<>();
			CSVParser csvParser = new CSVParser(event.getFile().getInputstream(), 20);
			while (csvParser.nextLine()) {
				Representation newRep = new Representation();
				newRep.setDate(csvParser.getDate(1));
				newRep.setHour(csvParser.getInt(2));
				newRep.setTeacher(csvParser.getString(5));
				newRep.setRepreTeacher(csvParser.getString(6));
				newRep.setSubject(csvParser.getString(7));
				newRep.setRoom(csvParser.getString(11));
				newRep.setRepreRoom(csvParser.getString(12));
				newRep.setClasses(csvParser.getString(14));
				list.add(newRep);
			}
			representationDAO.clearRepresentations();
			for (Representation r : list) {
				representationDAO.create(r);
			}
			exporter.getImportedRepresentations().clear();
			for (Representation r : list) {
				exporter.getImportedRepresentations().add(r);
			}
			addMessage("successImportRepresentation");
			init();
		} catch (IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorInvalidFormat");
		} catch (IOException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorInvalidBinaryStream");
		}
	}

	/**
	 * Wird ausgelöst, wenn ein Benutzer geändert werden soll, zudem wird darin
	 * dann der editierte Benutzer gespeichert.
	 * 
	 * @param event
	 *            Das verpackte Benutzer-Objekt
	 */
	public void onRowEdit(RowEditEvent event) {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to edit user values!");
			return;
		}
		try {
			user = (User) event.getObject();
			if (getSession().getUser().getId() == user.getId()) {
				if (!getSession().getUser().getUserRole().equals(user.getUserRole())) {
					addMessage("errorUserRole");
					return;
				}
				if (user.isBlocked()) {
					addMessage("errorAdminBlocked");
					return;
				}
			}
			userDAO.update(assertNotNull(user));
			addMessage("successUserdataComplete");
		} catch (final IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, getTranslation("errorUserdataIncomplete"));
		} catch (final DuplicateUsernameException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorUsernameAlreadyInUse", user.getLogin());
		} catch (final DuplicateEmailException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorEmailAlreadyInUse", user.getEmail());
		}
	}

	/**
	 * Wird ausgelöst, wenn ein Benutzer nicht mehr geändert werden soll.
	 * 
	 * @param event
	 *            Das verpackte Benutzer-Objekt
	 */
	public void onRowCancel(RowEditEvent event) {
	}

	/**
	 * Speichert eine jeweilige Seite in Bezug auf das Frontend wie bspw. der
	 * Seite 'FAQ'. Sollte der Seitenname doppelt sein, so wird eine Ausnahme
	 * ausgelöst.
	 */
	public void savePage() {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to save a page!");
			return;
		}
		if (selectedPage.getGermanContent().length() > 16392 || selectedPage.getEnglishContent().length() > 16392) {
			addMessage("errorMaxLength");
			return;
		}
		try {
			pageDAO.update(assertNotNull(selectedPage));
			addMessage("successPageComplete");
		} catch (final IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorPageIncomplete");
		} catch (DuplicatePageNameException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorPageNameAlreadyInUse");
		}
	}

	public List<Representation> getRepresentations() {
		List<Representation> result = representationDAO.getAllRepresentations();
		Collections.sort(result);
		return result;
	}

	public Set<Group> getGroups() {
		return user.getGroups();
	}

	public List<User> getAllUsers() {
		return allUsers;
	}

	public void setAllUsers(final List<User> allUsers) {
		this.allUsers = allUsers;
	}

	public User getUser() {
		return user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public List<User> getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(final List<User> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}

	public List<User> getFilteredUsers() {
		return allFilteredUsers;
	}

	public void setFilteredUsers(final List<User> allFilteredUsers) {
		this.allFilteredUsers = allFilteredUsers;
	}

	public void setSelectedPage(final Page selectedPage) {
		this.selectedPage = selectedPage;
	}

	public Page getSelectedPage() {
		return selectedPage;
	}

	public List<Page> getAllPages() {
		return allPages;
	}

	public void setAllPages(final List<Page> allPages) {
		this.allPages = allPages;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(final Page page) {
		this.page = page;
	}

	public Profile getProfile() {
		return user.getProfile();
	}

	public void setProfile(final Profile profile) {
		user.setProfile(profile);
	}

	public Session getExporter() {
		return exporter;
	}

	public void setExporter(final Session exporter) {
		this.exporter = exporter;
	}

	public java.util.Date getDeletionDate() { return deletionDate; }

	public void setDeletionDate(java.util.Date deletionDate) { this.deletionDate = deletionDate; }
}
