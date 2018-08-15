package de.unibremen.gradelog.controller;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.util.Assertion;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.unibremen.gradelog.exception.DuplicateTemplateNameException;
import de.unibremen.gradelog.model.Template;
import de.unibremen.gradelog.persistence.TemplateDAO;
import de.unibremen.gradelog.persistence.UserDAO;

/**
 * Ermöglicht das Konfigurieren des Front- und Backends hinsichtlich
 * verschiedener Parameter wie der Farbe oder dem Hintergrund.
 * 
 * @author Rune Krauss
 */
@Named("templateBean")
@ViewScoped
public class TemplateController extends AbstractController {
	/**
	 * Das jeweilige Backend Model Template
	 */
	private Template backendTemplate;

	/**
	 * Das jeweilige Frontend Model Template
	 */
	private Template frontendTemplate;

	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Template-Objekte übernimmt.
	 */
	private final TemplateDAO templateDAO;

	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * User-Objekte übernimmt.
	 */
	private final UserDAO userDAO;

	/**
	 * Die Liste aller innerhalb der Applikation bekannten Templates.
	 */
	private List<Template> allTemplates;

	/**
	 * Die eindeutige SerialisierungsID.
	 */
	private static final long serialVersionUID = -6560012086857165997L;

	/**
	 * Erzeugt einen {@link TelephoneController} mit definierter
	 * {@link Session}, {@link TemplateDAO} und {@link UserDAO}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden {@link TemplateController}s.
	 * @param pTemplateDAO
	 *		Die {@link TemplateDAO} des zu erzeugenden
	 *		{@link TemplateController}s.
	 * @param pUserDAO
	 * 		Die {@link UserDAO} des zu erzeugenden {@link TemplateController}s.
	 * @throws IllegalArgumentException
	 * 		Falls einer der übergebenen Parameter {@code null} ist.
     */
	@Inject
	public TemplateController(final Session pSession,
							  final TemplateDAO pTemplateDAO,
							  final UserDAO pUserDAO) {
		super(Assertion.assertNotNull(pSession));
		templateDAO = Assertion.assertNotNull(pTemplateDAO);
		userDAO = Assertion.assertNotNull(pUserDAO);
	}

	/**
	 * Holt sich alle Templates sowie das aktuelle Template.
	 */
	@PostConstruct
	public void init() {
		allTemplates = templateDAO.getAllTemplates();
		backendTemplate = templateDAO.getTemplateByName("backend");
		frontendTemplate = templateDAO.getTemplateByName("frontend");
	}

	/**
	 * Speichert die jeweiligen Einstellungen wie z. B. zur Farbe für das
	 * Frontend ab und aktualisiert diese. Wenn die jeweiligen Inhalte leer
	 * sind, so gelten die Werkseinstellungen.
	 */
	public void saveFrontend() {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to save the frontend template!");
			return;
		}
		if (frontendTemplate.getMaintenanceContent().length() > 128) {
			addMessage("errorMaxLength");
			return;
		}
		if (frontendTemplate.getTitle().isEmpty())
			frontendTemplate.setTitle(null);
		if (frontendTemplate.getIntro().isEmpty())
			frontendTemplate.setIntro(null);
		if (frontendTemplate.getSlogan().isEmpty())
			frontendTemplate.setSlogan(null);
		if (frontendTemplate.getCopyright().isEmpty())
			frontendTemplate.setCopyright(null);
		if (frontendTemplate.getMaintenanceContent().isEmpty()
				|| frontendTemplate.getMaintenanceContent().equalsIgnoreCase("<br>"))
			frontendTemplate.setMaintenanceContent(null);
		try {
			templateDAO.update(assertNotNull(frontendTemplate));
		} catch (DuplicateTemplateNameException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorTemplateNameAlreadyInUse");
		}
		addMessage("successTemplateComplete");
	}

	/**
	 * Speichert die jeweiligen Einstellungen wie z. B. zur Farbe für das
	 * Backend ab und aktualisiert diese. Wenn die jeweiligen Inhalte leer sind,
	 * so gelten die Werkseinstellungen.
	 */
	public void saveBackend() {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			logger.info("Session without user tried to save the backend template!");
			return;
		}
		if (backendTemplate.getMaintenanceContent().length() > 128) {
			addMessage("errorMaxLength");
			return;
		}
		if (backendTemplate.getTitle().isEmpty())
			backendTemplate.setTitle(null);
		if (backendTemplate.getMaintenanceContent().isEmpty()
				|| backendTemplate.getMaintenanceContent().equalsIgnoreCase("<br>"))
			backendTemplate.setMaintenanceContent(null);
		try {
			templateDAO.update(assertNotNull(backendTemplate));
		} catch (DuplicateTemplateNameException e) {
			addMessageWithLogging(e, logger, Level.DEBUG, "errorTemplateNameAlreadyInUse");
		}
		addMessage("successTemplateComplete");
	}

	public List<Template> getAllTemplates() {
		return allTemplates;
	}

	public void setAllTemplates(List<Template> allTemplates) {
		this.allTemplates = allTemplates;
	}

	public Template getBackendTemplate() {
		return backendTemplate;
	}

	public void setBackendTemplate(final Template backendTemplate) {
		this.backendTemplate = backendTemplate;
	}

	public Template getFrontendTemplate() {
		return frontendTemplate;
	}

	public void setFrontendTemplate(final Template frontendTemplate) {
		this.frontendTemplate = frontendTemplate;
	}

	public String getAdminEmail() {
		return userDAO.getById(1).getEmail();
	}
}