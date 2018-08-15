package de.unibremen.gradelog.controller;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.persistence.UserDAO;
import de.unibremen.gradelog.util.Assertion;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.unibremen.gradelog.model.Missing;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.persistence.MissingDAO;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Diese Bean verwaltet die Missing Models für die Seite absence.xhtml. Mit
 * Hilfe dieser Bean werden die Fehlzeiten verwaltet.
 * 
 * @author Mirco Bockholt
 * @author Marco Glander
 */
@Named("missingBean")
@SessionScoped
public class MissingController extends AbstractController {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = -2088557340524816954L;

	/** Neues Missing Objekt */
	private Missing missing;
	/** MissingDAO zum Persistieren von Missings */
	private final MissingDAO missingDao;
	/** UserDAO zum Persistieren von Usern */
	private final UserDAO userDao;
	/** Hier wird das selektierte Missing (DataTable) abgelegt */
	private Missing selectedMissing;

	/**
	 * Erzeugt einen {@link MissingController} mit definierter {@link Session},
	 * {@link MissingDAO} und {@link UserDAO}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden {@link MissingController}s.
	 * @param pMissingDao
	 * 		Die {@link MissingDAO} des zu erzeugenden
	 * 		{@link MissingController}s.
	 * @param pUserDao
	 * 		Die {@link UserDAO} des zu erzeugenden {@link MissingController}s.
	 * @throws IllegalArgumentException
	 * 		Falls einer der übergebenen Parameter {@code null} ist.
     */
	@Inject
	public MissingController(final Session pSession,
							 final MissingDAO pMissingDao,
							 final UserDAO pUserDao) {
		super(Assertion.assertNotNull(pSession));
		missingDao = Assertion.assertNotNull(pMissingDao);
		userDao = Assertion.assertNotNull(pUserDao);
	}

	/**
	 * Wird nach dem Konstruktor ausgeführt. Es werden zwei neue Missing Objekte
	 * erstellt, wobei eines davon beim Erstellen einer neuen Fehlzeit und das
	 * andere beim Editieren gebraucht wird. Dies ist der Fall, da die Datatable
	 * die selektierte Fehlzeit in das Attribut speichert.
	 */
	@PostConstruct
	public void init() {
		missing = new Missing();
		selectedMissing = new Missing();
	}

	/**
	 * Schreibt eine neue Fehlzeit in die Datenbank.
	 */
	public void create() {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to save calendar values!");
			return;
		}
		if (missing != null && missing.getStartTime().after(missing.getEndTime())) {
			addMessage("errorDateRange");
			return;
		}
		User user = getSession().getUser();
		missing.setUser(assertNotNull(user));
		missingDao.create(assertNotNull(missing));
		user.addMissing(assertNotNull(missing));
		addMessage("successfulAddAbsence");
		try {
			userDao.update(assertNotNull(user));
		} catch (final IllegalArgumentException e) {
			addMessage("errorIllegalArgument");
		} catch (final DuplicateUsernameException e) {
			addMessageWithLogging("registerUserForm:username", e, getLogger(), Level.DEBUG, "errorUsernameAlreadyInUse",
					getSession().getUser().getLogin());
		} catch (final DuplicateEmailException e) {
			addMessageWithLogging("registerUserForm:email", e, getLogger(), Level.DEBUG, "errorEmailAlreadyInUse",
					getSession().getUser().getEmail());
		}
		init();
	}

	/**
	 * Überschreibt eine bestehende Fehlzeit in der Datenbank.
	 */
	public void update() {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to save missing values!");
			return;
		}
		if (selectedMissing != null && selectedMissing.getStartTime().after(selectedMissing.getEndTime())) {
			addMessage("errorDateRange");
			return;
		}
		try {
			missingDao.update(assertNotNull(selectedMissing));
			addMessage("successfulEdit");
		} catch (final IllegalArgumentException e) {
			addMessage("errorIllegalArgument");
		}
	}

	/**
	 * Löscht eine bestehende Fehlzeit aus der Datenbank.
	 */
	public void remove() {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to delete missing values!");
			return;
		}
		assertNotNull(selectedMissing);
		User user = selectedMissing.getUser();
		user.removeMissing(selectedMissing);
		missingDao.delete(selectedMissing);
		try {
			userDao.update(user);
			addMessage("successfulRemove");
		} catch (final IllegalArgumentException e) {
			addMessage("errorIllegalArgument");
		} catch (final DuplicateUsernameException e) {
			addMessageWithLogging("registerUserForm:username", e, getLogger(), Level.DEBUG, "errorUsernameAlreadyInUse",
					getSession().getUser().getLogin());
		} catch (final DuplicateEmailException e) {
			addMessageWithLogging("registerUserForm:email", e, getLogger(), Level.DEBUG, "errorEmailAlreadyInUse",
					getSession().getUser().getEmail());
		}
	}

	/**
	 * Speichert hochgeladenes Bild als Byte Array in die Datenbank.
	 */
	public void uploadImage(FileUploadEvent event) {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to set missing image");
			return;
		}
		UploadedFile file = event.getFile();
		if (file != null) {
			try {
				assertNotNull(selectedMissing);
				selectedMissing.setImage(file.getContents());
				missingDao.update(selectedMissing);
				addMessage("successfulUpload");
			} catch (Exception e) {
				addMessageWithLogging(e, logger, Level.DEBUG, "errorCannotUpload");
			}
		}
	}

	/**
	 * Setzt ein bereits hochgeladenes Bild in der Datenbank auf null.
	 */
	public void resetImage() {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to set missing image");
			return;
		}
		try {
			assertNotNull(selectedMissing);
			selectedMissing.setImage(null);
			missingDao.update(selectedMissing);
			addMessage("successfulReset");
		} catch (Exception e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorReset");
		}
	}

	public Set<Missing> getMissings() {
		return getSession().getUser().getMissings();
	}

	public Missing getMissing() {
		return missing;
	}

	public void setMissing(Missing m) {
		missing = m;
	}

	public Missing getSelectedMissing() {
		return selectedMissing;
	}

	public void setSelectedMissing(Missing sm) {
		selectedMissing = sm;
	}

	public List<Missing> getMissingsAsList() {
		return new ArrayList<>(getMissings());
	}
}
