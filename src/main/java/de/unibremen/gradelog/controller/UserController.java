package de.unibremen.gradelog.controller;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.util.Assertion;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.model.Profile;
import de.unibremen.gradelog.model.Group;
import de.unibremen.gradelog.model.Representation;
import de.unibremen.gradelog.persistence.RepresentationDAO;
import de.unibremen.gradelog.persistence.UserDAO;

@Named("userBean")
@ConversationScoped
/**
 * Dieser Controller kümmert sich vorwiegend um die Einstellungen für den
 * jeweiligen eingeloggten Benutzer als auch um die Verwaltung der jeweiligen
 * Profilbilder.
 * 
 * @author Rune Krauss
 * @author Christopher Wojtkow
 * @author Steffen Gerken
 * @author Mirco Bockholt
 * @author Marco Glander
 */
public class UserController extends AbstractController {
	/**
	 * Der aktuell angezeigte Benutzer, dessen Attribute durch die UIKomponenten
	 * des Facelets geschrieben und gelesen werden.
	 */
	private User user;

	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Benutzer-Objekte übernimmt.
	 */
	private final UserDAO userDAO;

	/**
	 * Die eindeutige SerialisierungsID.
	 */
	private static final long serialVersionUID = -6560012086857165997L;

	/**
	 * Die Liste aller innerhalb der Applikation bekannten Benutzer.
	 */
	private List<User> allUsers;

	/**
	 * Die Liste aller innerhalb der Applikation bekannten öffentlichen
	 * Benutzer.
	 */
	private List<User> allPublicUsers;

	/**
	 * Die Liste aller in einer Datentabelle gefilterten Benutzer.
	 */
	private List<User> allFilteredUsers;

	/**
	 * Der in einer Datentabelle selektierte Benutzer.
	 */
	private User selectedUser;

	private final RepresentationDAO representationDao;

	/**
	 * Erzeugt einen {@link UserController} mit definierter {@link Session},
	 * {@link UserDAO} und {@link RepresentationDAO}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden {@link UserController}s.
	 * @param pUserDAO
	 * 		Die {@link UserDAO} des zu erzeugenden {@link UserController}s.
	 * @param pRepresentationDao
	 * 		Die {@link RepresentationDAO} des zu erzeugenden
	 * 		{@link UserController}s.
	 * @throws IllegalArgumentException
	 * 		Falls einer der übergebenen Parameter {@code null} ist.
     */
	@Inject
	public UserController(final Session pSession,
						  final UserDAO pUserDAO,
						  final RepresentationDAO pRepresentationDao) {
		super(Assertion.assertNotNull(pSession));
		userDAO = Assertion.assertNotNull(pUserDAO);
		representationDao = Assertion.assertNotNull(pRepresentationDao);
	}

	/**
	 * Wird bei der Initialisierung von den Modulen aufgerufen, welche diesen
	 * Controller benutzen. D. h. es wird der aktuelle Benutzer aus der Session
	 * geholt als auch alle Benutzer in dem System geladen.
	 */
	@PostConstruct
	public void init() {
		user = getSession().getUser();
		allUsers = userDAO.getAllUsers();
	}

	/**
	 * Schreibt die Attributwerte dieses Models per DAO in die Datenbank, d. h.
	 * wenn ein Benutzer bspw. seine Einstellungen geändert hat. Der
	 * Benutzername als auch die E-Mail dürfen dabei nicht doppelt vorkommen.
	 */
	public void save() {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to save profile values!");
			return;
		}
		if (user.getProfile().getAboutMe().length() > 4096) {
			addMessage("errorMaxLength");
			return;
		}
		try {
			userDAO.update(assertNotNull(user));
			addMessage("successUserdataComplete");
			init();
		} catch (final IllegalArgumentException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorUserdataIncomplete");
		} catch (final DuplicateUsernameException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorUsernameAlreadyInUse", user.getLogin());
		} catch (final DuplicateEmailException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorEmailAlreadyInUse", user.getEmail());
		}
	}

	/**
	 * Speichert das hochgeladene Bild in Form eines Binärstroms in die
	 * Datenbank.
	 * 
	 * @param event
	 *            Hochgeladenes File
	 */
	public void upload(FileUploadEvent event) {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to upload a profile picture!");
			return;
		}
		UploadedFile file = assertNotNull(event.getFile());
		try {
			user.getProfile().setPicture(file.getContents());
			getProfile().setOwnPicture(true);
			userDAO.update(user);
			addMessage("successUserdataComplete");
			init();
		} catch (Exception e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorUserdataIncomplete");
		}
	}

	/**
	 * Löscht ein benutzerspezifisches Bild aus dem Profil, sodass das
	 * Standardbild wieder gilt.
	 */
	public void deletePicture() {
		if (getProfile().isOwnPicture()) {
			Logger logger = getLogger();
			user.getProfile().setPicture(null);
			getProfile().setOwnPicture(false);
			addMessage("successPictureDelete");
			try {
				userDAO.update(assertNotNull(user));
			} catch (final DuplicateUsernameException e) {
				addMessageWithLogging(e, logger, Level.DEBUG, "errorUsernameAlreadyInUse", user.getLogin());
			} catch (final DuplicateEmailException e) {
				addMessageWithLogging(e, logger, Level.DEBUG, "errorEmailAlreadyInUse", user.getEmail());
			}
		}
	}



	/**
	 * Gibt alle Benutzer zurück, die nicht privat sind.
	 * 
	 * @return Öffentliche Benutzer
	 */
	public List<User> getAllPublicUsers() {
		return allUsers.stream()
				.filter(u -> !u.getProfile().isPrivated())
				.collect(Collectors.toList());
	}

	/**
	 * Gibt eine anzuzeigende Liste mit allen innerhalb der Applikation
	 * bekannten Lehrer zurück.
	 *
	 * @return Die anzuzeigende Liste aller innerhalb der Applikation bekannten
	 *         Benutzern.
	 */
	public List<User> getAllTeachers() {
		return userDAO.getAllTeachers();
	}

	public List<Representation> getRepresentations() {
		List<Representation> result = representationDao.getAllRepresentations();
		Collections.sort(result);
		return result;
	}

	public Set<Group> getGroups() {
		return user.getGroups();
	}

	public List<User> getAllUsers() {
		return allUsers;
	}

	public User getUser() {
		return user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public Profile getProfile() {
		return user.getProfile();
	}

	public void setProfile(final Profile profile) {
		user.setProfile(profile);
	}

	public List<User> getFilteredUsers() {
		return allFilteredUsers;
	}

	public void setFilteredUsers(final List<User> allFilteredUsers) {
		this.allFilteredUsers = allFilteredUsers;
	}

	public User getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(final User selectedUser) {
		this.selectedUser = selectedUser;
	}
}
