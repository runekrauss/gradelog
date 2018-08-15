package de.unibremen.gradelog.controller;

import static de.unibremen.gradelog.util.Assertion.assertNotEmpty;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;

import de.unibremen.gradelog.model.Profile;
import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.util.Assertion;
import org.apache.log4j.Logger;

import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.persistence.UserDAO;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * Dieser Controller übernimmt die Logik zur Anzeige eines Benutzers in diesem
 * System, kümmert sich also auch bspw. um die jeweiligen Weiterleitungen usw.
 * 
 * @author Rune Krauss
 *
 */
@Named("profileBean")
@SessionScoped
public class ProfileController extends AbstractController {
	/**
	 * Der aktuell angezeigte Benutzer
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
	 * Erzeugt einen {@link ProfileController} mit definierter {@link Session}
	 * und {@link UserDAO}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden {@link ProfileController}s.
	 * @param pUserDAO
	 * 		Die {@link UserDAO} des zu erzeugenden {@link ProfileController}s.
	 * @throws IllegalArgumentException
	 * 		Falls einer der übergebenen Parameter {@code null} ist.
     */
	@Inject
	public ProfileController(final Session pSession, final UserDAO pUserDAO) {
		super(Assertion.assertNotNull(pSession));
		userDAO = Assertion.assertNotNull(pUserDAO);
	}

	@PostConstruct
	public void init() {
	}

	/**
	 * Diese Methode nimmt einen GET-Parameter (id) entgegen und holt sich
	 * anhanddessen den dazugehörigen Benutzer. Letztendlich leitet sie dann zum
	 * Profil weiter.
	 * 
	 * @return Weiterleitung zum jeweiligen Profil
	 */
	public String show() {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to show a profile!");
			return null;
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		int userId = Integer.parseInt(getUserParam(fc));
		user = userDAO.getById(userId);
		return "/scheduler/community/profile" + "?faces-redirect=true";
	}



	/**
	 * Holt sich die jeweilige ID aus einem Benutzer im Hinblick auf den
	 * jeweiligen Request.
	 * 
	 * @param fc
	 *            Kontext mit den Parametern
	 * @return Benutzer-ID
	 */
	private String getUserParam(FacesContext fc) {
		Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
		return assertNotEmpty(params.get("userId"));
	}

	/**
	 * Verwaltet die Logik, um zur Anzeige aller Benutzer zurückzukehren.
	 * 
	 * @return Anzeige bzgl. aller Benutzer
	 */
	public String back() {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to show public users!");
			return null;
		}
		return "/scheduler/community/users.xhtml?faces-redirect=true";
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Profile getProfile() {
		return user.getProfile();
	}

	public void setProfile(final Profile profile) {
		user.setProfile(profile);
	}
}
